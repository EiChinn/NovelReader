package com.example.newbiechen.ireader.presenter

import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.presenter.contract.BillBookContract
import com.example.newbiechen.ireader.ui.base.RxPresenter
import com.example.newbiechen.ireader.utils.LogUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by newbiechen on 17-5-3.
 */

class BillBookPresenter : RxPresenter<BillBookContract.View>(), BillBookContract.Presenter {
    override fun refreshBookBrief(billId: String) {
        val remoteDisp = RemoteRepository.instance
                .getBillBooks(billId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { beans ->
                            mView.finishRefresh(beans)
                            mView.complete()
                        },
                        { e ->
                            mView.showError()
                            LogUtils.e(e.toString())
                        }
                )
        addDisposable(remoteDisp)
    }
}
