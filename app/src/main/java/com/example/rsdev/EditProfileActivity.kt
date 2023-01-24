package com.example.rsdev

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.appcompat.widget.LinearLayoutCompat

import java.text.SimpleDateFormat
import java.util.*

//import androidx.core.util.Range

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // éléments présents dans cette vue
        val selected_dob = findViewById<TextInputEditText>(R.id.selected_dob)
        val firstname = findViewById<TextInputEditText>(R.id.firstname)
        val lastname = findViewById<TextInputEditText>(R.id.lastname)
        val username = findViewById<TextInputEditText>(R.id.username)
        val e_mail = findViewById<TextInputEditText>(R.id.e_mail)
        val reset = findViewById<Button>(R.id.reset)
        val validate = findViewById<Button>(R.id.validate)

        // connexion à la bdd firestore
        val db = Firebase.firestore
        // l'ID de l'utilisateur connecté (Firebase Authentication)
        val id_user_connected = FirebaseAuth.getInstance().currentUser?.uid.toString()
        // accès à l'utilisateur connecté
        val userRef = db.collection("users").document(id_user_connected)
        userRef.get().addOnSuccessListener { currentUser ->

            // s'il n'y a pas de date de naissance dans le document Firestore
            if(currentUser.get("dob") == null || currentUser.get("dob").toString().isEmpty() ) {
                selected_dob.setText("aucune date de naissance renseignée")
                userRef
                    .update("dob", "")
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener { }
            }

            else {
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val dob = currentUser.get("dob") as String

                val saved_firstname = if (currentUser.get("firstname").toString() != null) currentUser.get("firstname").toString() else ""
                val saved_lastname = if (currentUser.get("lastname").toString() != null) currentUser.get("lastname").toString() else ""
                val saved_dob = if (currentUser.get("dob").toString() != null) currentUser.get("dob").toString() else ""
                val saved_username = if (currentUser.get("username").toString() != null) currentUser.get("username").toString() else ""
                val saved_email = if (currentUser.get("email").toString() != null) currentUser.get("email").toString() else ""

                firstname.setText(saved_firstname)
                lastname.setText(saved_lastname)
                username.setText(saved_username)
                e_mail.setText(saved_email)
                selected_dob.setText(dob)

                selected_dob.setOnClickListener {

                    val builder = MaterialDatePicker.Builder.datePicker()
                    val now = System.currentTimeMillis()
                    builder.setSelection(now)
                    val picker = builder.build()
                    picker.show(supportFragmentManager, "date_picker_tag")

                    picker.addOnPositiveButtonClickListener {
                        val selectedDate = picker.selection
                        val formattedDate = dateFormat.format(Date(selectedDate ?: 0))
                        selected_dob.setText(formattedDate)
                    }
                }

                reset.setOnClickListener {

                    firstname.setText(saved_firstname)
                    lastname.setText(saved_lastname)
                    username.setText(saved_username)
                    e_mail.setText(saved_email)
                    selected_dob.setText(saved_dob)
                }

                validate.setOnClickListener {

                    userRef.update(mapOf(
                    "dob" to selected_dob.text.toString().trim(),
                    "email" to e_mail.text.toString().trim(),
                    "firstname" to firstname.text.toString().trim(),
                    "lastname" to lastname.text.toString().trim(),
                    "username" to username.text.toString().trim()

                    )) .addOnSuccessListener {
                        Toast.makeText(this@EditProfileActivity, "données modifiées avec succés", Toast.LENGTH_SHORT).show()
                        val EditProfileActivity = Intent(this, EditProfileActivity::class.java)
                        startActivity(EditProfileActivity)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@EditProfileActivity, "Erreur lors de la modification des données !", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}