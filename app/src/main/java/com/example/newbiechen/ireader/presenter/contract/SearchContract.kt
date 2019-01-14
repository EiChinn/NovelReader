package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.model.bean.packages.SearchBookPackage
import com.example.newbiechen.ireader.ui.base.BaseContract

interface SearchContract {
    interface View : BaseContract.BaseView {
        fun finishHotWords(hotWords: List<String>)

        fun finishKeyWords(hotWords: List<String>)

        fun finishBooks(books: List<@JvmSuppressWildcards SearchBookPackage.BooksBean>)

        fun errorBooks()
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun searchHotWord()

        //搜索提示
        fun searchKeyWord(query: String)

        //搜索书籍
        fun searchBook(query: String)
    }
}