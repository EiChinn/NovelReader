package com.example.newbiechen.ireader.model.remote

import com.example.newbiechen.ireader.db.entity.BookChapter
import com.example.newbiechen.ireader.db.entity.CollBook
import com.example.newbiechen.ireader.model.bean.*
import com.example.newbiechen.ireader.model.bean.packages.*
import io.reactivex.Single
import retrofit2.Retrofit
import java.util.*

/**
 * Created by newbiechen on 17-4-20.
 */

class RemoteRepository private constructor() {
    private val mRetrofit: Retrofit
    private val mBookApi: BookApi

    /** */
    /**
     * 获取书籍的分类
     * @return
     */
    val bookSortPackage: Single<BookSortPackage>
        get() = mBookApi.bookSortPackage

    /**
     * 获取书籍的子分类
     * @return
     */
    val bookSubSortPackage: Single<BookSubSortPackage>
        get() = mBookApi.bookSubSortPackage

    /** */

    /**
     * 排行榜的类型
     * @return
     */
    val billboardPackage: Single<BillboardPackage>
        get() = mBookApi.billboardPackage

    /**
     * 获取书单的标签|类型
     * @return
     */
    val bookTags: Single<BookTagPackage>
        get() = mBookApi.bookTagPackage
    /********************************书籍搜索 */
    /**
     * 搜索热词
     * @return
     */
    val hotWords: Single<List<String>>
        get() = mBookApi.hotWordPackage
                .map { (_, hotWords1) -> hotWords1 }

    init {
        mRetrofit = RemoteHelper.instance
                .retrofit

        mBookApi = mRetrofit.create(BookApi::class.java)
    }

    fun getRecommendBooks(gender: String): Single<List<CollBook>> {
        return mBookApi.getRecommendBookPackage(gender)
                .map { (_, books) -> books }
    }

    fun getBookMixChapters(bookMixId: String): Single<List<BookChapter>> {
        return mBookApi.getBookMixChapterPackage(bookMixId, "chapters")
                .map { bean ->
                    if (bean.mixToc == null) {
                        ArrayList(1)
                    } else {
                        bean.mixToc!!.chapters
                    }
                }
    }

    fun getBookSourceChapters(bookSourceId: String): Single<List<BookChapter>> {
        return mBookApi.getBookSourceChapterPackage(bookSourceId, "chapters")
                .map { (_, _, _, _, _, chapters) -> chapters }
    }

    fun getBookSources(bookId: String): Single<List<BookSourcesBean>> {
        return mBookApi.getBookSources(bookId, "summary")
        //                .map(bean -> bean.getBookSources());
    }

    /**
     * 注意这里用的是同步请求
     * @param url
     * @return
     */
    fun getChapterInfo(url: String): Single<ChapterInfoBean> {
        return mBookApi.getChapterInfoPackage(url)
                .map { (_, chapter) -> chapter }
    }

    /** */


    fun getBookComment(block: String, sort: String, start: Int, limit: Int, distillate: String): Single<List<BookCommentBean>> {

        return mBookApi.getBookCommentList(block, "all", sort, "all", start.toString() + "", limit.toString() + "", distillate)
                .map { listBean -> listBean.posts }
    }

    fun getBookHelps(sort: String, start: Int, limit: Int, distillate: String): Single<List<BookHelpsBean>> {
        return mBookApi.getBookHelpList("all", sort, start.toString() + "", limit.toString() + "", distillate)
                .map { listBean -> listBean.helps }
    }

    fun getBookReviews(sort: String, bookType: String, start: Int, limited: Int, distillate: String): Single<List<BookReviewBean>> {
        return mBookApi.getBookReviewList("all", sort, bookType, start.toString() + "", limited.toString() + "", distillate)
                .map { (_, reviews) -> reviews }
    }

    fun getCommentDetail(detailId: String): Single<CommentDetailBean> {
        return mBookApi.getCommentDetailPackage(detailId)
                .map { (_, post) -> post }
    }

    fun getReviewDetail(detailId: String): Single<ReviewDetailBean> {
        return mBookApi.getReviewDetailPacakge(detailId)
                .map { (_, review) -> review }
    }

    fun getHelpsDetail(detailId: String): Single<HelpsDetailBean> {
        return mBookApi.getHelpsDetailPackage(detailId)
                .map { (_, help) -> help }
    }

