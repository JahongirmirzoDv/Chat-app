package com.chsd.pdpgram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.chsd.pdpgram.adapters.MessagesAdapter
import com.chsd.pdpgram.databinding.ActivityMessagesBinding
import com.chsd.pdpgram.models.Message
import com.chsd.pdpgram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class Messages : AppCompatActivity() {
    lateinit var binding: ActivityMessagesBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var mlist: ArrayList<Message>
    private val TAG = "Messages"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("Messages")

        val user = intent.getSerializableExtra("user") as User

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
            binding.editText.text.clear()
            reference.child(key!!)
                .setValue(message)
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