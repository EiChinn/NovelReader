package com.example.newbiechen.ireader.model.bean

data class HelpsDetailBean(
        val _id: String,
        val type: String,
        val author: AuthorBean,
        val title: String,
        val content: String,
        val state: String,
        val updated: String,
        val created: String,
        val commentCount: Int
)