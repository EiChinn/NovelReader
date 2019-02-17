package com.example.newbiechen.ireader.ui.activity


import androidx.appcompat.widget.Toolbar
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.ui.base.BaseActivity

/**
 * Created by newbiechen on 17-5-26.
 */

class CommunityActivity : BaseActivity() {

    override fun getContentId(): Int {
        return R.layout.activity_community
    }

    override fun setUpToolbar(toolbar: Toolbar) {
        super.setUpToolbar(toolbar)
        supportActionBar!!.title = "社区"
    }
}
