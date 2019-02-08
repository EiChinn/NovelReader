package com.example.newbiechen.ireader.model.flag

enum class BookSort(private val typeName: String, private val netName: String, val dbName: String) : BookConvert {
    DEFAULT("默认排序", "updated", "Updated"),
    CREATED("最新发布", "created", "Created"),
    HELPFUL("最多推荐", "helpful", "LikeCount"),
    COMMENT_COUNT("最多评论", "comment-count", "CommentCount");

    override fun getTypeName() = typeName

    override fun getNetName() = netName

}