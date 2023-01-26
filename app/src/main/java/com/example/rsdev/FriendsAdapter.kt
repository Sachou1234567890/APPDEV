package com.example.rsdev

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rsdev.data.Friend

class FriendsAdapter(private var friends: List<Friend>) : RecyclerView.Adapter<FriendsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return FriendsViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {

        val friend = friends[position]
        holder.bind(friend)
        holder.itemView.setTag(friend.friend_id)
//        holder.bind(friends[position])

    }

    override fun getItemCount(): Int {
        return friends.size
    }

    fun updateData(newFriends: List<Friend>) {
        friends = newFriends
        notifyDataSetChanged()
    }
}
