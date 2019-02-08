package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.model.bean.BookCommentBean
import com.example.newbiechen.ireader.model.flag.BookDistillate
import com.example.newbiechen.ireader.model.flag.BookSort
import com.example.newbiechen.ireader.model.flag.CommunityType
import com.example.newbiechen.ireader.ui.base.BaseContract

interface DiscCommentContract{
    interface View : BaseContract.BaseView {
        fun finishRefresh(beans: List<@JvmSuppressWildcards BookCommentBean>)
        fun finishLoading(beans: List<@JvmSuppressWildcards BookCommentBean>)
        fun showErrorTip()
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun firstLoading(block: CommunityType, sort: BookSort, start: Int, limited: Int, distillate: BookDistillate)
        fun refreshComment(block: CommunityType, sort: BookSort, start: Int, limited: Int, distillate: BookDistillate)
        fun loadingComment(block: CommunityType, sort: BookSort, start: Int, limited: Int, distillate: BookDistillate)
        fun saveComment(beans: List<@JvmSuppressWildcards BookCommentBean>)
    }
}