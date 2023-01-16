package com.example.rsdev

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(FirebaseAuth.getInstance().currentUser == null) {
            val LoginActivity = Intent(this, LoginActivity::class.java)
            LoginActivity.putExtra("keyIdentifier", "value")
            startActivity(LoginActivity)
        }
        else {
            val FeedActivity = Intent(this, FeedActivity::class.java)
            FeedActivity.putExtra("keyIdentifier", "value")
            startActivity(FeedActivity)

//            val LoginActivity = Intent(this, LoginActivity::class.java)
//            LoginActivity.putExtra("keyIdentifier", "value")
//            startActivity(LoginActivity)
        }
    }
}
