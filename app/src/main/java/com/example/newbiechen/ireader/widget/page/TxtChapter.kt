package com.example.newbiechen.ireader.widget.page

/**
 * Created by newbiechen on 17-7-1.
 */

class TxtChapter {

    //章节所属的小说(网络)
    var bookId: String = ""
    //章节的链接(网络)
    var link: String = ""

    //章节名(共用)
    var title: String = ""

    //章节内容在文章中的起始位置(本地)
    var start: Long = 0
    //章节内容在文章中的终止位置(本地)
    var end: Long = 0

    override fun toString(): String {
        return "TxtChapter{" +
                "title='" + title + '\''.toString() +
                ", start=" + start +
                ", end=" + end +
                '}'.toString()
    }
}
