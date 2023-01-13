package com.example.rsdev

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.actionBar?.hide()
        setContentView(R.layout.activity_register)

        val db = Firebase.firestore

        //Recupération des variables sur la vue
        val btn_login = findViewById(R.id.btn_login) as Button
        val btn_register = findViewById(R.id.btn_register) as Button

        // Action au click de coannexion
        btn_login.setOnClickListener {

            val firstname = findViewById<EditText>(R.id.firstname_user).text.toString();
            val lastname = findViewById<EditText>(R.id.lastname_user).text.toString();
            val username = findViewById<EditText>(R.id.username_user).text.toString();
            val birthDate =  findViewById<DatePicker>(R.id.brith_date);
            val email = findViewById<EditText>(R.id.email_user).text.toString();
            val password = findViewById<EditText>(R.id.password_login).text.toString();
            val confirmPassword = findViewById<EditText>(R.id.confirm_password).text.toString();
            val day_brith: Int = birthDate.getDayOfMonth();
            val month_brith: Int = birthDate.getMonth() + 1;
            val year_brith: Int = birthDate.getYear();

            //On vérifie que les champs ne sont pas vides
            if(email.trim().isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
//                login du user avec son MDP
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                            task ->
                        if (task.isSuccessful) {
                            var ranString = Functions.getRandomString(20) // un String aléatoire de 20 caractères
                            val user = hashMapOf(
                                "user_id" to ranString,
                                "firstname" to firstname,
                                "lastname" to lastname,
                                "username" to username,
                                "email" to email,
                                "friends" to null // tableau contenants les IDs des amis (par défaut null)
                            )
                            // inscription d'un nouvel utilisateur
                            val users = db.collection("users")
                            users.document(ranString).set(user)
                                .addOnSuccessListener { documentReference ->
                                    Toast.makeText(applicationContext,"user added successfully", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(applicationContext,"failed to add user", Toast.LENGTH_LONG).show()
                                }
                        }
                    }
            }
            else{
                Toast.makeText(applicationContext, "Merci d'entrer des identifiants valides", Toast.LENGTH_SHORT).show()
            }
        }

        btn_register.setOnClickListener {
            val LoginActivity = Intent(this, LoginActivity::class.java)
            startActivity(LoginActivity)
        }


    }





}
