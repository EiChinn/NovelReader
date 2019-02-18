package com.example.newbiechen.ireader.model.local

import androidx.room.Room
import com.example.newbiechen.ireader.App
import com.example.newbiechen.ireader.db.NovelReaderDatabase
import com.example.newbiechen.ireader.db.dao.BookChapterDao
import com.example.newbiechen.ireader.db.dao.CollBookDao
import com.example.newbiechen.ireader.db.entity.BookChapter
import com.example.newbiechen.ireader.db.entity.CollBook
import com.example.newbiechen.ireader.utils.BookManager
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.FileUtils
import io.reactivex.Single
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException

/**
 * Created by newbiechen on 17-5-8.
 * 存储关于书籍内容的信息(CollBook(收藏书籍),BookChapter(书籍列表),ChapterInfo(书籍章节),BookRecord(记录))
 */

class BookRepository private constructor() {
    private val db = Room.databaseBuilder(App.getInstance(),
            NovelReaderDatabase::class.java, "NovelReader")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    private val collBookDao: CollBookDao = db.collBookDao()
    private val bookChapterDao: BookChapterDao = db.bookChapterDao()

    fun getAllCollBooks(): List<CollBook> {
        return collBookDao.getAllCollBooks()
    }
    fun insertOrUpdateCollBooks(collBooks: List<CollBook>) {
        collBookDao.insertOrUpdateCollBooks(collBooks)
    }
    fun insertOrUpdateCollBook(collBook: CollBook) {
        collBookDao.insertOrUpdateCollBook(collBook)
    }



    fun changeBookSource(collBook: CollBook) {
        // 更新数据库信息，主要是更新currentSourceId，currentSourceName这两个字段
        insertOrUpdateCollBook(collBook)
        // 换源之后删除本地章节缓存
        deleteBookCache(collBook.bookId)
    }

    /**
     * 异步存储BookChapter
     * @param beans
     */
    fun saveBookChaptersWithAsync(beans: List<BookChapter>) {
        bookChapterDao.insertBookChapters(beans)
    }

    /**
     * 异步存储BookChapter
     * @param beans
     */
    fun resetBookChaptersWithAsync(bookId: String, beans: List<BookChapter>) {
        deleteBookChapter(bookId)
        saveBookChaptersWithAsync(beans)
    }

    /**
     * 存储章节
     * @param folderName
     * @param fileName
     * @param content
     */
    fun saveChapterInfo(folderName: String, fileName: String, content: String) {
        val file = BookManager.getBookFile(folderName, fileName)
        //获取流并存储
        try {
            BufferedWriter(FileWriter(file)).use { writer ->
                writer.write(content)
                writer.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /*****************************get */
    fun getCollBook(bookId: String): CollBook? {
        return collBookDao.queryCollBookById(bookId)
    }


    //获取书籍列表
    fun getBookChaptersInRx(bookId: String): Single<List<BookChapter>> {
        return Single.create { e ->
            val beans = getBookChapters(bookId)
            e.onSuccess(beans)
        }
    }
    //获取书籍列表
    fun getBookChapters(bookId: String): List<BookChapter> {
        return bookChapterDao.queryBookChaptersByBookId(bookId)
    }

    /** */

    /** */
    fun deleteCollBookInRx(bean: CollBook): Single<Void> {
        return Single.create { e ->
            //查看文本中是否存在删除的数据
            deleteBookCache(bean.bookId)
            //删除任务
            deleteDownloadTask(bean.bookId)
            //删除目录
            deleteBookChapter(bean.bookId)
            //删除CollBook
            collBookDao.deleteCollBook(bean)
            e.onSuccess(Void())
        }
    }

    private fun deleteBookChapter(bookId: String) {
        bookChapterDao.deleteBookChaptersByBookId(bookId)
    }

    fun deleteCollBook(collBook: CollBook) {
        collBookDao.deleteCollBook(collBook)
    }

    //删除书籍
    fun deleteBookCache(bookId: String) {
        FileUtils.deleteFile(Constant.BOOK_CACHE_PATH + bookId)
    }

    //删除任务
    fun deleteDownloadTask(bookId: String) {
        /*session.downloadTaskBeanDao
                .queryBuilder()
                .where(DownloadTaskBeanDao.Properties.BookId.eq(bookId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities()*/
    }

    companion object {
        private val TAG = "CollBookManager"
        @Volatile
        private var sInstance: BookRepository? = null

        val instance: BookRepository
            get() {
                if (sInstance == null) {
                    synchronized(BookRepository::class.java) {
                        if (sInstance == null) {
                            sInstance = BookRepository()
                        }
                    }
                }
                return sInstance!!
            }
    }
}
