package com.example.newbiechen.ireader.ui.adapter.view

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.example.newbiechen.ireader.App
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.BookCommentBean
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.StringUtils
import com.example.newbiechen.ireader.widget.transform.CircleTransform

/**
 * Created by newbiechen on 17-4-20.
 */

class DiscCommentHolder : ViewHolderImpl<BookCommentBean>() {

    private var mIvPortrait: ImageView? = null
    private var mTvName: TextView? = null
    private var mTvLv: TextView? = null
    private var mTvTime: TextView? = null
    private var mTvBrief: TextView? = null
    private var mTvLabelDistillate: TextView? = null
    private var mTvResponseCount: TextView? = null
    private var mTvLikeCount: TextView? = null

    override fun initView() {
        mIvPortrait = findById(R.id.disc_comment_iv_portrait)
        mTvName = findById(R.id.disc_comment_tv_name)
        mTvLv = findById(R.id.disc_comment_tv_lv)
        mTvTime = findById(R.id.disc_comment_tv_time)
        mTvBrief = findById(R.id.disc_comment_tv_brief)
        mTvLabelDistillate = findById(R.id.disc_comment_tv_label_distillate)
        mTvResponseCount = findById(R.id.disc_comment_tv_response_count)
        mTvLikeCount = findById(R.id.disc_comment_tv_like_count)
    }

    override fun onBind(value: BookCommentBean, pos: Int) {
        //头像
        Glide.with(getContext())
                .load(Constant.IMG_BASE_URL + value.author.avatar)
                .placeholder(R.drawable.ic_default_portrait)
                .error(R.drawable.ic_load_error)
                .transform(CircleTransform(App.getInstance()))
                .into(mIvPortrait!!)
        //名字
        mTvName!!.text = value.author.nickname
        //等级
        mTvLv!!.text = StringUtils.getString(R.string.nb_user_lv,
                value.author.lv)
        //简介
        mTvBrief!!.text = value.title
        //label
        if (value.state == Constant.BOOK_STATE_DISTILLATE) {
            mTvLabelDistillate!!.visibility = View.VISIBLE
            mTvTime!!.visibility = View.VISIBLE
        } else {
            mTvLabelDistillate!!.visibility = View.GONE
            mTvTime!!.visibility = View.GONE
        }
        //comment or vote
        val type = value.type
        var drawable: Drawable? = null
        when (type) {
            Constant.BOOK_TYPE_COMMENT -> drawable = getContext().resources.getDrawable(R.drawable.ic_notif_post)
            Constant.BOOK_TYPE_VOTE -> drawable = getContext().resources.getDrawable(R.drawable.ic_notif_vote)
            else -> drawable = getContext().resources.getDrawable(R.mipmap.ic_launcher)
        }
        drawable!!.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        //time
        mTvTime!!.text = StringUtils.dateConvert(value.updated, Constant.FORMAT_BOOK_DATE)

        mTvResponseCount!!.setCompoundDrawables(drawable, null, null, null)
        //response count
        mTvResponseCount!!.text = value.commentCount.toString() + ""
        //like count
        mTvLikeCount!!.text = value.likeCount.toString() + ""
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_disc_comment
    }
}
