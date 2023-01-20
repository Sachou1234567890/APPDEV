package com.example.rsdev

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.telecom.Call.Details
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.rsdev.data.CommentModel
import com.example.rsdev.data.LikeModel
import com.example.rsdev.data.PostModel
import com.example.rsdev.data.UserModel
import com.example.rsdev.databinding.ActivityFeedBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.*


class FeedActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

    private lateinit var binding: ActivityFeedBinding

    private lateinit var allFeedRecyclerviewAdapter: AllFeedRecyclerviewAdapter

    protected lateinit var auth: FirebaseAuth

    protected lateinit var mFirestore: FirebaseFirestore

    private var allPostsList: ArrayList<PostModel> = ArrayList()

    private var currentUserModel: UserModel = UserModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFeedBinding.inflate(layoutInflater)

        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        mFirestore = FirebaseFirestore.getInstance()

        binding.toolbar.setSubtitle("Home");
        binding.toolbar.inflateMenu(R.menu.main_menu)
        binding.toolbar.setOnMenuItemClickListener(this);


        binding.createNewPost.setOnClickListener {
            val LoginActivity = Intent(this, CreatePostActivity::class.java)
            startActivity(LoginActivity)
        }

        binding.profile.setOnClickListener {
            val ProfileActivity = Intent(this, ProfileActivity::class.java)
            startActivity(ProfileActivity)
        }

        binding.addFriend.setOnClickListener {
            val AddFriendActivity = Intent(this, AddFriendActivity::class.java)
            startActivity(AddFriendActivity)
        }

        binding.createMessages.setOnClickListener {
            val MyMessage = Intent(this, MyMessage::class.java)
            startActivity(MyMessage)
        }

         binding.swiperefresh.setOnRefreshListener {
            getAllPosts()
            binding.swiperefresh.isRefreshing = false
        }

        allFeedRecyclerviewAdapter = AllFeedRecyclerviewAdapter(this,
            auth.currentUser?.uid.toString(),
            object : OnPositionItemTypeClick {
                override fun onItemClick(
                    model: Any,
                    position: Int,
                    type: String,
                    image: Bitmap?
                ) {
                    if (type.equals("Like", ignoreCase = true)) {
                        handleLikeEvent(model as PostModel)
                    } else if (type.equals("Comment", ignoreCase = true)) {
                         handleCommentEvent(model as PostModel)
                    } else if (type.equals("Detail", ignoreCase = true)) {
                        PostDetailActivity.selectedPost = model as PostModel
                        val activity = Intent(this@FeedActivity, PostDetailActivity::class.java)
                        startActivity(activity)
                    }
                }
            })

        binding.homeRecyclerView.adapter = allFeedRecyclerviewAdapter

        getUserProfile()

        getAllPosts()
    }

    private fun showLoading(){
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun hideLoading(){
        binding.progressBar.visibility = View.GONE
    }

    private fun handleCommentEvent(postModel: PostModel) {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.getLayoutInflater()
        val dialogView = inflater.inflate(R.layout.custom_comment_dialog, null)
        dialogBuilder.setView(dialogView)

        val cancel = dialogView.findViewById<ImageView>(R.id.cancel)
        val comment = dialogView.findViewById<EditText>(R.id.comment)
        val shareCommentBtn = dialogView.findViewById<Button>(R.id.shareCommentBtn)

        val alertDialog = dialogBuilder.create()


        shareCommentBtn.setOnClickListener {
            val message = comment.text.trim().toString()
            if (message.isNullOrEmpty()) {
                showToastShort("No Comment Written")
            } else {
                val commentList = postModel.postComment
                commentList.add(
                    CommentModel(
                        userId = currentUserModel.user_id!!,
                        username = currentUserModel.username!!,
                        postId = postModel.postId,
                        message = message,
                        datetime = Calendar.getInstance().timeInMillis.toString()
                    )
                )
                val mEventModel = postModel.copy(postComment = commentList)
                updatePost(mEventModel) {
                    alertDialog.dismiss()
                    showToastShort("Your Comment Shared")
                    getAllPosts()
                }
            }
        }

        cancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun handleLikeEvent(postModel: PostModel) {

        val userId = auth.currentUser?.uid!!

        var likeList = postModel.postLikedBy

        var foundedUser = likeList.findLast { it.userId == userId }

        if (foundedUser != null) {
            // remove
            likeList.remove(foundedUser)
        } else {
            likeList.add(
                LikeModel(
                    userId = userId,
                    username = currentUserModel.username,
                    postId = postModel.postId,
                    datetime = Calendar.getInstance().timeInMillis.toString()
                )
            )
        }

        val mEventModel = postModel.copy(postLikedBy = likeList)

        updatePost(mEventModel) {
            getAllPosts()
        }
    }

    private fun getAllPosts() {

        showLoading()

        mFirestore.collection("AllPosts").get()
            .addOnSuccessListener {
                val list: ArrayList<PostModel> = ArrayList()
                for (document in it) {
                    document.toObject<PostModel>().let { it1 -> list.add(it1) }
                }

                allPostsList = ArrayList()
                allPostsList.addAll(list)

                allFeedRecyclerviewAdapter.setData(allPostsList)

                hideLoading()

            }.addOnFailureListener {

            }
    }

    fun getUserProfile() {
        auth.currentUser?.uid?.let { userId ->
            mFirestore.collection("users").document(userId).get().addOnSuccessListener {
                currentUserModel = it.toObject<UserModel>()!!
            }.addOnFailureListener {
            }.addOnCanceledListener {
            }
        }
    }

    fun updatePost(
        postModel: PostModel, callback: (String) -> Unit
    ) {
        showLoading()
        mFirestore.collection("AllPosts").document(postModel.postId).update(postModel.toMap())
            .addOnSuccessListener {
                callback("Post Updated")
                hideLoading()
            }.addOnFailureListener {
                callback("Post Update Failed")
            }.addOnCanceledListener {
                callback("Post Update Cancelled")
            }

    }

    fun showToastShort(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut();
                val LoginActivity = Intent(this, LoginActivity::class.java)
                LoginActivity.putExtra("keyIdentifier", "value")
                startActivity(LoginActivity)
                this.finish()
                showToastShort("Logout")
                true
            }
            else -> true
        }
    }

}