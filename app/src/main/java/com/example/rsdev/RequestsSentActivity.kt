package com.example.rsdev

import com.google.firebase.firestore.Query
import com.example.rsdev.data.SentRequest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rsdev.SentRequestAdapter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
class RequestsSentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests_sent)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_requests)

        // connexion à la bdd firestore
        val db = Firebase.firestore
        // ID de l'utilisateur connecté (FirebaseAuth)
        val id_user_connected = FirebaseAuth.getInstance().currentUser?.uid
        // accès à la collection "friend_requests"
        val sent_friend_requests = db.collection("friend_requests").whereEqualTo("id_sender", id_user_connected)
            .whereEqualTo("validated", false)

        sent_friend_requests.get().addOnSuccessListener { querySnapshot ->
            val requestsList = ArrayList<SentRequest>()
            for (document in querySnapshot.documents) {
                val receiverId = document.get("id_receiver").toString()

                // fetch receiver data and create SentRequest object
                val users = db.collection("users")
                users.document(receiverId).get().addOnSuccessListener { receiver ->
                    val receiverFirstName = receiver.getString("firstname")
                    val receiverLastName = receiver.getString("lastname")
                    val SentRequest = SentRequest(

                        receiverFirstName.toString(),
                        receiverLastName.toString()
                    )

                    requestsList.add(SentRequest)
                    // update the recyclerview's adapter with the new list
                    recyclerView.adapter = SentRequestAdapter(requestsList)
                }
            }

            // Define the layout manager
            val layoutManager = LinearLayoutManager(this)
            // Define the adapter
            val adapter = SentRequestAdapter(requestsList)
            // Attach the adapter and layout manager to the RecyclerView
            recyclerView.layoutManager = layoutManager

        }
    }

}