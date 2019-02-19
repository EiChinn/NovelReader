package com.example.newbiechen.ireader.presenter

import com.example.newbiechen.ireader.RxBus
import com.example.newbiechen.ireader.db.entity.CollBook
import com.example.newbiechen.ireader.model.bean.BookDetailBean
import com.example.newbiechen.ireader.model.bean.DownloadTaskBean
import com.example.newbiechen.ireader.model.local.BookRepository
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.presenter.contract.BookShelfContract
import com.example.newbiechen.ireader.ui.base.RxPresenter
import com.example.newbiechen.ireader.utils.LogUtils
import com.example.newbiechen.ireader.utils.RxUtils
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.SingleTransformer
import io.reactivex.disposables.Disposable

/**
 * Created by newbiechen on 17-5-8.
 */

class BookShelfPresenter : RxPresenter<BookShelfContract.View>(), BookShelfContract.Presenter {

    override fun refreshCollBooks() {
        val collBooks = BookRepository
                .instance.getAllCollBooks()
        mView.finishRefresh(collBooks)
    }

    override fun createDownloadTask(collBook: CollBook) {
        val task = DownloadTaskBean()
        task.taskName = collBook.title
        task.bookId = collBook.bookId
        val bookChapters = BookRepository.instance.getBookChapters(collBook.bookId)
        task.bookChapters = bookChapters
        task.lastChapter = bookChapters.size

        RxBus.getInstance().post(task)
    }

    //需要修改
    override fun updateCollBooks(collBookBeans: List<CollBook>) {
        if (collBookBeans == null || collBookBeans.isEmpty()) {
            mView.showError()
            return
        }
        val collBooks = ArrayList(collBookBeans)
        val observables = ArrayList<Single<BookDetailBean>>(collBooks.size)
        val it = collBooks.iterator()
        while (it.hasNext()) {
            val collBook = it.next()
            //删除本地文件
            if (collBook.isLocal) {
                it.remove()
            } else {
                observables.add(RemoteRepository.instance
                        .getBookDetail(collBook.bookId))
            }
        }
        //zip可能不是一个好方法。
        Single.zip(observables) { objects ->
            val newCollBooks = ArrayList<CollBook>(objects.size)
            for (i in collBooks.indices) {
                val oldCollBook = collBooks[i]
                val newCollBook = (objects[i] as BookDetailBean).getCollBook()!!
                //如果是oldBook是update状态，或者newCollBook与oldBook章节数不同
                newCollBook.isUpdate = oldCollBook.isUpdate || oldCollBook.lastChapter != newCollBook.lastChapter
                newCollBook.lastRead = oldCollBook.lastRead
                // 保留当前书籍源
                newCollBook.currentSourceId = oldCollBook.currentSourceId
                newCollBook.currentSourceName = oldCollBook.currentSourceName
                newCollBooks.add(newCollBook)
                //存储到数据库中
                BookRepository.instance.insertOrUpdateCollBooks(newCollBooks)
            }
            newCollBooks
        }
                .compose<List<CollBook>>(SingleTransformer<List<CollBook>, List<CollBook>> { RxUtils.toSimpleSingle(it) })
                .subscribe(object : SingleObserver<List<CollBook>> {
                    override fun onSubscribe(d: Disposable) {
                        addDisposable(d)
                    }

                    override fun onSuccess(value: List<CollBook>) {
                        //跟原先比较
                        mView.finishUpdate()
                        mView.complete()
                    }

                    override fun onError(e: Throwable) {
                        //提示没有网络
                        mView.showErrorTip(e.toString())
                        mView.complete()
                        LogUtils.e(e.toString())
                    }
                })
    }

}
