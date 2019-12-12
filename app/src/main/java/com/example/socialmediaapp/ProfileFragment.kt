@file:Suppress("DEPRECATION")

package com.example.socialmediaapp


import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage.getInstance
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {


    private lateinit var firebaseAuth: FirebaseAuth
    private  var firebaseUser: FirebaseUser? = null
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    //storage
    private lateinit var storageReference : StorageReference
    //path where images of user profile and cover will be stored
    private var storagePath = "Users_Profile_Cover_Imgs/"
    //views from xml
    private lateinit var ivAvatar : ImageView
    private lateinit var ivCover : ImageView
    private lateinit var tvEmail : TextView
    private lateinit var tvName : TextView
    private lateinit var tvPhone : TextView
    private lateinit var fabEdit : FloatingActionButton
    private lateinit var progressDialog : ProgressDialog

    companion object {
        const val CAMERA_REQUEST_CODE = 100
        const val STORAGE_REQUEST_CODE = 200
        const val IMAGE_PICK_GALLERY_CODE = 300
        const val IMAGE_PICK_CAMERA_CODE = 400
    }
    private lateinit var cameraPermissions : Array<String>
    private lateinit var storagePermissions : Array<String>

    //uri of picked image
    private lateinit var image_uri : Uri

    //for checking profile or covered photo
    private lateinit var profileOrCoverPhoto : String

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
        storageReference = getInstance().reference

        //init arrays of permissions
        cameraPermissions = arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)


        //Init views
        ivAvatar = view.findViewById(R.id.imageViewPicture)
        ivCover = view.findViewById(R.id.imageViewCover)
        tvEmail = view.findViewById(R.id.textViewEmail)
        tvName = view.findViewById(R.id.textViewName)
        tvPhone = view.findViewById(R.id.textViewPhone)
        fabEdit = view.findViewById(R.id.floatingActionButton)
        progressDialog = ProgressDialog(activity)

        //floating button listener
        fabEdit.setOnClickListener {
            showEditProfileDialog()
        }

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
                    val cover : String = ""+ ds.child("cover").value
                    //set data
                    tvEmail.text = email
                    tvName.text = name
                    tvPhone.text = phone
                    try {
                        Picasso.get().load(image).into(ivAvatar)
                    }catch (e : Exception){
                        Picasso.get().load(R.drawable.ic_face_white).into(ivAvatar)
                    }
                    try {
                        Picasso.get().load(cover).into(ivCover)
                    }catch (e : Exception){

                    }

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        return view
    }
    //check storage permission
    private fun checkStoragePermission() : Boolean{
        return ContextCompat.checkSelfPermission(activity!!,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission(){
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE)
    }

    private fun checkCameraPermission() : Boolean{
        val result = ContextCompat.checkSelfPermission(activity!!,android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
        val result1 = ContextCompat.checkSelfPermission(activity!!,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED

        return result && result1
    }

    private fun requestCameraPermission(){
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE)
    }

    private fun showEditProfileDialog() {
        /*show dialog options
          1. Edit profile picture
          2. Edit cover photo
          3. Edit name
          4. Edit phone
        */
        //options to show in dialog
        val options = arrayOf("Edit Profile Picture","Edit cover photo","Edit name","Edit Phone")

        //
        val builder = AlertDialog.Builder(activity)
        with (builder){
            setTitle("Choose an option")
            setItems(options){dialog, which ->
                when (which){
                    0->{
                        progressDialog.setMessage("Updating profile picture")
                        profileOrCoverPhoto = "image"
                        showImagePicDialog()
                    }
                    1->{
                        progressDialog.setMessage("Updating cover photo")
                        profileOrCoverPhoto = "cover"
                        showImagePicDialog()
                    }
                    2->{
                        progressDialog.setMessage("Updating name")
                        showNamePhoneUpdateDialog("name")

                    }
                    3->{
                        progressDialog.setMessage("Updating phone")
                        showNamePhoneUpdateDialog("phone")

                    }

                }

            }
            show()
        }
    }

    private fun showNamePhoneUpdateDialog(key: String) {
        //custom dialog
        //Alert dialog
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Update $key")
        //LinearLayout
        val linearLayout = LinearLayout(activity)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(10,10,10,10)

        val editText = EditText(activity)
        editText.hint = "Enter $key"
        linearLayout.addView(editText)

        builder.setView(linearLayout)

        builder.setPositiveButton("Update"){dialog, which ->
            //Input
            val value = editText.text.toString().trim()
            if(value.isNotEmpty()){
                progressDialog.show()
                val result = HashMap<String?,Any>()
                result.put(key,value)
                databaseReference.child(firebaseUser!!.uid).updateChildren(result).addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(activity,"Updated...",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(activity,"Enter $key",Toast.LENGTH_SHORT).show()
            }

        }
        //Button Recover
        builder.setNegativeButton("Cancel"){dialog, which ->
            dialog.dismiss()
        }

        builder.create().show()

    }

    private fun showImagePicDialog() {
        val options = arrayOf("Camera","Gallery")

        //
        val builder = AlertDialog.Builder(activity)
        with (builder){
            setTitle("Pick Image")
            setItems(options){dialog, which ->
                when (which){
                    0->{
                        //camera clicked
                        if(!checkCameraPermission()){
                            requestCameraPermission()
                        }else{
                            pickFromCamera()
                        }
                    }
                    1->{
                        //gallery image
                        if(!checkStoragePermission()){
                            requestStoragePermission()
                        }else{
                            pickFromGallery()
                        }
                    }


                }

            }
            show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            CAMERA_REQUEST_CODE ->{
                if(grantResults.isNotEmpty()){
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if(cameraAccepted && writeStorageAccepted){
                        pickFromCamera()
                    }else{
                        //Permission denied
                        Toast.makeText(activity,"Please enable camera and storage permission",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            STORAGE_REQUEST_CODE ->{
                if(grantResults.isNotEmpty()){
                    val writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if( writeStorageAccepted){
                        pickFromGallery()
                    }else{
                        //Permission denied
                        Toast.makeText(activity,"Please enable storage permission",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data!!.data!!
                uploadProfileCoverPhoto(image_uri)

            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE) {
                uploadProfileCoverPhoto(image_uri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadProfileCoverPhoto(uri: Uri) {
        progressDialog.show()
        val filePathAndName = storagePath+""+profileOrCoverPhoto+"_"+ firebaseUser!!.uid

        val storageReference2: StorageReference = storageReference.child(filePathAndName)
        storageReference2.putFile(uri).addOnSuccessListener {
            val uriTask : Task<Uri> = it.storage.downloadUrl
            while (!uriTask.isSuccessful);
            val downloadUri = uriTask.result!!

            if(uriTask.isSuccessful){
                val results = HashMap<String?,Any>()
                results[profileOrCoverPhoto] = downloadUri.toString()

                databaseReference.child(firebaseUser!!.uid).updateChildren(results).addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(activity,"Image Updated ...",Toast.LENGTH_SHORT).show()

                }
            }else{
                progressDialog.dismiss()
                Toast.makeText(activity,"Error ...",Toast.LENGTH_SHORT).show()

            }
        }.addOnFailureListener{

            Toast.makeText(activity,it.message,Toast.LENGTH_SHORT).show()

        }

    }



    private fun pickFromCamera() {
        //intent of picking image from device camera
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic")
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description")

        //put image uri
        image_uri = activity!!.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)!!

        //intent to camera
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri)
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE)


    }

    private fun pickFromGallery() {
        //PICK FROM GALLERY
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE)

    }


}
