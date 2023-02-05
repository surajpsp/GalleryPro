package com.example.gallerypro.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gallerypro.R
import com.example.gallerypro.customDialog.DialogMore
import com.example.gallerypro.model.UserRequestModel
import com.example.gallerypro.view.ChatHomeActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import de.hdodenhof.circleimageview.CircleImageView

class ChatRequestAdapter(
    private val options: FirebaseRecyclerOptions<UserRequestModel>
) : FirebaseRecyclerAdapter<UserRequestModel, ChatRequestAdapter.MassageHolder>(options) {

    inner class MassageHolder(v: View?) : RecyclerView.ViewHolder(v!!) {
        var userName: TextView = itemView.findViewById(R.id.userName)
        var userImg: CircleImageView = itemView.findViewById(R.id.userImage)
    }

    override fun getItemViewType(position: Int): Int {
        return options.snapshots[position].uStatus!!
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MassageHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            SENT -> {
                MassageHolder(
                    inflater.inflate(
                        R.layout.recieved_request_lay,
                        parent,
                        false
                    )
                )
            }
            RECIEVE -> {
                MassageHolder(
                    inflater.inflate(
                        R.layout.accept_request_lay,
                        parent,
                        false
                    )
                )
            }
            SENT_CHAT, RECIEVE_CHAT -> {
                MassageHolder(
                    inflater.inflate(
                        R.layout.chat_request_lay,
                        parent,
                        false
                    )
                )
            }
            else -> {
                MassageHolder(
                    inflater.inflate(
                        R.layout.chat_request_lay,
                        parent,
                        false
                    )
                )
            }
        }

    }

    override fun onBindViewHolder(
        viewHolder: MassageHolder,
        position: Int,
        friendlyMessage: UserRequestModel
    ) {
        val context = viewHolder.itemView.context
        val itemType = viewHolder.itemViewType

        if (itemType == SENT) {
            viewHolder.userName.text = friendlyMessage.userId
            viewHolder.userImg.setImageDrawable(viewHolder.itemView.context.getDrawable(R.drawable.person_icon))
        } else {
            viewHolder.userName.text = friendlyMessage.uName
            Glide.with(context)
                .load(friendlyMessage.uProfile)
                .into(viewHolder.userImg)
        }


        viewHolder.itemView.setOnClickListener {
            when (itemType) {
                SENT -> toast(context)
                RECIEVE -> DialogMore(context).acceptRequestDialog(
                    friendlyMessage.userId!!,
                    friendlyMessage.uName!!
                ).show()
                RECIEVE_CHAT, SENT_CHAT -> chat(context, friendlyMessage)
            }

        }
    }

    private fun chat(context: Context, friendlyMessage: UserRequestModel) {
        val intent = Intent(context, ChatHomeActivity::class.java)
        intent.putExtra("id", friendlyMessage.userId)
        intent.putExtra("name", friendlyMessage.uName)
        intent.putExtra("profilePic", friendlyMessage.uProfile)
        intent.putExtra("status", friendlyMessage.uStatus)
        context.startActivity(intent)
    }

    private fun toast(context: Context) {
        Toast.makeText(context, "Not accepted yet!", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val SENT = 0
        private const val RECIEVE = 1
        private const val BLOCKED = 2
        private const val RECIEVE_CHAT = 4
        private const val SENT_CHAT = 3
    }

}