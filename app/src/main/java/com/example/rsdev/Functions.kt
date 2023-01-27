package com.example.rsdev

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FieldValue

class Functions {


    companion object {
        // génére un string aléatoire
        fun getRandomString(length: Int): String {
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            return (1..length)
                .map { allowedChars.random() }
                .joinToString("")
        }

        fun dpToPx(dp: Int, context: Context): Int {
            val density: Float = context.getResources().getDisplayMetrics().density
            return Math.round(dp.toFloat() * density)
        }

        // ajoute une demande d'ami à la collection "friendRequest"
        fun addFriendRequest(id_user_s: String, id_user_r: String) {
            val db = Firebase.firestore
//            val user_sender = db.collection("users")
//                .document(id_user_s) // utilisateur connecté (celui qui envoie la demande d'ami)
//            val user_receiver =
//                db.collection("users").document(id_user_r) // utilisateur recevant la demande d'ami
            val friend_request = hashMapOf(
                "id_sender" to id_user_s, // id du document de l'ami auquel on a envoyé la demande d'ami
                "id_receiver" to id_user_r, // id du document de l'ami auquel on a envoyé la demande d'ami ,
                "validated" to false // la demande d'ami n'a pas encore été validé
            )
            db.collection("friend_requests").document(id_user_s.plus(id_user_r))
                .set(friend_request)
        }

        fun addFriend(id_friend_sender: String, id_friend_receiver: String) {
            val db = Firebase.firestore
            val users = db.collection("users")
            val friend_sender = users.document(id_friend_sender) // l'ami qui a envoyé la demande d'ami
            val friend_receiver = users.document(id_friend_receiver) // l'ami qui a reçu la demande d'ami
            // Atomically add a new region to the "regions" array field.
            friend_sender.update("friends", FieldValue.arrayUnion(id_friend_receiver))
            friend_receiver.update("friends", FieldValue.arrayUnion(id_friend_sender))
        }


//        fun addMessage(id_friend_sender: String, id_friend_receiver: String) {
//            ...............
//        }


    }





}