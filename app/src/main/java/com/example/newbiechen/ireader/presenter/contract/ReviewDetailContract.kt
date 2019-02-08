package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.model.bean.CommentBean
import com.example.newbiechen.ireader.model.bean.ReviewDetailBean
import com.example.newbiechen.ireader.ui.base.BaseContract

interface ReviewDetailContract{
    interface View : BaseContract.BaseView {
        fun finishRefresh(reviewDetail: ReviewDetailBean, bestComments: List<CommentBean>, comments: List<CommentBean>)
        fun finishLoad(comments: List<CommentBean>)
        fun showLoadError()
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun refreshReviewDetail(detailId: String, start: Int, limit: Int)
        fun loadComment(detailId: String, start: Int, limit: Int)
    }
}