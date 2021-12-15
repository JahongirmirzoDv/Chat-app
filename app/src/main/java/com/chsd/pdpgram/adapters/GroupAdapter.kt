package com.chsd.pdpgram.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chsd.pdpgram.databinding.GroupItemBinding
import com.chsd.pdpgram.models.Group

class GroupAdapter(var list: ArrayList<Group>, var onpress: onPress) :
    RecyclerView.Adapter<GroupAdapter.Vh>() {
    inner class Vh(var itemview: GroupItemBinding) : RecyclerView.ViewHolder(itemview.root) {
        fun Bind(group: Group) {
            itemview.groupName.text = group.name
            itemview.container.setOnClickListener {
                onpress.onclick(group)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(GroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.Bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    interface onPress {
        fun onclick(group: Group)
    }
}