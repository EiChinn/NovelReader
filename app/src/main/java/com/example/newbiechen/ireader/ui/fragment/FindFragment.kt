package com.example.newbiechen.ireader.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.SectionBean
import com.example.newbiechen.ireader.model.flag.FindType
import com.example.newbiechen.ireader.ui.activity.BillboardActivity
import com.example.newbiechen.ireader.ui.activity.BookListActivity
import com.example.newbiechen.ireader.ui.activity.BookSortActivity
import com.example.newbiechen.ireader.ui.adapter.SectionAdapter
import com.example.newbiechen.ireader.ui.base.BaseFragment
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_find.*
import java.util.*

/**
 * Created by newbiechen on 17-4-15.
 */

class FindFragment : BaseFragment() {
    /*******************Object */
    internal var mAdapter: SectionAdapter? = null

    override fun getContentId(): Int {
        return R.layout.fragment_find
    }

    override fun initWidget(savedInstanceState: Bundle?) {
        setUpAdapter()
    }

    private fun setUpAdapter() {
        val sections = ArrayList<SectionBean>()
        for (type in FindType.values()) {
            sections.add(SectionBean(type.getTypeName(), type.iconId))
        }

        mAdapter = SectionAdapter()
        find_rv_content.setHasFixedSize(true)
        find_rv_content.layoutManager = LinearLayoutManager(context)
        find_rv_content.addItemDecoration(DividerItemDecoration(context!!))
        find_rv_content.adapter = mAdapter
        mAdapter!!.addItems(sections)
    }


    override fun initClick() {
        mAdapter!!.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                val type = FindType.values()[pos]
                val intent: Intent
                //跳转
                when (type) {
                    FindType.TOP -> {
                        intent = Intent(context, BillboardActivity::class.java)
                        startActivity(intent)
                    }
                    FindType.SORT -> {
                        intent = Intent(context, BookSortActivity::class.java)
                        startActivity(intent)
                    }
                    FindType.TOPIC -> {
                        intent = Intent(context, BookListActivity::class.java)
                        startActivity(intent)
                    }
                }
            }

        })

    }

    override fun onDestroy() {
        find_rv_content.adapter = null
        super.onDestroy()
    }
}
