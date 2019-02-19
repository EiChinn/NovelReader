package com.example.newbiechen.ireader.model.bean

import com.example.newbiechen.ireader.db.entity.BookChapter

/**
 * Created by newbiechen on 17-5-11.
 */
class DownloadTaskBean {

    //任务名称 -> 名称唯一不重复
    var taskName: String = ""
        set(taskName) {
            field = taskName
            for (bean in bookChapters) {
                bean.taskName = taskName
            }
        }
    //所属的bookId(外健)
    var bookId: String = ""

    /**
     * 这才是真正的列表使用类。
     *
     */
    var bookChapters: List<BookChapter> = emptyList()
        set(beans) {
            field = beans
            for (bean in bookChapters) {
                bean.taskName = taskName!!
            }
        }
    //章节的下载进度,默认为初始状态
    var currentChapter = 0
    //最后的章节
    var lastChapter = 0
    //状态:正在下载、下载完成、暂停、等待、下载错误。

    //多线程访问的问题，所以需要同步机制
    @Volatile
    var status = STATUS_WAIT
    //总大小 -> (完成之后才会赋值)
    var size: Long = 0

    companion object {
        const val STATUS_LOADING = 1
        const val STATUS_WAIT = 2
        const val STATUS_PAUSE = 3
        const val STATUS_ERROR = 4
        const val STATUS_FINISH = 5
    }

}
