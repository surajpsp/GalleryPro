package com.example.gallerypro.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.bumptech.glide.Glide
import com.example.gallerypro.FireBaseMethod
import com.example.gallerypro.ImageUpload
import com.example.gallerypro.MySharedPreference
import com.example.gallerypro.R
import com.example.gallerypro.adapter.FirebaseRecyclerView
import com.example.gallerypro.model.FriendlyMessage
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*


class ChatHomeActivity : AppCompatActivity(), TextWatcher, View.OnClickListener,
    ValueEventListener {

    private lateinit var database: FireBaseMethod
    private lateinit var chatId: String
    private lateinit var roomId: String
    private lateinit var msgId: String
    private lateinit var mSendButton: ImageView
    private lateinit var mMessageRecyclerView: RecyclerView
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mMessageEditText: EditText
    private lateinit var mAddMessageImageView: ImageView
    private lateinit var name: TextView
    private lateinit var staus: TextView
    private lateinit var profileImage: CircleImageView
    private lateinit var mFirebaseAdapter: FirebaseRecyclerView
    private lateinit var timer: Timer
    private val intentId: String? by lazy {
        intent.extras!!.getString("id")
    }
    private val intentName: String? by lazy {
        intent.extras!!.getString("name")
    }
    private val intentProfile: String? by lazy {
        intent.extras!!.getString("profilePic")
    }
    private val intentStatus: Int? by lazy {
        intent.extras!!.getInt("status")
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        name = findViewById(R.id.msgUser)
        staus = findViewById(R.id.onlineStatus)
        profileImage = findViewById(R.id.circleImageView)
        mProgressBar = findViewById(R.id.progressBar)
        mMessageRecyclerView = findViewById(R.id.messageRecyclerView)
        mMessageEditText = findViewById(R.id.messageEditText)
        mAddMessageImageView = findViewById(R.id.addMessageImageView)
        mSendButton = findViewById(R.id.sendButton)

        mSendButton.setOnClickListener(this)
        mAddMessageImageView.setOnClickListener(this)
        mAddMessageImageView.setOnClickListener(this)
        mMessageEditText.addTextChangedListener(this)
        timer = Timer()

        check()
        chatId = MySharedPreference(this).chatId!!
        database = FireBaseMethod(this)

        when (intentStatus) {
            3 -> {
                roomId = chatId
                msgId = intentId!!
            }
            4 -> {
                roomId = intentId!!
                msgId = chatId
            }
            else -> {
                roomId = chatId
                msgId = chatId
            }
        }

        mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager.stackFromEnd = true
        mMessageRecyclerView.layoutManager = mLinearLayoutManager

        // New child entries
        val parser = SnapshotParser<FriendlyMessage?> { dataSnapshot ->
            val friendlyMessage = dataSnapshot.getValue(
                FriendlyMessage::class.java
            )!!
//                friendlyMessage.setId(dataSnapshot.key)
            friendlyMessage
        }

        val msgRef: Query = database.msgReference(roomId, msgId)
        val options: FirebaseRecyclerOptions<FriendlyMessage> =
            FirebaseRecyclerOptions.Builder<FriendlyMessage>()
                .setQuery(msgRef, parser)
                .build()

        mFirebaseAdapter = FirebaseRecyclerView(options, chatId)
        mFirebaseAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                mProgressBar.visibility = View.GONE

                val friendlyMessageCount = mFirebaseAdapter.itemCount
                val lastVisiblePosition =
                    mLinearLayoutManager.findLastCompletelyVisibleItemPosition()
                if (lastVisiblePosition == -1 ||
                    positionStart >= friendlyMessageCount - 1 &&
                    lastVisiblePosition == positionStart - 1
                ) {
                    mMessageRecyclerView.scrollToPosition(positionStart)
                }
            }
        })

        mMessageRecyclerView.adapter = mFirebaseAdapter
        database.getOnlineStatus(intentId!!).addValueEventListener(this)
    }

    override fun onPause() {
        mFirebaseAdapter.stopListening()
        database.setMeOnline(intentId!!, 2)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mFirebaseAdapter.startListening()
        database.setMeOnline(intentId!!, 1)
    }

    private var typeStart: Boolean = true
    override fun afterTextChanged(s: Editable?) {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                Log.d("timer---------------", "timerRun")
                database.setMeOnline(intentId!!, 1)
                typeStart = true
            }
        }, 2000)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        mSendButton.isEnabled = s.toString().trim { it <= ' ' }.isNotEmpty()
        if (typeStart) {
            database.setMeOnline(intentId!!, 3)
            typeStart = false
        }
        timer.cancel()
    }

    @SuppressLint("SimpleDateFormat")
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.sendButton -> {
                if (mMessageEditText.text.isNotEmpty()) {
                    val sdf = SimpleDateFormat("hh:mm a").format(Date())
                    database.sendMassages(
                        roomId, msgId,
                        FriendlyMessage(
                            id = chatId,
                            time = sdf,
                            text = mMessageEditText.text.toString(),
                            image = null
                        )
                    )
                    mMessageEditText.text.clear()
                }
            }
            R.id.addMessageImageView -> {
                val intent = Intent(this, ImageUpload::class.java)
                intent.putExtra("RoomId",roomId)
                intent.putExtra("MsgId",msgId)
                intent.putExtra("ChatId",chatId)
                startActivity(intent)
            }
        }
    }

    private fun check() {
        name.text = intentName
        Glide.with(this)
            .load(intentProfile)
            .into(profileImage)
    }

    override fun onCancelled(error: DatabaseError) {
        Log.d("errro22---------", error.message)
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        val data = snapshot.value.toString()
        staus.text = when (data) {
            "2" -> getString(R.string.offline)
            "3" -> getString(R.string.typing)
            "1" -> getString(R.string.online)
            else -> "hided"
        }
    }
}