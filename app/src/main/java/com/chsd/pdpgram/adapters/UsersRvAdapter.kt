package com.chsd.pdpgram.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chsd.pdpgram.R
import com.chsd.pdpgram.databinding.UserIttemBinding
import com.chsd.pdpgram.models.Message
import com.chsd.pdpgram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UsersRvAdapter(
    var list: List<User>,
    var onpress: onPress,
    var ischat: Boolean,
    var mlist: List<Message>
) :
    RecyclerView.Adapter<UsersRvAdapter.Vh>() {
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var reference: DatabaseReference
    lateinit var userlist: ArrayList<String>

    inner class Vh(var itemview: UserIttemBinding) : RecyclerView.ViewHolder(itemview.root) {
        fun Bind(user: User, position: Int) {
            userlist = ArrayList()
            userlist.clear()
            Glide.with(itemView).load(user.photoUrl).into(itemview.profileImage)
            itemview.name.text = user.displayName
            for (i in mlist) {
                if (user.uid == i.receiver || list[position].uid == i.sender) {
                    itemview.title.text = i.message
                    itemview.time.text = i.date
                }
            }

            if (ischat) {
                if (user.status.equals("online")) {
                    itemview.status.setImageResource(R.color.teal_700)
                }
                itemview.container.setOnClickListener {
                    onpress.onclick(user)
                }
            }
            if (!ischat) {
                itemview.status.visibility = View.INVISIBLE
                itemview.container.setOnClickListener {
                    if (itemview.status.visibility == View.VISIBLE) {
                        itemview.status.visibility = View.INVISIBLE
                        userlist.remove(user.uid)
                    } else {
                        itemview.status.setImageResource(R.drawable.check)
                        itemview.status.visibility = View.VISIBLE
                        userlist.add(user.uid!!)
                    }
                    onpress.getUsers(duplicateUser(userlist))
                }
            }
        }

        private fun duplicateUser(list: List<String>): ArrayList<String> {
            var ans = ArrayList<String>()
            for (i in list) {
                if (!ans.contains(i)) {
                    ans.add(i)
                }
            }
            return ans
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(UserIttemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.Bind(list[position], position)
    }

    override fun getItemCount(): Int = list.size
    interface onPress {
        fun onclick(user: User)
        fun getUsers(userlist: ArrayList<String>)
    }
}