package com.example.newbiechen.ireader.presenter

import android.text.TextUtils
import com.example.newbiechen.ireader.RxBus
import com.example.newbiechen.ireader.model.bean.BookChapterBean
import com.example.newbiechen.ireader.model.bean.BookDetailBean
import com.example.newbiechen.ireader.model.bean.CollBookBean
import com.example.newbiechen.ireader.model.bean.DownloadTaskBean
import com.example.newbiechen.ireader.model.local.BookRepository
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.presenter.contract.BookShelfContract
import com.example.newbiechen.ireader.ui.base.RxPresenter
import com.example.newbiechen.ireader.utils.*
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.SingleTransformer
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Created by newbiechen on 17-5-8.
 */

class BookShelfPresenter : RxPresenter<BookShelfContract.View>(), BookShelfContract.Presenter {

    override fun refreshCollBooks() {
        val collBooks = BookRepository
                .getInstance().collBooks
        mView.finishRefresh(collBooks)
    }

    override fun createDownloadTask(collBookBean: CollBookBean) {
        val task = DownloadTaskBean()
        task.taskName = collBookBean.title
        task.bookId = collBookBean._id
        task.bookChapters = collBookBean.bookChapters
        task.lastChapter = collBookBean.bookChapters.size

        RxBus.getInstance().post(task)
    }


    override fun loadRecommendBooks(gender: String) {
        val disposable = RemoteRepository.getInstance()
                .getRecommendBooks(gender)
                .doOnSuccess { collBooks ->
                    //更新目录
                    updateCategory(collBooks)
                    //异步存储到数据库中
                    BookRepository.getInstance()
                            .saveCollBooksWithAsync(collBooks)
                }
                .compose<List<CollBookBean>>(SingleTransformer<List<CollBookBean>, List<CollBookBean>> { RxUtils.toSimpleSingle(it) })
                .subscribe(
                        { beans ->
                            mView.finishRefresh(beans)
                            mView.complete()
                        },
                        { e ->
                            //提示没有网络
                            LogUtils.e(e.toString())
                            mView.showErrorTip(e.toString())
                            mView.complete()
                        }
                )
        addDisposable(disposable)
    }


    //需要修改
    override fun updateCollBooks(collBookBeans: List<CollBookBean>) {
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
            if (collBook.isLocal()) {
                it.remove()
            } else {
                observables.add(RemoteRepository.getInstance()
                        .getBookDetail(collBook._id))
            }
        }
        //zip可能不是一个好方法。
        Single.zip(observables) { objects ->
            val newCollBooks = ArrayList<CollBookBean>(objects.size)
            for (i in collBooks.indices) {
                val oldCollBook = collBooks[i]
                val newCollBook = (objects[i] as BookDetailBean).collBookBean
                //如果是oldBook是update状态，或者newCollBook与oldBook章节数不同
                if (oldCollBook.isUpdate() || oldCollBook.lastChapter != newCollBook.lastChapter) {
                    newCollBook.setUpdate(true)
                } else {
                    newCollBook.setUpdate(false)
                }
                newCollBook.lastRead = oldCollBook.lastRead
                // 保留当前书籍源
                newCollBook.currentSourceId = oldCollBook.currentSourceId
                newCollBook.currentSourceName = oldCollBook.currentSourceName
                newCollBooks.add(newCollBook)
                //存储到数据库中
                BookRepository.getInstance()
                        .saveCollBooks(newCollBooks)
            }
            newCollBooks
        }
                .compose<List<CollBookBean>>(SingleTransformer<List<CollBookBean>, List<CollBookBean>> { RxUtils.toSimpleSingle(it) })
                .subscribe(object : SingleObserver<List<CollBookBean>> {
                    override fun onSubscribe(d: Disposable) {
                        addDisposable(d)
                    }

                    override fun onSuccess(value: List<CollBookBean>) {
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

    //更新每个CollBook的目录
    private fun updateCategory(collBookBeans: List<CollBookBean>) {
        val observables = ArrayList<Single<List<BookChapterBean>>>(collBookBeans.size)
        for (bean in collBookBeans) {
            val bookSourceId = bean.currentSourceId
            val bookMixId = bean._id
            if (TextUtils.isEmpty(bookSourceId)) {
                observables.add(
                        RemoteRepository.getInstance().getBookSourceChapters(bookSourceId)
                )
            } else {
                observables.add(
                        RemoteRepository.getInstance().getBookMixChapters(bookMixId)
                )
            }
        }
        val it = collBookBeans.iterator()
        //执行在上一个方法中的子线程中
        Single.concat(observables)
                .subscribe { chapterList ->

                    for (bean in chapterList) {
                        bean.id = MD5Utils.strToMd5By16(bean.link)
                    }

                    val bean = it.next()
                    bean.lastRead = StringUtils.dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE)
                    bean.bookChapters = chapterList
                }
    }
}
