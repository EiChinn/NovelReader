package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.model.bean.CommentBean
import com.example.newbiechen.ireader.model.bean.HelpsDetailBean
import com.example.newbiechen.ireader.ui.base.BaseContract

interface HelpsDetailContract{
    interface View : BaseContract.BaseView {
        fun finishRefresh(commentDetail: HelpsDetailBean, bestComments: List<CommentBean>, comments: List<CommentBean>)
        fun finishLoad(comments: List<CommentBean>)
        fun showLoadError()
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun refreshHelpsDetail(detailId: String, start: Int, limit: Int)
        fun loadComment(detailId: String, start: Int, limit: Int)
    }
}