package com.example.rsdev

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FieldValue



class FriendsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        // certains élements graphiques sur cette activity
        val mainLayout_friends = findViewById<ConstraintLayout>(R.id.mainLayout_friends)

        // connexion à la bdd firestore
        val db = Firebase.firestore
        // accaccès à la collection "users"
        val users = db.collection("users")
        // accès à la collection "friend_requests"
        var friend_requests = db.collection("friend_requests")
        // l'utilisateur connecté et son email (Firebase Authentication)
        val user_connected = FirebaseAuth.getInstance().currentUser
        val email_connected = user_connected?.email

        // l'utilisateur connecté (BDD Firestore)
        val user_conn = users.whereEqualTo("email", email_connected)

        user_conn.get().addOnSuccessListener { oneUser ->
            for (user in oneUser) {
                // tableau des IDs des amis de l'utilisateur connecté'
                val friendsArray = user.toObject<User>().friends // on a créé une classe "User" dans le fichier "User.kt"
                if (friendsArray != null) {
                    var incrementeur = 500 // indique la position verticale de la premiere ligne d'amis
                                           // (cette valeur s'incrémente de 120 à la fin de la boucle)

                    // on parcourt les IDs des amis de l'utilisateur connecté, et à partir de chaque ID on extrait les données de l'ami
                    for (friend_id in friendsArray) {
                        val friendRef = db.collection("users").document(friend_id)
                        friendRef.get().addOnSuccessListener { friend ->
                            val firstname_friend = friend.get("firstname").toString()
                            val lastname_friend = friend.get("lastname").toString()
                            val username_friend = friend.get("username").toString()

                            /* créer le <LinearLayout> (friend_layout) dans lequel on va mettre un <ImageView> et
                            un <TextView> (image et nom complet de l'ami )*/
                            val friend_layout = LinearLayout(this)
                                // les propriétés de friend_layout
                                friend_layout.id = LinearLayout.generateViewId()
                                friend_layout.layoutParams = ViewGroup.LayoutParams(Functions.dpToPx(270,this), Functions.dpToPx(70,this)) // (width,height)

                            // créer deux éspaces de séparation entre les éléments (ressource déjà disponible dans le dossier "drawable")
                            val divider_image = ImageView(this)
                            divider_image.setImageResource(R.drawable.divider_requests)
                            val divider_image2 = ImageView(this)
                            divider_image2.setImageResource(R.drawable.divider_requests)

                            // créer le <ImageView> (image_friend) représentant l'image de l'ami
                            val image_friend = ImageView(this)
                            // les propriétés de image_friend
                            image_friend.id = ImageView.generateViewId()
                            image_friend.setImageResource(R.drawable.profil_homme)
                            // friend_layout.layoutParams = LayoutParams(150, 150)
                            image_friend.layoutParams = LinearLayout.LayoutParams(Functions.dpToPx(40,this), Functions.dpToPx(40,this))

                            // créer le <TextView> (name_friend) représentant le nom complet de l'ami
                            val name_friend = TextView(this)
                            name_friend.id = TextView.generateViewId()
                                // les propriétés de name_friend
                                name_friend.setText(firstname_friend.plus(" ").plus(lastname_friend))
                                name_friend.textSize = 22.0f

                            // créer le <Button> (add_friend) confirmant la validation de la demande d'ami
                            val delete_friend = Button(this)
                            delete_friend.id = TextView.generateViewId()
                            // les propriétés de delete_friend
                            delete_friend.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            delete_friend.text = "supprimer"

                            // ajouter les différents éléments créés dans les éléments parents
                            friend_layout.addView(image_friend)
                            friend_layout.addView(divider_image)
                            friend_layout.addView(name_friend)
                            friend_layout.addView(divider_image2)
                            friend_layout.addView(delete_friend)
                            mainLayout_friends.addView(friend_layout)

                            //établir des contraintes pour friend_layout (bien le placer dans l'élément parent (id=mainLayout_friends) )
                            val set1 = ConstraintSet()
                            set1.clone(mainLayout_friends)
                            set1.connect(
                                friend_layout.id, ConstraintSet.TOP,
                                ConstraintSet.PARENT_ID, ConstraintSet.TOP, incrementeur
                            )
                            set1.connect(
                                friend_layout.id, ConstraintSet.START,
                                ConstraintSet.PARENT_ID, ConstraintSet.START, 50
                            )
                            set1.applyTo(mainLayout_friends)

                            incrementeur = incrementeur + 120

                            // supprimer la personne de la liste des amis
                            delete_friend.setOnClickListener {
                                Toast.makeText(this, friendRef.id, Toast.LENGTH_SHORT).show()
                                users.document(user.id).update("friends", FieldValue.arrayRemove(friendRef.id))
                            }
                        }
                    }
                }
            }
        }


    }

}