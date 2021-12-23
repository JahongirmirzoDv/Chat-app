package com.chsd.pdpgram.models

import java.io.Serializable

class User : Serializable {
    var displayName: String? = null
    var email: String? = null
    var uid: String? = null
    var photoUrl: String? = null
    var status: String? = null
    var token: String? = null

    constructor(displayName: String?, email: String?, uid: String?, photoUrl: String?) {
        this.displayName = displayName
        this.email = email
        this.uid = uid
        this.photoUrl = photoUrl
    }

    constructor()

    constructor(status: String?) {
        this.status = status
    }

    constructor(
        displayName: String?,
        email: String?,
        uid: String?,
        photoUrl: String?,
        status: String?,
        token: String?
    ) {
        this.displayName = displayName
        this.email = email
        this.uid = uid
        this.photoUrl = photoUrl
        this.status = status
        this.token = token
    }

    constructor(
        displayName: String?,
        email: String?,
        uid: String?,
        photoUrl: String?,
        status: String?
    ) {
        this.displayName = displayName
        this.email = email
        this.uid = uid
        this.photoUrl = photoUrl
        this.status = status
    }
}