package com.example.rsdev

import com.google.firebase.firestore.Query
import com.example.rsdev.data.SentMessage
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rsdev.SentMessageAdapter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
class MessagesSentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_sent)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_messages)

        // connexion à la bdd firestore
        val db = Firebase.firestore
        // accès à la collection "messages"
        val messages = db.collection("messages")
        // ID de l'utilisateur connecté (FirebaseAuth)
        val id_user_connected = FirebaseAuth.getInstance().currentUser?.uid
        val messages_envoyes = messages.whereEqualTo("from_user_id", id_user_connected)

        messages_envoyes.get().addOnSuccessListener { querySnapshot ->
            val messagesList = ArrayList<SentMessage>()
            for (document in querySnapshot.documents) {
                val receiverId = document.get("to_user_id").toString()
                val messageText = document.get("message").toString()
                val timestamp = document.get("timestamp") as Timestamp
                val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
                val sdf = SimpleDateFormat("dd/MM/yyyy")
                val netDate = java.util.Date(milliseconds)
                val date_envoi = sdf.format(netDate).toString()
//                val toUserId = document.get("to_user_id").toString()

                // fetch receiver data and create SentMessage object
                val users = db.collection("users")
                users.document(receiverId).get().addOnSuccessListener { sender ->
                    val senderFirstName = sender.getString("firstname")
                    val senderLastName = sender.getString("lastname")
                    val SentMessage = SentMessage(
                        messageText.toString(),
                        date_envoi.toString(),
                        senderFirstName.toString(),
                        senderLastName.toString()
                    )

                    messagesList.add(SentMessage)
                    // update the recyclerview's adapter with the new list
                    recyclerView.adapter = SentMessageAdapter(messagesList)
                }
            }

            // Define the layout manager
            val layoutManager = LinearLayoutManager(this)
            // Define the adapter
            val adapter = SentMessageAdapter(messagesList)
            // Attach the adapter and layout manager to the RecyclerView
            recyclerView.layoutManager = layoutManager
//            recyclerView.adapter = adapter
        }
            .addOnFailureListener {
                Toast.makeText(this@MessagesSentActivity, "Aucun message envoyé", Toast.LENGTH_SHORT).show()
            }

    }

}