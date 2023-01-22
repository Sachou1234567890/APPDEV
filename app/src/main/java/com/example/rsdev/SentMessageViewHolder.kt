package com.example.rsdev

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.rsdev.data.SentMessage


class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val messageTextView = itemView.findViewById<TextView>(R.id.messageTextView)
    val dateTextView = itemView.findViewById<TextView>(R.id.dateTextView)
    val receiverTextView = itemView.findViewById<TextView>(R.id.receiverTextView)

    fun bind(message: SentMessage) {
        messageTextView.text = message.messageText
        dateTextView.text = "envoyé le ".plus(message.dateMessage)
        receiverTextView.text = "à ".plus(message.senderFirstName).plus(" ").plus(message.senderLastName)
    }

}
