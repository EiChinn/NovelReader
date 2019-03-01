package com.example.newbiechen.ireader.ui.adapter

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.db.entity.CollBook
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.StringUtils

/**
 * Created by newbiechen on 17-5-8.
 */

class CollBookAdapter(val context: Context) : BaseQuickAdapter<CollBook, BaseViewHolder>(R.layout.item_coll_book) {

    override fun convert(helper: BaseViewHolder, item: CollBook) {
        val coverIv = helper.getView<ImageView>(R.id.coll_book_iv_cover)
        if (item.isLocal) {
            //本地文件的图片
            Glide.with(context)
                    .load(R.drawable.ic_local_file)
                    .fitCenter()
                    .into(coverIv)
            helper.setText(R.id.coll_book_tv_lately_update, "阅读进度:")

        } else {
            //书的图片
            Glide.with(context)
                    .load(Constant.IMG_BASE_URL + item.cover)
                    .placeholder(R.drawable.ic_book_loading)
                    .error(R.drawable.ic_load_error)
                    .fitCenter()
                    .into(coverIv)
            helper.setText(R.id.coll_book_tv_lately_update, StringUtils.dateConvert(item.updated, Constant.FORMAT_BOOK_DATE) + ":")
        }
        //书名
        helper.setText(R.id.coll_book_tv_name, item.title)
        //章节
        helper.setText(R.id.coll_book_tv_chapter, item.lastChapter)
        helper.setGone(R.id.coll_book_iv_red_rot, item.isUpdate)
    }

}
