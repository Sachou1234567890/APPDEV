package com.example.rsdev

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.rsdev.data.ReceivedMessage


class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val messageTextView = itemView.findViewById<TextView>(R.id.messageTextView)
    val dateTextView = itemView.findViewById<TextView>(R.id.dateTextView)
    val senderTextView = itemView.findViewById<TextView>(R.id.senderTextView)

        fun bind(message: ReceivedMessage) {
        messageTextView.text = message.messageText
        dateTextView.text = "envoyé le ".plus(message.dateMessage)
        senderTextView.text = "par ".plus(message.senderFirstName).plus(" ").plus(message.senderLastName)
    }

//    class ReceivedMessage {
//        var id: String = ""
//        var messageText: String = ""
//        var dateMessage: String = ""
//        var senderFirstName: String = ""
//        var senderLastName: String = ""
//        // Add this constructor
//        constructor(){}
//        //...
//    }


}
