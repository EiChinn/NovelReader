package com.example.newbiechen.ireader.widget.page


import com.example.newbiechen.ireader.db.entity.BookChapter
import com.example.newbiechen.ireader.db.entity.CollBook
import com.example.newbiechen.ireader.model.local.BookRepository
import com.example.newbiechen.ireader.utils.BookManager
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.FileUtils
import com.example.newbiechen.ireader.utils.StringUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

/**
 * Created by newbiechen on 17-5-29.
 * 网络页面加载器
 */

class NetPageLoader(pageView: PageView, collBook: CollBook) : PageLoader(pageView, collBook) {

    private fun convertTxtChapter(bookChapters: List<BookChapter>): MutableList<TxtChapter> {
        val txtChapters = ArrayList<TxtChapter>(bookChapters.size)
        for (bean in bookChapters) {
            val chapter = TxtChapter()
            chapter.bookId = bean.bookId
            chapter.title = bean.title
            chapter.link = bean.link
            txtChapters.add(chapter)
        }
        return txtChapters
    }

    override fun refreshChapterList() {
        if (bookChapters.isEmpty()) return
        checkRecordValid()

        // 将 BookChapter 转换成当前可用的 Chapter
        mChapterList = convertTxtChapter(bookChapters)
        isChapterListPrepare = true

        // 目录加载完成，执行回调操作。
        if (mPageChangeListener != null) {
            mPageChangeListener!!.onCategoryFinish(mChapterList)
        }

        // 如果章节未打开
        if (!isChapterOpen) {
            // 打开章节
            openChapter()
        }
    }

    @Throws(Exception::class)
    override fun getChapterReader(chapter: TxtChapter): BufferedReader? {
        val file = File(Constant.BOOK_CACHE_PATH + collBook.bookId
                + File.separator + chapter.title + FileUtils.SUFFIX_NB)
        if (!file.exists()) return null

        val reader = FileReader(file)
        return BufferedReader(reader)
    }

    override fun hasChapterData(chapter: TxtChapter): Boolean {
        return BookManager.isChapterCached(collBook.bookId, chapter.title)
    }

    // 装载上一章节的内容
    override fun parsePrevChapter(): Boolean {
        val isRight = super.parsePrevChapter()

        if (pageStatus == PageLoader.STATUS_FINISH) {
            loadPrevChapter()
        } else if (pageStatus == PageLoader.STATUS_LOADING) {
            loadCurrentChapter()
        }
        return isRight
    }

    // 装载当前章内容。
    override fun parseCurChapter(): Boolean {
        val isRight = super.parseCurChapter()

        if (pageStatus == PageLoader.STATUS_LOADING) {
            loadCurrentChapter()
        }
        return isRight
    }

    // 装载下一章节的内容
    override fun parseNextChapter(): Boolean {
        val isRight = super.parseNextChapter()

        if (pageStatus == PageLoader.STATUS_FINISH) {
            loadNextChapter()
        } else if (pageStatus == PageLoader.STATUS_LOADING) {
            loadCurrentChapter()
        }

        return isRight
    }

    /**
     * 加载当前页的前面两个章节
     */
    private fun loadPrevChapter() {
        if (mPageChangeListener != null) {
            val end = chapterPos
            var begin = end - 2
            if (begin < 0) {
                begin = 0
            }

            requestChapters(begin, end)
        }
    }

    /**
     * 加载前一页，当前页，后一页。
     */
    private fun loadCurrentChapter() {
        if (mPageChangeListener != null) {
            var begin = chapterPos
            var end = chapterPos

            // 是否当前不是最后一章
            if (end < mChapterList.size) {
                end += 1
                if (end >= mChapterList.size) {
                    end = mChapterList.size - 1
                }
            }

            // 如果当前不是第一章
            if (begin != 0) {
                begin -= 1
                if (begin < 0) {
                    begin = 0
                }
            }

            requestChapters(begin, end)
        }
    }

    /**
     * 加载当前页的后两个章节
     */
    private fun loadNextChapter() {
        if (mPageChangeListener != null) {

            // 提示加载后两章
            val begin = chapterPos + 1
            var end = begin + 1

            // 判断是否大于最后一章
            if (begin >= mChapterList.size) {
                // 如果下一章超出目录了，就没有必要加载了
                return
            }

            if (end > mChapterList.size) {
                end = mChapterList.size - 1
            }

            requestChapters(begin, end)
        }
    }

    private fun requestChapters(start: Int, end: Int) {
        var start = start
        var end = end
        // 检验输入值
        if (start < 0) {
            start = 0
        }

        if (end >= mChapterList.size) {
            end = mChapterList.size - 1
        }


        val chapters = ArrayList<TxtChapter>()

        // 过滤，哪些数据已经加载了
        for (i in start..end) {
            val txtChapter = mChapterList[i]
            if (!hasChapterData(txtChapter)) {
                chapters.add(txtChapter)
            }
        }

        if (!chapters.isEmpty()) {
            mPageChangeListener!!.requestChapters(chapters)
        }
    }

    override fun saveRecord() {
        super.saveRecord()
        if (isChapterListPrepare) {
            //表示当前CollBook已经阅读
            collBook.isUpdate = false
            collBook.lastRead = StringUtils.dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE)
            //直接更新
            //todo 重复处理？
            BookRepository.instance.updateCollBook(collBook)
        }
    }
}

