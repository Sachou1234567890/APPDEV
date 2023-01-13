package com.example.rsdev.data

data class PostModel(
    val postId: String = "",
    val postByUserId: String = "",
    val postCreatedDate: String = "",
    val postImageUrl: String = "",
    val postTitle: String = "",
    val postDescription: String = "",
    val postLikedBy: ArrayList<LikeModel> = arrayListOf(),
    val postComment: ArrayList<CommentModel> = arrayListOf(),
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "postId" to postId,
            "postByUserId" to postByUserId,
            "postCreatedDate" to postCreatedDate,
            "postImageUrl" to postImageUrl,
            "postTitle" to postTitle,
            "postDescription" to postDescription,
            "postLikedBy" to postLikedBy,
            "postComment" to postComment,
        )
    }
}

data class LikeModel(
    val userId: String = "",
    val username: String = "",
    val postId: String = "",
    val datetime: String = ""
)

data class CommentModel(
    val userId: String = "",
    val username: String = "",
    val postId: String = "",
    val message: String = "",
    val datetime: String = ""
)