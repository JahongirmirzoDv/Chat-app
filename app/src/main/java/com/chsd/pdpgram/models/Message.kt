package com.chsd.pdpgram.models

import android.content.BroadcastReceiver

class Message {
    var message: String? = null
    var date: String? = null
    var image: String? = null
    var sender: String? = null
    var receiver: String? = null



    constructor()
    constructor(
        message: String?,
        date: String?,
        image: String?,
        sender: String?,
        receiver: String?
    ) {
        this.message = message
        this.date = date
        this.image = image
        this.sender = sender
        this.receiver = receiver
    }


}