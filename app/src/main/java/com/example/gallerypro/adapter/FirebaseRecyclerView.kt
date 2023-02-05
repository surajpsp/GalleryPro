package com.example.gallerypro.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gallerypro.R
import com.example.gallerypro.model.FriendlyMessage
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class FirebaseRecyclerView(
    private val options: FirebaseRecyclerOptions<FriendlyMessage>, private val id: String
) : FirebaseRecyclerAdapter<FriendlyMessage, FirebaseRecyclerView.MassageHolder>(options) {

    inner class MassageHolder(v: View?) : RecyclerView.ViewHolder(v!!) {
        var messageTextView: TextView =
            itemView.findViewById<View>(R.id.messageTextView) as TextView
        var messengerTextView: TextView = itemView.findViewById<View>(R.id.timeLineTxt) as TextView
        var image:ImageView = itemView.findViewById(R.id.imageHolder)
    }

    override fun getItemViewType(position: Int): Int {
        return if (options.snapshots[position].getId() == id) SEND
        else RECIEVE
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MassageHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MassageHolder(
            if (viewType == SEND) inflater.inflate(R.layout.sent_msg_lay, parent, false)
            else inflater.inflate(R.layout.recieved_msg_lay, parent, false)
        )
    }

    override fun onBindViewHolder(
        viewHolder: MassageHolder,
        position: Int,
        friendlyMessage: FriendlyMessage
    ) {
        viewHolder.messageTextView.text = friendlyMessage.getText()
        viewHolder.messengerTextView.text = friendlyMessage.getTime()

        Log.d("ImageLInt-----------",friendlyMessage.getImage().toString())
        viewHolder.image.setImageResource(0)
        if (friendlyMessage.getImage() == null){
            Log.d("ImageLInt-----------",friendlyMessage.getImage().toString())
        }else{
            Log.d("ImageLInt-----------",friendlyMessage.getImage().toString())
            Glide.with(viewHolder.image.context)
                .load(friendlyMessage.getImage())
                .into(viewHolder.image)
        }
    }

    companion object {
        private const val SEND = 1
        private const val RECIEVE = 2
    }
}