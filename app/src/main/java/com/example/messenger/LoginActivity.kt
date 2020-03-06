package com.example.messenger

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
  setContentView(R.layout.login_activity)
        login_button.setOnClickListener {
            val email = email_editText_login.text.toString()
            val password = password_editText_login.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)

            Log.d("Login","Attempt login  with  email /pw: $email/***")
        }
    back_register_textView.setOnClickListener {
        finish()
    }


    }
}