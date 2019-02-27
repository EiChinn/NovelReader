package com.example.newbiechen.ireader.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.RxBus
import com.example.newbiechen.ireader.event.BookSubSortEvent
import com.example.newbiechen.ireader.model.bean.BookSubSortBean
import com.example.newbiechen.ireader.model.flag.BookSortListType
import com.example.newbiechen.ireader.ui.adapter.HorizonTagAdapter
import com.example.newbiechen.ireader.ui.base.BaseTabActivity
import com.example.newbiechen.ireader.ui.fragment.BookSortListFragment
import kotlinx.android.synthetic.main.activity_book_sort_list.*
import java.util.*

/**
 * Created by newbiechen on 17-4-24.
 * Book Sort List: 分类书籍列表
 */

class BookSortListActivity : BaseTabActivity() {

    /** */
    private var mTagAdapter: HorizonTagAdapter? = null
    /** */
    private var mSubSortBean: BookSubSortBean? = null
    private var mGender: String? = null

    override fun getContentId(): Int {
        return R.layout.activity_book_sort_list
    }

    override fun setUpToolbar(toolbar: Toolbar) {
        super.setUpToolbar(toolbar)
        supportActionBar!!.title = mSubSortBean!!.major
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        if (savedInstanceState != null) {
            mGender = savedInstanceState.getString(EXTRA_GENDER)
            mSubSortBean = savedInstanceState.getParcelable(EXTRA_SUB_SORT)
        } else {
            mGender = intent.getStringExtra(EXTRA_GENDER)
            mSubSortBean = intent.getParcelableExtra(EXTRA_SUB_SORT)
        }
    }

    override fun createTabFragments(): MutableList<Fragment> {
        val fragments = ArrayList<Fragment>()
        for (type in BookSortListType.values()) {
            fragments.add(BookSortListFragment.newInstance(mGender!!, mSubSortBean!!.major, type))
        }
        return fragments
    }

    override fun createTabTitles(): MutableList<String> {
        val titles = ArrayList<String>()
        for (type in BookSortListType.values()) {
            titles.add(type.typeName)
        }
        return titles
    }

    override fun initClick() {
        super.initClick()
        mTagAdapter!!.setOnItemClickListener { view, pos ->
            val subType = mTagAdapter!!.getItem(pos)
            RxBus.getInstance().post(BookSubSortEvent(subType))
        }
    }

    override fun initWidget() {
        super.initWidget()
        setUpAdapter()
    }

    private fun setUpAdapter() {
        mTagAdapter = HorizonTagAdapter()
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        book_sort_list_rv_tag!!.layoutManager = linearLayoutManager
        book_sort_list_rv_tag!!.adapter = mTagAdapter

        mSubSortBean!!.mins.add(0, "全部")
        mTagAdapter!!.addItems(mSubSortBean!!.mins)
    }

    /** */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_GENDER, mGender)
        outState.putParcelable(EXTRA_SUB_SORT, mSubSortBean)
    }

    companion object {
        private const val EXTRA_GENDER = "extra_gender"
        private const val EXTRA_SUB_SORT = "extra_sub_sort"

        fun startActivity(context: Context, gender: String, subSortBean: BookSubSortBean) {
            val intent = Intent(context, BookSortListActivity::class.java)
            intent.putExtra(EXTRA_GENDER, gender)
            intent.putExtra(EXTRA_SUB_SORT, subSortBean)
            context.startActivity(intent)
        }
    }
}
