@file:Suppress("DEPRECATION")

package com.example.socialmediaapp

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    companion object{
        const val RC_SIGN_IN = 100
    }
    //Defining widgets
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText
    private lateinit var btnLogin : Button
    private lateinit var btnGoogleLogin : SignInButton
    private lateinit var tvNotHaveAccount : TextView
    private lateinit var tvRecoverPassword : TextView

    //Defining firebase and google
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient

    //Progress Dialog
    private lateinit var  progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Action Bar and its title
        supportActionBar?.title = "Create Account"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //Init
        etEmail = findViewById(R.id.editTextEmail)
        etPassword = findViewById(R.id.editTextPassword)
        btnLogin = findViewById(R.id.buttonLogin)
        btnGoogleLogin = findViewById(R.id.buttonGoogleSignIn)
        tvNotHaveAccount = findViewById(R.id.textViewNotHaveAccount)
        tvRecoverPassword = findViewById(R.id.textViewRecoverPassword)

        //Before mAuth
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        //Set progressDialog
        progressDialog = ProgressDialog(this)


        //Button login listener
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            //validate
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                //invalid email
                etEmail.error = "Invalid Email"
                etEmail.isFocusable = true
            }else{
                //valid email
                loginUser(email,password)
            }
        }

        //Button google Login
        btnGoogleLogin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        //Not have account textView listener
        tvNotHaveAccount.setOnClickListener {
            startActivity( Intent(this,RegisterActivity::class.java))
            finish()
        }
        //Recover password textView listener
        tvRecoverPassword.setOnClickListener {
            showRecoverPasswordDialog()
        }
    }

    private fun showRecoverPasswordDialog() {
        //Alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Recover")
        //LinearLayout
        val linearLayout = LinearLayout(this)

        //Views to dialog
        val emailEt = EditText(this)
        emailEt.hint = "Email"
        emailEt.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        emailEt.minEms = 10

        linearLayout.addView(emailEt)
        linearLayout.setPadding(10,10,10,10)

        builder.setView(linearLayout)

        //Buttons recover
        builder.setPositiveButton("Recover"){dialog, which ->
            //Input Email
            val emailDialog = emailEt.text.toString().trim()
            beginRecovery(emailDialog)

        }
        //Button Recover
        builder.setNegativeButton("Cancel"){dialog, which ->
            dialog.dismiss()
        }
        //Show dialog
        builder.create().show()
    }

    private fun beginRecovery(emailDialog: String) {
        progressDialog.setMessage("Sending Email...")
        progressDialog.show()
        auth.sendPasswordResetEmail(emailDialog).addOnCompleteListener{task ->
            if (task.isSuccessful){
                progressDialog.dismiss()
                Toast.makeText(this,"Email Sent",Toast.LENGTH_SHORT).show()
            }else{
                progressDialog.dismiss()
                Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            progressDialog.dismiss()
            Toast.makeText(this,""+it.message,Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginUser(email: String, password: String) {
        progressDialog.setMessage("Logging In...")
        progressDialog.show()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //dismiss progressDialog
                    progressDialog.dismiss()
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    startActivity( Intent(this,DashboardActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    //dismiss progressDialog
                    progressDialog.dismiss()
                    Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }

                // ...
            }

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this,""+e.message,Toast.LENGTH_SHORT).show()
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser

                    ///if user is singing in first time then get and show user info from google account
                    if(task.result?.additionalUserInfo?.isNewUser!!){
                        //get user email and uid from auth
                        val hEmail : String? = user?.email
                        val uid = user?.uid

                        val hashMap = HashMap<Any,String?>()
                        hashMap["email"] = hEmail
                        hashMap["uid"] = uid
                        hashMap["name"] = ""
                        hashMap["phone"] = ""
                        hashMap["image"] = ""
                        //Firebase database instance
                        val dataBase = FirebaseDatabase.getInstance()
                        //path to store userdata
                        val reference : DatabaseReference = dataBase.getReference("Users")

                        if (uid != null) {
                            reference.child(uid).setValue(hashMap)
                        }
                    }



                    //show user email in toast
                    Toast.makeText(this,""+user!!.email,Toast.LENGTH_SHORT).show()
                    //Go to profile
                    startActivity( Intent(this,DashboardActivity::class.java))
                    finish()
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this,"Login Failed...",Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }

                // ...
            }.addOnFailureListener {
                Toast.makeText(this,""+it.message,Toast.LENGTH_SHORT).show()
            }
    }
}
