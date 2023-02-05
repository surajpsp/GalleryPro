package com.example.gallerypro

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gallerypro.model.FriendlyMessage
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ImageUpload : AppCompatActivity(),View.OnClickListener {

    companion object{
        private const val PICK_IMAGE_REQUEST: Int = 88
    }

    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private lateinit var filePath: Uri

    private lateinit var imageView:ImageView
    private lateinit var sendImage:ImageView
    private lateinit var captionText:EditText
    private lateinit var reAddImage:ImageView

    private lateinit var progressDialog:ProgressDialog
    private lateinit var database: FireBaseMethod

    private val msgId: String? by lazy {
        intent.extras!!.getString("MsgId")
    }
    private val roomId: String? by lazy {
        intent.extras!!.getString("RoomId")
    }
    private val chatId: String? by lazy {
        intent.extras!!.getString("ChatId")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_upload)

        imageView = findViewById(R.id.showImages)
        sendImage = findViewById(R.id.sendImages)
        captionText = findViewById(R.id.caption)
        reAddImage = findViewById(R.id.reAddImage)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Hold on !")

        database = FireBaseMethod(this)

        sendImage.setOnClickListener(this)
        reAddImage.setOnClickListener(this)

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        chooseImage()
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            PICK_IMAGE_REQUEST
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            filePath = data!!.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun compressImages():ByteArray{
        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        Log.d("ImageSize1-----------", baos.size().toString())
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        Log.d("ImageSize2-----------", baos.size().toString())
        return baos.toByteArray()
    }

    private fun getFileExtension(uri: Uri?): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri!!))
    }


    @SuppressLint("SimpleDateFormat")
    private fun uploadImage() {
        val sdf = SimpleDateFormat("d,MMMM,yyyy").format(Date())
        val chatId = MySharedPreference(this).chatId!!

        progressDialog.show()

        val ref = storageReference!!.child("$sdf/" + "$chatId/" + UUID.randomUUID().toString())

        val uploadTask = ref.putBytes(compressImages())
            .addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                    .totalByteCount
                progressDialog.setMessage("Uploading" + progress.toInt() + "%")
            }

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                onUplaodFail()
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                sendImageNow(task.result.toString())
                Log.d("Download1-------------", task.result.toString())

            } else {
                onUplaodFail()
                task.exception?.let {
                    throw it
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun sendImageNow(imageLink:String){

        val caption = if (captionText.text.isNotEmpty()) captionText.text.toString() else " "
            val sdf = SimpleDateFormat("hh:mm a").format(Date())
            database.sendMassages(
                roomId!!, msgId!!,
                FriendlyMessage(
                    id = chatId,
                    time = sdf,
                    text = caption,
                    image = imageLink
                )
            ).addOnSuccessListener {
                progressDialog.dismiss()
                database
                finish()
                onBackPressed()
            }
    }

    private fun onUplaodFail(){
        progressDialog.dismiss()
        Toast.makeText(this, "Failed ", Toast.LENGTH_SHORT)
            .show()

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.sendImages -> uploadImage()
            R.id.reAddImage -> chooseImage()
        }
    }

}