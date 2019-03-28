package com.example.newbiechen.ireader.ui.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.BillBookBean
import com.example.newbiechen.ireader.utils.Constant

class LeaderBoardBookAdapter(data: List<BillBookBean>, val onItemClicked: (String) -> Unit) : BaseQuickAdapter<BillBookBean, BaseViewHolder>(R.layout.item_book_brief, data) {
    override fun convert(helper: BaseViewHolder, item: BillBookBean) {
        val bookCoverIv = helper.getView<ImageView>(R.id.book_brief_iv_portrait)
        //头像
        Glide.with(bookCoverIv.context)
                .load(Constant.IMG_BASE_URL + item.cover)
                .placeholder(R.drawable.ic_default_portrait)
                .error(R.drawable.ic_load_error)
                .fitCenter()
                .into(bookCoverIv)
        helper.setText(R.id.book_brief_tv_title, item.title)
        helper.setText(R.id.book_brief_tv_author, item.author)
        helper.setText(R.id.book_brief_tv_brief, item.shortIntro)
        helper.setText(R.id.book_brief_tv_msg, "${item.latelyFollower}人在追 | ${item.retentionRatio}读者留存")
        helper.setOnClickListener(R.id.rl_container) {
            onItemClicked(item._id)
        }

    }

}