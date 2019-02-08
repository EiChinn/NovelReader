package com.example.newbiechen.ireader.model.bean.packages

import com.example.newbiechen.ireader.model.bean.SortBookBean

data class SortBookPackage(
        val ok: Boolean,
        val total: Int,
        val books: List<SortBookBean>
)