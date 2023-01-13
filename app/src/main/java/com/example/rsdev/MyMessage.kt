package com.example.rsdev

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MyMessage : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.message_my)
        val database = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = Firebase.firestore
        val sendButton = findViewById<Button>(R.id.send_button)
        val messageInput = findViewById<EditText>(R.id.message_input)
        val messagesRef = database.collection("messages")

        val query = messagesRef
            .whereEqualTo("receiver", userId)
            .orderBy("timestamp", Query.Direction.ASCENDING)


        query.get().addOnSuccessListener { snapshot ->
            for (document in snapshot) {
                val sender = document.get("to_user_id") as String
                val content = document.get("message") as String
                val timestamp = document.get("timestamp") as Timestamp
                val message = "$sender: $content ($timestamp)"
                println(message)
            }
        }

        // Ajoutez un écouteur de clic sur le bouton "Envoyer"
        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            val messagedate = hashMapOf(
                "from_user_id" to userId,
                "to_user_id" to userId,
                "message" to message,
                "timestamp" to FieldValue.serverTimestamp()
            )


            db.collection("messages").add(messagedate)

                .addOnSuccessListener { documentReference ->
                    Log.d(
                        ContentValues.TAG,
                        "DocumentSnapshot written with ID: ${documentReference.id}"
                    )
                }

        }
    }
}
