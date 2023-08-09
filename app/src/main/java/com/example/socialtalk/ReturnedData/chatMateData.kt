package com.example.socialtalk.ReturnedData

import com.example.socialtalk.R

data class chatMateData(
    val profileImage: Int = R.drawable.person_outline,
    var profileImageUrl: String = "",
    val username: String? = "",
    val userid: String? = "",
    val userContact: String? = "",
    var lastMsg: String? = "",
    var chatTime: Long? = 0,
)