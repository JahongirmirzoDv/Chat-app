package com.chsd.pdpgram

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}