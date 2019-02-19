package com.example.newbiechen.ireader.model.bean

import com.example.newbiechen.ireader.db.entity.CollBook

/**
 * Created by newbiechen on 17-5-4.
 */

class BookDetailBean {
    /**
     * _id : 525253d094336b3155000dd8       (Collect)
     * author : w风雪                       (Collect)
     * cover : /agent/http://image.cmfu.com/books/2797907/2797907.jpg  (Collect)
     * creater : iPhone 5 (GSM+CDMA)
     * longIntro : 一死今生了却凡尘！         (Collect)
     * 重生洪荒造化苍生！
     * 天道之下尽皆蝼蚁！
     * 唯有异数勘破万法！
     * 且看主角这个穿入洪荒世界的异数如何：
     * 造化福泽苍生
     * 道法纵横天地
     * 挣脱天道束缚
     * 一剑破空而去
     * 自此逍遥无束...
     * 书友群：209425550
     * title : 洪荒造化
     * cat : 洪荒封神
     * majorCate : 仙侠
     * minorCate : 洪荒封神
     * _le : false
     * allowMonthly : false
     * allowVoucher : true
     * allowBeanVoucher : false
     * hasCp : true              (Collect)
     * postCount : 121
     * latelyFollower : 1233      (Collect)
     * followerCount : 35
     * wordCount : 5947980
     * serializeWordCount : 4614
     * retentionRatio : 18.04     (Collect)
     * updated : 2016-04-03T13:48:05.907Z  (Collect)
     * isSerial : true
     * chaptersCount : 1294         (Collect)
     * lastChapter : 完本感言！     (Collect)
     * gender : ["male"]
     * tags : ["热血","洪荒封神","洪荒","架空","修炼","仙侠"]
     * donate : false
     * copyright : 阅文集团正版授权
     */

    var _id: String = ""
    var author: String = ""
    var cover: String = ""
    var creater: String? = null
    var longIntro: String = ""
    var title: String = ""
    var cat: String? = null
    var majorCate: String? = null
    var minorCate: String? = null
    var is_le: Boolean = false
    var isAllowMonthly: Boolean = false
    var isAllowVoucher: Boolean = false
    var isAllowBeanVoucher: Boolean = false
    var isHasCp: Boolean = false
    var postCount: Int = 0
    var latelyFollower: Int = 0
    var followerCount: Int = 0
    var wordCount: Int = 0
    var serializeWordCount: Int = 0
    var retentionRatio: String? = null
    var updated: String = ""
    var isIsSerial: Boolean = false
    var chaptersCount: Int = 0
    var lastChapter: String = ""
    var isDonate: Boolean = false
    var copyright: String? = null
    var gender: List<String>? = null
    var tags: List<String>? = null


    private var collBook: CollBook? = null

    fun getCollBook(): CollBook? {
        if (collBook == null) {
            collBook = createCollBookBean()
        }
        return collBook
    }

    private fun createCollBookBean(): CollBook {
        val bean = CollBook()
        bean.bookId = _id
        bean.title = title
        bean.author = author
        bean.shortIntro = longIntro
        bean.cover = cover
        bean.hasCp = isHasCp
        bean.latelyFollower = latelyFollower
        bean.retentionRatio = java.lang.Double.parseDouble(retentionRatio!!)
        bean.updated = updated
        bean.chaptersCount = chaptersCount
        bean.lastChapter = lastChapter
        return bean
    }
}
