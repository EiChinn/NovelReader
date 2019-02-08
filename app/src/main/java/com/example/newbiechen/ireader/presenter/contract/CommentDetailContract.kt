package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.model.bean.CommentBean
import com.example.newbiechen.ireader.model.bean.CommentDetailBean
import com.example.newbiechen.ireader.ui.base.BaseContract

interface CommentDetailContract{
    interface View : BaseContract.BaseView {
        fun finishRefresh(commentDetail: CommentDetailBean, bestComments: List<CommentBean>, comments: List<CommentBean>)
        fun finishLoad(comments: List<CommentBean>)
        fun showLoadError()
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun refreshCommentDetail(detailId: String, start: Int, limit: Int)
        fun loadComment(detailId: String, start: Int, limit: Int)
    }
}