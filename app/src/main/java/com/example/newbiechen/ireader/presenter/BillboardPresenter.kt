package com.example.newbiechen.ireader.presenter

import com.example.newbiechen.ireader.model.bean.packages.BillboardPackage
import com.example.newbiechen.ireader.model.local.LocalRepository
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.presenter.contract.BillboardContract
import com.example.newbiechen.ireader.ui.base.RxPresenter

import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by newbiechen on 17-4-23.
 */

class BillboardPresenter : RxPresenter<BillboardContract.View>(), BillboardContract.Presenter {

    override fun loadBillboardList() {
        //这个最好是设定一个默认时间采用Remote加载，如果Remote加载失败则采用数据中的数据。我这里先写死吧
        val bean = LocalRepository.instance!!.getBillboardPackage()
        if (bean == null) {
            RemoteRepository.instance
                    .billboardPackage
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess { value ->
                        Schedulers.io().createWorker()
                                .schedule {
                                    LocalRepository.instance!!.saveBillboardPackage(value)
                                }
                    }
                    .subscribe(object : SingleObserver<BillboardPackage> {
                        override fun onSubscribe(d: Disposable) {
                            addDisposable(d)
                        }

                        override fun onSuccess(value: BillboardPackage) {
                            mView.finishRefresh(value)
                            mView.complete()
                        }

                        override fun onError(e: Throwable) {
                            mView.showError()
                        }
                    })
        } else {
            mView.finishRefresh(bean)
            mView.complete()
        }
    }
}
