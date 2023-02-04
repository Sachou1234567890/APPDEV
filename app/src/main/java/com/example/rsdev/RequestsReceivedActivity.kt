package com.example.rsdev

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.firestore.Query
import com.example.rsdev.data.Friend
//import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rsdev.FriendsAdapter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.toObjects
import java.text.SimpleDateFormat
import android.content.Context
import android.content.Intent
//import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.example.rsdev.data.ReceivedRequest
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FieldValue
import com.example.rsdev.databinding.ActivityRequestsReceivedBinding


class RequestsReceivedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestsReceivedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests_received)

        // inflate the header fragment
        binding = ActivityRequestsReceivedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setSubtitle("Invitations reçues");
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

        val recyclerView_requests = findViewById<RecyclerView>(R.id.recyclerView_requests)
        val swipe_refresh_layout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)

//        val delete_friend_bt = findViewById<Button>(R.id.delete_friend_bt)


        // connexion à la bdd firestore
        val db = Firebase.firestore
        // accaccès à la collection "users"
        val users = db.collection("users")
        // accès à la collection "friend_requests"
        var friend_requests = db.collection("friend_requests")
        // l'Id de utilisateur connecté (Firebase Authentication)
        val id_user_connected = FirebaseAuth.getInstance().currentUser?.uid.toString()

        // accès à la collection "friend_requests"
        val received_friend_requests = db.collection("friend_requests").whereEqualTo("id_receiver", id_user_connected)
            .whereEqualTo("validated", false)

        received_friend_requests.get().addOnSuccessListener { querySnapshot ->
            val requestsList = ArrayList<ReceivedRequest>()
            for (document in querySnapshot.documents) {
                val senderId = document.get("id_sender").toString()

                // fetch sender data and create ReceivedRequest object
                val users = db.collection("users")
                users.document(senderId).get().addOnSuccessListener { sender ->
                    if (sender != null ) {
                        val senderFirstName = sender.getString("firstname").toString()
                        val senderLastName = sender.getString("lastname").toString()

                        val receivedRequest = ReceivedRequest(
                            sender.id,
                            senderFirstName,
                            senderLastName
                        )

                        requestsList.add(receivedRequest)
                        // update the recyclerview's adapter with the new list
                        recyclerView_requests.adapter = ReceivedRequestAdapter(requestsList)
                        val layoutManager = LinearLayoutManager(this)
                        recyclerView_requests.layoutManager = layoutManager
                    }
                }
            }

        }



        swipe_refresh_layout.setOnRefreshListener {
            // update data here
//            FriendsAdapter.updateData(newData)
//            recyclerView_friends.adapter?.notifyDataSetChanged()
            swipe_refresh_layout.isRefreshing = false
        }






    }


}