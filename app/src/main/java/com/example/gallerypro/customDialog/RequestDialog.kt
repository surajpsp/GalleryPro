package com.example.gallerypro.customDialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDialog
import com.example.gallerypro.FireBaseMethod
import com.example.gallerypro.MySharedPreference
import com.example.gallerypro.R
import com.example.gallerypro.utils.MainUtil
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class RequestDialog(context: Context, private val listner: RoomDialogListner) :
    AppCompatDialog(context), ValueEventListener {

    private lateinit var textValue: TextInputEditText
    private lateinit var textInputLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.get_room_dialog)

        val btn = findViewById<ImageView>(R.id.continueBtn)
        textValue = findViewById(R.id.roomEditText)!!
        textInputLayout = findViewById(R.id.textViewRequest)!!
        MainUtil.onTextChange(textValue,textInputLayout)
        val chatId = MySharedPreference(context).chatId!!

        btn!!.setOnClickListener {
            if (textValue.text!!.isNotEmpty() && textValue.text.toString() != chatId) {
                FireBaseMethod(context).checkUserAvailable(textValue.text.toString().toUpperCase(Locale.ROOT))
                    .addListenerForSingleValueEvent(this)
            } else textInputLayout.error = "Enter Value"
        }
    }

    override fun onCancelled(error: DatabaseError) {
        Log.d("errro22---------", error.message)
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        if (snapshot.exists()) {
            listner.onClick(textValue.text.toString().toUpperCase(Locale.ROOT))
            dismiss()
        }
        else {
            textInputLayout.error = "No User"
        }
    }
}