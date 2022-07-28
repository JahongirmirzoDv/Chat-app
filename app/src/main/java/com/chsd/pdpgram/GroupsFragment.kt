package com.chsd.pdpgram

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.chsd.pdpgram.adapters.GroupAdapter
import com.chsd.pdpgram.databinding.DialogBinding
import com.chsd.pdpgram.databinding.FragmentGroupsBinding
import com.chsd.pdpgram.models.Group
import com.chsd.pdpgram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GroupsFragment : Fragment() {

    lateinit var binding: FragmentGroupsBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var groupList: ArrayList<Group>
    private val TAG = "GroupsFragment"
    lateinit var nn: ArrayList<Group>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGroupsBinding.inflate(inflater, container, false)
        groupList = ArrayList()

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("Groups")
        nn = ArrayList()

        binding.fab.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(
                DialogBinding.inflate(
                    LayoutInflater.from(requireContext())
                ).root
            )
            dialog.setCancelable(true)
            dialog.findViewById<TextView>(R.id.cancle).setOnClickListener {
                dialog.cancel()
            }
            dialog.findViewById<TextView>(R.id.next).setOnClickListener {
                val toString = dialog.findViewById<EditText>(R.id.geoup_name).text.toString()
                addGroup(toString)
                dialog.cancel()
            }
            dialog.show()
        }
        return binding.root
    }

    private fun addGroup(name: String) {
        var intent = Intent(requireContext(), AddGruopUsers::class.java)
        intent.putExtra("name", name)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nn.clear()
                groupList.clear()
                var li = ArrayList<String>()
                val value = snapshot.children
                for (i in value) {
                    val value1 = i.getValue(Group::class.java)
                    value1?.key = i.key
                    if (value1 != null) {
                        groupList.add(value1)
                    }
                }
                Log.d(TAG, "onDataChange: ${groupList.size}")

                for (i in 0 until groupList.size) {
                    for (k in 0 until groupList[i].userList?.size!!) {
                        if (groupList[i].userList!![k] == firebaseAuth.currentUser?.uid) {
                            nn.add(groupList[i])
                            Log.d(TAG, "onDataChange: ${groupList[i].name}")
                        }
                    }
                }
                val groupAdapter = GroupAdapter(duplicate(nn), object : GroupAdapter.onPress {
                    override fun onclick(group: Group) {
                        var intent = Intent(requireContext(), GroupMessages::class.java)
                        intent.putExtra("group", group)
                        startActivity(intent)
                    }
                })
                binding.groupRv.adapter = groupAdapter
            }

            private fun duplicate(list: ArrayList<Group>): java.util.ArrayList<Group> {
                val mf = ArrayList<Group>()
                for (i in list) {
                    if (!mf.contains(i)) {
                        mf.add(i)
                    }
                }
                return mf
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onStop() {
        super.onStop()

    }
}