package com.example.newbiechen.ireader.ui.activity

import android.view.View
import android.widget.ExpandableListView
import butterknife.BindView
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.BillboardBean
import com.example.newbiechen.ireader.model.bean.packages.BillboardPackage
import com.example.newbiechen.ireader.presenter.BillboardPresenter
import com.example.newbiechen.ireader.presenter.contract.BillboardContract
import com.example.newbiechen.ireader.ui.adapter.BillboardAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPActivity
import com.example.newbiechen.ireader.widget.RefreshLayout
import java.util.*

/**
 * Created by newbiechen on 17-4-23.
 * 数据的初始化，Expand的配置
 * 1. 查看Api制作数据Bean，制作相应的Adapter
 * 2. 初始化Expandable
 * 3. 制作数据获取类。
 */

class BillboardActivity : BaseMVPActivity<BillboardContract.View, BillboardContract.Presenter>(), BillboardContract.View, ExpandableListView.OnGroupClickListener {

    @BindView(R.id.billboard_rl_refresh)
    @JvmField internal var mRlRefresh: RefreshLayout? = null
    @BindView(R.id.billboard_elv_boy)
    @JvmField internal var mElvBoy: ExpandableListView? = null
    @BindView(R.id.billboard_elv_girl)
    @JvmField internal var mElvGirl: ExpandableListView? = null

    private var mBoyAdapter: BillboardAdapter? = null
    private var mGirlAdapter: BillboardAdapter? = null
    override fun getContentId(): Int {
        return R.layout.activity_bilboard
    }

    override fun initWidget() {
        super.initWidget()
        setUpAdapter()
    }

    private fun setUpAdapter() {
        mBoyAdapter = BillboardAdapter()
        mGirlAdapter = BillboardAdapter()
        mElvBoy!!.setAdapter(mBoyAdapter)
        mElvGirl!!.setAdapter(mGirlAdapter)
    }

    override fun initClick() {
        super.initClick()
        mRlRefresh!!.setOnReloadingListener { mPresenter.loadBillboardList() }

        mElvBoy!!.setOnGroupClickListener (object : ExpandableListView.OnGroupClickListener{
            override fun onGroupClick(parent: ExpandableListView?, v: View?, groupPosition: Int, id: Long): Boolean {
                if (groupPosition != mBoyAdapter!!.groupCount - 1) {
                    val (_id, _, _, _, monthRank, totalRank) = mBoyAdapter!!.getGroup(groupPosition)
                    BillBookActivity.startActivity(this@BillboardActivity, _id, monthRank, totalRank)
                    return true
                }
                return false
            }
        })
        mElvBoy!!.setOnChildClickListener(object : ExpandableListView.OnChildClickListener {

            override fun onChildClick(parent: ExpandableListView?, v: View?, groupPosition: Int, childPosition: Int, id: Long): Boolean {
                if (groupPosition == mBoyAdapter!!.groupCount - 1) {
                    val bean = mBoyAdapter!!.getChild(groupPosition, childPosition)
                    OtherBillBookActivity.startActivity(this@BillboardActivity, bean!!.title, bean._id)
                    return true
                }
                return false
            }
        })


        mElvGirl!!.setOnGroupClickListener(object : ExpandableListView.OnGroupClickListener {
            override fun onGroupClick(parent: ExpandableListView?, v: View?, groupPosition: Int, id: Long): Boolean {
                if (groupPosition != mGirlAdapter!!.groupCount - 1) {
                    val (_id, _, _, _, monthRank, totalRank) = mGirlAdapter!!.getGroup(groupPosition)
                    BillBookActivity.startActivity(this@BillboardActivity, _id, monthRank, totalRank)
                    return true
                }
                return false
            }

        })

        mElvGirl!!.setOnChildClickListener(object : ExpandableListView.OnChildClickListener {
            override fun onChildClick(parent: ExpandableListView?, v: View?, groupPosition: Int, childPosition: Int, id: Long): Boolean {
                if (groupPosition == mGirlAdapter!!.groupCount - 1) {
                    val bean = mGirlAdapter!!.getChild(groupPosition, childPosition)
                    OtherBillBookActivity.startActivity(this@BillboardActivity, bean!!.title, bean._id)
                    return true
                }
                return false
            }

        })
    }

    override fun bindPresenter(): BillboardContract.Presenter {
        return BillboardPresenter()
    }

    override fun processLogic() {
        super.processLogic()

        mRlRefresh!!.showLoading()
        mPresenter.loadBillboardList()
    }

    override fun finishRefresh(beans: BillboardPackage) {
        if (beans?.male == null || beans.female == null
                || beans.male!!.isEmpty() || beans.female!!.isEmpty()) {
            mRlRefresh!!.showEmpty()
            return
        }
        updateMaleBillboard(beans.male!!)
        updateFemaleBillboard(beans.female!!)
    }

    private fun updateMaleBillboard(disposes: List<BillboardBean>) {
        val maleGroups = ArrayList<BillboardBean>()
        val maleChildren = ArrayList<BillboardBean>()
        for (bean in disposes) {
            if (bean.isCollapse) {
                maleChildren.add(bean)
            } else {
                maleGroups.add(bean)
            }
        }
        maleGroups.add(BillboardBean("别人家的排行榜"))
        mBoyAdapter!!.addGroups(maleGroups)
        mBoyAdapter!!.addChildren(maleChildren)
    }

    private fun updateFemaleBillboard(disposes: List<BillboardBean>) {
        val femaleGroups = ArrayList<BillboardBean>()
        val femaleChildren = ArrayList<BillboardBean>()

        for (bean in disposes) {
            if (bean.isCollapse) {
                femaleChildren.add(bean)
            } else {
                femaleGroups.add(bean)
            }
        }
        femaleGroups.add(BillboardBean("别人家的排行榜"))
        mGirlAdapter!!.addGroups(femaleGroups)
        mGirlAdapter!!.addChildren(femaleChildren)
    }

    override fun showError() {
        mRlRefresh!!.showError()
    }

    override fun complete() {
        mRlRefresh!!.showFinish()
    }

    override fun onGroupClick(parent: ExpandableListView, v: View, groupPosition: Int, id: Long): Boolean {
        return false
    }
}
