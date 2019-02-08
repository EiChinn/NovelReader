package com.example.newbiechen.ireader.model.flag

import androidx.annotation.StringRes
import com.example.newbiechen.ireader.App
import com.example.newbiechen.ireader.R

enum class FindType(@StringRes val typeNameId: Int, @StringRes val iconId: Int) {
    TOP(R.string.nb_fragment_find_top, R.drawable.ic_section_top),
    TOPIC(R.string.nb_fragment_find_topic, R.drawable.ic_section_topic),
    SORT(R.string.nb_fragment_find_sort, R.drawable.ic_section_sort),
    LISTEN(R.string.nb_fragment_find_listen, R.drawable.ic_section_listen);

    fun getTypeName(): String {
        return App.getInstance().resources.getString(typeNameId)
    }
}