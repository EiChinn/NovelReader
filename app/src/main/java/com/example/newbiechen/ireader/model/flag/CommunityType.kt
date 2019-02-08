package com.example.newbiechen.ireader.model.flag

import androidx.annotation.StringRes
import com.example.newbiechen.ireader.App
import com.example.newbiechen.ireader.R

enum class CommunityType(@StringRes val typeNameId: Int, val netName: String, @StringRes val iconId: Int) {
    COMMENT(R.string.nb_fragment_community_comment, "ramble", R.drawable.ic_section_comment),
    REVIEW(R.string.nb_fragment_community_discussion, "", R.drawable.ic_section_discuss),
    HELP(R.string.nb_fragment_community_help, "", R.drawable.ic_section_help),
    GIRL(R.string.nb_fragment_community_girl, "girl", R.drawable.ic_section_girl),
    COMPOSE(R.string.nb_fragment_community_compose, "original", R.drawable.ic_section_compose);

    fun getTypeName(): String {
        return App.getInstance().resources.getString(typeNameId)
    }
}