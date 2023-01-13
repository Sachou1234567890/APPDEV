package com.example.rsdev

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.example.rsdev.data.CommentModel
import com.example.rsdev.databinding.ItemCommentBinding
import java.text.SimpleDateFormat
import java.util.*

class CommentRecyclerviewAdapter (
    private val context: Context,
) : RecyclerView.Adapter<CommentRecyclerviewAdapter.ViewHolder>() {

    private var mainList = mutableListOf<CommentModel>()

    val options: RequestOptions = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.ic_launcher_background)
        .error(R.drawable.ic_error)

    var sdf: SimpleDateFormat = SimpleDateFormat("MMM dd,yyyy HH:mm:ss")

    fun setData(list: ArrayList<CommentModel>) {
        mainList = list
        notifyDataSetChanged()
    }


    inner class ViewHolder(val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mainList[position]) {

                binding.username.text = username

                binding.message.text = message

                var resultdate = Date(datetime.toLong())

                binding.dateTime.text = sdf.format(resultdate)
            }
        }
    }

    override fun getItemCount(): Int {
        return mainList.size
    }

}