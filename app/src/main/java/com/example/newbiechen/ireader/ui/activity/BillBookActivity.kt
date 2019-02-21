package com.example.newbiechen.ireader.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.ui.base.BaseTabActivity
import com.example.newbiechen.ireader.ui.fragment.BillBookFragment
import java.util.*

/**
 * Created by newbiechen on 17-5-3.
 * BillboardBookActivity:排行榜内的书籍详情
 */

class BillBookActivity : BaseTabActivity() {


    private lateinit var mWeekId: String
    private lateinit var mMonthId: String
    private lateinit var mTotalId: String

    override fun getContentId(): Int {
        return R.layout.activity_base_tab
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        if (savedInstanceState != null) {
            mWeekId = savedInstanceState.getString(EXTRA_WEEK_ID) ?: ""
            mMonthId = savedInstanceState.getString(EXTRA_MONTH_ID) ?: ""
            mTotalId = savedInstanceState.getString(EXTRA_TOTAL_ID) ?: ""
        } else {
            mWeekId = intent.getStringExtra(EXTRA_WEEK_ID) ?: ""
            mMonthId = intent.getStringExtra(EXTRA_MONTH_ID) ?: ""
            mTotalId = intent.getStringExtra(EXTRA_TOTAL_ID) ?: ""
        }
    }

    override fun createTabFragments(): MutableList<Fragment> {
        val fragments = ArrayList<Fragment>()
        fragments.add(BillBookFragment.newInstance(mWeekId))
        fragments.add(BillBookFragment.newInstance(mMonthId))
        fragments.add(BillBookFragment.newInstance(mTotalId))
        return fragments
    }

    override fun createTabTitles(): MutableList<String> {
        val titles = resources.getStringArray(R.array.nb_fragment_bill_book)
        return titles.toMutableList()
    }

    override fun setUpToolbar(toolbar: Toolbar) {
        super.setUpToolbar(toolbar)
        toolbar.title = "追书最热榜"
    }

    /** */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_WEEK_ID, mWeekId)
        outState.putString(EXTRA_MONTH_ID, mMonthId)
        outState.putString(EXTRA_TOTAL_ID, mTotalId)
    }

    companion object {
        private const val EXTRA_WEEK_ID = "extra_week_id"
        private const val EXTRA_MONTH_ID = "extra_month_id"
        private const val EXTRA_TOTAL_ID = "extra_total_id"
        fun startActivity(context: Context, weekId: String, monthId: String?, totalId: String?) {
            val intent = Intent(context, BillBookActivity::class.java)
            intent.putExtra(EXTRA_WEEK_ID, weekId)
            intent.putExtra(EXTRA_MONTH_ID, monthId)
            intent.putExtra(EXTRA_TOTAL_ID, totalId)

            context.startActivity(intent)
        }
    }
}
