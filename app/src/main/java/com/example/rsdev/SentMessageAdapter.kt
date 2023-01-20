package com.example.rsdev

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rsdev.data.Message
import com.example.rsdev.SentMessageViewHolder




class SentMessageAdapter(private val messages: List<Message>) : RecyclerView.Adapter<SentMessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentMessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sent_message_item, parent, false)
        return SentMessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: SentMessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}
