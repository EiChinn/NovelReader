package com.example.newbiechen.ireader.ui.adapter.view

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.CommentBean
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.StringUtils
import com.example.newbiechen.ireader.widget.transform.CircleTransform

/**
 * Created by newbiechen on 17-4-29.
 */

class CommentHolder(bestComment: Boolean) : ViewHolderImpl<CommentBean>() {

    private var ivPortrait: ImageView? = null
    private var tvFloor: TextView? = null
    private var tvName: TextView? = null
    private var tvLv: TextView? = null
    private var tvTime: TextView? = null
    private var tvLikeCount: TextView? = null
    private var tvContent: TextView? = null
    private var tvReplyName: TextView? = null
    private var tvReplyFloor: TextView? = null

    private var isBestComment = false

    init {
        isBestComment = bestComment
    }

    override fun initView() {
        ivPortrait = findById(R.id.comment_iv_portrait)
        tvFloor = findById(R.id.comment_tv_floor)
        tvName = findById(R.id.comment_tv_name)
        tvLv = findById(R.id.comment_tv_lv)
        tvTime = findById(R.id.comment_tv_time)
        tvLikeCount = findById(R.id.comment_tv_like_count)
        tvContent = findById(R.id.comment_tv_content)
        tvReplyName = findById(R.id.comment_tv_reply_name)
        tvReplyFloor = findById(R.id.comment_tv_reply_floor)
    }

    override fun onBind(value: CommentBean, pos: Int) {
        //头像
        Glide.with(getContext())
                .load(Constant.IMG_BASE_URL + value.author.avatar)
                .placeholder(R.drawable.ic_loadding)
                .error(R.drawable.ic_load_error)
                .transform(CircleTransform(getContext()))
                .into(ivPortrait)
        //名字
        tvName!!.text = value.author.nickname
        //等级
        tvLv!!.text = StringUtils.getString(R.string.nb_user_lv, value.author.lv)
        //楼层
        tvFloor!!.text = StringUtils.getString(R.string.nb_comment_floor, value.floor)
        if (isBestComment) {
            //点赞数
            tvTime!!.visibility = View.GONE
            tvLikeCount!!.visibility = View.VISIBLE
            tvLikeCount!!.text = StringUtils.getString(R.string.nb_comment_like_count, value.likeCount)
        } else {
            //时间
            tvTime!!.visibility = View.VISIBLE
            tvLikeCount!!.visibility = View.GONE
            tvTime!!.text = StringUtils.dateConvert(value.created, Constant.FORMAT_BOOK_DATE)
        }
        //内容
        tvContent!!.text = value.content
        //回复的人名
        val replyToBean = value.replyTo
        if (replyToBean != null) {
            tvReplyName!!.visibility = View.VISIBLE
            tvReplyFloor!!.visibility = View.VISIBLE
            tvReplyName!!.text = StringUtils.getString(R.string.nb_comment_reply_nickname,
                    replyToBean.author.nickname)
            //回复的楼层
            tvReplyFloor!!.text = StringUtils.getString(R.string.nb_comment_reply_floor,
                    replyToBean.floor)
        } else {
            tvReplyName!!.visibility = View.GONE
            tvReplyFloor!!.visibility = View.GONE
        }
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_comment
    }
}
