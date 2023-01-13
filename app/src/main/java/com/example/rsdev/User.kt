package com.example.rsdev

data class User(
    var firstname: String? = null,
    var lastname: String? = null,
    var username: String? = null,
    var email: String? = null,
    val friends: List<String>? = null
)