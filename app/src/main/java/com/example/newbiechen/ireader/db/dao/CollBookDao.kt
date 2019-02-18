package com.example.newbiechen.ireader.db.dao

import androidx.room.*
import com.example.newbiechen.ireader.db.entity.CollBook

@Dao
interface CollBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateCollBooks(collBooks: List<CollBook>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateCollBook(collBook: CollBook)

    @Query("SELECT * FROM CollBook WHERE bookId = :bookId")
    fun queryCollBookById(bookId: String): CollBook

    @Query("SELECT * FROM CollBook ORDER BY lastRead DESC")
    fun getAllCollBooks(): List<CollBook>

    @Delete
    fun deleteCollBook(collBook: CollBook)
}