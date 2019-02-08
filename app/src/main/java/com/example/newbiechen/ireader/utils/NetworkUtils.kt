package com.example.newbiechen.ireader.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.example.newbiechen.ireader.App

object NetworkUtils {
    private fun getNetworkInfo(): NetworkInfo {
        val cm = App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }

    @JvmStatic
    fun isAvailable(): Boolean {
        val info = getNetworkInfo()
        return info != null && info.isAvailable
    }
}