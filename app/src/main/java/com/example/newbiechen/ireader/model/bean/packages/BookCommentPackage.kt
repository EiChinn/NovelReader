package com.example.newbiechen.ireader.model.bean.packages

import com.example.newbiechen.ireader.model.bean.BaseBean
import com.example.newbiechen.ireader.model.bean.BookCommentBean

class BookCommentPackage(ok: Boolean) : BaseBean(ok) {
    var posts: List<BookCommentBean>? = null
}