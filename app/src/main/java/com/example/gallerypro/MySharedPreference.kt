package com.example.gallerypro

import android.content.Context
import android.content.SharedPreferences

@Suppress("DEPRECATION")
class MySharedPreference(context: Context) {
    var sharedPref: SharedPreferences? = null

    //// variables start
    private val roomId: String = "roomId"
    private val myChatId: String = "myId"
    private val name: String = "name"
    private val profile: String = "profile"
    //// variable end

    init {
        sharedPref = context.getSharedPreferences(
            context.resources.getString(R.string.app_name),
            Context.MODE_MULTI_PROCESS
        )
    }

    private fun putData(key: String?, value: String?) {
        val editor = sharedPref?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    private fun getData(key: String?, defValue: String? = ""): String? {
        return sharedPref?.getString(key, defValue)
    }

    var room: String?
        get() = getData(roomId)
        set(value) = putData(roomId, value)

    var chatId: String?
        get() = getData(myChatId)
        set(value) = putData(myChatId, value)

    var myName: String?
        get() = getData(name)
        set(value) = putData(name, value)

    var myProfile: String?
        get() = getData(profile)
        set(value) = putData(profile, value)

}