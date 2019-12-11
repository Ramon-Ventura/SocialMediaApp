package com.example.socialmediaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileActivity : AppCompatActivity() {
    //Defining Widgets
    private lateinit var tvProfile : TextView

    //Firebase variable
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        //Action Bar and its title
        supportActionBar?.title = "Profile"

        //Init
        tvProfile = findViewById(R.id.textViewProfile)
        auth = FirebaseAuth.getInstance()
    }

    private fun checkUserStatus (){
        val user : FirebaseUser? = auth.currentUser
        if(user != null){
            //user is signed in
            tvProfile.text = user.email
        }else{
            //user is not signed in, go to main
            startActivity( Intent(this,MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        //check on start of app
        checkUserStatus()
        super.onStart()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        //get item id
        val id : Int = item!!.itemId
        if(id == R.id.action_logout){
            auth.signOut()
            checkUserStatus()
        }
        return super.onOptionsItemSelected(item)
    }
}
