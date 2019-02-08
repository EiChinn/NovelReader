package com.example.newbiechen.ireader.utils

import androidx.annotation.StringRes
import com.example.newbiechen.ireader.App
import java.text.SimpleDateFormat
import java.util.*

object StringUtils {
    private const val HOUR_OF_DAY = 24
    private const val DAY_OF_YESTERDAY = 2
    private const val TIME_UNIT = 60

    @JvmStatic
    fun dateConvert(time: Long, pattern: String): String {
        return SimpleDateFormat(pattern, Locale.CHINA).format(Date(time))
    }

    /**
     * 将日期转换成昨天、今天、明天
     */
    @JvmStatic
    fun dateConvert(source: String, pattern: String): String {
        val format = SimpleDateFormat(pattern)
        val calendar = Calendar.getInstance()
        try {
            val date = format.parse(source)
            val curTime = calendar.timeInMillis
            //将MISC 转换成 sec
            val difSec = Math.abs((curTime - date.time) / 1000)
            val difMin = difSec / 60
            val difHour = difMin / 60
            val difDate = difHour / 24
            val oldHour = calendar.get(Calendar.HOUR)
            //如果没有时间
            if (oldHour == 0) {
                //比日期:昨天今天和明天
                return when {
                    difDate == 0L -> "今天"
                    difDate < DAY_OF_YESTERDAY -> "昨天"
                    else -> SimpleDateFormat("yyyy-MM-dd").format(date)
                }

            }

            return when {
                difSec < TIME_UNIT -> "${difSec}秒前"
                difMin < TIME_UNIT -> "${difMin}分钟前"
                difHour < HOUR_OF_DAY -> "${difHour}小时前"
                difDate < DAY_OF_YESTERDAY -> "昨天"
                else -> SimpleDateFormat("yyyy-MM-dd").format(date)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""

    }

    @JvmStatic
    fun getString(@StringRes id: Int): String {
        return App.getInstance().resources.getString(id)
    }

    @JvmStatic
    fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
        return App.getInstance().resources.getString(id, formatArgs)
    }

    /**
     * 将文本中的半角字符，转换成全角字符
     * @param input
     * @return
     */
    @JvmStatic
    fun halfToFull(input: String): String {
        val c = input.toCharArray()
        for (i in 0 until c.size) {
            if (c[i].toInt() == 32) { // 半角空格
                c[i] = 12288 as Char
            }

            if (c[i].toInt() in 33..126)
            //其他符号都转换为全角
                c[i] = (c[i].toInt() + 65248).toChar()
        }
        return String(c)
    }
}

fun main() {
    for (i in 0..5) {
        print(i)
    }
}