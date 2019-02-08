package com.example.newbiechen.ireader.model.bean.packages

import com.example.newbiechen.ireader.model.bean.BaseBean
import com.example.newbiechen.ireader.model.bean.BillBookBean
import com.google.gson.annotations.SerializedName

class BillBookPackage(ok: Boolean) : BaseBean(ok) {
    var ranking: RankingBean? = null
    /**
     * _id : 564d820bc319238a644fb408
     * updated : 2015-11-20T10:06:08.571Z
     * title : 追书最热榜月榜男
     * tag : zhuishuMonthHotMale
     * cover : /ranking-cover/144792013856420
     * __v : 1
     * created : 2017-05-08T10:25:33.192Z
     * isSub : true
     * collapse : false
     * new : true
     * gender : male
     * priority : 1000
     * books : [
     *              {
     *                  "_id":"5642be60f1b24c7a7468c5d7",
     *                  "title":"逆鳞",
     *                  "author":"柳下挥",
     *                  "shortIntro":"天生废材，遭遇龙神附体。继承了神龙的意念和能力，生鳞幻爪、御水龙息、行云降雨，肉身无敌。 在这个人人都想屠龙的时代，李牧羊一直生活的很有压力。",
     *                  "cover":"/agent/http://static.zongheng.com/upload/cover/2015/11/1447211572067.jpg"
     *              }
     *          ]
     **/
    data class RankingBean(
            val _id: String,
            val updated: String,
            val title: String,
            val tag: String,
            val cover: String,
            val __v: Int,
            val created: String,
            val isSub: Boolean,
            val collapse: String,
            @SerializedName("new")
            val newX: Boolean,
            val gender: String,
            val priority: Int,
            val id: String,
            val total: String,
            val books: List<BillBookBean>
            )
}