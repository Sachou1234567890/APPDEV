package com.example.rsdev

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rsdev.data.SentRequest


class SentRequestAdapter(private val requests: List<SentRequest>) : RecyclerView.Adapter<SentRequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentRequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sent_request, parent, false)
        return SentRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: SentRequestViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    override fun getItemCount(): Int {
        return requests.size
    }
}