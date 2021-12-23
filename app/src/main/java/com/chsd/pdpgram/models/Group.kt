package com.chsd.pdpgram.models

import java.io.Serializable

class Group : Serializable {
    var name: String? = null
    var userList: ArrayList<String>? = null
    var messagesList: ArrayList<Message>? = null
    var key: String? = null




    constructor(name: String?, userList: ArrayList<String>?) {
        this.name = name
        this.userList = userList
    }


    constructor(
        name: String?,
        userList: ArrayList<String>?,
        messagesList: ArrayList<Message>?,
        key: String?
    ) {
        this.name = name
        this.userList = userList
        this.messagesList = messagesList
        this.key = key
    }

    constructor()


}