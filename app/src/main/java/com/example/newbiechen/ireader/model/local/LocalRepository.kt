package com.example.newbiechen.ireader.model.local

import com.example.newbiechen.ireader.model.bean.*
import com.example.newbiechen.ireader.model.bean.packages.BillboardPackage
import com.example.newbiechen.ireader.model.bean.packages.BookSortPackage
import com.example.newbiechen.ireader.model.flag.BookSort
import com.example.newbiechen.ireader.model.gen.*
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.LogUtils
import com.example.newbiechen.ireader.utils.SharedPreUtils
import com.google.gson.Gson
import io.reactivex.Single
import org.greenrobot.greendao.Property
import org.greenrobot.greendao.query.Join
import org.greenrobot.greendao.query.QueryBuilder
import java.util.*

/**
 * Created by newbiechen on 17-4-26.
 */

class LocalRepository private constructor() : SaveDbHelper, GetDbHelper, DeleteDbHelper {
    private val mSession: DaoSession = DaoDbHelper.instance!!.session

    /*************************************数据存储 */
    /**
     * 存储BookComment
     * @param beans
     */
    override fun saveBookComments(beans: List<BookCommentBean>) {
        //存储Author,为了保证高效性，所以首先转换成List进行统一存储。
        val authorBeans = ArrayList<AuthorBean>(beans.size)
        for (i in beans.indices) {
            val commentBean = beans[i]
            authorBeans.add(commentBean.authorBean)
        }
        saveAuthors(authorBeans)
        //
        mSession.bookCommentBeanDao
                .insertOrReplaceInTx(beans)
    }

    override fun saveBookHelps(beans: List<BookHelpsBean>) {
        mSession.startAsyncSession()
                .runInTx {
                    //存储Author,为了保证高效性，所以首先转换成List进行统一存储。
                    val authorBeans = ArrayList<AuthorBean>(beans.size)
                    for (helpsBean in beans) {
                        authorBeans.add(helpsBean.authorBean)
                    }
                    saveAuthors(authorBeans)

                    mSession.bookHelpsBeanDao
                            .insertOrReplaceInTx(beans)
                }

    }

    override fun saveBookReviews(beans: List<BookReviewBean>) {
        mSession.startAsyncSession()
                .runInTx {
                    //数据转换
                    val bookBeans = ArrayList<ReviewBookBean>(beans.size)
                    val helpfulBeans = ArrayList<BookHelpfulBean>(beans.size)
                    for (reviewBean in beans) {
                        bookBeans.add(reviewBean.bookBean)
                        helpfulBeans.add(reviewBean.helpfulBean)
                    }
                    saveBookHelpfuls(helpfulBeans)
                    saveBooks(bookBeans)
                    //存储BookReview
                    mSession.bookReviewBeanDao
                            .insertOrReplaceInTx(beans)
                }
    }

    override fun saveBooks(beans: List<ReviewBookBean>) {
        mSession.reviewBookBeanDao
                .insertOrReplaceInTx(beans)
    }

    override fun saveAuthors(beans: List<AuthorBean>) {
        mSession.authorBeanDao
                .insertOrReplaceInTx(beans)
    }

    override fun saveBookHelpfuls(beans: List<BookHelpfulBean>) {
        mSession.bookHelpfulBeanDao
                .insertOrReplaceInTx(beans)
    }

    override fun saveBookSortPackage(bean: BookSortPackage) {
        val json = Gson().toJson(bean)
        SharedPreUtils.putString(Constant.SHARED_SAVE_BOOK_SORT, json)
    }

    override fun saveBillboardPackage(bean: BillboardPackage) {
        val json = Gson().toJson(bean)
        SharedPreUtils.putString(Constant.SHARED_SAVE_BILLBOARD, json)
    }

    override fun saveDownloadTask(bean: DownloadTaskBean) {
        BookRepository.instance!!.saveBookChaptersWithAsync(bean.bookChapters)
        mSession.downloadTaskBeanDao
                .insertOrReplace(bean)
    }

    /***************************************read data */