    fun getBestComments(detailId: String): Single<List<CommentBean>> {
        return mBookApi.getBestCommentPackage(detailId)
                .map { (_, comments) -> comments }
    }

    /**
     * 获取的是 综合讨论区的 评论
     * @param detailId
     * @param start
     * @param limit
     * @return
     */
    fun getDetailComments(detailId: String, start: Int, limit: Int): Single<List<CommentBean>> {
        return mBookApi.getCommentPackage(detailId, start.toString() + "", limit.toString() + "")
                .map { (_, comments) -> comments }
    }

    /**
     * 获取的是 书评区和书荒区的 评论
     * @param detailId
     * @param start
     * @param limit
     * @return
     */
    fun getDetailBookComments(detailId: String, start: Int, limit: Int): Single<List<CommentBean>> {
        return mBookApi.getBookCommentPackage(detailId, start.toString() + "", limit.toString() + "")
                .map { (_, comments) -> comments }
    }

    /**
     * 根据分类获取书籍列表
     * @param gender
     * @param type
     * @param major
     * @param minor
     * @param start
     * @param limit
     * @return
     */
    fun getSortBooks(gender: String, type: String, major: String, minor: String, start: Int, limit: Int): Single<List<SortBookBean>> {
        return mBookApi.getSortBookPackage(gender, type, major, minor, start, limit)
                .map { (_, _, books) -> books }
    }
    /**
     * 根据分类获取书籍列表
     * @param gender
     * @param type
     * @param major
     * @param minor
     * @param start
     * @param limit
     * @return
     */
    fun getSortBookPage(gender: String, type: String, major: String, minor: String, start: Int, limit: Int): Single<SortBookPackage> {
        return mBookApi.getSortBookPackage(gender, type, major, minor, start, limit)
    }

    /**
     * 排行榜的书籍
     * @param billId
     * @return
     */
    fun getBillBooks(billId: String): Single<List<BillBookBean>> {
        return mBookApi.getBillBookPackage(billId)
                .map { bean -> bean.ranking!!.books }
    }

    /***********************************书单 */

    /**
     * 获取书单列表
     * @param duration
     * @param sort
     * @param start
     * @param limit
     * @param tag
     * @param gender
     * @return
     */
    fun getBookLists(duration: String, sort: String,
                     start: Int, limit: Int,
                     tag: String, gender: String): Single<List<BookListBean>> {
        return mBookApi.getBookListPackage(duration, sort, start.toString() + "", limit.toString() + "", tag, gender)
                .map { bean -> bean.bookLists }
    }

    /**
     * 获取书单的详情
     * @param detailId
     * @return
     */
    fun getBookListDetail(detailId: String): Single<BookListDetailBean> {
        return mBookApi.getBookListDetailPackage(detailId)
                .map { bean -> bean.bookList }
    }

    /***************************************书籍详情 */
    fun getBookDetail(bookId: String): Single<BookDetailBean> {
        return mBookApi.getBookDetail(bookId)
    }

    fun getHotComments(bookId: String): Single<List<HotCommentBean>> {
        return mBookApi.getHotCommnentPackage(bookId)
                .map { (_, reviews) -> reviews }
    }

    fun getRecommendBookList(bookId: String, limit: Int): Single<List<BookListBean>> {
        return mBookApi.getRecommendBookListPackage(bookId, limit.toString() + "")
                .map { (_, booklists) -> booklists }
    }

    /**
     * 搜索关键字
     * @param query
     * @return
     */
    fun getKeyWords(query: String): Single<List<String>> {
        return mBookApi.getKeyWordPacakge(query)
                .map { (_, keywords) -> keywords }

    }

    /**
     * 查询书籍
     * @param query:书名|作者名
     * @return
     */
    fun getSearchBooks(query: String): Single<List<SearchBooksBean>> {
        return mBookApi.getSearchBookPackage(query)
                .map { (_, books) -> books }
    }

    companion object {

        private var sInstance: RemoteRepository? = null

        val instance: RemoteRepository
            get() {
                if (sInstance == null) {
                    synchronized(RemoteHelper::class.java) {
                        if (sInstance == null) {
                            sInstance = RemoteRepository()
                        }
                    }
                }
                return sInstance!!
            }
    }
}
