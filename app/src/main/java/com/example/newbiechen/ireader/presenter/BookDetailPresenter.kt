package com.example.newbiechen.ireader.presenter

import com.example.newbiechen.ireader.model.bean.BookDetailBean
import com.example.newbiechen.ireader.model.bean.CollBookBean
import com.example.newbiechen.ireader.model.local.BookRepository
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.presenter.contract.BookDetailContract
import com.example.newbiechen.ireader.ui.base.RxPresenter
import com.example.newbiechen.ireader.utils.LogUtils
import com.example.newbiechen.ireader.utils.MD5Utils
import com.example.newbiechen.ireader.utils.ToastUtils
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by newbiechen on 17-5-4.
 */

class BookDetailPresenter : RxPresenter<BookDetailContract.View>(), BookDetailContract.Presenter {
    private var bookId: String? = null

    override fun refreshBookDetail(bookId: String) {
        this.bookId = bookId
        refreshBook()
        refreshComment()
        refreshRecommend()

    }

    override fun addToBookShelf(collBookBean: CollBookBean) {
        val disposable = RemoteRepository.instance
                .getBookMixChapters(collBookBean._id)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { d -> mView.waitToBookShelf() }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { beans ->

                            //设置 id
                            for (bean in beans) {
                                bean.id = MD5Utils.strToMd5By16(bean.link)
                            }

                            //设置目录
                            collBookBean.bookChapters = beans
                            //存储收藏
                            BookRepository.instance
                                    .saveCollBookWithAsync(collBookBean)

                            mView.succeedToBookShelf()
                        },
                        { e ->
                            mView.errorToBookShelf()
                            LogUtils.e(e.toString())
                        }
                )
        addDisposable(disposable)
    }

    private fun refreshBook() {
        RemoteRepository
                .instance
                .getBookDetail(bookId!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<BookDetailBean> {
                    override fun onSubscribe(d: Disposable) {
                        addDisposable(d)
                    }

                    override fun onSuccess(value: BookDetailBean) {
                        mView.finishRefresh(value)
                        mView.complete()
                    }

                    override fun onError(e: Throwable) {
                        mView.showError()
                    }
                })
    }

    private fun refreshComment() {
        val disposable = RemoteRepository
                .instance
                .getHotComments(bookId!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { value -> mView.finishHotComment(value) },
                        { e -> ToastUtils.show(e.toString()) }
                )
        addDisposable(disposable)
    }

    private fun refreshRecommend() {
        val disposable = RemoteRepository
                .instance
                .getRecommendBookList(bookId!!, 3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { value -> mView.finishRecommendBookList(value) },
                        { e -> ToastUtils.show(e.toString()) }
                )
        addDisposable(disposable)
    }
}
