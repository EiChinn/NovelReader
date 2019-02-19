package com.example.newbiechen.ireader.service

import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.text.TextUtils
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.RxBus
import com.example.newbiechen.ireader.db.entity.BookChapter
import com.example.newbiechen.ireader.event.DeleteResponseEvent
import com.example.newbiechen.ireader.event.DeleteTaskEvent
import com.example.newbiechen.ireader.event.DownloadMessage
import com.example.newbiechen.ireader.model.bean.DownloadTaskBean
import com.example.newbiechen.ireader.model.local.BookRepository
import com.example.newbiechen.ireader.model.local.LocalRepository
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.ui.base.BaseService
import com.example.newbiechen.ireader.utils.BookManager
import com.example.newbiechen.ireader.utils.LogUtils
import com.example.newbiechen.ireader.utils.NetworkUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.Executors

/**
 * Created by newbiechen on 17-5-10.
 */

class DownloadService : BaseService() {


    //线程池
    private val mSingleExecutor = Executors.newSingleThreadExecutor()
    //加载队列
    private val mDownloadTaskQueue = Collections.synchronizedList(ArrayList<DownloadTaskBean>())
    //Handler
    private var mHandler: Handler? = null

    //包含所有的DownloadTask
    private var mDownloadTaskList: MutableList<DownloadTaskBean>? = null

    private var mDownloadListener: OnDownloadListener? = null
    private var isBusy = false
    private var isCancel = false
    override fun onCreate() {
        super.onCreate()
        mHandler = Handler(mainLooper)
        //从数据库中获取所有的任务
        mDownloadTaskList = LocalRepository.instance.getDownloadTaskList()
    }

