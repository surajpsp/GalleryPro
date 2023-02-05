package com.example.gallerypro.model

class FriendlyMessage(
    private var id: String? = null,
    private var time: String? = null,
    private var text: String? = null,
    private var image:String? = null
) {

    fun getId():String?{
        return id
    }

    fun getText(): String? {
        return text
    }

    fun getTime():String?{
        return time
    }

    fun getImage():String?{
        return image
    }
}