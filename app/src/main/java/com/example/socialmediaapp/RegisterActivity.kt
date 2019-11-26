package com.example.socialmediaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText

class RegisterActivity : AppCompatActivity() {
    //Defining buttons
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText
    private lateinit var btnRegister : Button

    //Firebase variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        //Init
        etEmail = findViewById(R.id.editTextEmail)
        etPassword = findViewById(R.id.editTextPassword)
        btnRegister = findViewById(R.id.ar_buttonRegister)
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

    }
}
