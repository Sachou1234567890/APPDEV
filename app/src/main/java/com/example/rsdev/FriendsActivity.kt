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
import com.example.rsdev.databinding.ActivityFriendsBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FieldValue
class FriendsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        // inflate the header fragment
        binding = ActivityFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setSubtitle("Mes amis");
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


        val recyclerView_friends = findViewById<RecyclerView>(R.id.recyclerView_friends)
        val swipe_refresh_layout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)

        // connexion ?? la bdd firestore
        val db = Firebase.firestore
        // accacc??s ?? la collection "users"
        val users = db.collection("users")
        // acc??s ?? la collection "friend_requests"
        var friend_requests = db.collection("friend_requests")
        // l'Id de utilisateur connect?? (Firebase Authentication)
        val id_user_connected = FirebaseAuth.getInstance().currentUser?.uid.toString()

        // l'utilisateur connect?? (BDD Firestore)
        val user_conn = db.collection("users").document(id_user_connected)
        user_conn.get().addOnSuccessListener { currentUser ->
                // tableau des IDs des amis de l'utilisateur connect??'
                val friendsArray = currentUser.toObject<User>()?.friends // on a cr???? une classe "User" dans le fichier "User.kt"
                if (friendsArray != null) {

                    val FriendsList = ArrayList<Friend>()
                    // on parcourt les IDs des amis de l'utilisateur connect??, et ?? partir de chaque ID on extrait les donn??es de l'ami
                    for (friend_id in friendsArray) {
                        val friendRef = db.collection("users").document(friend_id)
                        friendRef.get().addOnSuccessListener { friend ->

                            val firstname_friend = friend.get("firstname").toString()
                            val lastname_friend = friend.get("lastname").toString()
                            val id_friend = friend.get("user_id").toString()

                            val friend_element = Friend(

                                id_friend,
                                firstname_friend,
                                lastname_friend
                            )
                            FriendsList.add(friend_element)

                            recyclerView_friends.adapter = FriendsAdapter(FriendsList)
                            // Define the layout manager
                            val layoutManager = LinearLayoutManager(this)
                            // Attach the adapter and layout manager to the recyclerView_friends
                            recyclerView_friends.layoutManager = layoutManager
//                             supprimer la personne de la liste des amis
//                            delete_friend_bt.setOnClickListener {
//                                users.document(id_user_connected).update("friends", FieldValue.arrayRemove(friendRef.id))
//                            }
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