package com.example.socialtalk.ReturnedData

data class ChatMessage(
    val message: String? = "",
    val userID: String? = "",
    val timeStamp: Long? = 0
)