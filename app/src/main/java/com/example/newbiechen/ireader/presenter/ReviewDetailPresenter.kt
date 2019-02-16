package com.example.newbiechen.ireader.presenter

import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.presenter.contract.ReviewDetailContract
import com.example.newbiechen.ireader.ui.base.RxPresenter
import com.example.newbiechen.ireader.utils.LogUtils
import com.example.newbiechen.ireader.utils.RxUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by newbiechen on 17-4-30.
 */

class ReviewDetailPresenter : RxPresenter<ReviewDetailContract.View>(), ReviewDetailContract.Presenter {

    override fun refreshReviewDetail(detailId: String, start: Int, limit: Int) {
        val detailSingle = RemoteRepository
                .getInstance().getReviewDetail(detailId)

        val bestCommentsSingle = RemoteRepository
                .getInstance().getBestComments(detailId)

        val commentsSingle = RemoteRepository
                .getInstance().getDetailBookComments(detailId, start, limit)

        val detailDispo = RxUtils.toCommentDetail(detailSingle, bestCommentsSingle, commentsSingle)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { (detail, bestComments, comments) ->
                            mView.finishRefresh(detail,
                                    bestComments, comments)
                            mView.complete()
                        },
                        { e ->
                            mView.showError()
                            LogUtils.e(e.toString())
                        }
                )
        addDisposable(detailDispo)
    }

    override fun loadComment(detailId: String, start: Int, limit: Int) {
        val loadDispo = RemoteRepository.getInstance()
                .getDetailBookComments(detailId, start, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { bean -> mView.finishLoad(bean) },
                        { e ->
                            mView.showLoadError()
                            LogUtils.e(e.toString())
                        }
                )
        addDisposable(loadDispo)
    }
}
