package com.example.gallerypro.customDialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import com.example.gallerypro.FireBaseMethod
import com.example.gallerypro.MySharedPreference
import com.example.gallerypro.R
import com.example.gallerypro.model.FriendlyMessage
import com.example.gallerypro.model.UserRequestModel
import java.text.SimpleDateFormat
import java.util.*

class PermissionDialog(context: Context, private val userId: String) : AppCompatDialog(context) {

    private lateinit var databse: FireBaseMethod
    private lateinit var sharedPreference: MySharedPreference
    private lateinit var progressBar: ProgressBar

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.request_dialog)
        sharedPreference = MySharedPreference(context)
        databse = FireBaseMethod(context)
        progressBar = findViewById(R.id.progressBar0)!!

        val sdf = SimpleDateFormat("hh:mm a").format(Date())
        val msg = "Hi,I'm ${sharedPreference.myName}.Now you can msg me"

        databse.setAcceptMethod(userId, UserRequestModel(
            sharedPreference.myName, sharedPreference.myProfile, 4, null
        )).addOnSuccessListener {
            databse.acceptMethod(
                userId
            ).addOnSuccessListener {
                databse.sendMassages(
                    sharedPreference.chatId!!, userId, FriendlyMessage(
                        sharedPreference.chatId, sdf, msg
                    )
                ).addOnSuccessListener {
                    Toast.makeText(context, "Now you can msg", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }.addOnFailureListener {
                Log.d("fail01----------------", it.message.toString())
                onFail()
            }
        }.addOnFailureListener {
            Log.d("fail02----------------", it.message.toString())
            onFail()
        }
    }

    private fun onFail() {
        dismiss()
        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
        progressBar.visibility = View.GONE
    }
}