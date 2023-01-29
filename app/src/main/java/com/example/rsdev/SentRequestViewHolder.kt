package com.example.rsdev
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rsdev.data.SentRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SentRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val db = Firebase.firestore
    val users = db.collection("users")
    var friend_requests = db.collection("friend_requests")
    val id_user_connected = FirebaseAuth.getInstance().currentUser?.uid.toString()
//    val recyclerView = itemView.findViewById<RecyclerView>(R.id.recyclerView_friends)

    val friend_request_image = itemView.findViewById<ImageView>(R.id.friend_request_image)
    val friend_request_name = itemView.findViewById<TextView>(R.id.friend_request_name)
    val cancel_friend_request = itemView.findViewById<TextView>(R.id.cancel_friend_request)
    val recyclerView = itemView.findViewById<RecyclerView>(R.id.recyclerView_requests)

    fun bind(request: SentRequest) {

        friend_request_image.setImageResource(R.drawable.profil_homme)
        friend_request_name.text = request.receiverFirstName.plus(" ").plus(request.receiverLastName)

        cancel_friend_request.setOnClickListener {

            val receiverId = itemView.getTag() as String
            val friend_request = db.collection("friend_requests").whereEqualTo("id_receiver", receiverId)
                .whereEqualTo("id_sender", id_user_connected)
            friend_request.get().addOnSuccessListener { sentRequest ->
                for (request in sentRequest) {
                    request.reference.delete()
                        .addOnSuccessListener {
                            Toast.makeText(itemView.context, "Demande d'ami annulée", Toast.LENGTH_LONG).show()
                            if(itemView.context!=null) {
                                val requestsSentActivity = itemView.context as RequestsSentActivity
                                val intent = Intent(requestsSentActivity, RequestsSentActivity::class.java)
                                itemView.context.startActivity(intent)
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(itemView.context, "erreur!", Toast.LENGTH_LONG).show()
                        }
                }
            }
        }
    }




}
