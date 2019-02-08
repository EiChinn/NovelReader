package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.model.bean.packages.BillboardPackage
import com.example.newbiechen.ireader.ui.base.BaseContract

interface BillboardContract {
    interface View : BaseContract.BaseView {
        fun finishRefresh(beans: BillboardPackage)
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun loadBillboardList()
    }
}