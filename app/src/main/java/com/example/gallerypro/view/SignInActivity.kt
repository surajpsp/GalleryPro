package com.example.gallerypro.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.gallerypro.FireBaseMethod
import com.example.gallerypro.MySharedPreference
import com.example.gallerypro.R
import com.example.gallerypro.model.UsersModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class SignInActivity : AppCompatActivity(), View.OnClickListener,ValueEventListener {

    private var mSignInButton: SignInButton? = null
    private var mSignInClient: GoogleSignInClient? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var databse:FireBaseMethod
    private lateinit var it: GoogleSignInAccount
    private lateinit var genratedId:String
    private lateinit var sharedPreference:MySharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sharedPreference = MySharedPreference(this)

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mSignInClient = GoogleSignIn.getClient(this, gso)

        progressBar = findViewById(R.id.progressBarSignIn)
        mSignInButton = findViewById<View>(R.id.sign_in_button) as SignInButton
        mSignInButton!!.setSize(SignInButton.SIZE_WIDE)
        mSignInButton!!.setOnClickListener(this)
        databse = FireBaseMethod(this)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_button -> signIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("SignInActivity-------","OnActivity Result")

        Log.d("SignInActivity1-------",requestCode.toString())

        Log.d("SignInActivity2-------",resultCode.toString())

        Log.d("SignInActivity3-------",data.toString())

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            Log.d("SignInActivity4-------","IN If")
            task.addOnFailureListener {
                Log.d("SignInActivity5-------",it.stackTrace.toString())
                Log.d("SignInActivity5-------",it.message.toString())
                Log.d("SignInActivity5-------",it.localizedMessage!!.toString())
                Log.d("SignInActivity5-------",it.stackTrace.toString())
            }
            task.addOnSuccessListener {
                Log.d("SignInActivity--------","Task successfull")
                this.it = it
                genratedId = randId(it.displayName!!,it.id!!)
                databse.checkUserAvailable(genratedId).
                addListenerForSingleValueEvent(this)
            }
        }
    }

    private fun randId(name:String,id:String):String{
        val caps = name.toUpperCase(Locale.ROOT)
        return caps.substring(0,5)+id.substring(0,3)
    }

    private fun signIn() {
        progressBar.visibility = View.VISIBLE
        Log.d("SignInActivity-------","Signin")
        val signInIntent = mSignInClient!!.signInIntent
        startActivityForResult(signInIntent,
            RC_SIGN_IN
        )
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCancelled(error: DatabaseError) {
        Log.d("parse---------------",error.message)
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        if (snapshot.exists())
        {
            startChatDoor()
        }else{
            Log.d("SignInActivity--------","No Exists")
            databse.setUser(
                randId(it.displayName!!,it.id!!),
                UsersModel(
                    name = it.displayName!!,
                    profileIcon = it.photoUrl.toString(),
                    email = it.email!!
                )
            ).addOnSuccessListener {
                startChatDoor()
            }
        }
    }

    private fun startChatDoor(){
        sharedPreference.chatId = genratedId
        sharedPreference.myName = it.displayName
        sharedPreference.myProfile = it.photoUrl.toString()
        progressBar.visibility = View.GONE
        startActivity(Intent(this@SignInActivity, ChatDoor::class.java))
        finish()
    }
}