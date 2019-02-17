package com.example.newbiechen.ireader.presenter

import com.example.newbiechen.ireader.model.bean.packages.SearchBooksBean
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.presenter.contract.SearchContract
import com.example.newbiechen.ireader.ui.base.RxPresenter
import com.example.newbiechen.ireader.utils.LogUtils
import com.example.newbiechen.ireader.utils.RxUtils
import io.reactivex.SingleTransformer

/**
 * Created by newbiechen on 17-6-2.
 */

class SearchPresenter : RxPresenter<SearchContract.View>(), SearchContract.Presenter {

    override fun searchHotWord() {
        val disp = RemoteRepository.instance
                .hotWords
                .compose<List<String>>(SingleTransformer<List<String>, List<String>> { RxUtils.toSimpleSingle(it) })
                .subscribe(
                        { bean -> mView.finishHotWords(bean) },
                        { e -> LogUtils.e(e.toString()) }
                )
        addDisposable(disp)
    }

    override fun searchKeyWord(query: String) {
        val disp = RemoteRepository.instance
                .getKeyWords(query)
                .compose<List<String>>(SingleTransformer<List<String>, List<String>> { RxUtils.toSimpleSingle(it) })
                .subscribe(
                        { bean -> mView.finishKeyWords(bean) },
                        { e -> LogUtils.e(e.toString()) }
                )
        addDisposable(disp)
    }

    override fun searchBook(query: String) {
        val disp = RemoteRepository.instance
                .getSearchBooks(query)
                .compose<List<SearchBooksBean>>(SingleTransformer<List<SearchBooksBean>, List<SearchBooksBean>> { RxUtils.toSimpleSingle(it) })
                .subscribe(
                        { bean -> mView.finishBooks(bean) },
                        { e ->
                            LogUtils.e(e.toString())
                            mView.errorBooks()
                        }
                )
        addDisposable(disp)
    }
}
