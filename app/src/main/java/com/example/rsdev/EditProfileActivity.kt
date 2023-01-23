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
        val reset = findViewById<Button>(R.id.reset)
        val validate = findViewById<Button>(R.id.validate)

        // connexion à la bdd firestore
        val db = Firebase.firestore
        // l'ID de l'utilisateur connecté (Firebase Authentication)
        val id_user_connected = FirebaseAuth.getInstance().currentUser?.uid.toString()
        // accès à l'utilisateur connecté
        val userRef = db.collection("users").document(id_user_connected)
        userRef.get().addOnSuccessListener { currentUser ->

            // s'il n'y a pas de date de naissance
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
                selected_dob.setText("date de naissance : ".plus(dob))
                val saved_firstname = currentUser.get("firstname").toString()
                val saved_lastname = currentUser.get("lastname").toString()
                val saved_dob = dob
                firstname.setText(saved_firstname)
                lastname.setText(saved_lastname)

                selected_dob.setOnClickListener {

                    val builder = MaterialDatePicker.Builder.datePicker()
                    val now = System.currentTimeMillis()
                    builder.setSelection(now)
                    val picker = builder.build()
                    picker.show(supportFragmentManager, "date_picker_tag")

                    picker.addOnPositiveButtonClickListener {
//                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        val selectedDate = picker.selection
                        val formattedDate = dateFormat.format(Date(selectedDate ?: 0))

//                btnDatePicker.visibility = View.GONE
//                btnDatePicker.text = formattedDate
//                btnDatePicker.setOnClickListener(null)
//                selected_date.visibility = View.VISIBLE
                        selected_dob.setText("date sélectionnée : ".plus(formattedDate))
                    }
                }

                reset.setOnClickListener {
                    firstname.setText(saved_firstname)
                    lastname.setText(saved_lastname)
                    selected_dob.setText("date de naissance : ".plus(saved_dob))
                }



            }

        }








//        val btnDatePicker = findViewById<Button>(R.id.show_date_picker)
//        btnDatePicker.setOnClickListener {
//
//            val builder = MaterialDatePicker.Builder.dateRangePicker()
//            val now = System.currentTimeMillis()
//            builder.setSelection(Pair(now, now))
//            val picker = builder.build()
//            picker.show(supportFragmentManager, "date_picker_tag")
//        }





    }
}