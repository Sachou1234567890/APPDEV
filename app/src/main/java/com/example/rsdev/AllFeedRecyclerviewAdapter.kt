package com.example.rsdev

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rsdev.data.PostModel
import com.example.rsdev.databinding.ItemPostBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AllFeedRecyclerviewAdapter (
    private val context: Context,
    private val userId: String,
    private val listener: OnPositionItemTypeClick
) : RecyclerView.Adapter<AllFeedRecyclerviewAdapter.ViewHolder>() {

    private var mainList = mutableListOf<PostModel>()

    val options: RequestOptions = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.ic_launcher_background)
        .error(R.drawable.ic_error)

    fun setData(list: ArrayList<PostModel>) {
        mainList = list
        notifyDataSetChanged()
    }

    val sdf  = SimpleDateFormat("MMM dd, yyyy hh:mm a")

    inner class ViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mainList[position]) {

                binding.title.text = postTitle

                binding.description.text = postDescription

                val date = Date(postCreatedDate.toLong())

                binding.dateTime.text = "Date: " + sdf.format(date)

                try {
                    Glide.with(context).load(
                        postImageUrl
                    ).apply(options).into(binding.image)
                } catch (e: Exception) {

                }

                val fav = postLikedBy.findLast { it.userId == userId }

                if (fav != null) {
                    binding.favourite.setImageResource(R.drawable.ic_baseline_favorite_24)
                    binding.likeText.setTextColor(context.resources.getColor(R.color.redColor))
                    binding.likeText.setText("Liked")
                } else {
                    binding.favourite.setImageResource(R.drawable.ic_round_favorite_border_24)
                    binding.likeText.setTextColor(context.resources.getColor(R.color.grayColor))
                    binding.likeText.setText("Like")
                }

                binding.like.setOnClickListener {
                    listener.onItemClick(this@with, position, "Like")
                }

                binding.comment.setOnClickListener {
                    listener.onItemClick(this@with, position, "Comment")
                }

                binding.detailEvent.setOnClickListener {
                    listener.onItemClick(
                        this@with,
                        position,
                        "Detail",
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mainList.size
    }

}