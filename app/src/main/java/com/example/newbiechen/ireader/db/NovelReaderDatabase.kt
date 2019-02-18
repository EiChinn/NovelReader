package com.example.newbiechen.ireader.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newbiechen.ireader.db.dao.BookChapterDao
import com.example.newbiechen.ireader.db.dao.CollBookDao
import com.example.newbiechen.ireader.db.entity.BookChapter
import com.example.newbiechen.ireader.db.entity.CollBook

@Database(entities = [CollBook::class, BookChapter::class], version = 1)
abstract class NovelReaderDatabase : RoomDatabase() {
    abstract fun collBookDao(): CollBookDao
    abstract fun bookChapterDao(): BookChapterDao
}