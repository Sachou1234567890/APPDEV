package com.example.rsdev

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rsdev.data.SentMessage


class SentMessageAdapter(private val messages: List<SentMessage>) : RecyclerView.Adapter<SentMessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentMessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sent_message, parent, false)
        return SentMessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: SentMessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}
