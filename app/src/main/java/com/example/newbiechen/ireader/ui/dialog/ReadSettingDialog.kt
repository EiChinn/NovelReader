package com.example.newbiechen.ireader.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.local.ReadSettingManager
import com.example.newbiechen.ireader.ui.activity.MoreSettingActivity
import com.example.newbiechen.ireader.ui.activity.ReadActivity
import com.example.newbiechen.ireader.ui.adapter.PageStyleAdapter
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.utils.BrightnessUtils
import com.example.newbiechen.ireader.utils.ScreenUtils
import com.example.newbiechen.ireader.widget.page.PageLoader
import com.example.newbiechen.ireader.widget.page.PageMode
import com.example.newbiechen.ireader.widget.page.PageStyle
import kotlinx.android.synthetic.main.dialog_read_setting.*


class ReadSettingDialog(val mActivity: Activity, val mPageLoader: PageLoader) : Dialog(mActivity, R.style.ReadSettingDialog) {
    companion object {
        private const val DEFAULT_TEXT_SIZE = 16
    }

    private lateinit var mPageStyleAdapter: PageStyleAdapter
    private lateinit var mPageMode: PageMode
    private lateinit var mPageStyle: PageStyle
    private var mBrightness: Int = 0
    private var mTextSize: Int = 0
    private var isBrightnessAuto: Boolean = false
    private var isTextDefault: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_read_setting)
        setUpWindow()
        initData()
        initWidget()
        initClick()
    }

    private fun setUpWindow() {
        val lp = window?.attributes
        lp?.let {
            it.width = WindowManager.LayoutParams.MATCH_PARENT
            it.height = WindowManager.LayoutParams.WRAP_CONTENT
            it.gravity = Gravity.BOTTOM
            window?.attributes = it
        }

    }
    private fun initData() {
        isBrightnessAuto = ReadSettingManager.isBrightnessAuto()
        mBrightness = ReadSettingManager.getBrightness()
        mTextSize = ReadSettingManager.getTextSize()
        isTextDefault = ReadSettingManager.isDefaultTextSize()
        mPageMode = ReadSettingManager.getPageMode()
        mPageStyle = ReadSettingManager.getPageStyle()
    }
    private fun initWidget() {
        read_setting_sb_brightness.progress = mBrightness
        read_setting_tv_font.text = mTextSize.toString()
        read_setting_cb_brightness_auto.isChecked = isBrightnessAuto
        read_setting_cb_font_default.isChecked = isTextDefault
        initPageMode()
        setUpAdapter()
    }
    private fun initPageMode() {
        when (mPageMode) {
            PageMode.SIMULATION -> read_setting_rb_simulation.isChecked = true
            PageMode.COVER -> read_setting_rb_cover.isChecked = true
            PageMode.SLIDE -> read_setting_rb_slide.isChecked = true
            PageMode.NONE -> read_setting_rb_none.isChecked = true
            PageMode.SCROLL -> read_setting_rb_scroll.isChecked = true
        }
    }
    private fun setUpAdapter() {
        val drawables = arrayOf(getDrawable(R.color.nb_read_bg_1)
                , getDrawable(R.color.nb_read_bg_2)
                , getDrawable(R.color.nb_read_bg_3)
                , getDrawable(R.color.nb_read_bg_4)
                , getDrawable(R.color.nb_read_bg_5))
        mPageStyleAdapter = PageStyleAdapter()
        read_setting_rv_bg.layoutManager = GridLayoutManager(context, 5)
        read_setting_rv_bg.adapter = mPageStyleAdapter
        mPageStyleAdapter.refreshItems(drawables.toList())
        mPageStyleAdapter.setPageStyleChecked(mPageStyle)

    }
    private fun getDrawable(drawRes: Int): Drawable {
        return ContextCompat.getDrawable(context, drawRes)!!
    }
    private fun initClick() {
        read_setting_iv_brightness_minus.setOnClickListener {
            if (read_setting_cb_brightness_auto.isChecked) {
                read_setting_cb_brightness_auto.isChecked = false
            }

            val progress = read_setting_sb_brightness.progress - 1
            if (progress < 0) {
                return@setOnClickListener
            }
            read_setting_sb_brightness.progress = progress
            BrightnessUtils.setBrightness(mActivity, progress)
        }

        read_setting_iv_brightness_plus.setOnClickListener {
            if (read_setting_cb_brightness_auto.isChecked) {
                read_setting_cb_brightness_auto.isChecked = false
            }

            val progress = read_setting_sb_brightness.progress + 1
            if (progress > read_setting_sb_brightness.max) {
                return@setOnClickListener
            }
            read_setting_sb_brightness.progress = progress
            BrightnessUtils.setBrightness(mActivity, progress)
            ReadSettingManager.setBrightness(progress)
        }

        read_setting_sb_brightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (read_setting_cb_brightness_auto.isChecked) {
                    read_setting_cb_brightness_auto.isChecked = false
                }
                BrightnessUtils.setBrightness(mActivity, seekBar.progress)
                ReadSettingManager.setBrightness(seekBar.progress)
            }

        })

        read_setting_cb_brightness_auto.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                //获取屏幕的亮度
                BrightnessUtils.setBrightness(mActivity, BrightnessUtils.getScreenBrightness(mActivity))
            } else {
                //获取进度条的亮度
                BrightnessUtils.setBrightness(mActivity, read_setting_sb_brightness.progress)
            }
            ReadSettingManager.setAutoBrightness(isChecked)
        }

        read_setting_tv_font_minus.setOnClickListener {
            if (read_setting_cb_font_default.isChecked) {
                read_setting_cb_font_default.isChecked = false
            }

            val fontSize = read_setting_tv_font.text.toString().toInt() - 1
            if (fontSize < 0) {
                return@setOnClickListener
            }
            read_setting_tv_font.text = fontSize.toString()
            mPageLoader.setTextSize(fontSize)
        }
        read_setting_tv_font_plus.setOnClickListener {
            if (read_setting_cb_font_default.isChecked) {
                read_setting_cb_font_default.isChecked = false
            }

            val fontSize = read_setting_tv_font.text.toString().toInt() + 1
            read_setting_tv_font.text = fontSize.toString()
            mPageLoader.setTextSize(fontSize)
        }

        read_setting_cb_font_default.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val fontSize = ScreenUtils.dpToPx(DEFAULT_TEXT_SIZE)
                read_setting_tv_font.text = fontSize.toString()
                mPageLoader.setTextSize(fontSize)
            }
        }

        read_setting_rg_page_mode.setOnCheckedChangeListener { _, checkedId ->
            val pageMode = when (checkedId) {
                R.id.read_setting_rb_simulation -> PageMode.SIMULATION
                R.id.read_setting_rb_cover -> PageMode.COVER
                R.id.read_setting_rb_slide -> PageMode.SLIDE
                R.id.read_setting_rb_scroll -> PageMode.SCROLL
                R.id.read_setting_rb_none -> PageMode.NONE
                else -> PageMode.SIMULATION
            }

            mPageLoader.setPageMode(pageMode)
        }

        mPageStyleAdapter.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener{
            override fun onItemClick(view: View, pos: Int) {
                mPageLoader.setPageStyle(PageStyle.values()[pos])
            }

        })

        read_setting_tv_more.setOnClickListener {
            val intent = Intent(context, MoreSettingActivity::class.java)
            mActivity.startActivityForResult(intent, ReadActivity.REQUEST_MORE_SETTING)
            dismiss()
        }
    }

    fun isBrightFollowSystem(): Boolean {
        return read_setting_cb_brightness_auto?.isChecked ?: false
    }
}