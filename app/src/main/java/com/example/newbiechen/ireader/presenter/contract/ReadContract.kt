package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.model.bean.BookChapterBean
import com.example.newbiechen.ireader.ui.base.BaseContract
import com.example.newbiechen.ireader.widget.page.TxtChapter

interface ReadContract{
    interface View : BaseContract.BaseView {
        fun showCategory(bookChapterList: List<@JvmSuppressWildcards BookChapterBean>)
        fun finishChapter()
        fun errorChapter()
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun loadMixCategory(bookMixId: String)
        fun loadSourceCategory(bookBookId: String, bookMixId: String)
        fun loadChapter(bookId: String, bookChapterList: List<@JvmSuppressWildcards TxtChapter>)
    }
}