package com.example.newbiechen.ireader.utils

import android.content.Context
import com.example.newbiechen.ireader.App

object SharedPreUtils {
    private const val SHARED_NAME = "IReader_pref"
    private val sharedReadable = App.getInstance().getSharedPreferences(SHARED_NAME, Context.MODE_MULTI_PROCESS)
    private val sharedWritable = sharedReadable.edit()

    @JvmStatic
    fun getString(key: String): String {
        return sharedReadable.getString(key, "")!!

    }

    @JvmStatic
    fun putString(key: String, value: String) {
        sharedWritable.putString(key, value)
        sharedWritable.commit()
    }

    @JvmStatic
    fun getInt(key: String, def: Int): Int {
        return sharedReadable.getInt(key, def)

    }

    @JvmStatic
    fun putInt(key: String, value: Int) {
        sharedWritable.putInt(key, value)
        sharedWritable.commit()
    }

    @JvmStatic
    fun getBoolean(key: String, def: Boolean): Boolean {
        return sharedReadable.getBoolean(key, def)

    }

    @JvmStatic
    fun putBoolean(key: String, value: Boolean) {
        sharedWritable.putBoolean(key, value)
        sharedWritable.commit()
    }


}

