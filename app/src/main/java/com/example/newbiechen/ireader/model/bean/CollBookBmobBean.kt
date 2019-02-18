package com.example.newbiechen.ireader.model.bean

import cn.bmob.v3.BmobObject
import com.example.newbiechen.ireader.db.entity.CollBook

class CollBookBmobBean : BmobObject() {
    var id: String = "" // 本地书籍中，path 的 md5 值作为本地书籍的 id
    var title: String = ""
    var author: String = ""
    var shortIntro: String = ""
    var cover: String = "" // 在本地书籍中，该字段作为本地文件的路径
    var hasCp: Boolean = false
    var latelyFollower: Int = 0
    var retentionRatio: Double = 0.0
    //最新更新日期
    var updated: String = ""
    //最新阅读日期
    var lastRead: String = ""
    // 小说章节数， 需要注意的是：这个章节数是mix的章节数而不是源小说的章节数
    var chaptersCount: Int = 0
    var lastChapter: String = ""
    //是否更新或未阅读
    var isUpdate = true
    //是否是本地文件
    var isLocal = false


    // 获取目录和章节时使用
    var currentSourceName: String = ""
    var currentSourceId: String = ""
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CollBookBmobBean

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}

object BmobDaoUtils {
    fun bmob2Dao(sourceObj: CollBookBmobBean): CollBook {
        val result = CollBook()
        result.bookId = sourceObj.id
        result.title = sourceObj.title
        result.author = sourceObj.author
        result.shortIntro = sourceObj.shortIntro
        result.cover = sourceObj.cover
        result.hasCp = sourceObj.hasCp
        result.latelyFollower = sourceObj.latelyFollower
        result.retentionRatio = sourceObj.retentionRatio
        result.updated = sourceObj.updated
        result.lastRead = sourceObj.lastRead
        result.chaptersCount = sourceObj.chaptersCount
        result.lastChapter = sourceObj.lastChapter
        result.isUpdate = sourceObj.isUpdate
        result.isLocal = sourceObj.isLocal
        result.currentSourceId = sourceObj.currentSourceId
        result.currentSourceName = sourceObj.currentSourceName
        return result
    }
    fun dao2Bmob(sourceObj: CollBook): CollBookBmobBean {
        val result = CollBookBmobBean()
        result.id = sourceObj.bookId
        result.title = sourceObj.title
        result.author = sourceObj.author
        result.shortIntro = sourceObj.shortIntro
        result.cover = sourceObj.cover
        result.hasCp = sourceObj.hasCp
        result.latelyFollower = sourceObj.latelyFollower
        result.retentionRatio = sourceObj.retentionRatio
        result.updated = sourceObj.updated
        result.lastRead = sourceObj.lastRead ?: ""
        result.chaptersCount = sourceObj.chaptersCount
        result.lastChapter = sourceObj.lastChapter
        result.isUpdate = sourceObj.isUpdate
        result.isLocal = sourceObj.isLocal
        result.currentSourceId = sourceObj.currentSourceId ?: ""
        result.currentSourceName = sourceObj.currentSourceName ?: ""
        return result
    }
}