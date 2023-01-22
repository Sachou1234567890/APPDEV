package com.example.rsdev

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class MyMessage : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.message_my)

        // éléments graphiques sur la vue
        val firstname = findViewById<TextInputEditText>(R.id.firstname)
        val lastname = findViewById<TextInputEditText>(R.id.lastname)
        val message = findViewById<TextInputEditText>(R.id.message)
        val send_button = findViewById<MaterialButton>(R.id.send_button)

        // val database = FirebaseFirestore.getInstance()
        val db = Firebase.firestore
//        val firestore = FirebaseFirestore.getInstance()
        val firestore: FirebaseFirestore by lazy { Firebase.firestore }

        // val messageInput = findViewById<EditText>(R.id.message_input)
        val messagesRef = db.collection("messages")
        // accès à la collection <users>
        val users = db.collection("users")
        // ID de l'utilisateur connecté (FirebaseAuth)
        val id_user_connected = FirebaseAuth.getInstance().currentUser?.uid
//        val email_connected = user_connected?.email // son email

        // Ajoutez un écouteur de clic sur le bouton "Envoyer"
        send_button.setOnClickListener {

            val firstname = firstname.text.toString().trim()
            val lastname = lastname.text.toString().trim() 
            val message = message.text.toString().trim()

            // destinataire du message
            val friend_query = users.whereEqualTo("firstname", firstname).whereEqualTo("lastname", lastname).limit(1)

            friend_query.get().addOnCompleteListener { friend ->
                if (friend.result.isEmpty() || friend.result == null) {
                    Toast.makeText(this@MyMessage,"cet utilisateur n'existe pas!",Toast.LENGTH_LONG).show()
                }
                else {
                    val to_user_id = friend.result.documents[0].id
                    val from_user_id = id_user_connected


                    if(from_user_id == to_user_id ) {
                        Toast.makeText(this@MyMessage,"interdit d'envoyer un message à nous meme!",Toast.LENGTH_LONG).show()
                    }
                            val messagedate = hashMapOf(
                                "from_user_id" to id_user_connected,
                                "to_user_id" to to_user_id,
                                "message" to message,
                                "timestamp" to FieldValue.serverTimestamp()
                            )

                            db.collection("messages").add(messagedate)
                                .addOnSuccessListener { documentReference ->
                                    Toast.makeText(this@MyMessage, "message envoyé avec succés", Toast.LENGTH_SHORT).show()
                                    val MessagesSendedActivity = Intent(this, MessagesSendedActivity::class.java)
                                    startActivity(MessagesSendedActivity)
                                }
                }
            }

        }
    }






}
