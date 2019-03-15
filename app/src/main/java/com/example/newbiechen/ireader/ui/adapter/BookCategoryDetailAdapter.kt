package com.example.newbiechen.ireader.ui.adapter

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.SortBookBean
import com.example.newbiechen.ireader.utils.Constant

class BookCategoryDetailAdapter(val context: Context, data: List<SortBookBean>?) : BaseQuickAdapter<SortBookBean, BaseViewHolder>(R.layout.item_book_brief, data) {
    override fun convert(helper: BaseViewHolder, item: SortBookBean) {
        val portraitIv = helper.getView<ImageView>(R.id.book_brief_iv_portrait)
        //头像
        Glide.with(context)
                .load(Constant.IMG_BASE_URL + item.cover)
                .placeholder(R.drawable.ic_default_portrait)
                .error(R.drawable.ic_load_error)
                .fitCenter()
                .into(portraitIv)
        helper.setText(R.id.book_brief_tv_title, item.title)
        helper.setText(R.id.book_brief_tv_author, item.author)
        helper.setText(R.id.book_brief_tv_brief, item.shortIntro)
        helper.setText(R.id.book_brief_tv_msg,  context.resources.getString(R.string.nb_book_message,
                item.latelyFollower, item.retentionRatio))
    }

}