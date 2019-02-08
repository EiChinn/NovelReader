package com.example.newbiechen.ireader.utils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

object MD5Utils {
    private fun strToMd5By32(str: String): String? {
        var reStr: String? = null
        try {
            val md5 = MessageDigest.getInstance("MD5")
            val bytes = md5.digest(str.toByteArray())
            val stringBuffer = StringBuffer()
            for (b in bytes) {
                val bt = (b and 0xff.toByte()).toInt()
                if (bt < 16) {
                    stringBuffer.append(0)
                }
                stringBuffer.append(Integer.toHexString(bt))
            }
            reStr = stringBuffer.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return reStr

    }

    @JvmStatic
    fun strToMd5By16(str: String): String {
        return strToMd5By32(str)?.substring(8, 24) ?: str
    }
}