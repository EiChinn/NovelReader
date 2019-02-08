package com.example.newbiechen.ireader.model.bean

data class ReplyToBean(
        val _id: String,
        val floor: Int,
        val author: ReplyAuthorBean
)

data class ReplyAuthorBean(
        val _id: String,
        val nickname: String

)