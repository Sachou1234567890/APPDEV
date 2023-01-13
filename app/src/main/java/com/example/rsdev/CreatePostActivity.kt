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
import com.example.rsdev.databinding.ActivityCreatePostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class CreatePostActivity : AppCompatActivity() {

    val AUTHORITY: String = "com.example.rsdev.CreatePostActivity"

    private lateinit var binding: ActivityCreatePostBinding

    private val pickImageFromGallery_Code = 100
    private val pickImageFromCamera_Code = 101

    var photoFile: File? = null

    var mImageBitmap: Bitmap? = null

    protected lateinit var auth: FirebaseAuth

    lateinit var storage: FirebaseStorage

    protected lateinit var mFirestore: FirebaseFirestore

    private lateinit var managePermissions: ManagePermissions

    private val PermissionsRequestCode = 123

    var permissionsList = listOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()

        mFirestore = FirebaseFirestore.getInstance()

        setContentView(binding.root)

        binding.toolbar.setSubtitle("Create a Post");

        storage = Firebase.storage

        binding.imageCard.setOnClickListener {
            showImageChoiceDialogue()
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.createNewPostBtn.setOnClickListener {

            if (checkValidation()) {
                saveToFirebase()
            }

        }

        // Initialize a new instance of ManagePermissions class
        managePermissions = ManagePermissions(this, permissionsList, PermissionsRequestCode)

        managePermissions.checkPermissions()

    }

    private fun saveToFirebase() {
        val EventModel = PostModel(
            postId = UUID.randomUUID().toString(),
            postCreatedDate = Calendar.getInstance().timeInMillis.toString(),
            postByUserId = auth.currentUser?.uid.toString(),
            postComment = arrayListOf(),
            postDescription = binding.postDescription.text.toString(),
            postLikedBy = arrayListOf(),
            postTitle = binding.postTitle.text.toString(),
        )
        uploadFile(mImageBitmap!!, EventModel)
    }

    private fun checkValidation(): Boolean {
        if (mImageBitmap == null) {
            showToastShort("Image is missing")
            return false
        } else if (binding.postTitle.text?.trim().isNullOrEmpty()) {
            showToastShort("Post Title is missing")
            return false
        } else if (binding.postDescription.text?.trim().isNullOrEmpty()) {
            showToastShort("Post Description is missing")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == pickImageFromGallery_Code) {
            var imageUri: Uri? = data?.data
            val uri = Utils.readUriImage(this, imageUri!!)
            binding?.profileImage?.setImageURI(uri)

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
                binding.profileImage?.setImageBitmap(
                    rotatedImage
                )
                mImageBitmap = rotatedImage
            }
        }
    }


    private fun uploadFile(bitmap: Bitmap, postModel: PostModel) {
        Utils.uploadPitureToFirebase(
            "AllPostsData",
            bitmap,
            {
                showLoading()
            }, {
                showLoading()
            }, { message ->

            }, { url ->
                val postModel = postModel.copy(postImageUrl = url)
                // Add to firebase
                createNewBikingEvent(postModel) {
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

    fun createNewBikingEvent(
        postModel: PostModel,
        callback: (String) -> Unit
    ) {

        auth.currentUser?.uid?.let { userId ->
            mFirestore.collection("AllPosts")
                .document(postModel.postId)
                .set(postModel)
                .addOnSuccessListener {
                    callback("Post Shared Successfully")

                }.addOnFailureListener {

                    callback("Error")
                }.addOnCanceledListener {

                    callback("Cancel")
                }
        }
    }

    private fun showLoading(){
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun hideLoading(){
        binding.progressBar.visibility = View.GONE
    }

}