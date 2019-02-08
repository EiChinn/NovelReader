package com.example.newbiechen.ireader.model.bean

import java.util.*

data class BookSourcesBean(
        val _id: String,
        val source: String,
        val name: String,
        val link: String,
        val lastChapter: String,
        val isCharge: Boolean,
        val chaptersCount: Int,
        val updated: Date,
        val starting: Boolean,
        val host: String
)