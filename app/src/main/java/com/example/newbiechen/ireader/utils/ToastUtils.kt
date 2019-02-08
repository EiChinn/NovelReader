package com.example.newbiechen.ireader.utils

import android.widget.Toast
import com.example.newbiechen.ireader.App

object ToastUtils {
    @JvmStatic
    fun show(msg: String) {
        Toast.makeText(App.getInstance(), msg, Toast.LENGTH_SHORT).show()
    }
}