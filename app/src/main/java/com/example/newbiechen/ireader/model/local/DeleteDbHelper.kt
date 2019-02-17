package com.example.newbiechen.ireader.model.local

import com.example.newbiechen.ireader.model.bean.*

/**
 * Created by newbiechen on 17-4-28.
 */

interface DeleteDbHelper {
    fun deleteBookComments(beans: List<BookCommentBean>)
    fun deleteBookReviews(beans: List<BookReviewBean>)
    fun deleteBookHelps(beans: List<BookHelpsBean>)
    fun deleteAuthors(beans: List<AuthorBean>)
    fun deleteBooks(beans: List<ReviewBookBean>)
    fun deleteBookHelpful(beans: List<BookHelpfulBean>)
    fun deleteAll()
}
