package com.example.newbiechen.ireader.widget.page

import com.example.newbiechen.ireader.db.entity.BookChapter
import com.example.newbiechen.ireader.db.entity.CollBook
import com.example.newbiechen.ireader.model.local.BookRepository
import com.example.newbiechen.ireader.model.local.Void
import com.example.newbiechen.ireader.utils.*
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.SingleOnSubscribe
import io.reactivex.SingleTransformer
import io.reactivex.disposables.Disposable
import java.io.*
import java.util.*
import java.util.regex.Pattern

/**
 * Created by newbiechen on 17-7-1.
 * 问题:
 * 1. 异常处理没有做好
 */

class LocalPageLoader(pageView: PageView, collBook: CollBook) : PageLoader(pageView, collBook) {

    //章节解析模式
    private var mChapterPattern: Pattern? = null
    //获取书本的文件
    private var mBookFile: File? = null
    //编码类型
    private var mCharset: Charset? = null

    private var mChapterDisp: Disposable? = null

    init {
        pageStatus = PageLoader.STATUS_PARING
    }

    private fun convertTxtChapter(bookChapters: List<BookChapter>): MutableList<TxtChapter> {
        val txtChapters = ArrayList<TxtChapter>(bookChapters.size)
        for (bean in bookChapters) {
            val chapter = TxtChapter()
            chapter.title = bean.title
            chapter.start = bean.start
            chapter.end = bean.end
            txtChapters.add(chapter)
        }
        return txtChapters
    }

