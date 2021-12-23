package com.chsd.pdpgram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.chsd.pdpgram.adapters.MessagesAdapter
import com.chsd.pdpgram.databinding.ActivityMessagesBinding
import com.chsd.pdpgram.models.Message
import com.chsd.pdpgram.models.User
import com.chsd.pdpgram.notification.*
import com.chsd.pdpgram.notification.models.Data
import com.chsd.pdpgram.notification.models.Responce
import com.chsd.pdpgram.notification.models.Sender
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Call
import java.util.*
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class Messages : AppCompatActivity() {
    lateinit var binding: ActivityMessagesBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var mlist: ArrayList<Message>
    private val TAG = "Messages"
    lateinit var apiService: APIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("Messages")

        val user = intent.getSerializableExtra("user") as User
        apiService =
            Client.getRetrofit("https://fcm.googleapis.com/").create(APIService::class.java)

        binding.name.text = user.displayName
        Glide.with(this).load(user.photoUrl).into(binding.profileImage)

        binding.send.setOnClickListener {
            val str = binding.editText.text.toString()
            val hour = Date().hours
            val minute = Date().minutes
            var sender = firebaseAuth.currentUser?.uid
            var receiver = user.uid
            val key = reference.push().key
            val message = Message(str, "$hour:$minute", null, sender, receiver)
            reference.child(key!!)
                .setValue(message)
            Log.d(TAG, "firebase user uid:${firebaseAuth.currentUser?.uid}")
            Log.d(TAG, "user uid:${user.uid}")

            apiService.sendNotification(
                Sender(
                    Data(
                        firebaseAuth.currentUser?.uid,
                        R.drawable.ic_launcher_foreground,
                        str,
                        "New Message",
                        user
                    ),
                    "${user.token}"
                )
            )
                .enqueue(object : Callback<Responce> {
                    override fun onResponse(
                        call: Call<Responce>,
                        response: Response<Responce>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@Messages, "Success", Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, "Success:")
                        }
                    }

                    override fun onFailure(call: Call<Responce>, t: Throwable) {
                        t.printStackTrace()
                        Log.d(TAG, "onFailure: ${t.message}")
                    }

                })

            binding.editText.text.clear()
        }

        reference
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mlist = ArrayList()
                    val children = snapshot.children
                    for (i in children) {
                        val value = i.getValue(Message::class.java)!!
                        if (value.sender?.equals(firebaseAuth.currentUser?.uid)!! && value.receiver?.equals(
                                user.uid
                            )!! || value.sender.equals(user.uid) && value.receiver?.equals(
                                firebaseAuth.currentUser?.uid
                            )!!
                        ) {
                            mlist.add(value)
                        }
                    }
                    val messagesAdapter = MessagesAdapter(mlist, firebaseAuth.currentUser?.uid!!)
                    Log.d(TAG, "onDataChange: ${mlist.size}")
                    binding.mRv.adapter = messagesAdapter
                    binding.mRv.scrollToPosition(mlist.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}