package com.example.newbiechen.ireader.model.bean

/**
 * Created by newbiechen on 17-4-21.
 * 书籍类别讨论
 */
class BookReviewBean {
    /**
     * _id : 58f8f3efedaa9fe3624a87bb
     * title : 为你写一个中肯的书评，我的访客
     * book : {"_id":"530f3912651881e60d04deb3","cover":"/agent/http://img.17k.com/images/bookcover/2014/3769/18/753884-1399818238000.jpg","title":"我的26岁女房客","site":"zhuishuvip","type":"dsyn","latelyFollower":null,"retentionRatio":null}
     * helpful : {"total":1,"no":5,"yes":6}
     * likeCount : 0
     * haveImage : false
     * state : distillate
     * updated : 2017-04-21T08:20:15.991Z
     * created : 2017-04-20T17:46:23.366Z
     */
    var _id: String = ""
    //获取Book的外键
    var bookId: String? = null


    var title: String? = null
    var book: ReviewBookBean = ReviewBookBean()
    var helpful: BookHelpfulBean? = null
    var likeCount: Int = 0
    var haveImage: Boolean = false
    var state: String? = null
    var updated: String = ""
    var created: String? = null
}