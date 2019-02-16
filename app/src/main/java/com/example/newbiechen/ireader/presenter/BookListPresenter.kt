package com.example.newbiechen.ireader.presenter

import com.example.newbiechen.ireader.model.bean.BookListBean
import com.example.newbiechen.ireader.model.flag.BookListType
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.presenter.contract.BookListContract
import com.example.newbiechen.ireader.ui.base.RxPresenter
import com.example.newbiechen.ireader.utils.LogUtils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by newbiechen on 17-5-1.
 */

class BookListPresenter : RxPresenter<BookListContract.View>(), BookListContract.Presenter {


    override fun refreshBookList(type: BookListType, tag: String, start: Int, limited: Int) {
        var tag = tag

        if (tag == "全本") {
            tag = ""
        }

        val refreshDispo = getBookListSingle(type, tag, start, limited)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { beans ->
                            mView.finishRefresh(beans)
                            mView.complete()
                        },
                        { e ->
                            mView.complete()
                            mView.showError()
                            LogUtils.e(e.toString())
                        }
                )
        addDisposable(refreshDispo)
    }

    override fun loadBookList(type: BookListType, tag: String, start: Int, limited: Int) {
        val refreshDispo = getBookListSingle(type, tag, start, limited)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { beans -> mView.finishLoading(beans) },
                        { e ->
                            mView.showLoadError()
                            LogUtils.e(e.toString())
                        }
                )
        addDisposable(refreshDispo)
    }

    private fun getBookListSingle(type: BookListType, tag: String, start: Int, limited: Int): Single<List<BookListBean>>? {
        var tag = tag
        var bookListSingle: Single<List<BookListBean>>? = null
        //数据类型转换
        var gender = ""
        if (tag == "男生") {
            gender = "male"
            tag = ""
        } else if (tag == "女生") {
            gender = "female"
            tag = ""
        }

        bookListSingle = when (type) {
            BookListType.HOT -> RemoteRepository.getInstance()
                    .getBookLists(type.netName, "collectorCount", start, limited, tag, gender)
            BookListType.NEWEST -> RemoteRepository.getInstance()
                    .getBookLists("all", type.netName, start, limited, tag, gender)
            BookListType.COLLECT -> RemoteRepository.getInstance()
                    .getBookLists("all", type.netName, start, limited, tag, gender)
        }
        return bookListSingle
    }
}
