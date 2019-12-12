package com.example.socialmediaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class DashboardActivity : AppCompatActivity() {
    //Defining Widgets
    private lateinit var actionBar: ActionBar
    //Firebase variable
    private lateinit var auth: FirebaseAuth

    //Fragments
    private lateinit var homeFragment: HomeFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var usersFragment: UsersFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        //Action Bar and its title
        supportActionBar?.title = "Profile"

        //Init
        auth = FirebaseAuth.getInstance()

        //Home fragment as default on start
        supportActionBar?.title = "Home"
        homeFragment = HomeFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout,homeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

        //Bottom navigation listener
        val bottomNavigation : BottomNavigationView = findViewById(R.id.bottomNavMenu)

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId){

                R.id.nav_home->{
                    supportActionBar?.title = "Home"
                    homeFragment = HomeFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout,homeFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                //When user clicks profile option
                R.id.nav_profile ->{
                    supportActionBar?.title = "Profile"
                    profileFragment = ProfileFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout,profileFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()

                }
                //When user clicks users option
                R.id.nav_users->{
                    supportActionBar?.title = "Users"
                    usersFragment = UsersFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout,usersFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
            }
            true
        }

    }

    private fun checkUserStatus (){
        val user : FirebaseUser? = auth.currentUser
        if(user != null){
            //user is signed in
            //tvProfile.text = user.email
        }else{
            //user is not signed in, go to main
            startActivity( Intent(this,MainActivity::class.java))
            finish()
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onStart() {
        //check on start of app
        checkUserStatus()
        super.onStart()
    }

    //Inflate the menu with the options
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Handling users sign out
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
