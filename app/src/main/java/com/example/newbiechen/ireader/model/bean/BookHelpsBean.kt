package com.example.newbiechen.ireader.model.bean

/**
 * Created by newbiechen on 17-4-20.
 */
class BookHelpsBean {
    /**
     * _id : 58f7590223c128231d6fc3ec
     * author : {"_id":"575558500082adf41f499536","avatar":"/avatar/19/40/1940310d1277e583b07369dce21ca701","nickname":"秋名山丶公子","activityAvatar":"/activities/20170120/1.jpg","type":"commentator","lv":8,"gender":"female"}
     * title : 【读书会-送给喜欢末世的你】末世流精选小说（第三期）
     * likeCount : 19
     * haveImage : false
     * state : normal
     * updated : 2017-04-20T05:45:19.976Z
     * created : 2017-04-19T12:33:06.285Z
     * commentCount : 46
     */
    var _id: String = ""
    var authorId: String? = null
    var author: AuthorBean = AuthorBean()
    var title: String? = null
    var likeCount: Int = 0
    var haveImage: Boolean = false
    var state: String? = null
    var updated: String? = null
    var created: String? = null
    var commentCount: Int = 0
}