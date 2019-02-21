package com.example.newbiechen.ireader.ui.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import com.example.newbiechen.ireader.R
import com.google.android.material.tabs.TabLayout

abstract class BaseTabActivity : BaseActivity() {
    /**************View***************/
    @BindView(R.id.tab_tl_indicator)
    protected lateinit var mTlIndicator: TabLayout
    @BindView(R.id.tab_vp)
    protected lateinit var mVp: ViewPager

    /************Params*******************/
    private lateinit var mFragmentList: MutableList<Fragment>
    private lateinit var mTitleList: MutableList<String>

    /**************abstract***********/
    protected abstract fun createTabFragments(): MutableList<Fragment>
    protected abstract fun createTabTitles(): MutableList<String>

    override fun initWidget() {
        super.initWidget()
        setUpTabLayout()

    }

    private fun setUpTabLayout() {
        mFragmentList = createTabFragments()
        mTitleList = createTabTitles()

        checkParamsIsRight()

        val adapter = TabFragmentPageAdapter(supportFragmentManager)
        mVp.adapter = adapter
        mVp.offscreenPageLimit = 3
        mTlIndicator.setupWithViewPager(mVp)


    }

    /**
     * 检查输入的参数是否正确。即Fragment和title是成对的。
     */
    private fun checkParamsIsRight() {
        if (mFragmentList.size != mTitleList.size) {
            throw IllegalArgumentException("fragment and title size must equal")
        }

    }

    override fun onDestroy() {
        mFragmentList.clear()
        mTitleList.clear()
        super.onDestroy()
    }


    inner class TabFragmentPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int) = mFragmentList[position]

        override fun getCount() = mFragmentList.size

        override fun getPageTitle(position: Int) = mTitleList[position]

    }


}