package com.example.rsdev

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rsdev.databinding.MessageMyBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.widget.EditText


class MyMessage : AppCompatActivity()  {

    private lateinit var binding: MessageMyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.message_my)

        // inflate the header fragment
        binding = MessageMyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setSubtitle("Envoyer un Message");
        binding.toolbar.setSubtitleTextAppearance(this, R.style.ToolbarSubtitleAppearance)
        binding.toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleAppearance)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // inflate the footer fragment
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val footerFragment = FooterFragment()
        fragmentTransaction.add(R.id.footer_container, footerFragment)
        fragmentTransaction.commit()

        // éléments graphiques sur la vue
        val firstname = findViewById<EditText>(R.id.firstname)
        val lastname = findViewById<EditText>(R.id.lastname)
        val message = findViewById<EditText>(R.id.message)
        val send_message = findViewById<MaterialButton>(R.id.send_message)


        // differents données Firebase
        val db = Firebase.firestore
        val firestore: FirebaseFirestore by lazy { Firebase.firestore }
        val messagesRef = db.collection("messages")
        // accès à la collection <users>
        val users = db.collection("users")
        // ID de l'utilisateur connecté (FirebaseAuth)
        val id_user_connected = FirebaseAuth.getInstance().currentUser?.uid


        send_message.setOnClickListener {

            val firstname_text = firstname.text?.toString()?.lowercase() ?: ""
            val lastname_text = lastname.text?.toString()?.lowercase() ?: ""
            val message_text = message.text?.toString()?.lowercase() ?: ""



            // destinataire du message
            val friend_query = users.whereEqualTo("firstname", firstname_text).whereEqualTo("lastname", lastname_text).limit(1)

            friend_query.get().addOnCompleteListener { friend ->
                if (friend.result.isEmpty() || friend.result == null) {
                    Toast.makeText(this,"cet utilisateur n'existe pas!",Toast.LENGTH_LONG).show()
                }
                else {
                    val to_user_id = friend.result.documents[0].id
                    val from_user_id = id_user_connected

                    if(from_user_id == to_user_id || (from_user_id == to_user_id && message_text.isEmpty()) ) {
                        Toast.makeText(this,"interdit d'envoyer un message à nous meme!",Toast.LENGTH_LONG).show()
                    }
                    else if(message_text.isEmpty() ) {
                        Toast.makeText(this,"le message est vide !",Toast.LENGTH_LONG).show()
                    }
                    else {

                        val messagedata = hashMapOf(
                            "from_user_id" to id_user_connected,
                            "to_user_id" to to_user_id,
                            "message" to message_text,
                            "timestamp" to FieldValue.serverTimestamp()
                        )
                        messagesRef.add(messagedata)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Message envoyé avec succés!", Toast.LENGTH_SHORT).show()
                                val MessagesSentActivity = Intent(this, MessagesSentActivity::class.java)
                                startActivity(MessagesSentActivity)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to send message!", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
        }
    }






}
