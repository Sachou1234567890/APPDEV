package com.example.rsdev

import com.google.firebase.firestore.Query
import com.example.rsdev.data.ReceivedRequest
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rsdev.ReceivedRequestAdapter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
class RequestsReceivedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests_received)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_requests)
//        val accept_friend_request = findViewById<Button>(R.id.accept_friend_request)
//
//        // connexion à la bdd firestore
//        val db = Firebase.firestore
//        // ID de l'utilisateur connecté (FirebaseAuth)
//        val id_user_connected = FirebaseAuth.getInstance().currentUser?.uid.toString()
//        // accès à la collection "friend_requests"
//        val received_friend_requests = db.collection("friend_requests").whereEqualTo("id_receiver", id_user_connected)
//            .whereEqualTo("validated", false)
//
//        received_friend_requests.get().addOnSuccessListener { querySnapshot ->
//            val requestsList = ArrayList<ReceivedRequest>()
//            for (document in querySnapshot.documents)
//            {
//                val senderId = document.get("id_sender").toString()
////                val receiverId = document.get("id_receiver").toString()
//
//                // fetch sender data and create ReceivedRequest object
//                val users = db.collection("users")
//                users.document(senderId).get().addOnSuccessListener { sender ->
//                    val senderFirstName = sender.getString("firstname")
//                    val senderLastName = sender.getString("lastname")
//                    val receivedRequest = ReceivedRequest(
//
//                        senderFirstName.toString(),
//                        senderLastName.toString()
//                    )
//
//                    requestsList.add(receivedRequest)
//                    // update the recyclerview's adapter with the new list
//                    recyclerView.adapter = ReceivedRequestAdapter(requestsList)
//                }
//
//                // valider la demande d'ami reçue
//                accept_friend_request.setOnClickListener {
//                    val friend_request =
//                        db.collection("friend_requests").document(document.id)
//                    // Set the "validated" field to true
//                    friend_request.update("validated", true)
//                        .addOnSuccessListener {
//                            Functions.addFriend(senderId, id_user_connected)
//                            Toast.makeText(
//                                this,
//                                "Demande d'ami validée",
//                                Toast.LENGTH_LONG
//                            ).show()
//                        }
//                        .addOnFailureListener {
//                            Toast.makeText(
//                                this,
//                                "erreur!",
//                                Toast.LENGTH_LONG
//                            ).show()
//                        }
//                }
//
//            }
//
//            // Define the layout manager
//            val layoutManager = LinearLayoutManager(this)
//            // Define the adapter
//            val adapter = ReceivedRequestAdapter(requestsList)
//            // Attach the adapter and layout manager to the RecyclerView
//            recyclerView.layoutManager = layoutManager
//        }

    }

}