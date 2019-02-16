package com.example.newbiechen.ireader.presenter


import com.example.newbiechen.ireader.model.bean.BookChapterBean
import com.example.newbiechen.ireader.model.bean.ChapterInfoBean
import com.example.newbiechen.ireader.model.local.BookRepository
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.presenter.contract.ReadContract
import com.example.newbiechen.ireader.ui.base.RxPresenter
import com.example.newbiechen.ireader.utils.LogUtils
import com.example.newbiechen.ireader.utils.MD5Utils
import com.example.newbiechen.ireader.utils.RxUtils
import com.example.newbiechen.ireader.widget.page.TxtChapter
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.*

/**
 * Created by newbiechen on 17-5-16.
 */

class ReadPresenter : RxPresenter<ReadContract.View>(), ReadContract.Presenter {

    private var mChapterSub: Subscription? = null

    override fun loadMixCategory(bookMixId: String) {
        val disposable = RemoteRepository.getInstance()
                .getBookMixChapters(bookMixId)
                .doOnSuccess { bookChapterBeen ->
                    //进行设定BookChapter所属的书的id。
                    for (bookChapter in bookChapterBeen) {
                        bookChapter.id = MD5Utils.strToMd5By16(bookChapter.link)
                        bookChapter.bookId = bookMixId
                    }
                }
                .compose<List<BookChapterBean>>(SingleTransformer<List<BookChapterBean>, List<BookChapterBean>> { RxUtils.toSimpleSingle(it) })
                .subscribe(
                        { beans -> mView.showCategory(beans) },
                        { e ->
                            //TODO: Haven't grate conversation method.
                            LogUtils.e(e.toString())
                        }
                )
        addDisposable(disposable)
    }

    override fun loadSourceCategory(bookSourceId: String, bookMixId: String) {
        val disposable = RemoteRepository.getInstance()
                .getBookSourceChapters(bookSourceId)
                .doOnSuccess { bookChapterBeen ->
                    //进行设定BookChapter所属的书的id。
                    for (bookChapter in bookChapterBeen) {
                        bookChapter.id = MD5Utils.strToMd5By16(bookChapter.link)
                        bookChapter.bookId = bookMixId
                    }
                }
                .compose<List<BookChapterBean>>(SingleTransformer<List<BookChapterBean>, List<BookChapterBean>> { RxUtils.toSimpleSingle(it) })
                .subscribe(
                        { beans -> mView.showCategory(beans) },
                        { e ->
                            //TODO: Haven't grate conversation method.
                            LogUtils.e(e.toString())
                        }
                )
        addDisposable(disposable)
    }

    override fun loadChapter(bookId: String, bookChapters: List<TxtChapter>) {
        val size = bookChapters.size

        //取消上次的任务，防止多次加载
        if (mChapterSub != null) {
            mChapterSub!!.cancel()
        }

        val chapterInfos = ArrayList<Single<ChapterInfoBean>>(bookChapters.size)
        val titles = ArrayDeque<String>(bookChapters.size)

        // 将要下载章节，转换成网络请求。
        for (i in 0 until size) {
            val bookChapter = bookChapters[i]
            // 网络中获取数据
            val chapterInfoSingle = RemoteRepository.getInstance()
                    .getChapterInfo(bookChapter.link)

            chapterInfos.add(chapterInfoSingle)

            titles.add(bookChapter.title)
        }

        Single.concat(chapterInfos)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        object : Subscriber<ChapterInfoBean> {
                            internal var title = titles.poll()

                            override fun onSubscribe(s: Subscription) {
                                s.request(Integer.MAX_VALUE.toLong())
                                mChapterSub = s
                            }

                            override fun onNext(chapterInfoBean: ChapterInfoBean) {
                                //存储数据
                                BookRepository.getInstance().saveChapterInfo(
                                        bookId, title, chapterInfoBean.body
                                )
                                mView.finishChapter()
                                //将获取到的数据进行存储
                                title = titles.poll()
                            }

                            override fun onError(t: Throwable) {
                                //只有第一个加载失败才会调用errorChapter
                                if (bookChapters[0].title == title) {
                                    mView.errorChapter()
                                }
                                LogUtils.e(t.toString())
                            }

                            override fun onComplete() {}
                        }
                )
    }

    override fun detachView() {
        super.detachView()
        if (mChapterSub != null) {
            mChapterSub!!.cancel()
        }
    }

}
