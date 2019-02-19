package com.example.newbiechen.ireader.model.bean

/**
 * Created by newbiechen on 17-4-20.
 * 作者
 */
class AuthorBean {
    /**
     * _id : 553136ba70feaa764a096f6f
     * avatar : /avatar/26/eb/26ebf8ede76d7f52cd377960bd66383b
     * nickname : 九歌
     * activityAvatar :
     * type : normal
     * lv : 8
     * gender : female
     */
    var _id: String? = null

    var avatar: String? = null
    var nickname: String? = null
    var activityAvatar: String? = null
    var type: String? = null
    var lv: Int = 0
    var gender: String? = null

    override fun toString(): String {
        return "AuthorBean{" +
                "_id='" + _id + '\''.toString() +
                ", avatar='" + avatar + '\''.toString() +
                ", nickname='" + nickname + '\''.toString() +
                ", activityAvatar='" + activityAvatar + '\''.toString() +
                ", type='" + type + '\''.toString() +
                ", lv=" + lv +
                ", gender='" + gender + '\''.toString() +
                '}'.toString()
    }
}