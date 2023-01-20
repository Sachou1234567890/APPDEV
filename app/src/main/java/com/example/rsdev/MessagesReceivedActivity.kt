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
import com.google.firebase.Timestamp
import com.google.type.Date
import java.text.SimpleDateFormat

class MessagesReceivedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_received)

        // certains élements graphiques sur cette activity
        var mainLayout_messages_r = findViewById<ConstraintLayout>(R.id.mainLayout_messages_r)
        // connexion à la bdd firestore
        val db = Firebase.firestore
        // accès à la collection "users"
        val users = db.collection("users")
        // accès à la collection "messages"
        val messages = db.collection("messages")
        // ID de l'utilisateur connecté (FirebaseAuth)
        val id_user_connected = FirebaseAuth.getInstance().currentUser?.uid

        val messages_recus = messages.whereEqualTo("to_user_id", id_user_connected)

        var incrementeur = 400 // on va l'utiliser pour éspacer verticalement les differentes <LinearLayout> qu'on va créér

        messages_recus.get().addOnSuccessListener { messages ->
            for (message in messages) {
                val sender = db.collection("users").document(message.get("from_user_id").toString())
                sender.get().addOnSuccessListener { sender ->
                    if (sender != null) {
                        // données du receveur du message
                        val firstname = sender.get("firstname").toString()
                        val lastname = sender.get("lastname").toString()
                        val message_received_text = message.get("message").toString()
                        val timestamp = message.get("timestamp") as Timestamp
                        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
                        val sdf = SimpleDateFormat("dd/MM/yyyy")
                        val netDate = java.util.Date(milliseconds)
                        val date_envoi = sdf.format(netDate).toString()

                        // créer un <LinearLayout> (message_sender_layout)
                        val message_sender_layout = LinearLayout(this)
                        message_sender_layout.id = LinearLayout.generateViewId()
                        message_sender_layout.layoutParams = LayoutParams(Functions.dpToPx(400,this), Functions.dpToPx(60,this))

                        // créer deux éspaces de séparation entre les éléments (ressource déjà disponible dans le dossier "drawable")
                        val divider_image = ImageView(this)
                        divider_image.setImageResource(R.drawable.divider_requests)
                        val divider_image2 = ImageView(this)
                        divider_image2.setImageResource(R.drawable.divider_requests)

                        // créer le <TextView> (name_sender) représentant le nom complet du destinataire de la demande
                        val name_sender = TextView(this)
                        name_sender.id = TextView.generateViewId()
                        // les propriétés de name_sender
                        name_sender.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        name_sender.setText("envoyé le ".plus(date_envoi).plus(" par : ").plus(firstname).plus(" ").plus(lastname))

                        /*créer un <TextView> (message_text) dans lequel on va mettre le contenu du message envoyé*/
                        val message_text = TextView(this)
                        message_text.id = TextView.generateViewId()
                        // les propriétés de name_sender
                        message_text.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        message_text.setText(message_received_text)

                        // ajouter les différents vues dans leurs éléments parents
                        message_sender_layout.addView(name_sender)
                        message_sender_layout.addView(divider_image2)
                        mainLayout_messages_r.addView(message_sender_layout)
                        mainLayout_messages_r.addView(message_text)

                        //établir des contraintes pour message_sender_layout (bien le placer dans l'élément parent)
                        val set1 = ConstraintSet()
                        set1.clone(mainLayout_messages_r)
                        set1.connect(
                            message_sender_layout.id, ConstraintSet.TOP,
                            ConstraintSet.PARENT_ID, ConstraintSet.TOP, incrementeur
                        )
                        set1.connect(
                            message_sender_layout.id, ConstraintSet.START,
                            ConstraintSet.PARENT_ID, ConstraintSet.START, 50
                        )
                        set1.applyTo(mainLayout_messages_r)

                        //établir des contraintes pour message_text (bien le placer dans l'élément parent)
                        val set2 = ConstraintSet()
                        set2.clone(mainLayout_messages_r)
                        set2.connect(
                            message_text.id, ConstraintSet.TOP,
                            ConstraintSet.PARENT_ID, ConstraintSet.TOP, incrementeur - 100
                        )
                        set2.connect(
                            message_text.id, ConstraintSet.START,
                            ConstraintSet.PARENT_ID, ConstraintSet.START, 50
                        )
                        set2.applyTo(mainLayout_messages_r)

                        incrementeur = incrementeur + 250
                    }
                }

            }

        }
    }

}