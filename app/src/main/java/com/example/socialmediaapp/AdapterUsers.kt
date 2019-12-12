package com.example.socialmediaapp

import android.content.Context
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.lang.Exception

class AdapterUsers(var cont : Context, var userL : List<ModelUser>) : RecyclerView.Adapter<AdapterUsers.MyHolder>() {

    var context : Context = cont
    var userList : List<ModelUser> = userL


    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var avatar : ImageView = itemView.findViewById(R.id.imageViewAvatar)
        var name : TextView = itemView.findViewById(R.id.textViewNameC)
        var email : TextView = itemView.findViewById(R.id.textViewEmailC)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.row_users,parent)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        //set data
        val userImage : String = userList[position].pImage
        val userName : String = userList[position].pName
        val userEmail : String = userList[position].pEmail

        holder.name.text = userName
        holder.email.text = userEmail
        try {
            Picasso.get().load(userImage).placeholder(R.drawable.ic_face_purple).into(holder.avatar)
        }catch (e : Exception){

        }
        //handle item click
        holder.itemView.setOnClickListener {
            Toast.makeText(context,""+userEmail,Toast.LENGTH_SHORT).show()
        }

    }
}



