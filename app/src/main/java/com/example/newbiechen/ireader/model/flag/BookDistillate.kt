package com.example.newbiechen.ireader.model.flag

enum class BookDistillate(private val typeName: String, private val netName: String, val dbName: String) : BookConvert {
    ALL("全部", "", "normal"),
    BOUTIQUES("精品", "true", "distillate");


    override fun getTypeName(): String = typeName

    override fun getNetName(): String = netName
}