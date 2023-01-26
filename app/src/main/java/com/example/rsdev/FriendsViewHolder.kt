package com.example.rsdev
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.rsdev.data.Friend
import com.example.rsdev.data.ReceivedRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val friend_image = itemView.findViewById<ImageView>(R.id.friend_image)
    val friend_name = itemView.findViewById<TextView>(R.id.friend_name)
    val delete_friend_bt = itemView.findViewById<TextView>(R.id.delete_friend_bt)

    // connexion à la bdd firestore
    val db = Firebase.firestore
    // accaccès à la collection "users"
    val users = db.collection("users")
    // accès à la collection "friend_requests"
    var friend_requests = db.collection("friend_requests")
    // l'Id de utilisateur connecté (Firebase Authentication)
    val id_user_connected = FirebaseAuth.getInstance().currentUser?.uid.toString()
//    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_friends)

    fun bind(friend: Friend) {
//        fun bind(request: ReceivedRequest) {
//        val adapter = FriendsAdapter(FriendsList)
//        recyclerView.adapter = adapter

        // supprimer la personne de la liste des amis
        delete_friend_bt.setOnClickListener {
            val friend_id = itemView.getTag() as String
            users.document(id_user_connected).update("friends", FieldValue.arrayRemove(friend_id))
                .addOnSuccessListener {
                    // Notify the adapter that the data has changed
                    (itemView.parent as RecyclerView).adapter?.notifyDataSetChanged()

//                    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_requests)
//                    val adapter = MyAdapter(myData)
//                    recyclerView.adapter = adapter

//                    val FriendsActivity = Intent(this, FriendsActivity::class.java)
//                    startActivity(FriendsActivity)
//                    (recyclerView.adapter as FriendsAdapter).notifyDataSetChanged()
                }
        }

        friend_image.setImageResource(R.drawable.profil_homme)
        friend_name.text = friend.friendFirstName.plus(" ").plus(friend.friendLastName)

//        delete_friend_bt.setOnClickListener {
////            Toast.makeText(this@FriendsViewHolder, friendRef.id, Toast.LENGTH_SHORT).show()
//            users.document(id_user_connected).update("friends", FieldValue.arrayRemove(friendRef.id))
//        }

    }





}
