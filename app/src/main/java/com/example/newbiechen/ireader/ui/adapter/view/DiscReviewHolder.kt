package com.example.newbiechen.ireader.ui.adapter.view

import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.example.newbiechen.ireader.App
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.BookReviewBean
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.StringUtils

/**
 * Created by newbiechen on 17-4-21.
 */

class DiscReviewHolder : ViewHolderImpl<BookReviewBean>() {

    private lateinit var mIvPortrait: ImageView
    private lateinit var mTvBookName: TextView
    private lateinit var mTvBookType: TextView
    private lateinit var mTvTime: TextView
    private lateinit var mTvBrief: TextView
    private lateinit var mTvLabelDistillate: TextView
    private lateinit var mTvLabelHot: TextView
    private lateinit var mTvRecommendCount: TextView

    override fun initView() {
        mIvPortrait = findById(R.id.review_iv_portrait)
        mTvBookName = findById(R.id.review_tv_book_name)
        mTvBookType = findById(R.id.review_tv_book_type)
        mTvTime = findById(R.id.review_tv_time)
        mTvBrief = findById(R.id.review_tv_brief)
        mTvLabelDistillate = findById(R.id.review_tv_distillate)
        mTvLabelHot = findById(R.id.review_tv_hot)
        mTvRecommendCount = findById(R.id.review_tv_recommend)
    }

    override fun onBind(value: BookReviewBean, pos: Int) {
        //头像
        Glide.with(App.getInstance())
                .load(Constant.IMG_BASE_URL + value.bookBean.cover)
                .placeholder(R.drawable.ic_default_portrait)
                .error(R.drawable.ic_load_error)
                .fitCenter()
                .into(mIvPortrait)
        //名字
        mTvBookName.text = value.bookBean.title
        //类型
        val bookType = Constant.bookType[value.bookBean.type]
        mTvBookType.text = StringUtils.getString(R.string.nb_book_type, bookType!!)
        //简介
        mTvBrief.text = value.title
        //time
        mTvTime.text = StringUtils.dateConvert(value.updated, Constant.FORMAT_BOOK_DATE)
        //label
        if (value.state == Constant.BOOK_STATE_DISTILLATE) {
            mTvLabelDistillate.visibility = View.VISIBLE
        } else {
            mTvLabelDistillate.visibility = View.GONE
        }
        //response count
        mTvRecommendCount.text = StringUtils.getString(R.string.nb_book_recommend, value.helpfulBean!!.yes)
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_disc_review
    }
}