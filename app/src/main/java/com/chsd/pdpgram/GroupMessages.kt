package com.chsd.pdpgram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.chsd.pdpgram.adapters.GroupMessageAdapter
import com.chsd.pdpgram.adapters.MessagesAdapter
import com.chsd.pdpgram.databinding.ActivityGroupMessagesBinding
import com.chsd.pdpgram.databinding.ActivityMessagesBinding
import com.chsd.pdpgram.models.Group
import com.chsd.pdpgram.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class GroupMessages : AppCompatActivity() {
    lateinit var binding: ActivityGroupMessagesBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var mlist: ArrayList<Message>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("Groups")

        val group = intent.getSerializableExtra("group") as Group
        binding.name.text = group.name
        binding.send.setOnClickListener {
            val str = binding.editText.text.toString()
            val hour = Date().hours
            val minute = Date().minutes
            var sender = firebaseAuth.currentUser?.uid
            val key = reference.push().key
            var image = firebaseAuth.currentUser?.photoUrl
            val message = Message(str, "$hour:$minute", sender, image.toString(), null)
            binding.editText.text.clear()
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
}