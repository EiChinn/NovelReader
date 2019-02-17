package com.example.newbiechen.ireader.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.BillBookBean
import com.example.newbiechen.ireader.presenter.BillBookPresenter
import com.example.newbiechen.ireader.presenter.contract.BillBookContract
import com.example.newbiechen.ireader.ui.activity.BookDetailActivity
import com.example.newbiechen.ireader.ui.adapter.BillBookAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPFragment
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_refresh_list.*

/**
 * Created by newbiechen on 17-5-3.
 */

class BillBookFragment : BaseMVPFragment<BillBookContract.View, BillBookContract.Presenter>(), BillBookContract.View {

    private var mBillBookAdapter: BillBookAdapter? = null
    /** */
    private var mBillId: String? = null

    override fun getContentId(): Int {
        return R.layout.fragment_refresh_list
    }

    override fun bindPresenter(): BillBookContract.Presenter {
        return BillBookPresenter()
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mBillId = arguments!!.getString(EXTRA_BILL_ID)
    }

    override fun initClick() {
        super.initClick()
        mBillBookAdapter!!.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener{
            override fun onItemClick(view: View, pos: Int) {
                val bookId = mBillBookAdapter!!.getItem(pos)._id
                BookDetailActivity.startActivity(context!!, bookId)
            }
        })
    }

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        setUpAdapter()
    }

    override fun processLogic() {
        super.processLogic()
        refresh_layout.showLoading()
        mPresenter.refreshBookBrief(mBillId!!)
    }

    private fun setUpAdapter() {
        refresh_rv_content.layoutManager = LinearLayoutManager(context)
        refresh_rv_content.addItemDecoration(DividerItemDecoration(context!!))
        mBillBookAdapter = BillBookAdapter()
        refresh_rv_content.adapter = mBillBookAdapter
    }

    override fun finishRefresh(beans: List<BillBookBean>) {
        mBillBookAdapter!!.refreshItems(beans)
    }

    override fun showError() {
        refresh_layout.showError()
    }

    override fun complete() {
        refresh_layout.showFinish()
    }

    companion object {
        private const val EXTRA_BILL_ID = "extra_bill_id"

        fun newInstance(billId: String): Fragment {
            val bundle = Bundle()
            bundle.putString(EXTRA_BILL_ID, billId)
            val fragment = BillBookFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
