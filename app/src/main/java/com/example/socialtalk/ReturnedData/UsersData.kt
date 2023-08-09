package com.example.socialtalk.ReturnedData

import com.example.socialtalk.R

data class UsersData(
    val profileImage: Int = R.drawable.person_outline,
    var username: String = "",
    val num: String = "",
    val userid: String = "",
    val profileImageUrl: String = "",
)