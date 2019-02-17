package com.example.newbiechen.ireader.widget.page


import androidx.annotation.ColorRes
import com.example.newbiechen.ireader.R

/**
 * Created by newbiechen on 2018/2/5.
 * 作用：页面的展示风格。
 */

enum class PageStyle constructor(@param:ColorRes val fontColor: Int, @param:ColorRes val bgColor: Int) {
    BG_0(R.color.nb_read_font_1, R.color.nb_read_bg_1),
    BG_1(R.color.nb_read_font_2, R.color.nb_read_bg_2),
    BG_2(R.color.nb_read_font_3, R.color.nb_read_bg_3),
    BG_3(R.color.nb_read_font_4, R.color.nb_read_bg_4),
    BG_4(R.color.nb_read_font_5, R.color.nb_read_bg_5),
    NIGHT(R.color.nb_read_font_night, R.color.nb_read_bg_night)
}
