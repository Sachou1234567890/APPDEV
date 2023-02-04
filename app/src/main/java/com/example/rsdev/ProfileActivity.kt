package com.example.rsdev

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.example.rsdev.data.PostModel
import com.example.rsdev.data.UserModel
import com.example.rsdev.databinding.ActivityEditProfileBinding
import com.example.rsdev.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.rsdev.databinding.ActivityFriendsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class ProfileActivity : AppCompatActivity() {

    val AUTHORITY: String = "com.example.rsdev.EditProfileActivity"

    private val pickImageFromGallery_Code = 100
    private val pickImageFromCamera_Code = 101
    var photoFile: File? = null
    var mImageBitmap: Bitmap? = null
    protected lateinit var auth: FirebaseAuth
//    lateinit var storage: FirebaseStorage
    protected lateinit var mFirestore: FirebaseFirestore
    private lateinit var managePermissions: ManagePermissions
    private val PermissionsRequestCode = 123
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    var permissionsList = listOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    )

    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // inflate the header fragment
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setSubtitle("Mon Profil");
        binding.toolbar.setSubtitleTextAppearance(this, R.style.ToolbarSubtitleAppearance)
        binding.toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleAppearance)

        binding.toolbar.setNavigationOnClickListener {
            val FeedActivity = Intent(this, FeedActivity::class.java)
            startActivity(FeedActivity)
        }
        // inflate the footer fragment
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val footerFragment = FooterFragment()
        fragmentTransaction.add(R.id.footer_container, footerFragment)
        fragmentTransaction.commit()
        // certains élements graphiques sur cette activity
        val received_requests = findViewById<TextView>(R.id.received_requests)
        val sended_requests = findViewById<TextView>(R.id.sended_requests)
        val nom_prenom_profil = findViewById<TextView>(R.id.nom_prenom_profil)
        val friends = findViewById<TextView>(R.id.friends)
        val sended_messages = findViewById<TextView>(R.id.sended_messages)
        val received_messages = findViewById<TextView>(R.id.received_messages)
        val profile = findViewById<ImageView>(R.id.profile)
        val edit_profile_photo = findViewById<TextView>(R.id.edit_profile_photo)
        val edit_data = findViewById<TextView>(R.id.edit_data)

        // connexion à la bdd firestore
        val db = Firebase.firestore
        // accès à la collection "users"
        val users = db.collection("users")
        // l'utilisateur connecté: son email et son ID (Firebase Authentication)
        val user_connected = FirebaseAuth.getInstance().currentUser
        val email_connected = user_connected?.email
        val id_user_connected = user_connected?.uid.toString()

        auth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()

//        store image from firebase storage into Imageview (profile)
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        val imageRef = storageReference.child("profile_images/".plus(id_user_connected))
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
            // Decode the byte array into a Bitmap
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            // Set the Bitmap as the source for the ImageView
            profile.setImageBitmap(bmp)
        }.addOnFailureListener {
            // Handle the error
        }

        val user_conn = users.whereEqualTo("email", email_connected)
        user_conn.get().addOnSuccessListener { oneUser ->
            for (user in oneUser) {
                val prenom = user.get("firstname").toString().trim()
                val nom = user.get("lastname").toString().trim()
                nom_prenom_profil.text = prenom.plus(" ").plus(nom)
            }
        }

        edit_profile_photo.setOnClickListener {
            showImageChoiceDialogue()
        }

        edit_data.setOnClickListener {
            val EditProfileActivity = Intent(this, EditProfileActivity::class.java)
            startActivity(EditProfileActivity)
        }

        // vers la page des amis
        friends.setOnClickListener {
            val friendsActivity = Intent(this, FriendsActivity::class.java)
            startActivity(friendsActivity)
        }

        // vers la page des demandes d'ami envoyées
        sended_requests.setOnClickListener {
            val requestsSendedActivity = Intent(this, RequestsSentActivity::class.java)
            startActivity(requestsSendedActivity)
        }

        // vers la page des demandes d'ami reçues
        received_requests.setOnClickListener {
            val requestsReceivedActivity = Intent(this, RequestsReceivedActivity::class.java)
            startActivity(requestsReceivedActivity)
        }

        // vers la page des messages envoyés
        sended_messages.setOnClickListener {
            val MessagesSentActivity = Intent(this, MessagesSentActivity::class.java)
            startActivity(MessagesSentActivity)
        }

        // vers la page des messages reçus
        received_messages.setOnClickListener {
            val MessagesReceivedActivity = Intent(this, MessagesReceivedActivity::class.java)
            startActivity(MessagesReceivedActivity)
        }

        // vers la page de modification du profil
