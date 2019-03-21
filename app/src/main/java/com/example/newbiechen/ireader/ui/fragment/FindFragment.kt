package com.example.newbiechen.ireader.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.SectionBean
import com.example.newbiechen.ireader.model.flag.FindType
import com.example.newbiechen.ireader.ui.activity.BillboardActivity
import com.example.newbiechen.ireader.ui.activity.BookCategoryActivity
import com.example.newbiechen.ireader.ui.activity.BookListActivity
import com.example.newbiechen.ireader.ui.adapter.SectionAdapter
import com.example.newbiechen.ireader.ui.base.BaseFragment
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_find.*
import org.jetbrains.anko.support.v4.startActivity
import java.util.*

/**
 * Created by newbiechen on 17-4-15.
 */

class FindFragment : BaseFragment() {
    /*******************Object */
    private lateinit var mAdapter: SectionAdapter

    override fun getContentId() = R.layout.fragment_find

    override fun initWidget(savedInstanceState: Bundle?) {
        val sections = ArrayList<SectionBean>()
        for (type in FindType.values()) {
            sections.add(SectionBean(type.getTypeName(), type.iconId))
        }

        mAdapter = SectionAdapter(sections)
        find_rv_content.setHasFixedSize(true)
        find_rv_content.layoutManager = LinearLayoutManager(context)
        find_rv_content.addItemDecoration(DividerItemDecoration(context!!))
        find_rv_content.adapter = mAdapter
    }


    override fun initClick() {
        mAdapter.setOnItemClickListener { _, _, position ->
            //跳转
            when ( FindType.values()[position]) {
                FindType.TOP -> startActivity<BillboardActivity>()
                FindType.SORT -> startActivity<BookCategoryActivity>()
                FindType.TOPIC -> startActivity<BookListActivity>()
                FindType.LISTEN -> startActivity<BookCategoryActivity>()
            }
        }

    }

    override fun onDestroy() {
        if (this::mAdapter.isInitialized) {
            mAdapter.onItemClickListener = null
            find_rv_content?.adapter = null
        }
        super.onDestroy()
    }
}
