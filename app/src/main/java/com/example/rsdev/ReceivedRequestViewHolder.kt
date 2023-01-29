package com.example.rsdev
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.rsdev.data.Friend
import com.example.rsdev.data.ReceivedRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.DocumentReference

class ReceivedRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    val friend_request_image = itemView.findViewById<ImageView>(R.id.friend_request_image)
    val friend_request_name = itemView.findViewById<TextView>(R.id.friend_request_name)
    val accept_friend_request = itemView.findViewById<Button>(R.id.accept_friend_request)

    // connexion à la bdd firestore
    val db = Firebase.firestore
    // accaccès à la collection "users"
    val users = db.collection("users")
    // accès à la collection "friend_requests"
    var friend_requests = db.collection("friend_requests")
    // l'Id de utilisateur connecté (Firebase Authentication)
    val id_user_connected = FirebaseAuth.getInstance().currentUser?.uid.toString()
//    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_friends)

    fun bind(request: ReceivedRequest) {
//        fun bind(request: ReceivedRequest) {
//        val adapter = FriendsAdapter(FriendsList)
//        recyclerView.adapter = adapter

        // supprimer la personne de lar liste des amis
        accept_friend_request.setOnClickListener {

            val senderId = itemView.getTag() as String

            // valider la demande d'ami reçue
                val friend_request = db.collection("friend_requests").whereEqualTo("id_receiver", id_user_connected)
                    .whereEqualTo("id_sender", senderId)
                // Set the "validated" field to true
            friend_request.get().addOnSuccessListener { oneRequest ->
                for (request in oneRequest) {
                    request.reference.update("validated", true)
                        .addOnSuccessListener {
                            Functions.addFriend(senderId, id_user_connected)
                            Toast.makeText(itemView.context, "Demande d'ami validée", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(itemView.context, "erreur!", Toast.LENGTH_LONG).show()
                        }
                }
            }
        }





        friend_request_image.setImageResource(R.drawable.profil_homme)
        friend_request_name.text = request.senderFirstName.plus(" ").plus(request.senderLastName)
//        friend_request_name.text = arrayNameLastname[0].toString().plus(" ").plus(arrayNameLastname[1].toString())

//        delete_friend_bt.setOnClickListener {
////            Toast.makeText(this@FriendsViewHolder, friendRef.id, Toast.LENGTH_SHORT).show()
//            users.document(id_user_connected).update("friends", FieldValue.arrayRemove(friendRef.id))
//        }

    }





}
