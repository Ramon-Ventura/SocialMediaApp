package com.example.socialmediaapp


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private  var firebaseUser: FirebaseUser? = null
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    //views from xml
    private lateinit var ivAvatar : ImageView
    private lateinit var tvEmail : TextView
    private lateinit var tvName : TextView
    private lateinit var tvPhone : TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_profile, container, false)

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Users")

        //Init views
        ivAvatar = view.findViewById(R.id.imageViewPicture)
        tvEmail = view.findViewById(R.id.textViewEmail)
        tvName = view.findViewById(R.id.textViewName)
        tvPhone = view.findViewById(R.id.textViewPhone)

        //Query
        val query : Query = databaseReference.orderByChild("email").equalTo(firebaseUser?.email)
        query.addValueEventListener( object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds : DataSnapshot in dataSnapshot.children){
                    //Get data
                    val name : String = ""+ ds.child("name").value
                    val email : String = ""+ ds.child("email").value
                    val phone : String = ""+ ds.child("phone").value
                    val image : String = ""+ ds.child("image").value
                    //set data
                    tvEmail.text = email
                    tvName.text = name
                    tvPhone.text = phone
                    try {
                        Picasso.get().load(image).into(ivAvatar)
                    }catch (e : Exception){
                        Picasso.get().load(R.drawable.ic_add_image).into(ivAvatar)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        return view
    }


}
