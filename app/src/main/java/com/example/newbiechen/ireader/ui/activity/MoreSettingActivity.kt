package com.example.newbiechen.ireader.ui.activity

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.local.ReadSettingManager
import com.example.newbiechen.ireader.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_more_setting.*

/**
 * Created by newbiechen on 17-6-6.
 * 阅读界面的更多设置
 */

class MoreSettingActivity : BaseActivity() {
    private var isVolumeTurnPage: Boolean = false
    private var isFullScreen: Boolean = false
    private var convertType: Int = 0
    override fun getContentId(): Int {
        return R.layout.activity_more_setting
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        isVolumeTurnPage = ReadSettingManager.isVolumeTurnPage()
        isFullScreen = ReadSettingManager.isFullScreen()
        convertType = ReadSettingManager.getConvertType()
    }

    override fun setUpToolbar(toolbar: Toolbar) {
        super.setUpToolbar(toolbar)
        supportActionBar!!.title = "阅读设置"
    }

    override fun initWidget() {
        super.initWidget()
        initSwitchStatus()
    }

    private fun initSwitchStatus() {
        more_setting_sc_volume!!.isChecked = isVolumeTurnPage
        more_setting_sc_full_screen!!.isChecked = isFullScreen
    }

    override fun initClick() {
        super.initClick()
        more_setting_rl_volume!!.setOnClickListener { v ->
            isVolumeTurnPage = !isVolumeTurnPage
            more_setting_sc_volume!!.isChecked = isVolumeTurnPage
            ReadSettingManager.setVolumeTurnPage(isVolumeTurnPage)
        }

        more_setting_rl_full_screen!!.setOnClickListener { v ->
            isFullScreen = !isFullScreen
            more_setting_sc_full_screen!!.isChecked = isFullScreen
            ReadSettingManager.setFullScreen(isFullScreen)
        }
    }

}
