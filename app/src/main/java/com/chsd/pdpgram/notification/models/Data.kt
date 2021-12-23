package com.chsd.pdpgram.notification.models

import com.chsd.pdpgram.models.Group
import com.chsd.pdpgram.models.User
import java.io.Serializable

class Data : Serializable {
    var user: String? = null
    var icon: Int? = null
    var body: String? = null
    var title: String? = null
    var sented: User? = null
    var group: Group? = null
    var isgroup:Boolean? = null

    constructor(user: String?, icon: Int?, body: String?, title: String?, sented: User?) {
        this.user = user
        this.icon = icon
        this.body = body
        this.title = title
        this.sented = sented
    }


    constructor()
    constructor(user: String?, icon: Int?, body: String?, title: String?, group: Group?,isgroup: Boolean?) {
        this.user = user
        this.icon = icon
        this.body = body
        this.title = title
        this.group = group
        this.isgroup = isgroup
    }


}