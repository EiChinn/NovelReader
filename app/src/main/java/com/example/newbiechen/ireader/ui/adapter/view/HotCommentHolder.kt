package com.example.newbiechen.ireader.ui.adapter.view

import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.HotCommentBean
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.StringUtils
import com.example.newbiechen.ireader.widget.EasyRatingBar
import com.example.newbiechen.ireader.widget.transform.CircleTransform

/**
 * Created by newbiechen on 17-5-4.
 */

class HotCommentHolder : ViewHolderImpl<HotCommentBean>() {

    private var mIvPortrait: ImageView? = null
    private var mTvAuthor: TextView? = null
    private var mTvLv: TextView? = null
    private var mTvTitle: TextView? = null
    private var mErbRate: EasyRatingBar? = null
    private var mTvContent: TextView? = null
    private var mTvHelpful: TextView? = null
    private var mTvTime: TextView? = null

    override fun initView() {
        mIvPortrait = findById(R.id.hot_comment_iv_cover)
        mTvAuthor = findById(R.id.hot_comment_tv_author)
        mTvLv = findById(R.id.hot_comment_tv_lv)
        mTvTitle = findById(R.id.hot_comment_title)
        mErbRate = findById(R.id.hot_comment_erb_rate)
        mTvContent = findById(R.id.hot_comment_tv_content)
        mTvHelpful = findById(R.id.hot_comment_tv_helpful)
        mTvTime = findById(R.id.hot_comment_tv_time)
    }

    override fun onBind(value: HotCommentBean, pos: Int) {
        //头像
        Glide.with(getContext())
                .load(Constant.IMG_BASE_URL + value.author.avatar)
                .placeholder(R.drawable.ic_default_portrait)
                .error(R.drawable.ic_load_error)
                .transform(CircleTransform(getContext()))
                .into(mIvPortrait!!)
        //作者
        mTvAuthor!!.text = value.author.nickname
        //等级
        mTvLv!!.text = StringUtils.getString(R.string.nb_user_lv, value.author.lv)
        //标题
        mTvTitle!!.text = value.title
        //评分
        mErbRate!!.setRating(value.rating)
        //内容
        mTvContent!!.text = value.content
        //点赞数
        mTvHelpful!!.text = value.likeCount.toString() + ""
        //时间
        mTvTime!!.text = StringUtils.dateConvert(value.updated, Constant.FORMAT_BOOK_DATE)
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_hot_comment
    }
}
