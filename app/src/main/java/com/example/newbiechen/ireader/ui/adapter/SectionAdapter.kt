package com.example.newbiechen.ireader.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.SectionBean

/**
 * Created by newbiechen on 17-4-16.
 */

class SectionAdapter(data: List<SectionBean>) : BaseQuickAdapter<SectionBean, BaseViewHolder>(R.layout.item_section, data) {
    override fun convert(helper: BaseViewHolder, item: SectionBean) {
        helper.setImageResource(R.id.section_iv_icon, item.drawableId)
        helper.setText(R.id.section_tv_name, item.name)
    }

}
