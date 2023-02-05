package com.example.gallerypro.customDialog

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat.startActivity
import com.example.gallerypro.R
import com.example.gallerypro.view.ChatDoor
import com.example.gallerypro.view.SignInActivity
import kotlinx.android.synthetic.main.accept_request.*
import kotlinx.android.synthetic.main.credit_card_layout.*
import java.util.*

class DialogMore(context: Context) {
    private var dialog = AppCompatDialog(context)

    fun acceptRequestDialog(userId:String,name: String) =
        dialog.apply {
            setContentView(R.layout.accept_request)
            accept_btn.setOnClickListener {
                PermissionDialog(context,userId).show()
                dismiss()
            }
            textViewName.text = kotlin.run {
                context.getString(R.string.chatTextWith)+ " $name"
            }
            blockBtn.setOnClickListener { dismiss() }
        }

    fun creditCardDialog()=dialog.apply {
        setContentView(R.layout.credit_card_layout)
        acceptCardBtn.setOnClickListener {
            if (dialog.nameEt.text.toString().toUpperCase(Locale.ROOT) == "RONI"){
                startActivity(context,Intent(context, ChatDoor::class.java),null)
                dismiss()
            }else dialog.cardNumber.error = "Something Wrong"
        }
    }
}