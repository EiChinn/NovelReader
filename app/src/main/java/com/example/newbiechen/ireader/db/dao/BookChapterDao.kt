package com.example.newbiechen.ireader.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newbiechen.ireader.db.entity.BookChapter

@Dao
interface BookChapterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookChapters(bookChapters: List<BookChapter>)

    @Query("SELECT * FROM BookChapter WHERE bookId = :bookId")
    fun queryBookChaptersByBookId(bookId: String): List<BookChapter>

    @Query("DELETE FROM BookChapter WHERE bookId = :bookId")
    fun deleteBookChaptersByBookId(bookId: String)
}