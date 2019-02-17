package com.example.newbiechen.ireader.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.local.ReadSettingManager
import com.example.newbiechen.ireader.ui.base.BaseActivity

/**
 * Created by newbiechen on 17-6-6.
 * 阅读界面的更多设置
 */

class MoreSettingActivity : BaseActivity() {
    @BindView(R.id.more_setting_rl_volume)
    @JvmField internal var mRlVolume: RelativeLayout? = null
    @BindView(R.id.more_setting_sc_volume)
    @JvmField internal var mScVolume: SwitchCompat? = null
    @BindView(R.id.more_setting_rl_full_screen)
    @JvmField internal var mRlFullScreen: RelativeLayout? = null
    @BindView(R.id.more_setting_sc_full_screen)
    @JvmField internal var mScFullScreen: SwitchCompat? = null
    @BindView(R.id.more_setting_sc_convert_type)
    @JvmField internal var mScConvertType: Spinner? = null
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
        mScVolume!!.isChecked = isVolumeTurnPage
        mScFullScreen!!.isChecked = isFullScreen
    }

    override fun initClick() {
        super.initClick()
        mRlVolume!!.setOnClickListener { v ->
            isVolumeTurnPage = !isVolumeTurnPage
            mScVolume!!.isChecked = isVolumeTurnPage
            ReadSettingManager.setVolumeTurnPage(isVolumeTurnPage)
        }

        mRlFullScreen!!.setOnClickListener { v ->
            isFullScreen = !isFullScreen
            mScFullScreen!!.isChecked = isFullScreen
            ReadSettingManager.setFullScreen(isFullScreen)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val adapter = ArrayAdapter.createFromResource(this,
                R.array.conversion_type_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mScConvertType!!.adapter = adapter

        // initSwitchStatus() be called earlier than onCreate(), so setSelection() won't work
        mScConvertType!!.setSelection(convertType)

        mScConvertType!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                ReadSettingManager.setConvertType(position)
                convertType = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}
