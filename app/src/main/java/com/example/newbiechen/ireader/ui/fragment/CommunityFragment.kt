package com.example.newbiechen.ireader.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.SectionBean
import com.example.newbiechen.ireader.model.flag.CommunityType
import com.example.newbiechen.ireader.ui.activity.BookDiscussionActivity
import com.example.newbiechen.ireader.ui.adapter.SectionAdapter
import com.example.newbiechen.ireader.ui.base.BaseFragment
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_community.*
import org.jetbrains.anko.support.v4.startActivity
import java.util.*

/**
 * Created by newbiechen on 17-4-15.
 * 社区 tab 页
 */

class CommunityFragment : BaseFragment() {

    private lateinit var mAdapter: SectionAdapter

    override fun getContentId() = R.layout.fragment_community

    /***********************************init method */

    override fun initWidget(savedInstanceState: Bundle?) {
        val sections = ArrayList<SectionBean>()

        /*觉得采用枚举会好一些，要不然就是在Constant中创建Map类*/
        for (type in CommunityType.values()) {
            sections.add(SectionBean(type.getTypeName(), type.iconId))
        }

        mAdapter = SectionAdapter(sections)
        mAdapter.setOnItemClickListener { _, _, position ->
            //根据类型，启动相应的Discussion区
            val type = CommunityType.values()[position]
            startActivity<BookDiscussionActivity>(BookDiscussionActivity.EXTRA_COMMUNITY to type)
        }

        community_rv_content.setHasFixedSize(true)
        community_rv_content.layoutManager = LinearLayoutManager(context)
        community_rv_content.addItemDecoration(DividerItemDecoration(context!!))
        community_rv_content.adapter = mAdapter
    }

    override fun onDestroy() {
        if (this::mAdapter.isInitialized) {
            mAdapter.onItemClickListener = null
            community_rv_content?.adapter = null
        }
        super.onDestroy()
    }
}
