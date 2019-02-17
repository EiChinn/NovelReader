package com.example.newbiechen.ireader.model.local

import android.util.Log
import com.example.newbiechen.ireader.model.bean.BookChapterBean
import com.example.newbiechen.ireader.model.bean.BookRecordBean
import com.example.newbiechen.ireader.model.bean.ChapterInfoBean
import com.example.newbiechen.ireader.model.bean.CollBookBean
import com.example.newbiechen.ireader.model.gen.*
import com.example.newbiechen.ireader.utils.BookManager
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.FileUtils
import io.reactivex.Single
import java.io.*

/**
 * Created by newbiechen on 17-5-8.
 * 存储关于书籍内容的信息(CollBook(收藏书籍),BookChapter(书籍列表),ChapterInfo(书籍章节),BookRecord(记录))
 */

class BookRepository private constructor() {
    private val session: DaoSession = DaoDbHelper.instance!!
            .session
    private val mCollBookDao: CollBookBeanDao


    val collBooks: List<CollBookBean>
        get() = mCollBookDao
                .queryBuilder()
                .orderDesc(CollBookBeanDao.Properties.LastRead)
                .list()

    init {
        mCollBookDao = session.collBookBeanDao
    }

    //存储已收藏书籍
    fun saveCollBookWithAsync(bean: CollBookBean) {
        //启动异步存储
        session.startAsyncSession()
                .runInTx {
                    if (bean.bookChapters != null) {
                        // 存储BookChapterBean
                        session.bookChapterBeanDao
                                .insertOrReplaceInTx(bean.bookChapters)
                    }
                    //存储CollBook (确保先后顺序，否则出错)
                    mCollBookDao.insertOrReplace(bean)
                }
    }

    /**
     * 异步存储。
     * 同时保存BookChapter
     * @param beans
     */
    fun saveCollBooksWithAsync(beans: List<CollBookBean>) {
        session.startAsyncSession()
                .runInTx {
                    for (bean in beans) {
                        if (bean.bookChapters != null) {
                            //存储BookChapterBean(需要修改，如果存在id相同的则无视)
                            session.bookChapterBeanDao
                                    .insertOrReplaceInTx(bean.bookChapters)
                        }
                    }
                    //存储CollBook (确保先后顺序，否则出错)
                    mCollBookDao.insertOrReplaceInTx(beans)
                }
    }

    fun saveCollBook(bean: CollBookBean) {
        mCollBookDao.insertOrReplace(bean)
    }

    fun saveCollBooks(beans: List<CollBookBean>) {
        mCollBookDao.insertOrReplaceInTx(beans)
    }

    fun changeBookSource(collBookBean: CollBookBean) {
        // 更新数据库信息，主要是更新currentSourceId，currentSourceName这两个字段
        mCollBookDao.update(collBookBean)
        // 换源之后删除本地章节缓存
        deleteBookCache(collBookBean._id)
    }

    /**
     * 异步存储BookChapter
     * @param beans
     */
    fun saveBookChaptersWithAsync(beans: List<BookChapterBean>) {
        session.startAsyncSession()
                .runInTx {
                    // 清空旧目录
                    //存储BookChapterBean
                    session.bookChapterBeanDao
                            .insertOrReplaceInTx(beans)
                    Log.d(TAG, "saveBookChaptersWithAsync: " + "进行存储")
                }
    }

    /**
     * 异步存储BookChapter
     * @param beans
     */
    fun resetBookChaptersWithAsync(bookId: String, beans: List<BookChapterBean>) {
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

    fun saveBookRecord(bean: BookRecordBean) {
        session.bookRecordBeanDao
                .insertOrReplace(bean)
    }

    /*****************************get */
    fun getCollBook(bookId: String): CollBookBean {
        return mCollBookDao.queryBuilder()
                .where(CollBookBeanDao.Properties._id.eq(bookId))
                .unique()
    }


    //获取书籍列表
    fun getBookChaptersInRx(bookId: String): Single<List<BookChapterBean>> {
        return Single.create { e ->
            val beans = session
                    .bookChapterBeanDao
                    .queryBuilder()
                    .where(BookChapterBeanDao.Properties.BookId.eq(bookId))
                    .list()
            e.onSuccess(beans)
        }
    }

    //获取阅读记录
    fun getBookRecord(bookId: String): BookRecordBean {
        return session.bookRecordBeanDao
                .queryBuilder()
                .where(BookRecordBeanDao.Properties.BookId.eq(bookId))
                .unique()
    }

    //TODO:需要进行获取编码并转换的问题
    fun getChapterInfoBean(folderName: String, fileName: String): ChapterInfoBean? {
        val file = File(Constant.BOOK_CACHE_PATH + folderName
                + File.separator + fileName + FileUtils.SUFFIX_NB)
        if (!file.exists()) return null
        var str: String? = null
        val sb = StringBuilder()
        try {
            FileReader(file).use { reader ->
                BufferedReader(reader).use { br ->
                    str = br.readLine()
                    while (str != null) {
                        sb.append(str)
                        str = br.readLine()
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return ChapterInfoBean(fileName, sb.toString())
    }

    /** */

    /** */
    fun deleteCollBookInRx(bean: CollBookBean): Single<Void> {
        return Single.create { e ->
            //查看文本中是否存在删除的数据
            deleteBookCache(bean._id)
            //删除任务
            deleteDownloadTask(bean._id)
            //删除目录
            deleteBookChapter(bean._id)
            // 删除 book record
            deleteBookRecord(bean._id)
            //删除CollBook
            mCollBookDao.delete(bean)
            e.onSuccess(Void())
        }
    }

    //这个需要用rx，进行删除
    fun deleteBookChapter(bookId: String) {
        session.bookChapterBeanDao
                .queryBuilder()
                .where(BookChapterBeanDao.Properties.BookId.eq(bookId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities()
    }

    fun deleteCollBook(collBook: CollBookBean) {
        mCollBookDao.delete(collBook)
    }

    //删除书籍
    fun deleteBookCache(bookId: String) {
        FileUtils.deleteFile(Constant.BOOK_CACHE_PATH + bookId)
    }

    fun deleteBookRecord(id: String) {
        session.bookRecordBeanDao
                .queryBuilder()
                .where(BookRecordBeanDao.Properties.BookId.eq(id))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities()
    }

    //删除任务
    fun deleteDownloadTask(bookId: String) {
        session.downloadTaskBeanDao
                .queryBuilder()
                .where(DownloadTaskBeanDao.Properties.BookId.eq(bookId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities()
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