    /**
     * 获取数据
     * @param block
     * @param sort
     * @param distillate
     * @param start
     * @param limited
     * @return
     */
    override fun getBookComments(block: String, sort: String, start: Int, limited: Int, distillate: String): Single<List<BookCommentBean>> {

        val queryBuilder = mSession.bookCommentBeanDao
                .queryBuilder()
                .where(BookCommentBeanDao.Properties.Block.eq(block),
                        BookCommentBeanDao.Properties.State.eq(distillate))
                .offset(start)
                .limit(limited)

        queryOrderBy(queryBuilder, BookCommentBeanDao::class.java, sort)
        return queryToRx(queryBuilder)
    }

    /**
     *
     * @param sort
     * @param start
     * @param limited
     * @param distillate
     * @return
     */
    override fun getBookHelps(sort: String, start: Int, limited: Int, distillate: String): Single<List<BookHelpsBean>> {
        val queryBuilder = mSession.bookHelpsBeanDao
                .queryBuilder()
                .where(BookHelpsBeanDao.Properties.State.eq(distillate))
                .offset(start)
                .limit(limited)


        queryOrderBy(queryBuilder, BookHelpsBean::class.java, sort)
        return queryToRx(queryBuilder)
    }

    override fun getBookReviews(sort: String, bookType: String, start: Int, limited: Int, distillate: String): Single<List<BookReviewBean>> {
        val queryBuilder = mSession.bookReviewBeanDao
                .queryBuilder()
                .where(BookReviewBeanDao.Properties.State.eq(distillate))
                .limit(limited)
                .offset(start)
        //多表关联
        val bookJoin = queryBuilder.join(BookReviewBeanDao.Properties.BookId, ReviewBookBean::class.java)
                .where(ReviewBookBeanDao.Properties.Type.eq(bookType))

        queryBuilder.join(bookJoin as Join<*, BookReviewBean>, BookReviewBeanDao.Properties._id,
                BookHelpfulBean::class.java, BookHelpsBeanDao.Properties._id)

        //排序
        if (sort == BookSort.HELPFUL.dbName) {
            queryBuilder.orderDesc(BookHelpfulBeanDao.Properties.Yes)
        } else {
            queryOrderBy(queryBuilder, BookReviewBeanDao::class.java, sort)
        }

        return queryToRx(queryBuilder)
    }

    override fun getBookSortPackage(): BookSortPackage? {
        val json = SharedPreUtils.getString(Constant.SHARED_SAVE_BOOK_SORT)
        return if (json == null) {
            null
        } else {
            Gson().fromJson(json, BookSortPackage::class.java)
        }
    }

    override fun getBillboardPackage(): BillboardPackage? {
        val json = SharedPreUtils.getString(Constant.SHARED_SAVE_BILLBOARD)
        return if (json == null) {
            null
        } else {
            Gson().fromJson(json, BillboardPackage::class.java)
        }
    }

    override fun getAuthor(id: String): AuthorBean {
        return mSession.authorBeanDao
                .queryBuilder()
                .where(AuthorBeanDao.Properties._id.eq(id))
                .unique()
    }

    override fun getReviewBook(id: String): ReviewBookBean {
        return mSession.reviewBookBeanDao
                .queryBuilder()
                .where(ReviewBookBeanDao.Properties._id.eq(id))
                .unique()
    }

    override fun getBookHelpful(id: String): BookHelpfulBean {
        return mSession.bookHelpfulBeanDao
                .queryBuilder()
                .where(BookHelpfulBeanDao.Properties._id.eq(id))
                .unique()
    }

    override fun getDownloadTaskList(): MutableList<DownloadTaskBean> {
        return mSession.downloadTaskBeanDao
                .loadAll()
    }

    private fun <T> queryOrderBy(queryBuilder: QueryBuilder<*>, daoCls: Class<T>, orderBy: String) {
        //获取Dao中的Properties
        val innerCls = daoCls.classes
        var propertiesCls: Class<*>? = null
        for (cls in innerCls) {
            if (cls.simpleName == "Properties") {
                propertiesCls = cls
                break
            }
        }
        //如果不存在则返回
        if (propertiesCls == null) return

        //这里没有进行异常处理有点小问题
        try {
            val field = propertiesCls.getField(orderBy)
            val property = field.get(propertiesCls) as Property
            queryBuilder.orderDesc(property)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
            LogUtils.e(e.toString())
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            LogUtils.e(e.toString())
        }

    }