//        profile.setOnClickListener {
//            val EditProfileActivity = Intent(this, EditProfileActivity::class.java)
//            startActivity(EditProfileActivity)
//        }

    }

    private fun uploadFile(bitmap: Bitmap, userModel: UserModel) {
        Utils.uploadPitureToFirebase(
            "profile_images",
            bitmap,
            {
                showLoading()
            }, {
                showLoading()
            }, { message ->

            }, { url ->
                val userModel = userModel.copy(userImageUrl = url)
                // Add to firebase
                createNewBikingEvent(userModel) {
                    showToastLong(it)
                    hideLoading()
                    lifecycleScope.launch {
                        delay(1500)
                        onBackPressed()
                    }

                }
            }
        )
    }

    private fun saveToFirebase() {

        val EventModel = UserModel(
            user_id = auth.currentUser?.uid.toString()
        )
        uploadFile(mImageBitmap!!, EventModel)
    }

    fun createNewBikingEvent(
        userModel: UserModel,
        callback: (String) -> Unit
    ) {
        auth.currentUser?.uid?.let { userId ->
            mFirestore.collection("users")
                .document(userModel.user_id)
                .set(userModel)
                .addOnSuccessListener {
                    callback("photo de profil modifiée")

                }.addOnFailureListener {

                    callback("Error")
                }.addOnCanceledListener {

                    callback("Cancel")
                }
        }
    }

    private fun checkValidation(): Boolean {
        if (mImageBitmap == null) {
            showToastShort("Image is missing")
            return false
        }
        return true
    }

    private fun showImageChoiceDialogue() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.getLayoutInflater()
        val dialogView = inflater.inflate(R.layout.custom_choice_dialogue, null)
        dialogBuilder.setView(dialogView)

        val btn_cancel = dialogView.findViewById<ImageView>(R.id.cancel)
        val btn_camera = dialogView.findViewById<Button>(R.id.btn_camera)
        val btn_gallery = dialogView.findViewById<Button>(R.id.btn_gallery)
        val txt_dialog_title = dialogView.findViewById<TextView>(R.id.txt_dialog_content)
        val alertDialog = dialogBuilder.create()
        txt_dialog_title.setText("Choose Image From")

        btn_camera.setOnClickListener {
            alertDialog.dismiss()
            onLaunchCamera()
        }

        btn_gallery.setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                pickImageFromGallery_Code
            )
//            saveToFirebase()
        }

        btn_cancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    fun showToastShort(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    fun showToastLong(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    fun onLaunchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = Utils.createFileForImage(this)
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(this, AUTHORITY, photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if (intent.resolveActivity(this.packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, pickImageFromCamera_Code)
            }
        }
    }


    //    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
//            // Get the selected image's URI
//            val selectedImageUri = data?.data
//            // Do something with the selected image (e.g. display it in an ImageView)
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == pickImageFromGallery_Code) {
            var imageUri: Uri? = data?.data
            val uri = Utils.readUriImage(this, imageUri!!)
//            binding?.profileImage?.setImageURI(uri)

            val user_connected = FirebaseAuth.getInstance().currentUser
            val email_connected = user_connected?.email
            val id_user_connected = user_connected?.uid.toString()
            val storage = Firebase.storage
// Create a reference to the file you want to upload
            val profile_images = storage.getReference("profile_images/".plus(id_user_connected))
// Create a file from the local file path
            val file = Uri.fromFile(File("path/to/local/image.jpg"))
// Upload the file to Firebase Storage
            val uploadTask = profile_images.putFile(imageUri)

// Register observers to listen for when the upload is done or if it fails
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
            }.addOnSuccessListener {
                // Handle successful uploads
            }

            lifecycleScope.launch {
                val rotatedImage = Utils.rotateImageIfRequired(
                    BitmapFactory.decodeFile(uri!!.path),
                    uri
                )
                mImageBitmap = rotatedImage
            }
        }
        if (requestCode === pickImageFromCamera_Code && resultCode === Activity.RESULT_OK) {

            val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)

            lifecycleScope.launch {
                val rotatedImage = Utils.rotateImageIfRequired(
                    takenImage,
                    photoFile!!.toUri()
                )
//                binding.profileImage?.setImageBitmap(
//                    rotatedImage
//                )
                mImageBitmap = rotatedImage
            }
        }
    }

    private fun showLoading(){
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun hideLoading(){
        binding.progressBar.visibility = View.GONE
    }

    private fun setImage(view: ImageView, imageResource: Int) {
        view.setImageResource(imageResource)
    }


}




