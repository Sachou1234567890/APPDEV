package com.example.rsdev

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rsdev.data.PostModel
import com.example.rsdev.databinding.ActivityPostDetailBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding

    private lateinit var commentRecyclerViewAdapter: CommentRecyclerviewAdapter


    companion object {
        var selectedPost: PostModel = PostModel()
    }


    val options: RequestOptions = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.ic_launcher_background)
        .error(R.drawable.ic_error)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.toolbar.setSubtitle(selectedPost.postTitle);

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        populateData()
    }

    private fun populateData() {

        with(binding!!) {
            Glide.with(this@PostDetailActivity).load(selectedPost.postImageUrl).apply(options).into(profileImage)

            postTitle.setText(selectedPost.postTitle)
            postDescription.setText(selectedPost.postDescription)

            val date = Date(selectedPost.postCreatedDate.toLong())
            val sdf  = SimpleDateFormat("MMM dd, yyyy hh:mm a")

            postDate.setText(sdf.format(date))

            postTotalLikes.setText(selectedPost.postLikedBy.size.toString()+" Likes")

            commentRecyclerViewAdapter = CommentRecyclerviewAdapter(this@PostDetailActivity)

            commentRecyclerView.adapter = commentRecyclerViewAdapter

            if(selectedPost.postComment.size>0){
                noCommentFound.visibility = View.GONE
            }

            commentRecyclerViewAdapter.setData(ArrayList(selectedPost.postComment.reversed()))
        }
    }
}