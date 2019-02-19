package com.example.newbiechen.ireader.model.bean


/**
 * Created by newbiechen on 17-4-20.
 */
class BookCommentBean {
    /**
     * _id : 58f805798c8c193a414c6853
     * title : 女朋友生气的奇葩理由我知道这里是女生区可我觉得好玩还是很想转
     * author : {"_id":"553136ba70feaa764a096f6f","avatar":"/avatar/26/eb/26ebf8ede76d7f52cd377960bd66383b","nickname":"九歌","activityAvatar":"","type":"normal","lv":8,"gender":"female"}
     * type : normal
     * likeCount : 6
     * block : girl
     * haveImage : false
     * state : normal
     * updated : 2017-04-20T05:34:17.931Z
     * created : 2017-04-20T00:48:57.085Z
     * commentCount : 35
     * voteCount : 0
     */
    var _id: String = ""
    var authorId: String? = null
    var title: String? = null
    val author: AuthorBean = AuthorBean()
    var type: String? = null
    var likeCount: Int = 0
    var block: String? = null
    var haveImage: Boolean = false
    var state: String? = null
    var updated: String = ""
    var created: String? = null
    var commentCount: Int = 0
    var voteCount: Int = 0


}