package com.example.gallerypro.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gallerypro.R
import com.example.gallerypro.customDialog.DialogMore
import com.example.gallerypro.utils.*


class MainActivity : AppCompatActivity(), itemClickListener {

    private lateinit var folderRecycler: RecyclerView
    private var empty: TextView? = null
    private var folds: ArrayList<imageFolder>? = arrayListOf()
    private lateinit var menuBtn: ImageView

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        menuBtn = findViewById(R.id.menuChat)
        menuBtn.setOnClickListener {
            DialogMore(this).creditCardDialog().show()
        }

        empty = findViewById(R.id.empty)
        folderRecycler = findViewById(R.id.folderRecycler)
        folderRecycler.addItemDecoration(MarginDecoration(this))
        folderRecycler.hasFixedSize()

        if (checkPermission()) {
            folds = getPicturePaths()
            folderRecycler.adapter = pictureFolderAdapter(folds, this@MainActivity, this)
        } else {
            requestPermission()
        }
    }

    companion object {
        private const val READ_EXTERNAL_STORAGE = 1
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getPicturePaths(): ArrayList<imageFolder>? {
        val picFolders = ArrayList<imageFolder>()
        val picPaths = ArrayList<String>()
        val allImagesuri =
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID
        )
        val cursor =
            this.contentResolver.query(allImagesuri, projection, null, null, null)
        try {
            cursor?.moveToFirst()
            do {
                val folds = imageFolder()
                cursor!!.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                val folder =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                val datapath =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                var folderpaths =
                    datapath.substring(0, datapath.lastIndexOf("$folder/"))
                folderpaths = "$folderpaths$folder/"
                if (!picPaths.contains(folderpaths)) {
                    picPaths.add(folderpaths)
                    folds.path = folderpaths
                    folds.folderName = folder
                    folds.firstPic =
                        datapath //if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                    folds.addpics()
                    picFolders.add(folds)
                } else {
                    for (i in picFolders.indices) {
                        if (picFolders[i].path.equals(folderpaths)) {
                            picFolders[i].firstPic = datapath
                            picFolders[i].addpics()
                        }
                    }
                }
            } while (cursor!!.moveToNext())
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        for (i in picFolders.indices) {
            Log.d(
                "picture folders",
                picFolders[i].folderName.toString() + " and path = " + picFolders[i]
                    .path + " " + picFolders[i].numberOfPics
            )
        }
        return picFolders
    }


    override fun onPicClicked(
        holder: PicHolder?,
        position: Int,
        pics: ArrayList<pictureFacer?>?
    ) {
    }

    override fun onPicClicked(
        pictureFolderPath: String?,
        folderName: String?
    ) {
        val move = Intent(this@MainActivity, ImageDisplay::class.java)
        move.putExtra("folderPath", pictureFolderPath)
        move.putExtra("folderName", folderName)
        startActivity(move)
    }

    private fun checkPermission(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this,
                "Write External Storage permission allows us to save files. Please allow this permission in App Settings.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    folds = getPicturePaths()
                    folderRecycler.adapter = pictureFolderAdapter(folds, this@MainActivity, this)
                } else {
                    Log.d("permission----------", "granted")
                }
            }
        }
    }

}