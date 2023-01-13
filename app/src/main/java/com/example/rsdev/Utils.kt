package com.example.rsdev

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.net.toUri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


object Utils {


    fun createFileForImage(context: Context): File? {
        val directoryToStore: File
        directoryToStore = context.getExternalFilesDir("RSDEV")!!
        if (!directoryToStore.exists()) {
            if (directoryToStore.mkdir());
        }
        var n = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            Calendar.getInstance().time
        }
        val fname = "Image-$n.jpg"
        val file = File(directoryToStore, fname)
        if (file.exists()) file.delete()
        file.createNewFile()
        try {
            val out = FileOutputStream(file)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }

    fun readUriImage(context: Context, selectedFileUri: Uri): Uri? {
        try {
            val parcelFileDescriptor: ParcelFileDescriptor? =
                context.getContentResolver()?.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            if (parcelFileDescriptor != null) {
                parcelFileDescriptor.close()
            }

            return createDirectoryAndSaveFile(context, image)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null;
    }

    fun createDirectoryAndSaveFile(context: Context, imageToSave: Bitmap): Uri? {
        val directoryToStore: File
        directoryToStore = context.getExternalFilesDir("RSDEV")!!
        if (!directoryToStore.exists()) {
            if (directoryToStore.mkdir());
        }
        var n = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            Calendar.getInstance().time
        }
        val fname = "Image-$n.jpg"
        val file = File(directoryToStore, fname)
        if (file.exists()) file.delete()
        file.createNewFile()
        try {
            val out = FileOutputStream(file)
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.toUri()
    }

    fun rotateImageIfRequired(img: Bitmap, selectedImage: Uri): Bitmap? {
        val ei = ExifInterface(selectedImage.path!!)
        val orientation: Int =
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }

    fun rotateImage(img: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

    fun uploadPitureToFirebase(
        folder: String,
        bitmap: Bitmap,
        showProgress: () -> Unit,
        hideProgress: () -> Unit,
        message: (String) -> Unit,
        picUrl: (String) -> Unit
    ) {
        showProgress()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://rsdev-47072.appspot.com/")
        val mountainImagesRef =
            storageRef.child(folder + "/" + FirebaseAuth.getInstance().currentUser?.uid + "_" + Calendar.getInstance().timeInMillis + ".jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        val data = baos.toByteArray()
        val uploadTask = mountainImagesRef.putBytes(data)
        uploadTask
            .addOnFailureListener {
                // Handle unsuccessful uploads
                hideProgress()
                message("Something went wrong")
            }
            .addOnSuccessListener { taskSnapshot -> // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                val downloadUri: Task<Uri> = taskSnapshot.storage.downloadUrl

                downloadUri.addOnSuccessListener {
                    hideProgress()
                    picUrl(downloadUri.result.toString())
                    message("Profile Picture Updated")
                }.addOnFailureListener {
                    hideProgress()
                    message("Something went wrong")
                }
                    .addOnCanceledListener {
                        hideProgress()
                        message("Something went wrong")
                    }
            }.addOnCanceledListener {
                hideProgress()
                message("Something went wrong")
            }
    }

    fun changeDateFormat(date: String, inputFormat: String, outpuFormat: String): String {
        val inputFormat = SimpleDateFormat(inputFormat)
        val outPutFormat = SimpleDateFormat(outpuFormat)
        return outPutFormat.format(inputFormat.parse(date))
    }
}


interface OnPositionItemTypeClick {
    fun onItemClick(model: Any, position: Int, type: String, image: Bitmap? = null)
}