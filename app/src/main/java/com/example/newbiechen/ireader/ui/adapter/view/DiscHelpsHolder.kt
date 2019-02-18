package com.example.newbiechen.ireader.ui.adapter.view

import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.BookHelpsBean
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.StringUtils
import com.example.newbiechen.ireader.widget.transform.CircleTransform

/**
 * Created by newbiechen on 17-4-21.
 */

class DiscHelpsHolder : ViewHolderImpl<BookHelpsBean>() {

    private lateinit var mIvPortrait: ImageView
    private lateinit var mTvName: TextView
    private lateinit var mTvLv: TextView
    private lateinit var mTvTime: TextView
    private lateinit var mTvBrief: TextView
    private lateinit var mTvLabelDistillate: TextView
    private lateinit var mTvLabelHot: TextView
    private lateinit var mTvResponseCount: TextView
    private lateinit var mTvLikeCount: TextView

    override fun initView() {
        mIvPortrait = findById(R.id.disc_comment_iv_portrait)
        mTvName = findById(R.id.disc_comment_tv_name)
        mTvLv = findById(R.id.disc_comment_tv_lv)
        mTvTime = findById(R.id.disc_comment_tv_time)
        mTvBrief = findById(R.id.disc_comment_tv_brief)
        mTvLabelDistillate = findById(R.id.disc_comment_tv_label_distillate)
        mTvLabelHot = findById(R.id.disc_comment_tv_label_hot)
        mTvResponseCount = findById(R.id.disc_comment_tv_response_count)
        mTvLikeCount = findById(R.id.disc_comment_tv_like_count)
    }

    override fun onBind(value: BookHelpsBean, pos: Int) {
        //头像
        Glide.with(getContext())
                .load(Constant.IMG_BASE_URL + value.author.avatar)
                .placeholder(R.drawable.ic_default_portrait)
                .error(R.drawable.ic_load_error)
                .transform(CircleTransform(getContext()))
                .into(mIvPortrait)
        //名字
        mTvName.text = value.author.nickname
        //等级
        mTvLv.text = StringUtils.getString(R.string.nb_user_lv,
                value.author.lv)
        //简介
        mTvBrief.text = value.title
        //label
        if (value.state == Constant.BOOK_STATE_DISTILLATE) {
            mTvLabelDistillate.visibility = View.VISIBLE
        } else {
            mTvLabelDistillate.visibility = View.GONE
        }
        //response count
        mTvResponseCount.text = value.commentCount.toString() + ""
        //like count
        mTvLikeCount.text = value.likeCount.toString() + ""
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_disc_comment
    }
}
