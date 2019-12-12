package com.example.socialmediaapp


import android.os.Bundle
import android.view.Display
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * A simple [Fragment] subclass.
 */
class UsersFragment : Fragment() {
    private lateinit var recyclerView : RecyclerView

    private lateinit var adapted : AdapterUsers
    private lateinit var userList : List<ModelUser>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_users, container, false)

        //init recycle
        recyclerView = view.findViewById(R.id.recyclerViewUsers)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager= LinearLayoutManager(activity)

        userList = ArrayList()

        getAllUsers()


        return view
    }

    private fun getAllUsers() {
        val fUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().getReference("Users")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.isEmpty()
                for(ds : DataSnapshot in dataSnapshot.children){
                    //var modelUser : ModelUser = ds.getValue(ModelUser ::class)

                }
            }

        })
    }


}
