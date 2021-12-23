package com.chsd.pdpgram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.chsd.pdpgram.adapters.GroupMessageAdapter
import com.chsd.pdpgram.adapters.MessagesAdapter
import com.chsd.pdpgram.adapters.UsersRvAdapter
import com.chsd.pdpgram.databinding.ActivityGroupMessagesBinding
import com.chsd.pdpgram.databinding.ActivityMessagesBinding
import com.chsd.pdpgram.models.Group
import com.chsd.pdpgram.models.Message
import com.chsd.pdpgram.models.User
import com.chsd.pdpgram.notification.APIService
import com.chsd.pdpgram.notification.Client
import com.chsd.pdpgram.notification.models.Data
import com.chsd.pdpgram.notification.models.Responce
import com.chsd.pdpgram.notification.models.Sender
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class GroupMessages : AppCompatActivity() {
    lateinit var binding: ActivityGroupMessagesBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var users_reference: DatabaseReference
    lateinit var mlist: ArrayList<Message>
    lateinit var userList: ArrayList<String>
    private val TAG = "GroupMessages"
    lateinit var userDataList: ArrayList<User>
    lateinit var tokens: ArrayList<User>
    lateinit var apiService: APIService
    lateinit var sentedList: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("Groups")
        users_reference = firebaseDatabase.getReference("Users")
        userList = ArrayList()
        userDataList = ArrayList()
        tokens = ArrayList()
        sentedList = ArrayList()
        groupUsers()
        getToken()

        apiService =
            Client.getRetrofit("https://fcm.googleapis.com/").create(APIService::class.java)

        val group = intent.getSerializableExtra("group") as Group
        binding.name.text = group.name
        binding.send.setOnClickListener {
            Log.d(TAG, "onDataChange 1:${userList.size}")
            val str = binding.editText.text.toString()
            val hour = Date().hours
            val minute = Date().minutes
            var sender = firebaseAuth.currentUser?.uid
            val key = reference.push().key
            var image = firebaseAuth.currentUser?.photoUrl
            val message = Message(str, "$hour:$minute", sender, image.toString(), null)
            binding.editText.text.clear()
            Log.d(TAG, "onCreate: ${tokens.size}")
            sendMessage(str, userList, tokens)
            group.key?.let { it1 ->
                reference.child(it1).child("messages").child(key!!)
                    .setValue(message)
            }
        }

        group.key?.let {
            reference.child(it).child("messages")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        mlist = ArrayList()
                        val children = snapshot.children
                        for (i in children) {
                            val value = i.getValue(Message::class.java)
                            if (value != null) {
                                mlist.add(value)
                            }
                        }
                        val messagesAdapter = GroupMessageAdapter(
                            mlist,
                            firebaseAuth.currentUser?.uid!!,
                            this@GroupMessages
                        )
                        binding.mRv.adapter = messagesAdapter
                        binding.mRv.scrollToPosition(mlist.size - 1)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
    }

    private fun sendMessage(str: String, list: ArrayList<String>, tokens: ArrayList<User>) {
        val group = intent.getSerializableExtra("group") as Group
        for (i in 0 until list.size) {
            Toast.makeText(this, "salom", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "sendMessage: $i")

            Log.d(TAG, "onDataChange 1:${tokens}")
            apiService.sendNotification(
                Sender(
                    Data(
                        firebaseAuth.currentUser?.uid,
                        R.drawable.ic_launcher_foreground,
                        str,
                        "New Message",
                        group,true
                    ),
                    "${tokens[i].token}"
                )
            )
                .enqueue(object : Callback<Responce> {
                    override fun onResponse(
                        call: Call<Responce>,
                        response: Response<Responce>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@GroupMessages, "Success", Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, "Success:")
                        }
                    }

                    override fun onFailure(call: Call<Responce>, t: Throwable) {
                        t.printStackTrace()
                        Log.d(TAG, "onFailure: ${t.message}")
                    }

                })
        }
    }

    private fun groupUsers() {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val group = intent.getSerializableExtra("group") as Group
                userList.clear()
                val value = snapshot.children
                for (i in value) {
                    val children = i.getValue(Group::class.java)
                    if (children!!.name == group.name) {
                        for (k in children!!.userList!!) {
                            if (firebaseAuth.currentUser!!.uid != k) {
                                userList.add(k)
                            }
                        }
                    }
                }
                Log.d(TAG, "onDataChange 2:${userList.size}")
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getToken() {
        users_reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                userDataList.clear()
                for (i in children) {
                    val value = i.getValue(User::class.java)
                    if (value?.uid != firebaseAuth.currentUser!!.uid) {
                        userDataList.add(value!!)
                    }
                }
                Log.d(TAG, "getToken 1: ${userDataList.size}")
                for (i in 0 until userList.size) {
                    for (k in 0 until userDataList.size) {
                        if (userDataList[k].uid == userList[i]) {
                            tokens.add(userDataList[i])
                            sentedList.add(userDataList[k])
                            Log.d(TAG, "getToken 2: ${tokens.size}")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}