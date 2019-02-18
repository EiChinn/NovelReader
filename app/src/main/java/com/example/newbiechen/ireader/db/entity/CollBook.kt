package com.example.newbiechen.ireader.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class CollBook : Serializable {
        @PrimaryKey
        var bookId: String = ""// 本地书籍中，path 的 md5 值作为本地书籍的 id
        var title: String = ""
        var author: String = ""
        var shortIntro: String = ""
        var cover: String = ""// 在本地书籍中，该字段作为本地文件的路径
        var hasCp: Boolean = false
        var latelyFollower: Int = 0
        var retentionRatio: Double = 0.0
        var updated: String = ""//最新更新日期
        var lastRead: String = "" //最新阅读日期
        var chaptersCount: Int = 0// 小说章节数， 需要注意的是：这个章节数是mix的章节数而不是源小说的章节数
        var lastChapter: String = ""
        var isUpdate: Boolean = true //是否更新或未阅读
        var isLocal: Boolean = false //是否是本地文件
        var currentSourceName: String =""
        var currentSourceId: String = ""
        var chapter: Int = 0 // 阅读到了第几章
        var pagePos: Int = 0 // 当前的页码
}