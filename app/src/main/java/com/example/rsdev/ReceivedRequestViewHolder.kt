//package com.example.rsdev
//
//import androidx.recyclerview.widget.RecyclerView
//import android.view.View
//import android.widget.ImageView
//import android.widget.TextView
//import com.example.rsdev.data.ReceivedRequest
//
//
//class ReceivedRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//    val friend_request_image = itemView.findViewById<ImageView>(R.id.friend_request_image)
//    val friend_request_name = itemView.findViewById<TextView>(R.id.friend_request_name)
//    val accept_friend_request = itemView.findViewById<Button>(R.id.accept_friend_request)
//
//    // connexion à la bdd firestore
//    val db = Firebase.firestore
//
//    // ID de l'utilisateur connecté (FirebaseAuth)
//    val id_user_connected = FirebaseAuth.getInstance().currentUser?.uid.toString()
//
//    // accès à la collection "friend_requests"
//    val received_friend_requests =
//        db.collection("friend_requests").whereEqualTo("id_receiver", id_user_connected)
//            .whereEqualTo("validated", false)
//
//    received_friend_requests.get().addOnSuccessListener
//    {
//        querySnapshot ->
//        val requestsList = ArrayList<ReceivedRequest>()
//        for (document in querySnapshot.documents) {
//            val senderId = document.get("id_sender").toString()
//            // fetch sender data and create ReceivedRequest object
//            val users = db.collection("users")
//            users.document(senderId).get().addOnSuccessListener { sender ->
//                val senderFirstName = sender.getString("firstname")
//                val senderLastName = sender.getString("lastname")
//                val receivedRequest = ReceivedRequest(
//                    senderFirstName.toString(),
//                    senderLastName.toString()
//                )
//                requestsList.add(receivedRequest)
//                // update the recyclerview's adapter with the new list
//                recyclerView.adapter = ReceivedRequestAdapter(requestsList)
//            }
//            // valider la demande d'ami reçue
//            accept_friend_request.setOnClickListener {
//                val friend_request =
//                    db.collection("friend_requests").document(document.id)
//                // Set the "validated" field to true
//                friend_request.update("validated", true)
//                    .addOnSuccessListener {
//                        Functions.addFriend(senderId, id_user_connected)
//                        Toast.makeText(
//                            this@ReceivedRequestViewHolder,
//                            "Demande d'ami validée",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                    .addOnFailureListener {
//                        Toast.makeText(this@ReceivedRequestViewHolder, "erreur!", Toast.LENGTH_LONG)
//                            .show()
//                    }
//            }
//        }
//    }
//
//}