package com.example.newbiechen.ireader.model.bean.packages

import com.example.newbiechen.ireader.model.bean.TagBookBean

data class TagSearchPackage(
        val ok: Boolean,
        val books: List<TagBookBean>
)