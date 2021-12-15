package com.chsd.pdpgram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.chsd.pdpgram.adapters.UsersRvAdapter
import com.chsd.pdpgram.databinding.ActivityAddGruopUsersBinding
import com.chsd.pdpgram.models.Group
import com.chsd.pdpgram.models.Message
import com.chsd.pdpgram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddGruopUsers : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var reference2: DatabaseReference
    lateinit var list: ArrayList<User>
    lateinit var gList:ArrayList<String>
    lateinit var mlist: ArrayList<Message>
    private val TAG = "UsersFragment"
    lateinit var binding: ActivityAddGruopUsersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGruopUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        reference = firebaseDatabase.getReference("Users")
        reference2 = firebaseDatabase.getReference("Groups")
        val photoUrl = firebaseAuth.currentUser?.photoUrl
        val displayName = firebaseAuth.currentUser?.displayName
        val email = firebaseAuth.currentUser?.email
        val uid = firebaseAuth.currentUser?.uid
        var status = "online"
        val user = User(displayName, email, uid, photoUrl.toString(), status)

        val reference1 = firebaseDatabase.getReference("Messages")
        reference1
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mlist = ArrayList()
                    val children = snapshot.children
                    for (i in children) {
                        val value = i.getValue(Message::class.java)!!
                        mlist.add(value)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        //add user with uid
        reference.child(uid!!).setValue(user)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                list = ArrayList()
                list.clear()
                for (i in children) {
                    val value = i.getValue(User::class.java)
                    if (value?.uid != uid) {
                        list.add(value!!)
                    }
                }

                val rvAdapter = UsersRvAdapter(list, object : UsersRvAdapter.onPress {
                    override fun onclick(user: User) {

                    }

                    override fun getUsers(userlist: ArrayList<String>) {
                        gList = ArrayList()
                        gList.clear()
                        Log.d(TAG, "getUsers: ${userlist.size}")
                        userlist.add(firebaseAuth.currentUser?.uid!!)
                        val stringExtra = intent.getStringExtra("name")
                        binding.fab.setOnClickListener {
                            val group = Group(stringExtra, userlist)
                            var key = reference2.push().key
                            reference2.child(key!!).setValue(group)
                            finish()
                        }
                    }
                }, false, mlist)
                binding.userRv.adapter = rvAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}