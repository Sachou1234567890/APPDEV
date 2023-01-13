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


class RequestsReceivedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests_received)

        // certains élements graphiques sur cette activity
        val mainLayout_requests_r = findViewById<ConstraintLayout>(R.id.mainLayout_requests_r)
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
                val requests_received = friend_requests.whereEqualTo("id_receiver", id_user)
                    .whereEqualTo("validated", false)
                requests_received.get().addOnSuccessListener { requests ->
                    /* on va parcourir la liste des demandes d'ami reçues, et pour chacune on va créer dans la vue
                     l'image de l'expediteur et son nom complet*/
                    var incrementeur = 400 // on va l'utiliser pour éspacer verticalement les differentes <LinearLayout> qu'on va créér
                    for (request in requests) {
                        val request_id = request.id
                        val id_sender = request.get("id_sender").toString()
                        val sender = db.collection("users").document(id_sender)
                        sender.get().addOnSuccessListener { sender ->
                            if (sender != null) {
                                // données de l'expediteur de la demande d'ami
                                val firstname = sender.get("firstname").toString()
                                val lastname = sender.get("lastname").toString()

                                /*créer un <LinearLayout> (friend_received_layout) dans lequel on va mettre un <ImageView>,
                                un <TextView> et un <Button> (image, nom complet de l'ami à ajouter et bouton de confirmation)*/

                                // créer le <LinearLayout>
                                // (friend_received_layout)
                                val friend_received_layout = LinearLayout(this)
                                friend_received_layout.id = LinearLayout.generateViewId()
                                friend_received_layout.layoutParams = LayoutParams(
                                    Functions.dpToPx(400, this),
                                    Functions.dpToPx(60, this)
                                )

                                // créer deux éspaces de séparation entre les éléments (ressource déjà disponible dans le dossier "drawable")
                                val divider_image = ImageView(this)
                                divider_image.setImageResource(R.drawable.divider_requests)
                                val divider_image2 = ImageView(this)
                                divider_image2.setImageResource(R.drawable.divider_requests)

                                // créer le <ImageView> (image_sender) représentant l'image de l'ami à ajouter
                                val image_sender = ImageView(this)
                                // les propriétés de image_sender
                                image_sender.id = ImageView.generateViewId()
                                image_sender.setImageResource(R.drawable.profil_homme)
                                image_sender.layoutParams = LinearLayout.LayoutParams(
                                    Functions.dpToPx(40, this),
                                    Functions.dpToPx(40, this)
                                )

                                // créer le <TextView> (name_sender) représentant le nom complet de l'ami à ajouter
                                val name_sender = TextView(this)
                                name_sender.id = TextView.generateViewId()
                                // les propriétés de name_sender
                                name_sender.layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                name_sender.setText(firstname.plus(" ").plus(lastname))

                                // créer le <Button> (add_friend) confirmant la validation de la demande d'ami
                                val add_friend = Button(this)
                                add_friend.id = TextView.generateViewId()
                                // les propriétés de add_friend
                                add_friend.layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                add_friend.text = "ACCEPTER"

                                // ajouter les différents vues dans leurs éléments parents
                                friend_received_layout.addView(image_sender)
                                friend_received_layout.addView(divider_image)
                                friend_received_layout.addView(name_sender)
                                friend_received_layout.addView(divider_image2)
                                friend_received_layout.addView(add_friend)
                                mainLayout_requests_r.addView(friend_received_layout)

                                //établir des contraintes pour friend_received_layout (bien le placer dans l'élément parent (id=mainLayout_requests_r) )
                                val set1 = ConstraintSet()
                                set1.clone(mainLayout_requests_r)
                                set1.connect(
                                    friend_received_layout.id, ConstraintSet.TOP,
                                    ConstraintSet.PARENT_ID, ConstraintSet.TOP, incrementeur
                                )
                                set1.connect(
                                    friend_received_layout.id, ConstraintSet.START,
                                    ConstraintSet.PARENT_ID, ConstraintSet.START, 50
                                )
                                set1.applyTo(mainLayout_requests_r)

                                incrementeur = incrementeur + 120

                                // valider la demande d'ami reçue
                                add_friend.setOnClickListener {
                                    val friend_request =
                                        db.collection("friend_requests").document(request_id)
                                    // Set the "validated" field to true
                                    friend_request.update("validated", true)
                                        .addOnSuccessListener {
                                            Functions.addFriend(id_sender, id_user)
                                            Toast.makeText(
                                                this,
                                                "Demande d'ami validée",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                this,
                                                "erreur!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    "cet utilisateur n'existe pas",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }

            }


        }


    }

}