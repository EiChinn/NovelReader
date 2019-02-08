package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.model.bean.BillBookBean
import com.example.newbiechen.ireader.ui.base.BaseContract

interface BillBookContract {
    interface View : BaseContract.BaseView {
        fun finishRefresh(beans: List<BillBookBean>)
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun refreshBookBrief(billId: String)
    }
}