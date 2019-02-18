package com.example.newbiechen.ireader.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = CollBook::class, parentColumns = ["bookId"], childColumns = ["bookId"], onDelete = CASCADE)],
        indices = [Index(value = ["bookId"])])
data class BookChapter(
        @PrimaryKey
        var id: String = "",
        var bookId: String = "",
        var link: String = "",
        var title: String = "",
        var taskName: String = "", //所属的下载任务
        var unreadable: Boolean = false,
        var start: Long = 0L, //在书籍文件中的起始位置
        var end: Long = 0L //在书籍文件中的终止位置
)