package com.example.rsdev

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddFriendActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        // connexion à la bdd firestore
        val db = Firebase.firestore
        // accès à la collection <users>
        val users = db.collection("users")
        // l'utilisateur connecté et son email (FirebaseAuth)
        val user_connected = FirebaseAuth.getInstance().currentUser // l'utilisateur connecté
        val email_connected = user_connected?.email // son email

        // éléments graphiques sur la vue
        val firstname_friend = findViewById<EditText>(R.id.firstname_friend)
        val lastname_friend = findViewById<EditText>(R.id.lastname_friend)
        val add_friend = findViewById<Button>(R.id.add_friend)

        // valider l'ajout d'un nouvel ami
        add_friend.setOnClickListener {
            val user_conn = users.whereEqualTo("email", email_connected) // l'utilisateur connecté
            user_conn.get().addOnSuccessListener { users2 ->
                for (user in users2) {
                    // On récupere les contenus des 2 <EditText> créés
                    val firstname = firstname_friend.text.toString().trim()
                    val lastname = lastname_friend.text.toString().trim()
                    // vérifier si l'ami recherché existe
                    val friend = users.whereEqualTo("firstname", firstname).whereEqualTo("lastname", lastname)
                    friend.get().addOnCompleteListener { friend ->
                        if (friend.result.isEmpty() || friend.result == null) {
                            Toast.makeText(this@AddFriendActivity,"cet utilisateur n'existe pas!",Toast.LENGTH_LONG).show()
                        }
                        else {
                            val id_r = friend.result.documents[0].id // le _r signifie "receiver" (destinataire)
                            val firsname_r = friend.result.documents[0].get("firstname").toString()
                            val lastname_r = friend.result.documents[0].get("lastname").toString()
                            val id_s = user.id // le _s signifie "sender" (expediteur)
                            val firsname_s = user.get("firstname").toString()
                            val lastname_s = user.get("lastname").toString()

                            if((firsname_r == user.get("firstname") && lastname_r == user.get("lastname"))) {
                                Toast.makeText(this@AddFriendActivity,"interdit d'envoyer une demande à nous meme!",Toast.LENGTH_LONG).show()
                            }
                            else {
                                Functions.addFriendRequest(id_s,id_r)
                                Toast.makeText(this@AddFriendActivity,"demande d'ami envoyée",Toast.LENGTH_LONG).show()
                                val FeedActivity = Intent(this, FeedActivity::class.java)
                                startActivity(FeedActivity)
                            }
                        }
                    }
                    break
                }
            }
        }


    }


}