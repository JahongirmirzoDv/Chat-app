package com.chsd.pdpgram.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chsd.pdpgram.databinding.FromItemBinding
import com.chsd.pdpgram.databinding.GroupToItemBinding
import com.chsd.pdpgram.databinding.ToItemBinding
import com.chsd.pdpgram.models.Message
import kotlin.contracts.contract

class GroupMessageAdapter(var list: List<Message>, var uid: String, var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class fromVh(var itemview: FromItemBinding) : RecyclerView.ViewHolder(itemview.root) {
        fun Bind(message: Message) {
            itemview.fromTxt.text = message.message
            itemview.fromTime.text = message.date
        }
    }

    inner class toVh(var itemview: GroupToItemBinding) : RecyclerView.ViewHolder(itemview.root) {
        fun Bind(message: Message) {
            itemview.toTxt.text = message.message
            itemview.toTime.text = message.date
            Glide.with(context)
                .load(message.sender)
                .into(itemview.profileImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            return fromVh(
                FromItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return toVh(
                GroupToItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1) {
            val fromVh = holder as fromVh
            fromVh.Bind(list[position])
        } else {
            val toVh = holder as toVh
            toVh.Bind(list[position])
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return if (list[position].image == uid) {
            1
        } else 2
    }
}