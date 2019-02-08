package com.example.newbiechen.ireader.utils

import android.util.DisplayMetrics
import android.util.TypedValue
import com.example.newbiechen.ireader.App

object ScreenUtils {
    @JvmStatic
    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), getDisplayMetrics()).toInt()
    }

    @JvmStatic
    fun spToPx(sp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), getDisplayMetrics()).toInt()
    }

    /**
     * 获取导航栏的高度
     * @return
     */
    @JvmStatic
    fun getStatusBarHeight(): Int {
        val resources = App.getInstance().resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 获取虚拟按键的高度
     * @return
     */
    @JvmStatic
    fun getNavigationBarHeight(): Int {
        var navigationBarHeight = 0
        val resources = App.getInstance().resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (hasNavigationBar() && resourceId > 0) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        return navigationBarHeight


    }

    /**
     * 是否存在虚拟按键
     * @return
     */
    private fun hasNavigationBar(): Boolean {
        var hasNavigationBar = false
        val resources = App.getInstance().resources
        val resourceId = resources.getIdentifier("config_showNavigationBar", "bool", "android")
        if (resourceId > 0) {
            hasNavigationBar = resources.getBoolean(resourceId)
        } else {
            try {
                val systemPropertiesClass = Class.forName("android.os.SystemProperties")
                val m = systemPropertiesClass.getMethod("get", String::class.java)
                val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
                if ("1" == navBarOverride) {
                    hasNavigationBar = false
                } else if ("0" == navBarOverride) {
                    hasNavigationBar = true
                }
            } catch (e: Exception) {

            }
        }
        return hasNavigationBar


    }

    private fun getDisplayMetrics(): DisplayMetrics {
        return App.getInstance().resources.displayMetrics
    }
}