package com.example.newbiechen.ireader.ui.adapter.view

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.db.entity.CollBook
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.StringUtils

/**
 * Created by newbiechen on 17-5-8.
 * CollectionBookView
 */

class CollBookHolder : ViewHolderImpl<CollBook>() {
    private var mIvCover: ImageView? = null
    private var mTvName: TextView? = null
    private var mTvChapter: TextView? = null
    private var mTvTime: TextView? = null
    private var mCbSelected: CheckBox? = null
    private var mIvRedDot: ImageView? = null
    private var mIvTop: ImageView? = null


    override fun initView() {
        mIvCover = findById(R.id.coll_book_iv_cover)
        mTvName = findById(R.id.coll_book_tv_name)
        mTvChapter = findById(R.id.coll_book_tv_chapter)
        mTvTime = findById(R.id.coll_book_tv_lately_update)
        mCbSelected = findById(R.id.coll_book_cb_selected)
        mIvRedDot = findById(R.id.coll_book_iv_red_rot)
        mIvTop = findById(R.id.coll_book_iv_top)
    }

    override fun onBind(value: CollBook, pos: Int) {
        if (value.isLocal) {
            //本地文件的图片
            Glide.with(getContext())
                    .load(R.drawable.ic_local_file)
                    .fitCenter()
                    .into(mIvCover!!)
        } else {
            //书的图片
            Glide.with(getContext())
                    .load(Constant.IMG_BASE_URL + value.cover)
                    .placeholder(R.drawable.ic_book_loading)
                    .error(R.drawable.ic_load_error)
                    .fitCenter()
                    .into(mIvCover!!)
        }
        //书名
        mTvName!!.text = value.title
        if (!value.isLocal) {
            //时间
            mTvTime!!.text = StringUtils.dateConvert(value.updated, Constant.FORMAT_BOOK_DATE) + ":"
            mTvTime!!.visibility = View.VISIBLE
        } else {
            mTvTime!!.text = "阅读进度:"
        }
        //章节
        mTvChapter!!.text = value.lastChapter
        //我的想法是，在Collection中加一个字段，当追更的时候设置为true。当点击的时候设置为false。
        //当更新的时候，最新数据跟旧数据进行比较，如果更新的话，设置为true。
        if (value.isUpdate) {
            mIvRedDot!!.visibility = View.VISIBLE
        } else {
            mIvRedDot!!.visibility = View.GONE
        }
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_coll_book
    }

    companion object {

        private val TAG = "CollBookView"
    }
}
