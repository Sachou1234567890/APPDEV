package com.example.rsdev

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true

    private lateinit var signUpRequest: BeginSignInRequest

    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.actionBar?.hide()
        setContentView(R.layout.activity_login)

        //Recupération des variables sur la vue
        val btn_login = findViewById(R.id.btn_login) as Button
        val btn_register = findViewById(R.id.btn_register) as Button
        val btn_google = findViewById(R.id.google_login) as Button
        val email_login_input = findViewById(R.id.firstname_user) as EditText
        val password_login_input = findViewById(R.id.password_login) as EditText

        // Action au click de connexion
        btn_login.setOnClickListener {
            val email: String = email_login_input.text.toString()
            val password: String = password_login_input.text.toString()

            //On vérifie que les champs ne sont pas vides
            if(email.toString().trim().isNotEmpty() && password.toString().isNotEmpty()) {
                //login du user avec son MDP
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                        { task ->
                            if(task.isSuccessful) {
                                Toast.makeText(applicationContext, "Connexion réussie", Toast.LENGTH_SHORT).show()
                                email_login_input.getText().clear()
                                password_login_input.getText().clear()
                                val FeedActivity = Intent(this, FeedActivity::class.java)
                                FeedActivity.putExtra("keyIdentifier", "value")
                                startActivity(FeedActivity)
                            } else {
                                Toast.makeText(applicationContext, "Identifants incorrects", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
            }
            else{
                Toast.makeText(applicationContext, "Merci d'entrer des identifiants valides", Toast.LENGTH_SHORT).show()
            }
        }

        btn_google.setOnClickListener{

            oneTapClient = Identity.getSignInClient(this)
            signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.google_client_id_on_tap_sign_ing))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build()
            oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(this) { e ->
                    // No Google Accounts found. Just continue presenting the signed-out UI.
                    Log.d(TAG, e.localizedMessage)
                }

        }

        btn_register.setOnClickListener {
            val RegisterActivity = Intent(this, RegisterActivity::class.java)
            startActivity(RegisterActivity)
        }



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            val data = hashMapOf(
                                "user_id" to idToken,
                            )

                            db.collection("users")
                                .add(data)
                                .addOnSuccessListener { documentReference ->
                                    Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding document", e)
                                }
                            Log.d(TAG, "Got ID token.")
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d(TAG, "No ID token!")
                        }
                    }
                } catch (e: ApiException) {

                }
            }
        }
    }






}
