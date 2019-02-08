package com.example.newbiechen.ireader.utils

import android.app.Activity
import android.view.View

/**
 * Created by newbiechen on 17-5-16.
 * 基于 Android 4.4
 *
 * 主要参数说明:
 *
 * SYSTEM_UI_FLAG_FULLSCREEN : 隐藏StatusBar
 * SYSTEM_UI_FLAG_HIDE_NAVIGATION : 隐藏NavigationBar
 * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN: 视图扩展到StatusBar的位置，并且StatusBar不消失。
 * 这里需要一些处理，一般是将StatusBar设置为全透明或者半透明。之后还需要使用fitSystemWindows=防止视图扩展到Status
 * Bar上面(会在StatusBar上加一层View，该View可被移动)
 * SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION: 视图扩展到NavigationBar的位置
 * SYSTEM_UI_FLAG_LAYOUT_STABLE:稳定效果
 * SYSTEM_UI_FLAG_IMMERSIVE_STICKY:保证点击任意位置不会退出
 *
 * 可设置特效说明:
 * 1. 全屏特效
 * 2. 全屏点击不退出特效
 * 3. 注意在19 <=sdk <=21 时候，必须通过Window设置透明栏
 */
object SystemBarUtils {
    private const val UNSTABLE_STATUS = View.SYSTEM_UI_FLAG_FULLSCREEN
    private const val UNSTABLE_NAV = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    private const val STABLE_STATUS = View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    private const val STABLE_NAV = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    private const val EXPAND_STATUS = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

    private fun setFlag(activity: Activity, flag: Int) {
        val decorView = activity.window.decorView
        val option = decorView.systemUiVisibility or flag
        decorView.systemUiVisibility = option

    }
    
    private fun clearFlag(activity: Activity, flag: Int) {
        val decorView = activity.window.decorView
        val option = decorView.systemUiVisibility and flag.inv()
        decorView.systemUiVisibility = option

    }

    @JvmStatic
    fun showUnStableStatusBar(activity: Activity) {
        //App全屏，隐藏StatusBar
        clearFlag(activity, UNSTABLE_STATUS)
    }

    @JvmStatic
    fun showUnStableNavBar(activity: Activity) {
        clearFlag(activity, UNSTABLE_NAV)
    }

    @JvmStatic
    fun showStableNavBar(activity: Activity) {
        clearFlag(activity, STABLE_NAV)
    }

    @JvmStatic
    fun hideStableStatusBar(activity: Activity) {
        setFlag(activity, STABLE_STATUS)
    }

    @JvmStatic
    fun hideStableNavBar(activity: Activity) {
        setFlag(activity, STABLE_NAV)
    }
    
    fun expandStatusBar(activity: Activity) {
        setFlag(activity, EXPAND_STATUS)
    }
    
    @JvmStatic
    fun transparentStatusBar(activity: Activity) {
        expandStatusBar(activity)
        activity.window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
    }

}
