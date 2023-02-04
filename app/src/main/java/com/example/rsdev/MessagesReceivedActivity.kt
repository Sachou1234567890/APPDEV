package com.example.rsdev

import android.content.Intent
import com.google.firebase.firestore.Query
import com.example.rsdev.data.ReceivedMessage
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rsdev.ReceivedMessageAdapter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import com.example.rsdev.databinding.ActivityMessagesReceivedBinding

class MessagesReceivedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessagesReceivedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_received)

        // inflate the header fragment
        binding = ActivityMessagesReceivedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setSubtitle("Messages reçus");
        binding.toolbar.setSubtitleTextAppearance(this, R.style.ToolbarSubtitleAppearance)
        binding.toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleAppearance)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // inflate the footer fragment
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val footerFragment = FooterFragment()
        fragmentTransaction.add(R.id.footer_container, footerFragment)
        fragmentTransaction.commit()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_messages)

        // connexion à la bdd firestore
        val db = Firebase.firestore
        // accès à la collection "messages"
        val messages = db.collection("messages")
        // ID de l'utilisateur connecté (FirebaseAuth)
        val id_user_connected = FirebaseAuth.getInstance().currentUser?.uid
        val messages_recus = messages.whereEqualTo("to_user_id", id_user_connected)

        messages_recus.get().addOnSuccessListener { querySnapshot ->
            val messagesList = ArrayList<ReceivedMessage>()
            for (document in querySnapshot.documents) {
                val senderId = document.get("from_user_id").toString()
                val messageText = document.get("message").toString()
                val timestamp = document.get("timestamp") as Timestamp
                val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
                val sdf = SimpleDateFormat("dd/MM/yyyy")
                val netDate = java.util.Date(milliseconds)
                val date_envoi = sdf.format(netDate).toString()
                val toUserId = document.get("to_user_id").toString()

                // fetch sender data and create ReceivedMessage object
                val users = db.collection("users")
                users.document(senderId).get().addOnSuccessListener { sender ->
                    val senderFirstName = sender.getString("firstname")
                    val senderLastName = sender.getString("lastname")
                        val receivedMessage = ReceivedMessage(
                            messageText.toString(),
                            date_envoi.toString(),
                            senderFirstName.toString(),
                            senderLastName.toString()
                        )

                    messagesList.add(receivedMessage)
                    // update the recyclerview's adapter with the new list
                    recyclerView.adapter = ReceivedMessageAdapter(messagesList)
                }
            }

            // Define the layout manager
            val layoutManager = LinearLayoutManager(this)
            // Define the adapter
            val adapter = ReceivedMessageAdapter(messagesList)
            // Attach the adapter and layout manager to the RecyclerView
            recyclerView.layoutManager = layoutManager
//            recyclerView.adapter = adapter


        }
    }

}