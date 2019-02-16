package com.example.newbiechen.ireader.ui.adapter.view

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView

import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.DownloadTaskBean
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl
import com.example.newbiechen.ireader.utils.FileUtils
import com.example.newbiechen.ireader.utils.StringUtils

/**
 * Created by newbiechen on 17-5-12.
 */

class DownloadHolder : ViewHolderImpl<DownloadTaskBean>() {

    private var mTvTitle: TextView? = null
    private var mTvMsg: TextView? = null
    private var mTvTip: TextView? = null
    private var mPbShow: ProgressBar? = null
    private var mRlToggle: RelativeLayout? = null
    private var mIvStatus: ImageView? = null
    private var mTvStatus: TextView? = null

    override fun initView() {
        mTvTitle = findById(R.id.download_tv_title)
        mTvMsg = findById(R.id.download_tv_msg)
        mTvTip = findById(R.id.download_tv_tip)
        mPbShow = findById(R.id.download_pb_show)
        mRlToggle = findById(R.id.download_rl_toggle)
        mIvStatus = findById(R.id.download_iv_status)
        mTvStatus = findById(R.id.download_tv_status)
    }

    override fun onBind(value: DownloadTaskBean, pos: Int) {

        if (mTvTitle!!.text != value.taskName) {
            mTvTitle!!.text = value.taskName
        }

        when (value.status) {
            DownloadTaskBean.STATUS_LOADING -> {
                changeBtnStyle(R.string.nb_download_pause,
                        R.color.nb_download_pause, R.drawable.ic_download_pause)

                //进度状态
                setProgressMax(value)
                mPbShow!!.progress = value.currentChapter

                setTip(R.string.nb_download_loading)

                mTvMsg!!.text = StringUtils.getString(R.string.nb_download_progress,
                        value.currentChapter, value.bookChapters.size)
            }
            DownloadTaskBean.STATUS_PAUSE -> {
                //按钮状态
                changeBtnStyle(R.string.nb_download_start,
                        R.color.nb_download_loading, R.drawable.ic_download_loading)

                //进度状态
                setProgressMax(value)
                setTip(R.string.nb_download_pausing)

                mPbShow!!.progress = value.currentChapter
                mTvMsg!!.text = StringUtils.getString(R.string.nb_download_progress,
                        value.currentChapter, value.bookChapters.size)
            }
            DownloadTaskBean.STATUS_WAIT -> {
                //按钮状态
                changeBtnStyle(R.string.nb_download_wait,
                        R.color.nb_download_wait, R.drawable.ic_download_wait)

                //进度状态
                setProgressMax(value)
                setTip(R.string.nb_download_waiting)

                mPbShow!!.progress = value.currentChapter
                mTvMsg!!.text = StringUtils.getString(R.string.nb_download_progress,
                        value.currentChapter, value.bookChapters.size)
            }
            DownloadTaskBean.STATUS_ERROR -> {
                //按钮状态
                changeBtnStyle(R.string.nb_download_error,
                        R.color.nb_download_error, R.drawable.ic_download_error)
                setTip(R.string.nb_download_source_error)
                mPbShow!!.visibility = View.INVISIBLE
                mTvMsg!!.visibility = View.GONE
            }
            DownloadTaskBean.STATUS_FINISH -> {
                //按钮状态
                changeBtnStyle(R.string.nb_download_finish,
                        R.color.nb_download_finish, R.drawable.ic_download_complete)
                setTip(R.string.nb_download_complete)
                mPbShow!!.visibility = View.INVISIBLE

                //设置文件大小
                mTvMsg!!.text = FileUtils.getFileSize(value.size)
            }
        }
    }

    private fun changeBtnStyle(strRes: Int, colorRes: Int, drawableRes: Int) {
        //按钮状态
        if (mTvStatus!!.text != StringUtils.getString(strRes)) {
            mTvStatus!!.text = StringUtils.getString(strRes)
            mTvStatus!!.setTextColor(getContext().resources.getColor(colorRes))
            mIvStatus!!.setImageResource(drawableRes)
        }
    }

    private fun setProgressMax(value: DownloadTaskBean) {
        if (mPbShow!!.max != value.bookChapters.size) {
            mPbShow!!.visibility = View.VISIBLE
            mPbShow!!.max = value.bookChapters.size
        }
    }

    //提示
    private fun setTip(strRes: Int) {
        if (mTvTip!!.text != StringUtils.getString(strRes)) {
            mTvTip!!.text = StringUtils.getString(strRes)
        }
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_download
    }
}