    override fun onBind(intent: Intent): IBinder? {
        return TaskBuilder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //接受创建的DownloadTask
        val disposable = RxBus.getInstance()
                .toObservable(DownloadTaskBean::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { event ->
                    //判断任务是否为轮询标志
                    //判断任务是否存在，并修改任务
                    if (TextUtils.isEmpty(event.bookId) || !checkAndAlterDownloadTask(event)) {
                        addToExecutor(event)
                    }
                }
        addDisposable(disposable)

        //是否删除数据的问题
        val deleteDisp = RxBus.getInstance()
                .toObservable(DeleteTaskEvent::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (collBook) ->
                    //判断是否该数据存在加载列表中
                    var isDelete = true
                    for (bean in mDownloadTaskQueue) {
                        if (bean.bookId == collBook.bookId) {
                            isDelete = false
                            break
                        }
                    }
                    //如果不存在则删除List中的task
                    if (isDelete) {
                        //
                        val taskIt = mDownloadTaskList!!.iterator()
                        while (taskIt.hasNext()) {
                            val task = taskIt.next()
                            if (task.bookId == collBook.bookId) {
                                taskIt.remove()
                            }
                        }
                    }
                    //返回状态
                    RxBus.getInstance().post(DeleteResponseEvent(isDelete, collBook))
                }
        addDisposable(deleteDisp)
        return super.onStartCommand(intent, flags, startId)
    }


    /**
     * 1. 查看是否任务已存在
     * 2. 修改DownloadTask的 taskName 和 list
     * @return
     */
    private fun checkAndAlterDownloadTask(newTask: DownloadTaskBean): Boolean {
        var isExist = false
        for (downloadTask in mDownloadTaskList!!) {
            //如果不相同则不往下执行，往下执行都是存在相同的情况
            if (downloadTask.bookId != newTask.bookId) continue

            if (downloadTask.status == DownloadTaskBean.STATUS_FINISH) {
                //判断是否newTask是已完成
                if (downloadTask.lastChapter == newTask.lastChapter) {
                    isExist = true

                    //发送回去已缓存
                    postMessage("当前书籍已缓存")
                } else if (downloadTask.lastChapter > newTask.lastChapter - newTask.bookChapters.size) {
                    //删除掉已经完成的章节
                    val remainChapterBeans = newTask.bookChapters
                            .subList(downloadTask.lastChapter,
                                    newTask.lastChapter)
                    val taskName = newTask.taskName + getString(R.string.nb_download_chapter_scope,
                            downloadTask.lastChapter, newTask.lastChapter)
                    //重置任务
                    newTask.bookChapters = remainChapterBeans
                    newTask.taskName = taskName

                    //发送添加到任务的提示
                    postMessage("成功添加到缓存队列")
                }//判断，是否已完成的章节的起始点比新Task大，如果更大则表示新Task中的该章节已被加载，所以需要剪切
            } else {
                isExist = true
                //发送回去:已经在加载队列中。
                postMessage("任务已存在")
            }//表示该任务已经在 下载、等待、暂停、网络错误中
        }
        //重置名字
        if (!isExist) {
            val taskName = newTask.taskName + getString(R.string.nb_download_chapter_scope,
                    1, newTask.lastChapter)
            newTask.taskName = taskName
            postMessage("成功添加到缓存队列")
        }
        return isExist
    }

    private fun addToExecutor(taskEvent: DownloadTaskBean) {

        //判断是否为轮询请求
        if (!TextUtils.isEmpty(taskEvent.bookId)) {

            if (!mDownloadTaskList!!.contains(taskEvent)) {
                //加入总列表中，表示创建，修改CollBean的状态。
                mDownloadTaskList!!.add(taskEvent)
            }
            // 添加到下载队列
            mDownloadTaskQueue.add(taskEvent)
        }

        // 从队列顺序取出第一条下载
        if (mDownloadTaskQueue.size > 0 && !isBusy) {
            isBusy = true
            executeTask(mDownloadTaskQueue[0])
        }
    }

    private fun executeTask(taskEvent: DownloadTaskBean) {
        val runnable = {

            taskEvent.status = DownloadTaskBean.STATUS_LOADING

            var result = LOAD_NORMAL
            val bookChapters = taskEvent.bookChapters

            //调用for循环，下载数据
            for (i in taskEvent.currentChapter until bookChapters.size) {

                val bookChapter = bookChapters[i]
                //首先判断该章节是否曾经被加载过 (从文件中判断)
                if (BookManager.isChapterCached(taskEvent.bookId, bookChapter.title)) {

                    //设置任务进度
                    taskEvent.currentChapter = i

                    //章节加载完成
                    postDownloadChange(taskEvent, DownloadTaskBean.STATUS_LOADING, i.toString() + "")

                    //无需进行下一步
                    continue
                }

                //判断网络是否出问题
                if (!NetworkUtils.isAvailable()) {
                    //章节加载失败
                    result = LOAD_ERROR
                    break
                }

                if (isCancel) {
                    result = LOAD_PAUSE
                    isCancel = false
                    break
                }

                //加载数据
                result = loadChapter(taskEvent.bookId, bookChapter)
                //章节加载完成
                if (result == LOAD_NORMAL) {
                    taskEvent.currentChapter = i
                    postDownloadChange(taskEvent, DownloadTaskBean.STATUS_LOADING, i.toString() + "")
                } else {
                    //遇到错误退出
                    break
                }//章节加载失败
            }


            if (result == LOAD_NORMAL) {
                //存储DownloadTask的状态
                taskEvent.status = DownloadTaskBean.STATUS_FINISH//Task的状态
                taskEvent.currentChapter = taskEvent.bookChapters.size//当前下载的章节数量
                taskEvent.size = BookManager.getBookSize(taskEvent.bookId)//Task的大小

                //发送完成状态
                postDownloadChange(taskEvent, DownloadTaskBean.STATUS_FINISH, "下载完成")
            } else if (result == LOAD_ERROR) {
                taskEvent.status = DownloadTaskBean.STATUS_ERROR//Task的状态
                //任务加载失败
                postDownloadChange(taskEvent, DownloadTaskBean.STATUS_ERROR, "资源或网络错误")
            } else if (result == LOAD_PAUSE) {
                taskEvent.status = DownloadTaskBean.STATUS_PAUSE//Task的状态
                postDownloadChange(taskEvent, DownloadTaskBean.STATUS_PAUSE, "暂停加载")
            } else if (result == LOAD_DELETE) {
                //没想好怎么做
            }

            //存储状态
            LocalRepository.instance.saveDownloadTask(taskEvent)

            //轮询下一个事件，用RxBus用来保证事件是在主线程

            //移除完成的任务
            mDownloadTaskQueue.remove(taskEvent)
            //设置为空闲
            isBusy = false
            //轮询
            post(DownloadTaskBean())
        }
        mSingleExecutor.execute(runnable)
    }

    private fun loadChapter(folderName: String, bean: BookChapter): Int {
        //加载的结果参数
        val result = intArrayOf(LOAD_NORMAL)

        //问题:(这里有个问题，就是body其实比较大，如何获取数据流而不是对象，)是不是直接使用OkHttpClient交互会更好一点
        val disposable = RemoteRepository.instance
                .getChapterInfo(bean.link)
                //表示在当前环境下执行
                .subscribe(
                        { (_, body) ->
                            //TODO:这里文件的名字用的是BookChapter的title,而不是chapter的title。
                            //原因是Chapter的title可能重复，但是BookChapter的title不会重复
                            //BookChapter的title = 卷名 + 章节名 chapter 的 title 就是章节名。。
                            BookRepository.instance
                                    .saveChapterInfo(folderName, bean.title, body)
                        },
                        { e ->
                            //当前进度加载错误（这里需要判断是什么问题，根据相应的问题做出相应的回答）
                            LogUtils.e(e.toString())
                            //设置加载结果
                            result[0] = LOAD_ERROR
                        }
                )
        addDisposable(disposable)
        return result[0]
    }

    private fun postDownloadChange(task: DownloadTaskBean, status: Int, msg: String) {
        if (mDownloadListener != null) {
            val position = mDownloadTaskList!!.indexOf(task)
            //通过handler,切换回主线程
            mHandler!!.post {
                mDownloadListener!!.onDownloadChange(
                        position, status, msg)
            }
        }
    }

    private fun postMessage(msg: String) {
        RxBus.getInstance().post(DownloadMessage(msg))
    }

    private fun post(task: DownloadTaskBean) {
        RxBus.getInstance().post(task)
    }


    override fun onUnbind(intent: Intent): Boolean {

        mDownloadListener = null
        return super.onUnbind(intent)
    }

    internal inner class TaskBuilder : Binder(), IDownloadManager {
        override val downloadTaskList: List<DownloadTaskBean>
            get() = Collections.unmodifiableList(mDownloadTaskList)

        override fun setOnDownloadListener(listener: OnDownloadListener) {
            mDownloadListener = listener
        }

        override fun setDownloadStatus(taskName: String, status: Int) {
            //修改某个Task的状态
            when (status) {
                //加入缓存队列
                DownloadTaskBean.STATUS_WAIT -> for (i in mDownloadTaskList!!.indices) {
                    val bean = mDownloadTaskList!![i]
                    if (taskName == bean.taskName) {
                        bean.status = DownloadTaskBean.STATUS_WAIT
                        mDownloadListener!!.onDownloadResponse(i, DownloadTaskBean.STATUS_WAIT)
                        addToExecutor(bean)
                        break
                    }
                }
                //从缓存队列中删除
                DownloadTaskBean.STATUS_PAUSE -> {
                    val it = mDownloadTaskQueue.iterator()
                    while (it.hasNext()) {
                        val bean = it.next()
                        if (bean.taskName == taskName) {
                            if (bean.status == DownloadTaskBean.STATUS_LOADING && bean.taskName == taskName) {
                                isCancel = true
                                break
                            } else {
                                bean.status = DownloadTaskBean.STATUS_PAUSE
                                mDownloadTaskQueue.remove(bean)
                                val position = mDownloadTaskList!!.indexOf(bean)
                                mDownloadListener!!.onDownloadResponse(position, DownloadTaskBean.STATUS_PAUSE)
                                break
                            }
                        }
                    }
                }
            }
        }

        //首先判断是否在加载队列中。
        //如果在加载队列中首先判断是否正在下载，
        //然后判断是否在完成队列中。
    }


    interface IDownloadManager {
        val downloadTaskList: List<DownloadTaskBean>
        fun setOnDownloadListener(listener: OnDownloadListener)
        fun setDownloadStatus(taskName: String, status: Int)
    }

    interface OnDownloadListener {
        /**
         *
         * @param pos : Task在item中的位置
         * @param status : Task的状态
         * @param msg: 传送的Msg
         */
        fun onDownloadChange(pos: Int, status: Int, msg: String)

        /**
         * 回复
         */
        fun onDownloadResponse(pos: Int, status: Int)
    }

    companion object {
        //加载状态
        private const val LOAD_ERROR = -1
        private const val LOAD_NORMAL = 0
        private const val LOAD_PAUSE = 1
        private const val LOAD_DELETE = 2 //正在加载时候，用户删除收藏书籍的情况。
    }
}
