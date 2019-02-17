package com.example.newbiechen.ireader.model.local

import com.example.newbiechen.ireader.model.bean.*
import com.example.newbiechen.ireader.model.bean.packages.BillboardPackage
import com.example.newbiechen.ireader.model.bean.packages.BookSortPackage
import io.reactivex.Single

/**
 * Created by newbiechen on 17-4-28.
 */

interface GetDbHelper {
     fun getBookSortPackage(): BookSortPackage?
     fun getBillboardPackage(): BillboardPackage?

    fun getDownloadTaskList(): MutableList<DownloadTaskBean>

    fun getBookComments(block: String, sort: String, start: Int, limited: Int, distillate: String): Single<List<BookCommentBean>>
    fun getBookHelps(sort: String, start: Int, limited: Int, distillate: String): Single<List<BookHelpsBean>>
    fun getBookReviews(sort: String, bookType: String, start: Int, limited: Int, distillate: String): Single<List<BookReviewBean>>

    fun getAuthor(id: String): AuthorBean
    fun getReviewBook(id: String): ReviewBookBean
    fun getBookHelpful(id: String): BookHelpfulBean
}
