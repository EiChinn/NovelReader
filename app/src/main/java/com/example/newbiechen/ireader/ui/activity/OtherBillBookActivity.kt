package com.example.newbiechen.ireader.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.BillBookBean
import com.example.newbiechen.ireader.presenter.BillBookPresenter
import com.example.newbiechen.ireader.presenter.contract.BillBookContract
import com.example.newbiechen.ireader.ui.adapter.BillBookAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPActivity
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_refresh_list.*

/**
 * Created by newbiechen on 17-5-3.
 */

class OtherBillBookActivity : BaseMVPActivity<BillBookContract.View, BillBookContract.Presenter>(), BillBookContract.View {
    private var mBillBookAdapter: BillBookAdapter? = null
    /** */
    private var mBillId: String? = null
    private var mBillName: String? = null

    override fun getContentId(): Int {
        return R.layout.activity_refresh_list
    }

    override fun bindPresenter(): BillBookContract.Presenter {
        return BillBookPresenter()
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        if (savedInstanceState != null) {
            mBillId = savedInstanceState.getString(EXTRA_BILL_ID)
            mBillName = savedInstanceState.getString(EXTRA_BILL_NAME)
        } else {
            mBillId = intent.getStringExtra(EXTRA_BILL_ID)
            mBillName = intent.getStringExtra(EXTRA_BILL_NAME)
        }
    }

    override fun setUpToolbar(toolbar: Toolbar) {
        super.setUpToolbar(toolbar)
        toolbar.title = mBillName
    }

    override fun initWidget() {
        super.initWidget()
        setUpAdapter()
    }

    override fun processLogic() {
        super.processLogic()
        refresh_layout!!.showLoading()
        mPresenter.refreshBookBrief(mBillId!!)
    }

    private fun setUpAdapter() {
        refresh_rv_content!!.layoutManager = LinearLayoutManager(this)
        refresh_rv_content!!.addItemDecoration(DividerItemDecoration(this))
        mBillBookAdapter = BillBookAdapter()
        refresh_rv_content!!.adapter = mBillBookAdapter
    }

    override fun finishRefresh(beans: List<BillBookBean>) {
        mBillBookAdapter!!.refreshItems(beans)
    }

    override fun showError() {
        refresh_layout!!.showError()
    }

    override fun complete() {
        refresh_layout!!.showFinish()
    }

    /** */
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString(EXTRA_BILL_ID, mBillId)
        outState.putString(EXTRA_BILL_NAME, mBillName)
    }

    companion object {
        private const val EXTRA_BILL_ID = "extra_bill_id"
        private const val EXTRA_BILL_NAME = "extra_bill_name"
        fun startActivity(context: Context, billName: String, billId: String) {
            val intent = Intent(context, OtherBillBookActivity::class.java)
            intent.putExtra(EXTRA_BILL_ID, billId)
            intent.putExtra(EXTRA_BILL_NAME, billName)
            context.startActivity(intent)
        }
    }
}
