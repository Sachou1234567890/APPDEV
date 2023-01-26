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
class FriendsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        val recyclerView_friends = findViewById<RecyclerView>(R.id.recyclerView_friends)
        val swipe_refresh_layout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)

        val delete_friend_bt = findViewById<Button>(R.id.delete_friend_bt)


        // connexion à la bdd firestore
        val db = Firebase.firestore
        // accaccès à la collection "users"
        val users = db.collection("users")
        // accès à la collection "friend_requests"
        var friend_requests = db.collection("friend_requests")
        // l'Id de utilisateur connecté (Firebase Authentication)
        val id_user_connected = FirebaseAuth.getInstance().currentUser?.uid.toString()

        // l'utilisateur connecté (BDD Firestore)
        val user_conn = db.collection("users").document(id_user_connected)
        user_conn.get().addOnSuccessListener { currentUser ->
                // tableau des IDs des amis de l'utilisateur connecté'
                val friendsArray = currentUser.toObject<User>()?.friends // on a créé une classe "User" dans le fichier "User.kt"
                if (friendsArray != null) {

                    val FriendsList = ArrayList<Friend>()
                    // on parcourt les IDs des amis de l'utilisateur connecté, et à partir de chaque ID on extrait les données de l'ami
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