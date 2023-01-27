package com.example.rsdev
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.rsdev.data.SentRequest


class SentRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val friend_request_image = itemView.findViewById<ImageView>(R.id.friend_request_image)
    val friend_request_name = itemView.findViewById<TextView>(R.id.friend_request_name)
    val cancel_friend_request = itemView.findViewById<TextView>(R.id.cancel_friend_request)

    fun bind(request: SentRequest) {

        friend_request_image.setImageResource(R.drawable.profil_homme)
        friend_request_name.text = request.receiverFirstName.plus(" ").plus(request.receiverLastName)
    }


}