    /**
     * 未完成的部分:
     * 1. 序章的添加
     * 2. 章节存在的书本的虚拟分章效果
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun loadChapters() {
        val chapters = ArrayList<TxtChapter>()
        //获取文件流
        RandomAccessFile(mBookFile, "r").use { bookStream ->
            //寻找匹配文章标题的正则表达式，判断是否存在章节名
            val hasChapter = checkChapterType(bookStream)
            //加载章节
            val blockBuffer = ByteArray(BLOCK_BUFFER_SIZE)
            //获取到的块起始点，在文件中的位置
            var blockOffset: Long = 0
            //block的个数
            var blockCount = 0
            //获取文件中的数据到buffer，直到没有数据为止
            var blockLength = bookStream.read(blockBuffer, 0, blockBuffer.size)
            while (blockLength > 0) {
                ++blockCount
                //如果存在Chapter
                if (hasChapter) {
                    //将数据转换成String
                    val blockContent = String(blockBuffer, 0, blockLength, java.nio.charset.Charset.forName(mCharset!!.charset))
                    //当前Block下使过的String的指针
                    var seekPos = 0
                    //进行正则匹配
                    val matcher = mChapterPattern!!.matcher(blockContent)
                    //如果存在相应章节
                    while (matcher.find()) {
                        //获取匹配到的字符在字符串中的起始位置
                        val chapterStart = matcher.start()

                        //如果 seekPos == 0 && nextChapterPos != 0 表示当前block处前面有一段内容
                        //第一种情况一定是序章 第二种情况可能是上一个章节的内容
                        if (seekPos == 0 && chapterStart != 0) {
                            //获取当前章节的内容
                            val chapterContent = blockContent.substring(seekPos, chapterStart)
                            //设置指针偏移
                            seekPos += chapterContent.length

                            //如果当前对整个文件的偏移位置为0的话，那么就是序章
                            if (blockOffset == 0L) {
                                //创建序章
                                val preChapter = TxtChapter()
                                preChapter.title = "序章"
                                preChapter.start = 0
                                preChapter.end = chapterContent.toByteArray(charset(mCharset!!.charset)).size.toLong() //获取String的byte值,作为最终值

                                //如果序章大小大于30才添加进去
                                if (preChapter.end - preChapter.start > 30) {
                                    chapters.add(preChapter)
                                }

                                //创建当前章节
                                val curChapter = TxtChapter()
                                curChapter.title = matcher.group()
                                curChapter.start = preChapter.end
                                chapters.add(curChapter)
                            } else {
                                //获取上一章节
                                val lastChapter = chapters[chapters.size - 1]
                                //将当前段落添加上一章去
                                lastChapter.end = lastChapter.end + chapterContent.toByteArray(charset(mCharset!!.charset)).size

                                //如果章节内容太小，则移除
                                if (lastChapter.end - lastChapter.start < 30) {
                                    chapters.remove(lastChapter)
                                }

                                //创建当前章节
                                val curChapter = TxtChapter()
                                curChapter.title = matcher.group()
                                curChapter.start = lastChapter.end
                                chapters.add(curChapter)
                            }//否则就block分割之后，上一个章节的剩余内容
                        } else {
                            //是否存在章节
                            if (chapters.size != 0) {
                                //获取章节内容
                                val chapterContent = blockContent.substring(seekPos, matcher.start())
                                seekPos += chapterContent.length

                                //获取上一章节
                                val lastChapter = chapters[chapters.size - 1]
                                lastChapter.end = lastChapter.start + chapterContent.toByteArray(charset(mCharset!!.charset)).size

                                //如果章节内容太小，则移除
                                if (lastChapter.end - lastChapter.start < 30) {
                                    chapters.remove(lastChapter)
                                }

                                //创建当前章节
                                val curChapter = TxtChapter()
                                curChapter.title = matcher.group()
                                curChapter.start = lastChapter.end
                                chapters.add(curChapter)
                            } else {
                                val curChapter = TxtChapter()
                                curChapter.title = matcher.group()
                                curChapter.start = 0
                                chapters.add(curChapter)
                            }//如果章节不存在则创建章节
                        }
                    }
                } else { //进行本地虚拟分章
                    //章节在buffer的偏移量
                    var chapterOffset = 0
                    //当前剩余可分配的长度
                    var strLength = blockLength
                    //分章的数量
                    var chapterCount = 0

                    while (strLength > 0) {
                        chapterCount++
                        //是否长度超过一章
                        if (strLength > MAX_LENGTH_WITH_NO_CHAPTER) {
                            //极端情况，这段block都没有换行
                            var end = blockLength
                            //寻找换行符作为终止点
                            for (i in chapterOffset + MAX_LENGTH_WITH_NO_CHAPTER until blockLength) {
                                //这里靠0x0a来划分段落其实并不严谨，例如“上”字在utf-16le编码中就是 0x0a 0x4e
                                //就会造成有些章节不是以完整的段落结尾的情况
                                if (blockBuffer[i] == Charset.BLANK) {
                                    end = i
                                    break
                                }
                            }
                            val chapter = TxtChapter()
                            chapter.title = "第" + blockCount + "章" + "(" + chapterCount + ")"
                            // 这里不能是chapter.start = blockOffset + chapterOffset + 1;
                            // 因为读取章节是读取[chapter.start, chapter.end)，+1会丢失chapter.end处的字节
                            chapter.start = blockOffset + chapterOffset
                            chapter.end = blockOffset + end
                            chapters.add(chapter)
                            //减去已经被分配的长度
                            strLength -= (end - chapterOffset)
                            //设置偏移的位置
                            chapterOffset = end
                        } else {
                            val chapter = TxtChapter()
                            chapter.title = "第" + blockCount + "章" + "(" + chapterCount + ")"
                            chapter.start = blockOffset + chapterOffset
                            chapter.end = blockOffset + blockLength
                            chapters.add(chapter)
                            strLength = 0
                        }
                    }
                }

                //block的偏移点
                blockOffset += blockLength.toLong()

                if (hasChapter) {
                    //设置上一章的结尾
                    val lastChapter = chapters[chapters.size - 1]
                    lastChapter.end = blockOffset
                }

                //当添加的block太多的时候，执行GC
                if (blockCount % 15 == 0) {
                    System.gc()
                    System.runFinalization()
                }

                blockLength = bookStream.read(blockBuffer, 0, blockBuffer.size)
            }

            mChapterList = chapters
        }

        System.gc()
        System.runFinalization()
    }

    /**
     * 从文件中提取一章的内容
     *
     * @param chapter
     * @return
     */
    private fun getChapterContent(chapter: TxtChapter): ByteArray {
        try {
            RandomAccessFile(mBookFile, "r").use { bookStream ->
                bookStream.seek(chapter.start)
                val extent = (chapter.end - chapter.start).toInt()
                val content = ByteArray(extent)
                bookStream.read(content, 0, extent)
                return content
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return ByteArray(0)
    }

    /**
     * 1. 检查文件中是否存在章节名
     * 2. 判断文件中使用的章节名类型的正则表达式
     *
     * @return 是否存在章节名
     */
    @Throws(IOException::class)
    private fun checkChapterType(bookStream: RandomAccessFile): Boolean {
        //首先获取128k的数据
        val buffer = ByteArray(BLOCK_BUFFER_SIZE / 4)
        val length = bookStream.read(buffer, 0, buffer.size)
        //进行章节匹配
        for (str in CHAPTER_PATTERNS) {
            val pattern = Pattern.compile(str, Pattern.MULTILINE)
            val matcher = pattern.matcher(String(buffer, 0, length, java.nio.charset.Charset.forName(mCharset!!.charset)))
            //如果匹配存在，那么就表示当前章节使用这种匹配方式
            if (matcher.find()) {
                mChapterPattern = pattern
                //重置指针位置
                bookStream.seek(0)
                return true
            }
        }

        //重置指针位置
        bookStream.seek(0)
        return false
    }

    override fun saveRecord() {
        super.saveRecord()
        //修改当前COllBook记录
        if (isChapterListPrepare) {
            //表示当前CollBook已经阅读
            collBook.isUpdate = false
            collBook.lastChapter = mChapterList[chapterPos].title
            collBook.lastRead = StringUtils.dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE)
            //直接更新
            //todo 重复处理？
            BookRepository.instance.updateCollBook(collBook)
        }
    }

    override fun closeBook() {
        super.closeBook()
        if (mChapterDisp != null) {
            mChapterDisp!!.dispose()
            mChapterDisp = null
        }
    }

    override fun refreshChapterList() {
        // 对于文件是否存在，或者为空的判断，不作处理。 ==> 在文件打开前处理过了。
        mBookFile = File(collBook.cover)
        //获取文件编码
        mCharset = FileUtils.getCharset(mBookFile!!.absolutePath)

        val lastModified = StringUtils.dateConvert(mBookFile!!.lastModified(), Constant.FORMAT_BOOK_DATE)

        // 判断文件是否已经加载过，并具有缓存
        if (!collBook.isUpdate && collBook.updated == lastModified) {

            mChapterList = convertTxtChapter(bookChapters)
            if (mChapterList.size > 0) {
                isChapterListPrepare = true

                //提示目录加载完成
                if (mPageChangeListener != null) {
                    mPageChangeListener!!.onCategoryFinish(mChapterList)
                }

                // 加载并显示当前章节
                openChapter()

                return
            }

        }

        // 通过RxJava异步处理分章事件
        Single.create(SingleOnSubscribe<Void> { e ->
            loadChapters()
            e.onSuccess(Void())
        }).compose<Void>(SingleTransformer<Void, Void> { RxUtils.toSimpleSingle(it) })
                .subscribe(object : SingleObserver<Void> {
                    override fun onSubscribe(d: Disposable) {
                        mChapterDisp = d
                    }

                    override fun onSuccess(value: Void) {
                        mChapterDisp = null
                        isChapterListPrepare = true

                        // 提示目录加载完成
                        if (mPageChangeListener != null) {
                            mPageChangeListener!!.onCategoryFinish(mChapterList)
                        }

                        // 存储章节到数据库
                        val bookChapterList = ArrayList<BookChapter>()
                        for (i in 0 until mChapterList.size) {
                            val chapter = mChapterList[i]
                            val bean = BookChapter()
                            bean.bookId = collBook.bookId // ForeignKey
                            bean.id = MD5Utils.strToMd5By16(mBookFile!!.absolutePath
                                    + File.separator + chapter.title) // 将路径+i 作为唯一值
                            bean.title = chapter.title.trim()
                            bean.start = chapter.start
                            bean.unreadable = false
                            bean.end = chapter.end
                            bookChapterList.add(bean)
                        }
                        bookChapters = bookChapterList
                        collBook.updated = lastModified

                        BookRepository.instance.insertOrUpdateCollBook(collBook)
                        BookRepository.instance.saveBookChaptersWithAsync(bookChapterList)

                        // 加载并显示当前章节
                        openChapter()
                    }

                    override fun onError(e: Throwable) {
                        chapterError()
                        LogUtils.d(TAG, "file load error:", e)
                    }
                })
    }

    @Throws(Exception::class)
    override fun getChapterReader(chapter: TxtChapter): BufferedReader? {
        //从文件中获取数据
        val content = getChapterContent(chapter)
        val bais = ByteArrayInputStream(content)
        return BufferedReader(InputStreamReader(bais, mCharset!!.charset))
    }

    override fun hasChapterData(chapter: TxtChapter): Boolean {
        return true
    }

    companion object {
        private val TAG = "LocalPageLoader"
        //默认从文件中获取数据的长度
        private val BLOCK_BUFFER_SIZE = 512 * 1024
        //没有标题的时候，每个章节的最大长度
        private val MAX_LENGTH_WITH_NO_CHAPTER = 10 * 1024

        // "序(章)|前言"
        private val mPreChapterPattern = Pattern.compile("^(\\s{0,10})((\u5e8f[\u7ae0\u8a00]?)|(\u524d\u8a00)|(\u6954\u5b50))(\\s{0,10})$", Pattern.MULTILINE)

        //正则表达式章节匹配模式
        // "(第)([0-9零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]{1,10})([章节回集卷])(.*)"
        private val CHAPTER_PATTERNS = arrayOf("^(.{0,8})(\u7b2c)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\u7ae0\u8282\u56de\u96c6\u5377])(.{0,30})\$",
                "^(\\s{0,4})([\\(\u3010\u300a]?(\u5377)?)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\\.:\uff1a\u0020\\u000C\t])(.{0,30})\$",
                "^(\\s{0,4})([\\(\uff08\u3010\u300a])(.{0,30})([\\)\uff09\u3011\u300b])(\\s{0,2})\$",
                "^(\\s{0,4})(\u6b63\u6587)(.{0,20})\$",
                "^(.{0,4})(Chapter|chapter)(\\s{0,4})([0-9]{1,4})(.{0,30})\$")
    }
}
