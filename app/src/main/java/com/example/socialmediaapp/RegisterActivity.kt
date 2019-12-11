@file:Suppress("DEPRECATION")

package com.example.socialmediaapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class RegisterActivity : AppCompatActivity() {
    //Defining buttons
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText
    private lateinit var btnRegister : Button

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
        btnRegister = findViewById(R.id.ar_buttonRegister)
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
                    if (user != null) {
                        Toast.makeText(this,"Registered \n"+user.email, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,ProfileActivity::class.java))
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
