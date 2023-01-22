package com.example.rsdev
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.rsdev.data.ReceivedRequest


class ReceivedRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val friend_request_image = itemView.findViewById<ImageView>(R.id.friend_request_image)
    val friend_request_name = itemView.findViewById<TextView>(R.id.friend_request_name)
    val accept_friend_request = itemView.findViewById<TextView>(R.id.accept_friend_request)

    fun bind(request: ReceivedRequest) {

        friend_request_image.setImageResource(R.drawable.profil_homme)
        friend_request_name.text = request.senderFirstName.plus(" ").plus(request.senderLastName)
    }


}
