package com.example.newbiechen.ireader.utils

import android.util.Log
import com.example.newbiechen.ireader.BuildConfig

object LogUtils {
    private const val LOG_SWITCH = true // // 日志文件总开关
    private const val LOG_TAG = "IReader" // // 日志文件总开关


    @JvmStatic
    fun e(msg: String) {
        e(LOG_TAG, msg)
    }


    @JvmStatic
    fun w(msg: String) {
        w(LOG_TAG, msg)
    }


    @JvmStatic
    fun d(msg: String) {
        d(LOG_TAG, msg)
    }

    @JvmStatic
    fun i(msg: String) {
        i(LOG_TAG, msg)
    }


    @JvmStatic
    fun v(msg: String) {
        v(LOG_TAG, msg)
    }
    @JvmStatic
    fun e(tag: String = LOG_TAG,  msg: String, tr: Throwable? = null) {
        log(tag, msg, tr, 'e')
    }


    @JvmStatic
    fun w(tag: String = LOG_TAG,  msg: String, tr: Throwable? = null) {
        log(tag, msg, tr, 'w')
    }

    @JvmStatic
    fun d(tag: String = LOG_TAG,  msg: String, tr: Throwable? = null) {
        log(tag, msg, tr, 'd')
    }


    @JvmStatic
    fun i(tag: String = LOG_TAG,  msg: String, tr: Throwable? = null) {
        log(tag, msg, tr, 'i')
    }


    @JvmStatic
    fun v(tag: String = LOG_TAG,  msg: String,tr: Throwable? = null) {
        log(tag, msg, tr, 'v')
    }

    private fun log(tag: String, msg: String, tr: Throwable?, level: Char) {
        if (BuildConfig.DEBUG && LOG_SWITCH) {
            when (level) {
                'e' -> Log.e(tag, createMessage(msg), tr)
                'w' -> Log.w(tag, createMessage(msg), tr)
                'd' -> Log.d(tag, createMessage(msg), tr)
                'i' -> Log.i(tag, createMessage(msg), tr)
                else -> Log.v(tag, createMessage(msg), tr)
            }
        }
    }
    private fun getFunctionName(): String? {
        val sts = Thread.currentThread().stackTrace
        for (i in 0 until sts.size) {
            if (sts[i].isNativeMethod) {
                continue
            }
            if (sts[i].className == Thread::class.java.name) {
                continue
            }
            if (sts[i].fileName == "LogUtils.java") {
                continue
            }
            return ("[" + Thread.currentThread().name + "("
                    + Thread.currentThread().id + "): " + sts[i].fileName
                    + ":" + sts[i].lineNumber + "]")
        }
        return null

    }

    private fun createMessage(msg: String): String {
        return getFunctionName()?.let { "$it - $msg" } ?: msg
    }
}