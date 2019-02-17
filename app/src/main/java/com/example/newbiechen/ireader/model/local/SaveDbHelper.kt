package com.example.newbiechen.ireader.model.local

import com.example.newbiechen.ireader.model.bean.*
import com.example.newbiechen.ireader.model.bean.packages.BillboardPackage
import com.example.newbiechen.ireader.model.bean.packages.BookSortPackage

/**
 * Created by newbiechen on 17-4-28.
 */

interface SaveDbHelper {
    fun saveBookComments(beans: List<BookCommentBean>)
    fun saveBookHelps(beans: List<BookHelpsBean>)
    fun saveBookReviews(beans: List<BookReviewBean>)
    fun saveAuthors(beans: List<AuthorBean>)
    fun saveBooks(beans: List<ReviewBookBean>)
    fun saveBookHelpfuls(beans: List<BookHelpfulBean>)

    fun saveBookSortPackage(bean: BookSortPackage)
    fun saveBillboardPackage(bean: BillboardPackage)
    /*************DownloadTask */
    fun saveDownloadTask(bean: DownloadTaskBean)
}
