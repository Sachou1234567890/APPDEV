package com.example.rsdev


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rsdev.data.Friend
import com.example.rsdev.data.ReceivedRequest


class ReceivedRequestAdapter(private var requests: List<ReceivedRequest>) : RecyclerView.Adapter<ReceivedRequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceivedRequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_received_request, parent, false)
        return ReceivedRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceivedRequestViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    override fun getItemCount(): Int {
        return requests.size
    }
}
