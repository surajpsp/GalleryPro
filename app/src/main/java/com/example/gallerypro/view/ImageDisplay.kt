package com.example.gallerypro.view

import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.transition.Fade
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gallerypro.R
import com.example.gallerypro.fragments.pictureBrowserFragment
import com.example.gallerypro.utils.*
import java.util.*

class ImageDisplay : AppCompatActivity(), itemClickListener {

    private var imageRecycler: RecyclerView? = null
    private var allpictures: ArrayList<pictureFacer?>? = null
    private var load: ProgressBar? = null
    private var foldePath: String? = null
    private var folderName: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)

        folderName = findViewById(R.id.foldername)
        folderName!!.text = intent.getStringExtra("folderName")

        foldePath = intent.getStringExtra("folderPath")
        allpictures = ArrayList()
        imageRecycler = findViewById(R.id.recycler)
        imageRecycler!!.addItemDecoration(MarginDecoration(this))
        imageRecycler!!.hasFixedSize()
        load = findViewById(R.id.loader)


        if (allpictures!!.isEmpty()) {
            load!!.visibility = View.VISIBLE
            allpictures = getAllImagesByFolder(foldePath!!)
            imageRecycler!!.adapter = picture_Adapter(allpictures, this@ImageDisplay, this)
            load!!.visibility = View.GONE
        }

    }

    override fun onPicClicked(holder: PicHolder?, position: Int, pics: ArrayList<pictureFacer>?) {
        val browser: pictureBrowserFragment =
            pictureBrowserFragment.newInstance(pics, position, this@ImageDisplay)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            browser.enterTransition = Fade()
            browser.exitTransition = Fade()
        }

        supportFragmentManager
            .beginTransaction()
            .addSharedElement(holder!!.picture, position.toString() + "picture")
            .add(R.id.displayContainer, browser)
            .addToBackStack(null)
            .commit()
    }

    override fun onPicClicked(pictureFolderPath: String?, folderName: String?) {
        Log.d("yjj-----","clicked")
    }

    private fun getAllImagesByFolder(path: String): ArrayList<pictureFacer?>? {
        var images = ArrayList<pictureFacer?>()
        val allVideosuri =
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE
        )
        val cursor = this@ImageDisplay.contentResolver.query(
            allVideosuri,
            projection,
            MediaStore.Images.Media.DATA + " like ? ",
            arrayOf("%$path%"),
            null
        )
        try {
            cursor!!.moveToFirst()
            do {
                val pic = pictureFacer()
                pic.picturName =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                pic.picturePath =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                pic.pictureSize =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
                images.add(pic)
            } while (cursor.moveToNext())
            cursor.close()
            val reSelection =
                ArrayList<pictureFacer?>()
            for (i in images.size - 1 downTo -1 + 1) {
                reSelection.add(images[i])
            }
            images = reSelection
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return images
    }
}