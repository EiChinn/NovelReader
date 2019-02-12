package com.example.newbiechen.ireader.model.local

import com.example.newbiechen.ireader.utils.ScreenUtils
import com.example.newbiechen.ireader.utils.SharedPreUtils
import com.example.newbiechen.ireader.widget.page.PageMode
import com.example.newbiechen.ireader.widget.page.PageStyle

object ReadSettingManager {
    private const val SHARED_READ_BG = "shared_read_bg"
    private const val SHARED_READ_BRIGHTNESS = "shared_read_brightness"
    private const val SHARED_READ_IS_BRIGHTNESS_AUTO = "shared_read_is_brightness_auto"
    private const val SHARED_READ_TEXT_SIZE = "shared_read_text_size"
    private const val SHARED_READ_IS_TEXT_DEFAULT = "shared_read_text_default"
    private const val SHARED_READ_PAGE_MODE = "shared_read_mode"
    private const val SHARED_READ_NIGHT_MODE = "shared_night_mode"
    private const val SHARED_READ_VOLUME_TURN_PAGE = "shared_read_volume_turn_page"
    private const val SHARED_READ_FULL_SCREEN = "shared_read_full_screen"
    private const val SHARED_READ_CONVERT_TYPE = "shared_read_convert_type"

    @JvmStatic
    fun setPageStyle(pageStyle: PageStyle) {
        SharedPreUtils.putInt(SHARED_READ_BG, pageStyle.ordinal)
    }

    @JvmStatic
    fun setBrightness(progress: Int) {
        SharedPreUtils.putInt(SHARED_READ_BRIGHTNESS, progress)
    }

    @JvmStatic
    fun setAutoBrightness(isAuto: Boolean) {
        SharedPreUtils.putBoolean(SHARED_READ_IS_BRIGHTNESS_AUTO, isAuto)
    }

    fun setDefaultTextSize(isDefault: Boolean) {
        SharedPreUtils.putBoolean(SHARED_READ_IS_TEXT_DEFAULT, isDefault)
    }

    @JvmStatic
    fun setTextSize(textSize: Int) {
        SharedPreUtils.putInt(SHARED_READ_TEXT_SIZE, textSize)
    }

    @JvmStatic
    fun setPageMode(mode: PageMode) {
        SharedPreUtils.putInt(SHARED_READ_PAGE_MODE, mode.ordinal)
    }

    @JvmStatic
    fun setNightMode(isNight: Boolean) {
        SharedPreUtils.putBoolean(SHARED_READ_NIGHT_MODE, isNight)
    }

    @JvmStatic
    fun getBrightness(): Int {
        return SharedPreUtils.getInt(SHARED_READ_BRIGHTNESS, 40)
    }

    @JvmStatic
    fun isBrightnessAuto(): Boolean {
        return SharedPreUtils.getBoolean(SHARED_READ_IS_BRIGHTNESS_AUTO, false)
    }
    @JvmStatic
    fun getTextSize(): Int {
        return SharedPreUtils.getInt(SHARED_READ_TEXT_SIZE, ScreenUtils.spToPx(28))
    }
    @JvmStatic
    fun isDefaultTextSize(): Boolean {
        return SharedPreUtils.getBoolean(SHARED_READ_IS_TEXT_DEFAULT, false)
    }

    @JvmStatic
    fun getPageMode(): PageMode {
        val mode = SharedPreUtils.getInt(SHARED_READ_PAGE_MODE, PageMode.SIMULATION.ordinal)
        return PageMode.values()[mode]
    }

    @JvmStatic
    fun getPageStyle(): PageStyle {
        val style = SharedPreUtils.getInt(SHARED_READ_BG, PageStyle.BG_0.ordinal)
        return PageStyle.values()[style]
    }

    @JvmStatic
    fun isNightMode(): Boolean {
        return SharedPreUtils.getBoolean(SHARED_READ_NIGHT_MODE, false)
    }

    @JvmStatic
    fun setVolumeTurnPage(isTurn: Boolean) {
        SharedPreUtils.putBoolean(SHARED_READ_VOLUME_TURN_PAGE, isTurn)
    }

    @JvmStatic
    fun isVolumeTurnPage(): Boolean {
        return SharedPreUtils.getBoolean(SHARED_READ_VOLUME_TURN_PAGE, false)
    }

    @JvmStatic
    fun setFullScreen(isFullScreen: Boolean) {
        SharedPreUtils.putBoolean(SHARED_READ_FULL_SCREEN, isFullScreen)
    }

    @JvmStatic
    fun isFullScreen(): Boolean {
        return SharedPreUtils.getBoolean(SHARED_READ_FULL_SCREEN, false)
    }

    @JvmStatic
    fun setConvertType(convertType: Int) {
        SharedPreUtils.putInt(SHARED_READ_CONVERT_TYPE, convertType)
    }

    @JvmStatic
    fun getConvertType(): Int {
        return SharedPreUtils.getInt(SHARED_READ_CONVERT_TYPE, 0)
    }
}