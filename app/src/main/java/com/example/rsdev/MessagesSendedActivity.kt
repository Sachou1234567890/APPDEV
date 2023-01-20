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

class MessagesSendedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_sended)

        // certains élements graphiques sur cette activity
        var mainLayout_messages_s = findViewById<ConstraintLayout>(R.id.mainLayout_messages_s)
        // connexion à la bdd firestore
        val db = Firebase.firestore
        // accès à la collection "users"
        val users = db.collection("users")
        // accès à la collection "messages"
        val messages = db.collection("messages")
        // ID de l'utilisateur connecté (FirebaseAuth)
        val id_user_connected = FirebaseAuth.getInstance().currentUser?.uid

        val messages_envoyes = messages.whereEqualTo("from_user_id", id_user_connected)

        var incrementeur = 400 // on va l'utiliser pour éspacer verticalement les differentes <LinearLayout> qu'on va créér

        messages_envoyes.get().addOnSuccessListener { messages ->
            for (message in messages) {
                val receiver = db.collection("users").document(message.get("to_user_id").toString())
                receiver.get().addOnSuccessListener { receiver ->
                    if (receiver != null) {
                        // données du receveur du message
                        val firstname = receiver.get("firstname").toString()
                        val lastname = receiver.get("lastname").toString()
                        val message_sended_text = message.get("message").toString()
                        val timestamp = message.get("timestamp") as Timestamp
                        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
                        val sdf = SimpleDateFormat("dd/MM/yyyy")
                        val netDate = java.util.Date(milliseconds)
                        val date_envoi = sdf.format(netDate).toString()
//                        val date_envoi = timestamp.toDate()
//                        val dateEnvoiString = date_envoi?.toString()


                        // créer un <LinearLayout> (message_receiver_layout)
                        val message_receiver_layout = LinearLayout(this)
                        message_receiver_layout.id = LinearLayout.generateViewId()
                        message_receiver_layout.layoutParams = LayoutParams(Functions.dpToPx(400,this), Functions.dpToPx(60,this))

                        // créer deux éspaces de séparation entre les éléments (ressource déjà disponible dans le dossier "drawable")
                        val divider_image = ImageView(this)
                        divider_image.setImageResource(R.drawable.divider_requests)
                        val divider_image2 = ImageView(this)
                        divider_image2.setImageResource(R.drawable.divider_requests)

                        // créer le <TextView> (name_receiver) représentant le nom complet du destinataire de la demande
                        val name_receiver = TextView(this)
                        name_receiver.id = TextView.generateViewId()
                        // les propriétés de name_receiver
                        name_receiver.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        name_receiver.setText("envoyé le ".plus(date_envoi).plus(" à : ").plus(firstname).plus(" ").plus(lastname))

                        /*créer un <TextView> (message_text) dans lequel on va mettre le contenu du message envoyé*/
                        val message_text = TextView(this)
                        message_text.id = TextView.generateViewId()
                        // les propriétés de name_receiver
                        message_text.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        message_text.setText(message_sended_text)

                        // ajouter les différents vues dans leurs éléments parents
                        message_receiver_layout.addView(name_receiver)
                        message_receiver_layout.addView(divider_image2)
                        mainLayout_messages_s.addView(message_receiver_layout)
                        mainLayout_messages_s.addView(message_text)

                        //établir des contraintes pour message_receiver_layout (bien le placer dans l'élément parent)
                        val set1 = ConstraintSet()
                        set1.clone(mainLayout_messages_s)
                        set1.connect(
                            message_receiver_layout.id, ConstraintSet.TOP,
                            ConstraintSet.PARENT_ID, ConstraintSet.TOP, incrementeur
                        )
                        set1.connect(
                            message_receiver_layout.id, ConstraintSet.START,
                            ConstraintSet.PARENT_ID, ConstraintSet.START, 50
                        )
                        set1.applyTo(mainLayout_messages_s)

                        //établir des contraintes pour message_text (bien le placer dans l'élément parent)
                        val set2 = ConstraintSet()
                        set2.clone(mainLayout_messages_s)
                        set2.connect(
                            message_text.id, ConstraintSet.TOP,
                            ConstraintSet.PARENT_ID, ConstraintSet.TOP, incrementeur - 100
                        )
                        set2.connect(
                            message_text.id, ConstraintSet.START,
                            ConstraintSet.PARENT_ID, ConstraintSet.START, 50
                        )
                        set2.applyTo(mainLayout_messages_s)

                        incrementeur = incrementeur + 250
                    }
                }

            }

        }
    }

}