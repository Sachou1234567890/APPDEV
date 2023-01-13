package com.example.rsdev

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
import android.view.Gravity
import kotlin.random.Random

class FeedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.actionBar?.hide()
        setContentView(R.layout.activity_feed)

// certains élements graphiques sur cette activity
        val mainLayout = findViewById<ConstraintLayout>(R.id.mainLayout)
        val bt_logout = findViewById<Button>(R.id.bt_logout)
        val bt_form_addFriend = findViewById<ImageButton>(R.id.bt_form_addFriend)
        val img_profile = findViewById<ImageView>(R.id.img_profile)

// connexion à la bdd firestore
        val db = Firebase.firestore
// accès à la collection <users>
        val users = db.collection("users")

// l'utilisateur connecté et son email (FirebaseAuth)
        val user_connected = FirebaseAuth.getInstance().currentUser // l'utilisateur connecté
        val email_connected = user_connected?.email // son email

// créer le formulaire d'ajout d'un ami
        bt_form_addFriend.setOnClickListener {

            // créer un <LinearLayout> (myLinearLayout) dans lequel on va mettre deux <EditText> (nom et prénom de l'ami à ajouter)
            val myLinearLayout = LinearLayout(this)
            myLinearLayout.id = LinearLayout.generateViewId()
            myLinearLayout.gravity = Gravity.CENTER // centrer les deux <EditText>
            // créer un <EditText> (prenomEditText) pour saisir le prénom de l'ami que l'on veut ajouter
            val prenomEditText = EditText(this)
            prenomEditText.id = EditText.generateViewId()
                // les propriétés de prenomEditText
                prenomEditText.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                prenomEditText.setHint("Prénom de l'ami")
            // créer un autre <EditText> (nomEditText) pour saisir le nom de l'ami que l'on veut ajouter
            val nomEditText = EditText(this)
            nomEditText.id = EditText.generateViewId()
                // les propriétés de nomEditText
                nomEditText.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                nomEditText.setHint("Nom de l'ami")
            // mettre les 2 <EditText> crées dans myLinearLayout
            myLinearLayout.addView(prenomEditText)
            myLinearLayout.addView(nomEditText)
            // mettre myLinearLayout dans l'élément parent (id=mainLayout)
            mainLayout.addView(myLinearLayout)
            // établir des contraintes pour myLinearLayout (bien le placer dans l'élément parent (id=mainLayout) )
            val set1 = ConstraintSet()
            set1.clone(mainLayout)
            // centrer myLinearLayout horizontalement dans l'élément parent
            set1.connect(
                myLinearLayout.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START
            )
            set1.connect(
                myLinearLayout.id,
                ConstraintSet.END,
                myLinearLayout.id,
                ConstraintSet.START
            )
            set1.setHorizontalChainStyle(myLinearLayout.id, ConstraintSet.CHAIN_SPREAD)
            set1.connect(
                myLinearLayout.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )
            set1.connect(
                myLinearLayout.id, ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP, 980
            )
            //appliquer les contraintes
            set1.applyTo(mainLayout)
            // créer un <Button> (bt_addFriend_confirm) pour valider l'ajout
            val bt_addFriend_confirm = Button(this)
            bt_addFriend_confirm.id = Button.generateViewId()
            // les propriétés de <Button>
            bt_addFriend_confirm.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            bt_addFriend_confirm.setText("VALIDER")
            // Ajouter bt_addFriend_confirm à l'élément parent (id=mainLayout)
            mainLayout.addView(bt_addFriend_confirm)
            //établir des contraintes pour bt_addFriend_confirm (bien le placer dans l'élément parent (id=mainLayout) )
            val set2 = ConstraintSet()
            set2.clone(mainLayout)
            // centrer bt_addFriend_confirm horizontalement dans l'élément parent
            set2.connect(
                bt_addFriend_confirm.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START
            )
            set2.connect(
                bt_addFriend_confirm.id,
                ConstraintSet.END,
                bt_addFriend_confirm.id,
                ConstraintSet.START
            )
            set2.setHorizontalChainStyle(bt_addFriend_confirm.id, ConstraintSet.CHAIN_SPREAD)
            set2.connect(
                bt_addFriend_confirm.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )
            set2.connect(
                bt_addFriend_confirm.id, ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP, 1200
            )
            //appliquer les contraintes
            set2.applyTo(mainLayout)

            // valider l'ajout d'un nouvel ami
            bt_addFriend_confirm.setOnClickListener {
                val user_conn = users.whereEqualTo("email", email_connected) // l'utilisateur connecté
                user_conn.get().addOnSuccessListener { users2 ->
                    for (user in users2) {
                        // On récupere les contenus des 2 <EditText> créés
                        val firstname = prenomEditText.text.toString().trim()
                        val lastname = nomEditText.text.toString().trim()
                        // vérifier si l'ami recherché existe
                        val friend = users.whereEqualTo("firstname", firstname).whereEqualTo("lastname", lastname)
                        friend.get().addOnCompleteListener { friend ->
                                if (friend.result.isEmpty() || friend.result == null) {
                                    Toast.makeText(this@FeedActivity,"cet utilisateur n'existe pas!",Toast.LENGTH_LONG).show()
                                }
                                else {
                                    val id_r = friend.result.documents[0].id // le _r signifie "receiver" (destinataire)
                                    val firsname_r = friend.result.documents[0].get("firstname").toString()
                                    val lastname_r = friend.result.documents[0].get("lastname").toString()
                                    val id_s = user.id // le _s signifie "sender" (expediteur)
                                    val firsname_s = user.get("firstname").toString()
                                    val lastname_s = user.get("lastname").toString()

                                    if((firsname_r == user.get("firstname") && lastname_r == user.get("lastname"))) {
                                        Toast.makeText(this@FeedActivity,"interdit d'envoyer une demande à nous meme!",Toast.LENGTH_LONG).show()
                                    }
                                    else {
                                        Functions.addFriendRequest(id_s,id_r)
                                        Toast.makeText(this@FeedActivity,"demande d'ami envoyée",Toast.LENGTH_LONG).show()
                                    }
                                }
                        }
                        break
                    }
                }
            }
            // FIN valider l'ajout d'un nouvel ami
        }
// FIN créer formulaire d'ajout d'un ami


// aller vers ProfileActivity
        img_profile.setOnClickListener {
            val ProfileActivity = Intent(this, ProfileActivity::class.java)
            startActivity(ProfileActivity)
        }
// FIN aller vers ProfileActivity

// se déconnecter
        bt_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val LoginActivity = Intent(this, LoginActivity::class.java)
            startActivity(LoginActivity)
        }
// FIN se déconnecter

    }



















}

