package com.example.rsdev.data

data class UserModel(
    val username: String = "",
    val firstname:String = "",
    val lastname: String = "",
    val dob: String = "",
    val email: String = "",
    val user_id: String = "",
    val friends: List<String>? = null,
    val userImageUrl: String = ""
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "username" to username,
            "firstname" to firstname,
            "lastname" to lastname,
            "dob" to dob,
            "email" to email,
            "user_id" to user_id,
            "friends" to friends,
            "userImageUrl" to userImageUrl
        )
    }
}
