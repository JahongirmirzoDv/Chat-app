package com.chsd.pdpgram

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chsd.pdpgram.adapters.UsersRvAdapter
import com.chsd.pdpgram.databinding.FragmentUsersBinding
import com.chsd.pdpgram.models.Message
import com.chsd.pdpgram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UsersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UsersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var binding: FragmentUsersBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var list: ArrayList<User>
    lateinit var mlist: ArrayList<Message>
    private val TAG = "UsersFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUsersBinding.inflate(inflater, container, false)
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()



        reference = firebaseDatabase.getReference("Users")
        val photoUrl = firebaseAuth.currentUser?.photoUrl
        val displayName = firebaseAuth.currentUser?.displayName
        val email = firebaseAuth.currentUser?.email
        val uid = firebaseAuth.currentUser?.uid
        var status = "online"

//        updateValue(AppFirebaseMessaginService())
        var user = User(displayName, email, uid, photoUrl.toString(), status,"")
        reference.child(uid!!).setValue(user)

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

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                list = ArrayList()
                for (i in children) {
                    val value = i.getValue(User::class.java)
                    if (value?.uid != uid) {
                        list.add(value!!)
                    }
                }

                val rvAdapter = UsersRvAdapter(list, object : UsersRvAdapter.onPress {
                    override fun onclick(user: User) {
                        var intent = Intent(requireContext(), Messages::class.java)
                        intent.putExtra("user", user)
                        startActivity(intent)
                    }

                    override fun getUsers(userlist: ArrayList<String>) {
                    }
                }, true, mlist)
                binding.userRv.adapter = rvAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isComplete) {
                var firebaseToken = it.result.toString()
                updateValue(firebaseToken)
            }
        }
        return binding.root
    }

    fun updateValue(token: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val reference = firebaseDatabase.getReference("Users").child(currentUser?.uid!!)
        val hashMap = HashMap<String, Any>()
//            val hashMap2 = HashMap<String, Any>()
        hashMap["token"] = token
//            hashMap2["token"] = token.toString()
        reference.updateChildren(hashMap)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UsersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UsersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}