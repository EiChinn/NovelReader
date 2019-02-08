package com.example.newbiechen.ireader.utils

/**
 * 编码类型
 */
enum class Charset(val charset: String) {
    UTF8("UTF-8"),
    UTF16LE("UTF-16LE"),
    UTF16BE("UTF-16BE"),
    GBK("GBK");


    companion object {
        const val BLANK: Byte = 0x0a
    }
}
