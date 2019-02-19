package com.example.newbiechen.ireader.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.SectionBean
import com.example.newbiechen.ireader.model.flag.CommunityType
import com.example.newbiechen.ireader.ui.activity.BookDiscussionActivity
import com.example.newbiechen.ireader.ui.adapter.SectionAdapter
import com.example.newbiechen.ireader.ui.base.BaseFragment
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_community.*
import java.util.*

/**
 * Created by newbiechen on 17-4-15.
 * 讨论区
 */

class CommunityFragment : BaseFragment(), BaseListAdapter.OnItemClickListener {

    private var mAdapter: SectionAdapter? = null

    override fun getContentId(): Int {
        return R.layout.fragment_community
    }

    /***********************************init method */

    override fun initWidget(savedInstanceState: Bundle?) {
        setUpAdapter()
    }

    private fun setUpAdapter() {
        val sections = ArrayList<SectionBean>()

        /*觉得采用枚举会好一些，要不然就是在Constant中创建Map类*/
        for (type in CommunityType.values()) {
            sections.add(SectionBean(type.getTypeName(), type.iconId))
        }

        mAdapter = SectionAdapter()
        community_rv_content.setHasFixedSize(true)
        community_rv_content.layoutManager = LinearLayoutManager(context)
        community_rv_content.addItemDecoration(DividerItemDecoration(context!!))
        community_rv_content.adapter = mAdapter
        mAdapter!!.addItems(sections)
    }

    /****************************click method */

    override fun initClick() {
        mAdapter!!.setOnItemClickListener(this)
    }

    override fun onItemClick(view: View, pos: Int) {
        //根据类型，启动相应的Discussion区
        val type = CommunityType.values()[pos]
        BookDiscussionActivity.startActivity(context!!, type)
    }

    override fun onDestroy() {
        mAdapter!!.setOnItemClickListener(null)
        community_rv_content?.adapter = null
        super.onDestroy()
    }
}
