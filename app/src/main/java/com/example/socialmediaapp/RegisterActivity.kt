@file:Suppress("DEPRECATION")

package com.example.socialmediaapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class RegisterActivity : AppCompatActivity() {
    //Defining widgets
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText
    private lateinit var btnRegister : Button
    private lateinit var tvHaveAccount : TextView

    //Firebase variable
    private lateinit var auth: FirebaseAuth

    //Progress Dialog
    private lateinit var  progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Init
        etEmail = findViewById(R.id.editTextEmail)
        etPassword = findViewById(R.id.editTextPassword)
        btnRegister = findViewById(R.id.buttonLogin)
        tvHaveAccount = findViewById(R.id.textViewNotHaveAccount)
        auth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Registering...")

        //Action Bar and its title
        supportActionBar?.title = "Create Account"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //Register Listener
        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            //validate
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                etEmail.error = "Invalid Email"
                etEmail.isFocusable = true
            }else if (etPassword.length() < 6) {
                etPassword.error = "Short Password"
                etPassword.isFocusable = true
            }else{
                registerUser(email,password)
            }
        }
        //Have account TextView Listener
        tvHaveAccount.setOnClickListener {
            startActivity( Intent(this,LoginActivity::class.java))
            finish()
        }
    }

    //Register user to firebase collection
    private fun registerUser(email: String, password: String) {
        progressDialog.show()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, dismiss dialog and start profile activity
                    progressDialog.dismiss()
                    val user = auth.currentUser
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

                    if (user != null) {
                        Toast.makeText(this,"Registered \n"+user.email, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,DashboardActivity::class.java))
                        finish()
                    }
                } else {
                    // If sign in fails, display a message to the user.
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
}
