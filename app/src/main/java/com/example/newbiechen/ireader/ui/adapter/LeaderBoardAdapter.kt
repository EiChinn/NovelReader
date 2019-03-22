package com.example.newbiechen.ireader.ui.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.BillboardBean
import com.example.newbiechen.ireader.model.bean.LeaderBoardLabel
import com.example.newbiechen.ireader.model.bean.LeaderBoardName
import com.example.newbiechen.ireader.utils.Constant

class LeaderBoardAdapter(data: List<MultiItemEntity>, val detail: (BillboardBean) -> Unit) : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(data) {
    init {
        addItemType(TYPE_LABEL, R.layout.item_book_category_label)
        addItemType(TYPE_NAME, R.layout.item_billboard_group)
        addItemType(TYPE_OTHER, R.layout.item_billboard_group)
    }
    override fun convert(helper: BaseViewHolder, item: MultiItemEntity) {
        when (item.itemType) {
            TYPE_LABEL -> {
                val label = item as LeaderBoardLabel
                helper.setText(R.id.tv_label, label.label)
            }
            TYPE_NAME -> {
                val name = item as LeaderBoardName
                helper.setImageResource(R.id.billboard_group_iv_symbol, R.drawable.ic_billboard_collapse)
                helper.setText(R.id.billboard_group_tv_name, name.title)
                helper.setOnClickListener(R.id.rl_container) {
                    val position = helper.adapterPosition
                    if (name.isExpanded) {
                        collapse(position)
                    } else {
                        expand(position)
                    }
                }

            }
            TYPE_OTHER -> {
                val bean = item as BillboardBean
                helper.setText(R.id.billboard_group_tv_name, bean.title)
                val billboard_group_iv_symbol = helper.getView<ImageView>(R.id.billboard_group_iv_symbol)
                Glide.with(billboard_group_iv_symbol.context)
                        .load(Constant.IMG_BASE_URL + bean.cover)
                        .placeholder(R.drawable.ic_loadding)
                        .error(R.drawable.ic_load_error)
                        .fitCenter()
                        .into(billboard_group_iv_symbol)
                helper.setOnClickListener(R.id.rl_container) {
                    detail(bean)
                }
            }
        }

    }

    companion object {
        const val TYPE_LABEL = 0xff01
        const val TYPE_NAME = 0xff02
        const val TYPE_OTHER = 0xff03
    }

}