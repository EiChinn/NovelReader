package com.example.newbiechen.ireader.model.bean.packages

import com.example.newbiechen.ireader.model.bean.BaseBean
import com.example.newbiechen.ireader.model.bean.BookChapterBean

class BookChapterPackage(ok: Boolean) : BaseBean(ok) {

    var mixToc: MixTocBean? = null
    /**
     * _id : 572072a2e3ee1dcc0accdb9a
     * book : 57206c3539a913ad65d35c7b
     * chaptersCount1 : 288
     * chaptersUpdated : 2017-05-09T10:02:34.705Z
     * chapters : [{"title":"第一章 他叫白小纯","link":"http://read.qidian.com/chapter/rJgN8tJ_cVdRGoWu-UQg7Q2/6jr-buLIUJSaGfXRMrUjdw2","unreadble":false},{"title":"第二章 火灶房","link":"http://read.qidian.com/chapter/rJgN8tJ_cVdRGoWu-UQg7Q2/wty0QJyYhjm2uJcMpdsVgA2","unreadble":false}
     */
    data class MixTocBean(
            val _id: String,
            val book: String,
            val chaptersCount1: Int,
            val chaptersUpdated: String,
            val updated: String,
            val chapters: List<BookChapterBean>
            )
}