package com.example.rsdev

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // certains élements graphiques sur cette activity
        val received_requests = findViewById<TextView>(R.id.received_requests)
        val sended_requests = findViewById<TextView>(R.id.sended_requests)
        val nom_prenom_profil = findViewById<TextView>(R.id.nom_prenom_profil)
        val friends = findViewById<TextView>(R.id.friends)
        // connexion à la bdd firestore
        val db = Firebase.firestore
        // accès à la collection "users"
        val users = db.collection("users")
        // l'utilisateur connecté et son email (Firebase Authentication)
        val user_connected = FirebaseAuth.getInstance().currentUser
        val email_connected = user_connected?.email

        val user_conn = users.whereEqualTo("email", email_connected)
        user_conn.get().addOnSuccessListener { oneUser ->
            for (user in oneUser) {
                val prenom = user.get("firstname").toString()
                val nom = user.get("lastname").toString()
                nom_prenom_profil.text = prenom.plus(" ").plus(nom)
            }
        }


        // vers la page des amis
        friends.setOnClickListener {
            val friendsActivity = Intent(this, FriendsActivity::class.java)
            startActivity(friendsActivity)
        }

        // vers la page des demandes d'ami envoyées
        sended_requests.setOnClickListener {
            val requestsSendedActivity = Intent(this, RequestsSendedActivity::class.java)
            startActivity(requestsSendedActivity)
        }

        // vers la page des demandes d'ami reçues
        received_requests.setOnClickListener {
            val requestsReceivedActivity = Intent(this, RequestsReceivedActivity::class.java)
            startActivity(requestsReceivedActivity)
        }


    }

}




