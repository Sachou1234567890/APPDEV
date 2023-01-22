package com.example.rsdev

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rsdev.data.ReceivedMessage


class ReceivedMessageAdapter(private val messages: List<ReceivedMessage>) : RecyclerView.Adapter<ReceivedMessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceivedMessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_received_message, parent, false)
        return ReceivedMessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceivedMessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}
