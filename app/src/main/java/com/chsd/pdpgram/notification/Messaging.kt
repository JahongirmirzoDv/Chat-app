package com.chsd.pdpgram.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.AudioManager.STREAM_NOTIFICATION
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.chsd.pdpgram.GroupMessages
import com.chsd.pdpgram.MainActivity
import com.chsd.pdpgram.Messages
import com.chsd.pdpgram.R
import com.chsd.pdpgram.models.Group
import com.chsd.pdpgram.models.Message
import com.chsd.pdpgram.models.User
import com.chsd.pdpgram.notification.models.Data
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

class Messaging : FirebaseMessagingService() {
    private val TAG = "MyFirebaseMessagingServ"

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        Log.d(TAG, "onMessageReceived: ${p0.notification?.title}")
        Log.d(TAG, "onMessageReceived: ${p0.notification?.body}")
        Log.d(TAG, "onMessageReceived: ${p0.data}")
        val s = p0.data["isgroup"]
        if (s == "true") {
            val fromJson = Gson().fromJson(p0.data["group"], Group::class.java)
            Log.d(TAG, "onMessageReceived1111111: ${fromJson}")
            val defaultSoundUri: Uri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notifyIntent = Intent(this, GroupMessages::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.putExtra("group", fromJson)

            val notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val builder = NotificationCompat.Builder(this, "channelId")
            builder.setSmallIcon(R.mipmap.ic_launcher)
            builder.setContentTitle(p0.data["title"])
            builder.setContentText(p0.data["body"])
            builder.setContentIntent(notifyPendingIntent)
            builder.setAutoCancel(true)
            builder.setSound(defaultSoundUri, STREAM_NOTIFICATION)
            val notification = builder.build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val mChannel = NotificationChannel("channelId", "Name", importance)
                mChannel.description = "descriptionText"
                notificationManager.createNotificationChannel(mChannel)
            }
            notificationManager.notify(1, notification)
        }else{
            val fromJson = Gson().fromJson(p0.data["sented"], User::class.java)
            Log.d(TAG, "onMessageReceived1111111: ${fromJson}")
            val defaultSoundUri: Uri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notifyIntent = Intent(this, Messages::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.putExtra("user", fromJson)

            val notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val builder = NotificationCompat.Builder(this, "channelId")
            builder.setSmallIcon(R.mipmap.ic_launcher)
            builder.setContentTitle(p0.data["title"])
            builder.setContentText(p0.data["body"])
            builder.setContentIntent(notifyPendingIntent)
            builder.setAutoCancel(true)
            builder.setSound(defaultSoundUri, STREAM_NOTIFICATION)
            val notification = builder.build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val mChannel = NotificationChannel("channelId", "Name", importance)
                mChannel.description = "descriptionText"
                notificationManager.createNotificationChannel(mChannel)
            }
            notificationManager.notify(1, notification)
        }
    }
}