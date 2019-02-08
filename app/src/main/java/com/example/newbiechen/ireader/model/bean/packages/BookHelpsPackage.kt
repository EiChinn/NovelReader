package com.example.newbiechen.ireader.model.bean.packages

import com.example.newbiechen.ireader.model.bean.BaseBean
import com.example.newbiechen.ireader.model.bean.BookHelpsBean

class BookHelpsPackage(ok: Boolean) : BaseBean(ok) {
    var helps: List<BookHelpsBean>? = null
}