package com.example.rsdev

//import android.widget.LinearLayout.LayoutParams

import android.os.Bundle
import android.widget.*
import android.widget.LinearLayout.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class RequestsSendedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests_sended)

        // certains élements graphiques sur cette activity
        val mainLayout_requests_s = findViewById<ConstraintLayout>(R.id.mainLayout_requests_s)
        // connexion à la bdd firestore
        val db = Firebase.firestore
        // accès à la collection "users"
        val users = db.collection("users")
        // accès à la collection "friend_requests"
        val friend_requests = db.collection("friend_requests")
        // l'utilisateur connecté et son email (Firebase Authentication)
        val user_connected = FirebaseAuth.getInstance().currentUser
        val email_connected = user_connected?.email

        // l'utilisateur connecté (BDD Firestore)
        val user_conn = users.whereEqualTo("email", email_connected)
        user_conn.get().addOnSuccessListener { oneUser ->
            for (user in oneUser) {
                var id_user = user.id // id du document de l'utilisateur connecté
                val requests_sended = friend_requests.whereEqualTo("id_sender", id_user).whereEqualTo("validated", false)
                requests_sended.get().addOnSuccessListener { requests ->
                    /* on va parcourir la liste des demandes d'ami envoyées, et pour chacune on va créer dans la vue
                     l'image du destinataire et son nom complet*/
                    var incrementeur = 400 // on va l'utiliser pour éspacer verticalement les differentes <LinearLayout> qu'on va créér
                    for (request in requests) {
                        val request_id = request.id
                        val id_receiver = request.get("id_receiver").toString()
                        val receiver = db.collection("users").document(id_receiver)
                        receiver.get().addOnSuccessListener { receiver ->
                            if (receiver != null) {
                                // données du receveur de la demande d'ami
                                val firstname = receiver.get("firstname").toString()
                                val lastname = receiver.get("lastname").toString()

                                /*créer un <LinearLayout> (friend_receiver_layout) dans lequel on va mettre un <ImageView>,
                                un <TextView> (image et nom complet de la personne qui a reçu le demande)*/

                                // créer le <LinearLayout> (friend_receiver_layout)
                                val friend_receiver_layout = LinearLayout(this)
                                friend_receiver_layout.id = LinearLayout.generateViewId()
                                friend_receiver_layout.layoutParams = LayoutParams(Functions.dpToPx(400,this), Functions.dpToPx(60,this))

                                // créer deux éspaces de séparation entre les éléments (ressource déjà disponible dans le dossier "drawable")
                                val divider_image = ImageView(this)
                                divider_image.setImageResource(R.drawable.divider_requests)
                                val divider_image2 = ImageView(this)
                                divider_image2.setImageResource(R.drawable.divider_requests)

                                // créer le <ImageView> (image_receiver) représentant l'image du destinataire de la demande
                                val image_receiver = ImageView(this)
                                // les propriétés de image_receiver
                                image_receiver.id = ImageView.generateViewId()
                                image_receiver.setImageResource(R.drawable.profil_homme)
                                image_receiver.layoutParams = LinearLayout.LayoutParams(Functions.dpToPx(40,this), Functions.dpToPx(40,this))

                                // créer le <TextView> (name_receiver) représentant le nom complet du destinataire de la demande
                                val name_receiver = TextView(this)
                                name_receiver.id = TextView.generateViewId()
                                // les propriétés de name_receiver
                                name_receiver.layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                name_receiver.setText(firstname.plus(" ").plus(lastname))

                                // ajouter les différents vues dans leurs éléments parents
                                friend_receiver_layout.addView(image_receiver)
                                friend_receiver_layout.addView(divider_image)
                                friend_receiver_layout.addView(name_receiver)
                                friend_receiver_layout.addView(divider_image2)
                                mainLayout_requests_s.addView(friend_receiver_layout)

                                //établir des contraintes pour friend_receiver_layout (bien le placer dans l'élément parent (id=mainLayout_requests_s) )
                                val set1 = ConstraintSet()
                                set1.clone(mainLayout_requests_s)
                                set1.connect(
                                    friend_receiver_layout.id, ConstraintSet.TOP,
                                    ConstraintSet.PARENT_ID, ConstraintSet.TOP, incrementeur
                                )
                                set1.connect(
                                    friend_receiver_layout.id, ConstraintSet.START,
                                    ConstraintSet.PARENT_ID, ConstraintSet.START, 50
                                )
                                set1.applyTo(mainLayout_requests_s)

                                incrementeur = incrementeur + 120

                            }
                            else {
                                Toast.makeText(this, "cet utilisateur n'existe pas", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

            }



        }


    }
}