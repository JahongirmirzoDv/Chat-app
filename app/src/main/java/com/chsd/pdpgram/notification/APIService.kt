package com.chsd.pdpgram.notification

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import com.chsd.pdpgram.notification.models.Responce
import com.chsd.pdpgram.notification.models.Sender

interface APIService {
    @Headers(
        "Content-type:application/json",
        "Authorization:key=AAAAJJjbF-A:APA91bFsxunqCLZ4oLoYULUa9BevuwCs8kpk_TaKIx4Jhy0QDHTDKhLa8mcGRu9UfSy6qsAlumca8DZzezE1hPrzfk6BjdtK_y8bbR-WCeZcEY55i5hbcndb07hI4useqCUCkcWjYnUn"
    )
    @POST("fcm/send")
    fun sendNotification(@Body sender: Sender): Call<Responce>
}