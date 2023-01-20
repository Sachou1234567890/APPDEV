package com.example.rsdev

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.rsdev.data.Message


class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val messageTextView = itemView.findViewById<TextView>(R.id.messageTextView)
    val timeTextView = itemView.findViewById<TextView>(R.id.timeTextView)
    val senderTextView = itemView.findViewById<TextView>(R.id.senderTextView)

    fun bind(message: Message) {
        messageTextView.text = message.text
        timeTextView.text = message.time
        senderTextView.text = message.sender
    }

}
