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

class LoginActivity : AppCompatActivity() {
    //Defining widgets
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText
    private lateinit var btnLogin : Button
    private lateinit var tvNotHaveAccount : TextView

    //Defining firebase
    private lateinit var auth: FirebaseAuth

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
        tvNotHaveAccount = findViewById(R.id.textViewNotHaveAccount)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        //Set progressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Logging In...")

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
        //Not have account textView listener
        tvNotHaveAccount.setOnClickListener {
            startActivity( Intent(this,RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        progressDialog.show()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //dismiss progressDialog
                    progressDialog.dismiss()
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    startActivity( Intent(this,ProfileActivity::class.java))
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
}