    private fun <T> queryToRx(builder: QueryBuilder<T>): Single<List<T>> {
        return Single.create { e ->
            var data: List<T>? = builder.list()
            if (data == null) {
                data = ArrayList(1)
            }
            e.onSuccess(data)
        }
    }

    /** */
    /**
     * 处理多出来的数据,一般在退出程序的时候进行
     */
    fun disposeOverflowData() {
        //固定存储100条数据，剩下的数据都删除
        mSession.startAsyncSession()
                .runInTx {
                    disposeBookComment()
                    disposeBookHelps()
                    disposeBookReviews()
                }
    }

    private fun disposeBookComment() {
        //第一种方法:使用get获取对象之后再依次删除。
        //第二种方法:直接调用Sqlite语句进行删除
        val commentBeanDao = mSession.bookCommentBeanDao
        val count = commentBeanDao.count().toInt()
        val bookCommentBeans = commentBeanDao
                .queryBuilder()
                .orderDesc(BookCommentBeanDao.Properties.Updated)
                .limit(count)
                .offset(100)
                .list()
        //存储Author,为了保证高效性，所以首先转换成List进行统一存储。
        val authorBeans = ArrayList<AuthorBean>(bookCommentBeans.size)
        for (commentBean in bookCommentBeans) {
            authorBeans.add(commentBean.authorBean)
        }
        deleteAuthors(authorBeans)
        deleteBookComments(bookCommentBeans)
    }

    private fun disposeBookHelps() {
        val helpfulDao = mSession.bookHelpsBeanDao
        val count = helpfulDao.count().toInt()
        val helpsBeans = helpfulDao
                .queryBuilder()
                .orderDesc(BookHelpsBeanDao.Properties.Updated)
                .limit(count)
                .offset(100)
                .list()
        val authorBeans = ArrayList<AuthorBean>(helpsBeans.size)
        for (commentBean in helpsBeans) {
            authorBeans.add(commentBean.authorBean)
        }
        deleteAuthors(authorBeans)
        deleteBookHelps(helpsBeans)
    }

    private fun disposeBookReviews() {
        val reviewDao = mSession.bookReviewBeanDao
        val count = reviewDao.count().toInt()
        val reviewBeans = reviewDao
                .queryBuilder()
                .orderDesc(BookHelpsBeanDao.Properties.Updated)
                .limit(count)
                .offset(100)
                .list()
        val bookBeans = ArrayList<ReviewBookBean>(reviewBeans.size)
        val helpfulBeans = ArrayList<BookHelpfulBean>(reviewBeans.size)
        for (reviewBean in reviewBeans) {
            bookBeans.add(reviewBean.bookBean)
            helpfulBeans.add(reviewBean.helpfulBean)
        }
        deleteBooks(bookBeans)
        deleteBookHelpful(helpfulBeans)
        deleteBookReviews(reviewBeans)
    }

    /************************************delete */
    override fun deleteBookComments(beans: List<BookCommentBean>) {
        mSession.bookCommentBeanDao
                .deleteInTx(beans)
    }

    override fun deleteBookReviews(beans: List<BookReviewBean>) {
        mSession.bookReviewBeanDao
                .deleteInTx(beans)
    }

    override fun deleteBookHelps(beans: List<BookHelpsBean>) {
        mSession.bookHelpsBeanDao
                .deleteInTx(beans)
    }

    override fun deleteAuthors(beans: List<AuthorBean>) {
        mSession.authorBeanDao
                .deleteInTx(beans)
    }

    override fun deleteBooks(beans: List<ReviewBookBean>) {
        mSession.reviewBookBeanDao
                .deleteInTx(beans)
    }

    override fun deleteBookHelpful(beans: List<BookHelpfulBean>) {
        mSession.bookHelpfulBeanDao
                .deleteInTx(beans)
    }

    override fun deleteAll() {
        //清空全部数据。
    }

    companion object {
        private val TAG = "LocalRepository"
        private val DISTILLATE_ALL = "normal"
        private val DISTILLATE_BOUTIQUES = "distillate"

        @Volatile
        private var sInstance: LocalRepository? = null

        val instance: LocalRepository
            get() {
                if (sInstance == null) {
                    synchronized(LocalRepository::class.java) {
                        if (sInstance == null) {
                            sInstance = LocalRepository()
                        }
                    }
                }
                return sInstance!!
            }
    }
}
