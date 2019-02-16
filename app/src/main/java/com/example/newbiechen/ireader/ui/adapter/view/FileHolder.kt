package com.example.newbiechen.ireader.ui.adapter.view

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.local.BookRepository
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.FileUtils
import com.example.newbiechen.ireader.utils.MD5Utils
import com.example.newbiechen.ireader.utils.StringUtils
import java.io.File
import java.util.*

/**
 * Created by newbiechen on 17-5-27.
 */

class FileHolder(private val mSelectedMap: HashMap<File, Boolean>) : ViewHolderImpl<File>() {
    private var mIvIcon: ImageView? = null
    private var mCbSelect: CheckBox? = null
    private var mTvName: TextView? = null
    private var mLlBrief: LinearLayout? = null
    private var mTvTag: TextView? = null
    private var mTvSize: TextView? = null
    private var mTvDate: TextView? = null
    private var mTvSubCount: TextView? = null

    override fun initView() {
        mIvIcon = findById(R.id.file_iv_icon)
        mCbSelect = findById(R.id.file_cb_select)
        mTvName = findById(R.id.file_tv_name)
        mLlBrief = findById(R.id.file_ll_brief)
        mTvTag = findById(R.id.file_tv_tag)
        mTvSize = findById(R.id.file_tv_size)
        mTvDate = findById(R.id.file_tv_date)
        mTvSubCount = findById(R.id.file_tv_sub_count)
    }

    override fun onBind(data: File, pos: Int) {
        //判断是文件还是文件夹
        if (data.isDirectory) {
            setFolder(data)
        } else {
            setFile(data)
        }
    }

    private fun setFile(file: File) {
        //选择
        val id = MD5Utils.strToMd5By16(file.absolutePath)

        if (BookRepository.getInstance().getCollBook(id) != null) {
            mIvIcon!!.setImageResource(R.drawable.ic_file_loaded)
            mIvIcon!!.visibility = View.VISIBLE
            mCbSelect!!.visibility = View.GONE
        } else {
            val isSelected = mSelectedMap[file]!!
            mCbSelect!!.isChecked = isSelected
            mIvIcon!!.visibility = View.GONE
            mCbSelect!!.visibility = View.VISIBLE
        }

        mLlBrief!!.visibility = View.VISIBLE
        mTvSubCount!!.visibility = View.GONE

        mTvName!!.text = file.name
        mTvSize!!.text = FileUtils.getFileSize(file.length())
        mTvDate!!.text = StringUtils.dateConvert(file.lastModified(), Constant.FORMAT_FILE_DATE)
    }

    fun setFolder(folder: File) {
        //图片
        mIvIcon!!.visibility = View.VISIBLE
        mCbSelect!!.visibility = View.GONE
        mIvIcon!!.setImageResource(R.drawable.ic_dir)
        //名字
        mTvName!!.text = folder.name
        //介绍
        mLlBrief!!.visibility = View.GONE
        mTvSubCount!!.visibility = View.VISIBLE

        mTvSubCount!!.text = getContext().getString(R.string.nb_file_sub_count, folder.list().size)
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_file
    }
}
