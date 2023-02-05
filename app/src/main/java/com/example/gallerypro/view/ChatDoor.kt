package com.example.gallerypro.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gallerypro.FireBaseMethod
import com.example.gallerypro.MySharedPreference
import com.example.gallerypro.R
import com.example.gallerypro.adapter.ChatRequestAdapter
import com.example.gallerypro.customDialog.RequestDialog
import com.example.gallerypro.customDialog.RoomDialogListner
import com.example.gallerypro.model.UserRequestModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class ChatDoor : AppCompatActivity(), View.OnClickListener {

    private var account:GoogleSignInAccount? = null
    private lateinit var chatIdTxt: TextView
    private lateinit var joinChatRoom: ImageView
    private lateinit var shareChatId: ImageView
    private lateinit var chatRoomId: String
    private lateinit var chatRequestRec: RecyclerView
    private lateinit var firbaseAdapter: ChatRequestAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var databse: FireBaseMethod
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_door)


        account = GoogleSignIn.getLastSignedInAccount(this)

        if (account?.id == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        chatRoomId = MySharedPreference(this).chatId!!
        chatIdTxt = findViewById(R.id.ChatIdTxt)
        joinChatRoom = findViewById(R.id.joinChat)
        shareChatId = findViewById(R.id.ShareBtn)
        chatRequestRec = findViewById(R.id.chatRequestRec)
        progress = findViewById(R.id.progressDoorBar)
        joinChatRoom.setOnClickListener(this)
        shareChatId.setOnClickListener(this)
        chatIdTxt.text = chatRoomId

        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        chatRequestRec.layoutManager = linearLayoutManager
        databse = FireBaseMethod(this)

        val parser = SnapshotParser<UserRequestModel?> { dataSnapshot ->
            val friendlyMessage = dataSnapshot.getValue(
                UserRequestModel::class.java
            )!!
            progress.visibility = View.GONE
            friendlyMessage.userId = dataSnapshot.key
            friendlyMessage
        }

        val options = FirebaseRecyclerOptions.Builder<UserRequestModel>()
            .setQuery(databse.userRequesReference(), parser)
            .build()

        firbaseAdapter = ChatRequestAdapter(options)

        firbaseAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val friendlyMessageCount = firbaseAdapter.itemCount
                val lastVisiblePosition =
                    linearLayoutManager.findLastCompletelyVisibleItemPosition()

                if (lastVisiblePosition == -1 ||
                    positionStart >= friendlyMessageCount - 1 &&
                    lastVisiblePosition == positionStart - 1
                ) {
                    chatRequestRec.scrollToPosition(positionStart)
                }
            }
        })
        chatRequestRec.adapter = firbaseAdapter
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.joinChat -> {
                val dialog = RequestDialog(this, object : RoomDialogListner {
                    override fun onClick(requestId: String) {
                        progress.visibility = View.VISIBLE
                        databse.requestMethod(
                            requestId, UserRequestModel(
                                account?.displayName, account?.photoUrl.toString(), 1, null
                            )
                        ).addOnSuccessListener {
                            databse.userRequesReference().child(requestId).setValue(
                                UserRequestModel(
                                    null, null, 0, null
                                )
                            ).addOnCanceledListener {
                                progress.visibility = View.GONE
                            }
                        }
                    }
                })
                dialog.show()
            }
            R.id.ShareBtn -> shareAppRefer()
        }
    }

    private fun shareAppRefer() {
        Toast.makeText(this, "Preparing...", Toast.LENGTH_SHORT).show()
        val referTextEnglish = "Here is my secret chat room id is :- $chatRoomId"
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, referTextEnglish)
        startActivity(Intent.createChooser(shareIntent, "Share Your Chat via"))
    }

    override fun onPause() {
        firbaseAdapter.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        progress.visibility = View.VISIBLE
        firbaseAdapter.startListening()
    }
}