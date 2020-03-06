package com.example.messenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {
    companion object{
        val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      register_bottom.setOnClickListener {
       performRegister()
      }
        already_have_acount_textView.setOnClickListener {
            Log.d("RegisterActivity","Try Agi")
         val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        select_photo_button.setOnClickListener {
            Log.d("RegisterActivity"," Try to show photo select")
            val intent = Intent(Intent.ACTION_PICK)
            intent . type = "image/*"
            startActivityForResult(intent,0)
        }


    }

    var selectedPhotoUri : Uri ?= null


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("RegisterActivity","photo  was select")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)

            select_image_register.setImageBitmap(bitmap)
            select_photo_button.alpha = 0f
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            select_photo_button.setBackgroundDrawable(bitmapDrawable)
        }

    }


    private  fun performRegister(){
        val email = email_editText.text.toString()
        val password = password_editText.text.toString()
        if (email.isEmpty()|| password.isEmpty()){
            Toast.makeText(this,"Please enter text in email and passwword",Toast.LENGTH_SHORT).show()
            return}
        Log.d("RegisterActivity","Email is"+email)
        Log.d("RegisterActivity","Password is $password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful) return@addOnCompleteListener
                Log.d("Main","Successful cread user  with uild :${it.result?.user?.uid}")
                uploadImageToFirebaseStore()
            }
            .addOnFailureListener {
                Log.d("Main","failed to create user: ${it.message}")
                Toast.makeText(this,"failed create user ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }
    private  fun uploadImageToFirebaseStore(){
        if (selectedPhotoUri==null)return
        val filename = UUID.randomUUID().toString()
    val ref= FirebaseStorage.getInstance().getReference("/image/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity","Successfully upload Image :${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d("RegisterActivity","file location :$it")
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {

            }
    }
    private  fun saveUserToFirebaseDatabase(profileImageUrl:String){
        val uid = FirebaseAuth.getInstance().uid ?:""
       val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
        val  user = User(uid,name_editText.text.toString(),profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity","Finally we saved the  user to Firebase Database")
                val intent = Intent(this,LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d(TAG,"Failed to set value to database :${it.message}")
            }
    }
}
class  User (var uid :String, val username :String , val profileImageUrl: String)
