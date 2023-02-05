package com.example.gallerypro

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.gallerypro.model.FriendlyMessage
import com.example.gallerypro.model.UserRequestModel
import com.example.gallerypro.model.UsersModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FireBaseMethod(private val context: Context) {

    private var mFirebaseInstance: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val myId: String = MySharedPreference(context).chatId!!

    fun setUser(userId: String, value: UsersModel) = mFirebaseInstance.child(
        FirebaseFolder.USERS
    ).child(userId).child(FirebaseFolder.INFO).setValue(value).addOnFailureListener {
        Toast.makeText(context, "Something wrong happen! Error::01", Toast.LENGTH_SHORT).show()
        Log.d("error01->>>>>>>>>>", it.message.toString())
    }

    fun msgReference(userId: String, msgId: String) =
        mFirebaseInstance.child(FirebaseFolder.USERS).child(userId)
            .child(FirebaseFolder.REQUEST_MSG)
            .child(msgId).child(FirebaseFolder.MSG)

    fun sendMassages(userId: String, msgId: String, msg: FriendlyMessage) =
        mFirebaseInstance.child(FirebaseFolder.USERS).child(userId)
            .child(FirebaseFolder.REQUEST_MSG)
            .child(msgId).child(FirebaseFolder.MSG).push().setValue(msg).addOnFailureListener {
                Toast.makeText(context, "Something wrong happen! Error::02", Toast.LENGTH_SHORT)
                    .show()
            }

    fun userRequesReference() =
        mFirebaseInstance.child(FirebaseFolder.USERS).child(myId)
            .child(FirebaseFolder.REQUEST_MSG)

    fun setAcceptMethod(requestId: String, usersModel: UserRequestModel) =
        mFirebaseInstance.child(FirebaseFolder.USERS).child(requestId)
            .child(FirebaseFolder.REQUEST_MSG).child(myId).setValue(usersModel)

    fun acceptMethod(requestId: String) =
        mFirebaseInstance.child(FirebaseFolder.USERS).child(myId)
            .child(FirebaseFolder.REQUEST_MSG).child(requestId).child("ustatus").setValue(3)

    fun requestMethod(requestId: String, requestModel: UserRequestModel) =
        mFirebaseInstance.child(FirebaseFolder.USERS).child(requestId)
            .child(FirebaseFolder.REQUEST_MSG).child(myId).setValue(requestModel)

    fun checkUserAvailable(requestId: String) =
        mFirebaseInstance.child(FirebaseFolder.USERS).child(requestId)

    fun setMeOnline(requestId: String, onlineValue: Int) =
        mFirebaseInstance.child(FirebaseFolder.USERS).child(myId).child(FirebaseFolder.REQUEST_MSG)
            .child(requestId).child(FirebaseFolder.ONLINE).setValue(onlineValue)
            .addOnFailureListener {
                Toast.makeText(context, "Something wrong happen! Error::0", Toast.LENGTH_SHORT)
                    .show()
            }

    fun getOnlineStatus(requestId: String) =
        mFirebaseInstance.child(FirebaseFolder.USERS).child(requestId)
            .child(FirebaseFolder.REQUEST_MSG)
            .child(myId).child(FirebaseFolder.ONLINE)

}