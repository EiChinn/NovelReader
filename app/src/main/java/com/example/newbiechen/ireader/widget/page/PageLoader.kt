package com.example.newbiechen.ireader.widget.page

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.newbiechen.ireader.App
import com.example.newbiechen.ireader.db.entity.BookChapter
import com.example.newbiechen.ireader.db.entity.CollBook
import com.example.newbiechen.ireader.model.local.BookRepository
import com.example.newbiechen.ireader.model.local.ReadSettingManager
import com.example.newbiechen.ireader.utils.*
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.SingleOnSubscribe
import io.reactivex.SingleTransformer
import io.reactivex.disposables.Disposable
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

/**
 * Created by newbiechen on 17-7-1.
 */

abstract class PageLoader(private var mPageView: PageView?, collBook: CollBook) {

    // 当前章节列表
    var mChapterList: MutableList<TxtChapter>? = null
    // 书本对象
    /**
     * 获取书籍信息
     *
     * @return
     */
    var collBook: CollBook
    var bookChapters: List<BookChapter> = emptyList()
    // 监听器
    protected var mPageChangeListener: OnPageChangeListener? = null

    private val mContext: Context = mPageView!!.context
    // 当前显示的页
    private var mCurPage: TxtPage? = null
    // 上一章的页面列表缓存
    private var mPrePageList: MutableList<TxtPage>? = null
    // 当前章节的页面列表
    private var mCurPageList: MutableList<TxtPage>? = null
    // 下一章的页面列表缓存
    private var mNextPageList: MutableList<TxtPage>? = null

    // 绘制电池的画笔
    private var mBatteryPaint: Paint? = null
    // 绘制提示的画笔
    private var mTipPaint: Paint? = null
    // 绘制标题的画笔
    private var mTitlePaint: Paint? = null
    // 绘制背景颜色的画笔(用来擦除需要重绘的部分)
    private var mBgPaint: Paint? = null
    // 绘制小说内容的画笔
    private var mTextPaint: TextPaint? = null
    // 被遮盖的页，或者认为被取消显示的页
    private var mCancelPage: TxtPage? = null
    private var mPreLoadDisp: Disposable? = null

    /*****************params */
    // 当前的状态
    /**
     * 获取当前页的状态
     *
     * @return
     */
    var pageStatus = STATUS_LOADING
    // 判断章节列表是否加载完成
    protected var isChapterListPrepare: Boolean = false

    // 是否打开过章节
    var isChapterOpen: Boolean = false
        private set
    private var isFirstOpen = true
    var isClose: Boolean = false
        private set
    // 页面的翻页效果模式
    private var mPageMode: PageMode? = null
    // 加载器的颜色主题
    private var mPageStyle: PageStyle? = null
    //当前是否是夜间模式
    private var isNightMode: Boolean = false
    //书籍绘制区域的宽高
    private var mVisibleWidth: Int = 0
    private var mVisibleHeight: Int = 0
    //应用的宽高
    private var mDisplayWidth: Int = 0
    private var mDisplayHeight: Int = 0
    //间距
    private var mMarginWidth: Int = 0
    /**
     * 获取距离屏幕的高度
     *
     * @return
     */
    var marginHeight: Int = 0
        private set
    //字体的颜色
    private var mTextColor: Int = 0
    //标题的大小
    private var mTitleSize: Int = 0
    //字体的大小
    private var mTextSize: Int = 0
    //行间距
    private var mTextInterval: Int = 0
    //标题的行间距
    private var mTitleInterval: Int = 0
    //段落距离(基于行间距的额外距离)
    private var mTextPara: Int = 0
    private var mTitlePara: Int = 0
    //电池的百分比
    private var mBatteryLevel: Int = 0
    //当前页面的背景
    private var mBgColor: Int = 0

    // 当前章
    /**
     * 获取当前章节的章节位置
     *
     * @return
     */
    var chapterPos = 0
    //上一章的记录
    private var mLastChapterPos = 0

    /**
     * 获取章节目录。
     *
     * @return
     */
    val chapterCategory: List<TxtChapter>?
        get() = mChapterList

    /**
     * 获取当前页的页码
     *
     * @return
     */
    val pagePos: Int
        get() = mCurPage!!.position

    /**
     * @return:获取上一个页面
     */
    private val prevPage: TxtPage?
        get() {
            val pos = mCurPage!!.position - 1
            if (pos < 0) {
                return null
            }
            if (mPageChangeListener != null) {
                mPageChangeListener!!.onPageChange(pos)
            }
            return mCurPageList!![pos]
        }

    /**
     * @return:获取下一的页面
     */
    private val nextPage: TxtPage?
        get() {
            val pos = mCurPage!!.position + 1
            if (pos >= mCurPageList!!.size) {
                return null
            }
            if (mPageChangeListener != null) {
                mPageChangeListener!!.onPageChange(pos)
            }
            return mCurPageList!![pos]
        }

    /**
     * @return:获取上一个章节的最后一页
     */
    private val prevLastPage: TxtPage
        get() {
            val pos = mCurPageList!!.size - 1

            if (mPageChangeListener != null) {
                mPageChangeListener!!.onPageChange(pos)
            }

            return mCurPageList!![pos]
        }

    init {
        this.collBook = collBook
        mChapterList = ArrayList(1)

        // 初始化数据
        initData()
        // 初始化画笔
        initPaint()
        // 初始化PageView
        initPageView()
        // 初始化书籍
        prepareBook()
    }

    private fun initData() {
        // 获取配置参数
        mPageMode = ReadSettingManager.getPageMode()
        mPageStyle = ReadSettingManager.getPageStyle()
        // 初始化参数
        mMarginWidth = ScreenUtils.dpToPx(DEFAULT_MARGIN_WIDTH)
        marginHeight = ScreenUtils.dpToPx(DEFAULT_MARGIN_HEIGHT)
        // 配置文字有关的参数
        setUpTextParams(ReadSettingManager.getTextSize())
    }

    /**
     * 作用：设置与文字相关的参数
     *
     * @param textSize
     */
    private fun setUpTextParams(textSize: Int) {
        // 文字大小
        mTextSize = textSize
        mTitleSize = mTextSize + ScreenUtils.spToPx(EXTRA_TITLE_SIZE)
        // 行间距(大小为字体的一半)
        mTextInterval = mTextSize / 2
        mTitleInterval = mTitleSize / 2
        // 段落间距(大小为字体的高度)
        mTextPara = mTextSize
        mTitlePara = mTitleSize
    }

    private fun initPaint() {
        // 绘制提示的画笔
        mTipPaint = Paint()
        mTipPaint!!.color = mTextColor
        mTipPaint!!.textAlign = Paint.Align.LEFT // 绘制的起始点
        mTipPaint!!.textSize = ScreenUtils.spToPx(DEFAULT_TIP_SIZE).toFloat() // Tip默认的字体大小
        mTipPaint!!.isAntiAlias = true
        mTipPaint!!.isSubpixelText = true

        // 绘制页面内容的画笔
        mTextPaint = TextPaint()
        mTextPaint!!.color = mTextColor
        mTextPaint!!.textSize = mTextSize.toFloat()
        mTextPaint!!.isAntiAlias = true

        // 绘制标题的画笔
        mTitlePaint = TextPaint()
        mTitlePaint!!.color = mTextColor
        mTitlePaint!!.textSize = mTitleSize.toFloat()
        mTitlePaint!!.style = Paint.Style.FILL_AND_STROKE
        mTitlePaint!!.typeface = Typeface.DEFAULT_BOLD
        mTitlePaint!!.isAntiAlias = true

        // 绘制背景的画笔
        mBgPaint = Paint()
        mBgPaint!!.color = mBgColor

        // 绘制电池的画笔
        mBatteryPaint = Paint()
        mBatteryPaint!!.isAntiAlias = true
        mBatteryPaint!!.isDither = true

        // 初始化页面样式
        setNightMode(ReadSettingManager.isNightMode())
    }

    private fun initPageView() {
        //配置参数
        mPageView!!.setPageMode(mPageMode!!)
        mPageView!!.setBgColor(mBgColor)
    }

    /****************************** public method */
    /**
     * 跳转到上一章
     *
     * @return
     */
    fun skipPreChapter(): Boolean {
        if (!hasPrevChapter()) {
            return false
        }

        // 载入上一章。
        if (parsePrevChapter()) {
            mCurPage = getCurPage(0)
        } else {
            mCurPage = TxtPage()
        }
        mPageView!!.drawCurPage(false)
        return true
    }

    /**
     * 跳转到下一章
     *
     * @return
     */
    fun skipNextChapter(): Boolean {
        if (!hasNextChapter()) {
            return false
        }

        //判断是否达到章节的终止点
        if (parseNextChapter()) {
            mCurPage = getCurPage(0)
        } else {
            mCurPage = TxtPage()
        }
        mPageView!!.drawCurPage(false)
        return true
    }

    /**
     * 跳转到指定章节
     *
     * @param pos:从 0 开始。
     */
    fun skipToChapter(pos: Int) {
        // 设置参数
        chapterPos = pos

        // 将上一章的缓存设置为null
        mPrePageList = null
        // 如果当前下一章缓存正在执行，则取消
        if (mPreLoadDisp != null) {
            mPreLoadDisp!!.dispose()
        }
        // 将下一章缓存设置为null
        mNextPageList = null

        // 打开指定章节
        openChapter()
    }

    /**
     * 跳转到指定的页
     *
     * @param pos
     */
    fun skipToPage(pos: Int): Boolean {
        if (!isChapterListPrepare) {
            return false
        }
        mCurPage = getCurPage(pos)
        mPageView!!.drawCurPage(false)
        return true
    }

    /**
     * 翻到上一页
     *
     * @return
     */
    fun skipToPrePage(): Boolean {
        return mPageView!!.autoPrevPage()
    }

    /**
     * 翻到下一页
     *
     * @return
     */
    fun skipToNextPage(): Boolean {
        return mPageView!!.autoNextPage()
    }

    /**
     * 更新时间
     */
    fun updateTime() {
        if (!mPageView!!.isRunning) {
            mPageView!!.drawCurPage(true)
        }
    }

    /**
     * 更新电量
     *
     * @param level
     */
    fun updateBattery(level: Int) {
        mBatteryLevel = level

        if (!mPageView!!.isRunning) {
            mPageView!!.drawCurPage(true)
        }
    }

    /**
     * 设置提示的文字大小
     *
     * @param textSize:单位为 px。
     */
    fun setTipTextSize(textSize: Int) {
        mTipPaint!!.textSize = textSize.toFloat()

        // 如果屏幕大小加载完成
        mPageView!!.drawCurPage(false)
    }

    /**
     * 设置文字相关参数
     *
     * @param textSize
     */
    fun setTextSize(textSize: Int) {
        // 设置文字相关参数
        setUpTextParams(textSize)

        // 设置画笔的字体大小
        mTextPaint!!.textSize = mTextSize.toFloat()
        // 设置标题的字体大小
        mTitlePaint!!.textSize = mTitleSize.toFloat()
        // 存储文字大小
        ReadSettingManager.setTextSize(mTextSize)
        // 取消缓存
        mPrePageList = null
        mNextPageList = null

        // 如果当前已经显示数据
        if (isChapterListPrepare && pageStatus == STATUS_FINISH) {
            // 重新计算当前页面
            dealLoadPageList(chapterPos)

            // 防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
            if (mCurPage!!.position >= mCurPageList!!.size) {
                mCurPage!!.position = mCurPageList!!.size - 1
            }

            // 重新获取指定页面
            mCurPage = mCurPageList!![mCurPage!!.position]
        }

        mPageView!!.drawCurPage(false)
    }

    /**
     * 设置夜间模式
     *
     * @param nightMode
     */
    fun setNightMode(nightMode: Boolean) {
        ReadSettingManager.setNightMode(nightMode)
        isNightMode = nightMode

        if (isNightMode) {
            mBatteryPaint!!.color = Color.WHITE
            setPageStyle(PageStyle.NIGHT)
        } else {
            mBatteryPaint!!.color = Color.BLACK
            setPageStyle(mPageStyle)
        }
    }

    /**
     * 设置页面样式
     *
     * @param pageStyle:页面样式
     */
    fun setPageStyle(pageStyle: PageStyle?) {
        if (pageStyle !== PageStyle.NIGHT) {
            mPageStyle = pageStyle
            ReadSettingManager.setPageStyle(pageStyle!!)
        }

        if (isNightMode && pageStyle !== PageStyle.NIGHT) {
            return
        }

        // 设置当前颜色样式
        mTextColor = ContextCompat.getColor(mContext, pageStyle.fontColor)
        mBgColor = ContextCompat.getColor(mContext, pageStyle.bgColor)

        mTipPaint!!.color = mTextColor
        mTitlePaint!!.color = mTextColor
        mTextPaint!!.color = mTextColor

        mBgPaint!!.color = mBgColor

        mPageView!!.drawCurPage(false)
    }

    /**
     * 翻页动画
     *
     * @param pageMode:翻页模式
     * @see PageMode
     */
    fun setPageMode(pageMode: PageMode) {
        mPageMode = pageMode

        mPageView!!.setPageMode(mPageMode!!)
        ReadSettingManager.setPageMode(mPageMode!!)

        // 重新绘制当前页
        mPageView!!.drawCurPage(false)
    }

    /**
     * 设置内容与屏幕的间距
     *
     * @param marginWidth  :单位为 px
     * @param marginHeight :单位为 px
     */
    fun setMargin(marginWidth: Int, marginHeight: Int) {
        mMarginWidth = marginWidth
        this.marginHeight = marginHeight

        // 如果是滑动动画，则需要重新创建了
        if (mPageMode === PageMode.SCROLL) {
            mPageView!!.setPageMode(PageMode.SCROLL)
        }

        mPageView!!.drawCurPage(false)
    }

    /**
     * 设置页面切换监听
     *
     * @param listener
     */
    fun setOnPageChangeListener(listener: OnPageChangeListener) {
        mPageChangeListener = listener

        // 如果目录加载完之后才设置监听器，那么会默认回调
        if (isChapterListPrepare) {
            mPageChangeListener!!.onCategoryFinish(mChapterList)
        }
    }

    /**
     * 保存阅读记录
     */
    open fun saveRecord() {

        if (mChapterList!!.isEmpty()) {
            return
        }

        collBook.chapter = chapterPos

        if (mCurPage != null) {
            collBook.pagePos = mCurPage!!.position
        } else {
            collBook.pagePos = 0
        }

        //存储到数据库
        BookRepository.instance
                .insertOrUpdateCollBook(collBook)
    }

    /**
     * 初始化书籍
     */
    private fun prepareBook() {
        chapterPos = collBook.chapter
        mLastChapterPos = chapterPos
    }

    /**
     * 打开指定章节
     */
    fun openChapter() {
        isFirstOpen = false

        if (!mPageView!!.isPrepare) {
            return
        }

        // 如果章节目录没有准备好
        if (!isChapterListPrepare) {
            pageStatus = STATUS_LOADING
            mPageView!!.drawCurPage(false)
            return
        }

        // 如果获取到的章节目录为空
        if (mChapterList!!.isEmpty()) {
            pageStatus = STATUS_CATEGORY_EMPTY
            mPageView!!.drawCurPage(false)
            return
        }

        if (parseCurChapter()) {
            // 如果章节从未打开
            if (!isChapterOpen) {
                var position = collBook.pagePos

                // 防止记录页的页号，大于当前最大页号
                if (position >= mCurPageList!!.size) {
                    position = mCurPageList!!.size - 1
                }
                mCurPage = getCurPage(position)
                mCancelPage = mCurPage
                // 切换状态
                isChapterOpen = true
            } else {
                mCurPage = getCurPage(0)
            }
        } else {
            mCurPage = TxtPage()
        }

        mPageView!!.drawCurPage(false)
    }

    fun chapterError() {
        //加载错误
        pageStatus = STATUS_ERROR
        mPageView!!.drawCurPage(false)
    }

    /**
     * 关闭书本
     */
    open fun closeBook() {
        isChapterListPrepare = false
        isClose = true

        if (mPreLoadDisp != null) {
            mPreLoadDisp!!.dispose()
        }

        clearList(mChapterList)
        clearList(mCurPageList)
        clearList(mNextPageList)

        mChapterList = null
        mCurPageList = null
        mNextPageList = null
        mPageView = null
        mCurPage = null
    }

    private fun clearList(list: MutableList<*>?) {
        list?.clear()
    }

    /**
     * 加载页面列表
     *
     * @param chapterPos:章节序号
     * @return
     */
    @Throws(Exception::class)
    private fun loadPageList(chapterPos: Int): MutableList<TxtPage>? {
        // 获取章节
        val chapter = mChapterList!![chapterPos]
        // 判断章节是否存在
        if (!hasChapterData(chapter)) {
            return null
        }
        // 获取章节的文本流
        getChapterReader(chapter).use { reader -> return loadPages(chapter, reader!!) }
    }

    /*******************************abstract method */

    /**
     * 刷新章节列表
     */
    abstract fun refreshChapterList()

    /**
     * 获取章节的文本流
     *
     * @param chapter
     * @return
     */
    @Throws(Exception::class)
    protected abstract fun getChapterReader(chapter: TxtChapter): BufferedReader?

    /**
     * 章节数据是否存在
     *
     * @return
     */
    protected abstract fun hasChapterData(chapter: TxtChapter): Boolean

    /***********************************default method */

    internal fun drawPage(bitmap: Bitmap, isUpdate: Boolean) {
        drawBackground(mPageView!!.bgBitmap, isUpdate)
        if (!isUpdate) {
            drawContent(bitmap)
        }
        //更新绘制
        mPageView!!.invalidate()
    }

    private fun drawBackground(bitmap: Bitmap?, isUpdate: Boolean) {
        val canvas = Canvas(bitmap!!)
        val tipMarginHeight = ScreenUtils.dpToPx(3)
        if (!isUpdate) {
            /****绘制背景 */
            canvas.drawColor(mBgColor)

            if (!mChapterList!!.isEmpty()) {
                /*****初始化标题的参数 */
                //需要注意的是:绘制text的y的起始点是text的基准线的位置，而不是从text的头部的位置
                val tipTop = tipMarginHeight - mTipPaint!!.fontMetrics.top
                //根据状态不一样，数据不一样
                if (pageStatus != STATUS_FINISH) {
                    if (isChapterListPrepare) {
                        canvas.drawText(mChapterList!![chapterPos].title, mMarginWidth.toFloat(), tipTop, mTipPaint!!)
                    }
                } else {
                    canvas.drawText(mCurPage!!.title, mMarginWidth.toFloat(), tipTop, mTipPaint!!)
                }

                /******绘制页码 */
                // 底部的字显示的位置Y
                val y = mDisplayHeight.toFloat() - mTipPaint!!.fontMetrics.bottom - tipMarginHeight.toFloat()
                // 只有finish的时候采用页码
                if (pageStatus == STATUS_FINISH) {
                    val percent = "${mCurPage!!.position + 1}/${mCurPageList!!.size}"
                    canvas.drawText(percent, mMarginWidth.toFloat(), y, mTipPaint!!)
                }
            }
        } else {
            //擦除区域
            mBgPaint!!.color = mBgColor
            canvas.drawRect((mDisplayWidth / 2).toFloat(), (mDisplayHeight - marginHeight + ScreenUtils.dpToPx(2)).toFloat(), mDisplayWidth.toFloat(), mDisplayHeight.toFloat(), mBgPaint!!)
        }

        /******绘制电池 */

        val visibleRight = mDisplayWidth - mMarginWidth
        val visibleBottom = mDisplayHeight - tipMarginHeight

        val outFrameWidth = mTipPaint!!.measureText("xxx").toInt()
        val outFrameHeight = mTipPaint!!.textSize.toInt()

        val polarHeight = ScreenUtils.dpToPx(6)
        val polarWidth = ScreenUtils.dpToPx(2)
        val border = 1
        val innerMargin = 1

        //电极的制作
        val polarLeft = visibleRight - polarWidth
        val polarTop = visibleBottom - (outFrameHeight + polarHeight) / 2
        val polar = Rect(polarLeft, polarTop, visibleRight,
                polarTop + polarHeight - ScreenUtils.dpToPx(2))

        mBatteryPaint!!.style = Paint.Style.FILL
        canvas.drawRect(polar, mBatteryPaint!!)

        //外框的制作
        val outFrameLeft = polarLeft - outFrameWidth
        val outFrameTop = visibleBottom - outFrameHeight
        val outFrameBottom = visibleBottom - ScreenUtils.dpToPx(2)
        val outFrame = Rect(outFrameLeft, outFrameTop, polarLeft, outFrameBottom)

        mBatteryPaint!!.style = Paint.Style.STROKE
        mBatteryPaint!!.strokeWidth = border.toFloat()
        canvas.drawRect(outFrame, mBatteryPaint!!)

        //内框的制作
        val innerWidth = (outFrame.width() - innerMargin * 2 - border) * (mBatteryLevel / 100.0f)
        val innerFrame = RectF((outFrameLeft + border + innerMargin).toFloat(), (outFrameTop + border + innerMargin).toFloat(),
                outFrameLeft.toFloat() + border.toFloat() + innerMargin.toFloat() + innerWidth, (outFrameBottom - border - innerMargin).toFloat())

        mBatteryPaint!!.style = Paint.Style.FILL
        canvas.drawRect(innerFrame, mBatteryPaint!!)

        /******绘制当前时间 */
        //底部的字显示的位置Y
        val y = mDisplayHeight.toFloat() - mTipPaint!!.fontMetrics.bottom - tipMarginHeight.toFloat()
        val time = StringUtils.dateConvert(System.currentTimeMillis(), Constant.FORMAT_TIME)
        val x = outFrameLeft.toFloat() - mTipPaint!!.measureText(time) - ScreenUtils.dpToPx(4).toFloat()
        canvas.drawText(time, x, y, mTipPaint!!)
    }

    private fun drawContent(bitmap: Bitmap) {
        val canvas = Canvas(bitmap)

        if (mPageMode === PageMode.SCROLL) {
            canvas.drawColor(mBgColor)
        }
        /******绘制内容 */

        if (pageStatus != STATUS_FINISH) {
            //绘制字体
            var tip = ""
            when (pageStatus) {
                STATUS_LOADING -> tip = "正在拼命加载中..."
                STATUS_ERROR -> tip = "加载失败(点击边缘重试)"
                STATUS_EMPTY -> tip = "文章内容为空"
                STATUS_PARING -> tip = "正在排版请等待..."
                STATUS_PARSE_ERROR -> tip = "文件解析错误"
                STATUS_CATEGORY_EMPTY -> tip = "目录列表为空"
            }

            //将提示语句放到正中间
            val fontMetrics = mTextPaint!!.fontMetrics
            val textHeight = fontMetrics.top - fontMetrics.bottom
            val textWidth = mTextPaint!!.measureText(tip)
            val pivotX = (mDisplayWidth - textWidth) / 2
            val pivotY = (mDisplayHeight - textHeight) / 2
            canvas.drawText(tip, pivotX, pivotY, mTextPaint!!)
        } else {
            var top: Float

            if (mPageMode === PageMode.SCROLL) {
                top = -mTextPaint!!.fontMetrics.top
            } else {
                top = marginHeight - mTextPaint!!.fontMetrics.top
            }

            //设置总距离
            val interval = mTextInterval + mTextPaint!!.textSize.toInt()
            val para = mTextPara + mTextPaint!!.textSize.toInt()
            val titleInterval = mTitleInterval + mTitlePaint!!.textSize.toInt()
            val titlePara = mTitlePara + mTextPaint!!.textSize.toInt()
            var str: String? = null

            //对标题进行绘制
            for (i in 0 until mCurPage!!.titleLines) {
                str = mCurPage!!.lines!![i]

                //设置顶部间距
                if (i == 0) {
                    top += mTitlePara.toFloat()
                }

                //计算文字显示的起始点
                val start = (mDisplayWidth - mTitlePaint!!.measureText(str)).toInt() / 2
                //进行绘制
                canvas.drawText(str!!, start.toFloat(), top, mTitlePaint!!)

                //设置尾部间距
                if (i == mCurPage!!.titleLines - 1) {
                    top += titlePara.toFloat()
                } else {
                    //行间距
                    top += titleInterval.toFloat()
                }
            }

            //对内容进行绘制
            for (i in mCurPage!!.titleLines until mCurPage!!.lines!!.size) {
                str = mCurPage!!.lines!!.get(i)

                canvas.drawText(str!!, mMarginWidth.toFloat(), top, mTextPaint!!)
                if (str!!.endsWith("\n")) {
                    top += para.toFloat()
                } else {
                    top += interval.toFloat()
                }
            }
        }
    }

    internal fun prepareDisplay(w: Int, h: Int) {
        // 获取PageView的宽高
        mDisplayWidth = w
        mDisplayHeight = h

        // 获取内容显示位置的大小
        mVisibleWidth = mDisplayWidth - mMarginWidth * 2
        mVisibleHeight = mDisplayHeight - marginHeight * 2

        // 重置 PageMode
        mPageView!!.setPageMode(mPageMode!!)

        if (!isChapterOpen) {
            // 展示加载界面
            mPageView!!.drawCurPage(false)
            // 如果在 display 之前调用过 openChapter 肯定是无法打开的。
            // 所以需要通过 display 再重新调用一次。
            if (!isFirstOpen) {
                // 打开书籍
                openChapter()
            }
        } else {
            // 如果章节已显示，那么就重新计算页面
            if (pageStatus == STATUS_FINISH) {
                dealLoadPageList(chapterPos)
                // 重新设置文章指针的位置
                mCurPage = getCurPage(mCurPage!!.position)
            }
            mPageView!!.drawCurPage(false)
        }
    }

    /**
     * 翻阅上一页
     *
     * @return
     */
    internal fun prev(): Boolean {
        // 以下情况禁止翻页
        if (!canTurnPage()) {
            return false
        }

        if (pageStatus == STATUS_FINISH) {
            // 先查看是否存在上一页
            val prevPage = prevPage
            if (prevPage != null) {
                mCancelPage = mCurPage
                mCurPage = prevPage
                mPageView!!.drawNextPage()
                return true
            }
        }

        if (!hasPrevChapter()) {
            return false
        }

        mCancelPage = mCurPage
        if (parsePrevChapter()) {
            mCurPage = prevLastPage
        } else {
            mCurPage = TxtPage()
        }
        mPageView!!.drawNextPage()
        return true
    }

    /**
     * 解析上一章数据
     *
     * @return:数据是否解析成功
     */
    internal open fun parsePrevChapter(): Boolean {
        // 加载上一章数据
        val prevChapter = chapterPos - 1

        mLastChapterPos = chapterPos
        chapterPos = prevChapter

        // 当前章缓存为下一章
        mNextPageList = mCurPageList

        // 判断是否具有上一章缓存
        if (mPrePageList != null) {
            mCurPageList = mPrePageList
            mPrePageList = null

            // 回调
            chapterChangeCallback()
        } else {
            dealLoadPageList(prevChapter)
        }
        return if (mCurPageList != null) true else false
    }

    private fun hasPrevChapter(): Boolean {
        //判断是否上一章节为空
        return if (chapterPos - 1 < 0) {
            false
        } else true
    }

    /**
     * 翻到下一页
     *
     * @return:是否允许翻页
     */
    internal operator fun next(): Boolean {
        // 以下情况禁止翻页
        if (!canTurnPage()) {
            return false
        }

        if (pageStatus == STATUS_FINISH) {
            // 先查看是否存在下一页
            val nextPage = nextPage
            if (nextPage != null) {
                mCancelPage = mCurPage
                mCurPage = nextPage
                mPageView!!.drawNextPage()
                return true
            }
        }

        if (!hasNextChapter()) {
            //如果当前章节没有下一页，也没有下一张时，可以判断是看到小说最后了
            Toast.makeText(App.getInstance(), "已经看到最后啦", Toast.LENGTH_SHORT).show()
            return false
        }

        mCancelPage = mCurPage
        // 解析下一章数据
        if (parseNextChapter()) {
            mCurPage = mCurPageList!![0]
        } else {
            mCurPage = TxtPage()
        }
        mPageView!!.drawNextPage()
        return true
    }

    private fun hasNextChapter(): Boolean {
        // 判断是否到达目录最后一章
        return chapterPos + 1 < mChapterList!!.size
    }

    internal open fun parseCurChapter(): Boolean {
        // 解析数据
        dealLoadPageList(chapterPos)
        // 预加载下一页面
        preLoadNextChapter()
        return mCurPageList != null
    }

    /**
     * 解析下一章数据
     *
     * @return:返回解析成功还是失败
     */
    internal open fun parseNextChapter(): Boolean {
        val nextChapter = chapterPos + 1

        mLastChapterPos = chapterPos
        chapterPos = nextChapter

        // 将当前章的页面列表，作为上一章缓存
        mPrePageList = mCurPageList

        // 是否下一章数据已经预加载了
        if (mNextPageList != null) {
            mCurPageList = mNextPageList
            mNextPageList = null
            // 回调
            chapterChangeCallback()
        } else {
            // 处理页面解析
            dealLoadPageList(nextChapter)
        }
        // 预加载下一页面
        preLoadNextChapter()
        return mCurPageList != null
    }

    private fun dealLoadPageList(chapterPos: Int) {
        try {
            mCurPageList = loadPageList(chapterPos)
            if (mCurPageList != null) {
                if (mCurPageList!!.isEmpty()) {
                    pageStatus = STATUS_EMPTY

                    // 添加一个空数据
                    val page = TxtPage()
                    page.lines = ArrayList(1)
                    mCurPageList!!.add(page)
                } else {
                    pageStatus = STATUS_FINISH
                }
            } else {
                pageStatus = STATUS_LOADING
            }
        } catch (e: Exception) {
            e.printStackTrace()

            mCurPageList = null
            pageStatus = STATUS_ERROR
        }

        // 回调
        chapterChangeCallback()
    }

    private fun chapterChangeCallback() {
        if (mPageChangeListener != null) {
            mPageChangeListener!!.onChapterChange(chapterPos)
            mPageChangeListener!!.onPageCountChange(if (mCurPageList != null) mCurPageList!!.size else 0)
        }
    }

    // 预加载下一章
    private fun preLoadNextChapter() {
        val nextChapter = chapterPos + 1

        // 如果不存在下一章，且下一章没有数据，则不进行加载。
        if (!hasNextChapter() || !hasChapterData(mChapterList!![nextChapter])) {
            return
        }

        //如果之前正在加载则取消
        if (mPreLoadDisp != null) {
            mPreLoadDisp!!.dispose()
        }

        //调用异步进行预加载加载
        Single.create(SingleOnSubscribe<MutableList<TxtPage>> { e -> e.onSuccess(loadPageList(nextChapter)!!) }).compose<MutableList<TxtPage>>(SingleTransformer<MutableList<TxtPage>, MutableList<TxtPage>> { RxUtils.toSimpleSingle(it) })
                .subscribe(object : SingleObserver<MutableList<TxtPage>> {
                    override fun onSubscribe(d: Disposable) {
                        mPreLoadDisp = d
                    }

                    override fun onSuccess(pages: MutableList<TxtPage>) {
                        mNextPageList = pages
                    }

                    override fun onError(e: Throwable) {
                        //无视错误
                    }
                })
    }

    // 取消翻页
    internal fun pageCancel() {
        if (mCurPage!!.position === 0 && chapterPos > mLastChapterPos) { // 加载到下一章取消了
            if (mPrePageList != null) {
                cancelNextChapter()
            } else {
                if (parsePrevChapter()) {
                    mCurPage = prevLastPage
                } else {
                    mCurPage = TxtPage()
                }
            }
        } else if (mCurPageList == null || mCurPage!!.position === mCurPageList!!.size - 1 && chapterPos < mLastChapterPos) {  // 加载上一章取消了

            if (mNextPageList != null) {
                cancelPreChapter()
            } else {
                if (parseNextChapter()) {
                    mCurPage = mCurPageList!![0]
                } else {
                    mCurPage = TxtPage()
                }
            }
        } else {
            // 假设加载到下一页，又取消了。那么需要重新装载。
            mCurPage = mCancelPage
        }
    }

    private fun cancelNextChapter() {
        val temp = mLastChapterPos
        mLastChapterPos = chapterPos
        chapterPos = temp

        mNextPageList = mCurPageList
        mCurPageList = mPrePageList
        mPrePageList = null

        chapterChangeCallback()

        mCurPage = prevLastPage
        mCancelPage = null
    }

    private fun cancelPreChapter() {
        // 重置位置点
        val temp = mLastChapterPos
        mLastChapterPos = chapterPos
        chapterPos = temp
        // 重置页面列表
        mPrePageList = mCurPageList
        mCurPageList = mNextPageList
        mNextPageList = null

        chapterChangeCallback()

        mCurPage = getCurPage(0)
        mCancelPage = null
    }

    /**************************************private method */
    /**
     * 将章节数据，解析成页面列表
     *
     * @param chapter：章节信息
     * @param br：章节的文本流
     * @return
     */
    private fun loadPages(chapter: TxtChapter, br: BufferedReader): MutableList<TxtPage> {
        //生成的页面
        val pages = ArrayList<TxtPage>()
        //使用流的方式加载
        val lines = ArrayList<String>()
        var rHeight = mVisibleHeight
        var titleLinesCount = 0
        var showTitle = true // 是否展示标题
        var paragraph: String? = chapter.title//默认展示标题
        try {
            while (showTitle || paragraph != null) {
                // 重置段落
                if (!showTitle) {
                    paragraph = paragraph!!.replace("\\s".toRegex(), "")
                    // 如果只有换行符，那么就不执行
                    if (paragraph == "") {
                        paragraph = br.readLine()
                        continue
                    }
                    paragraph = "  $paragraph\n"
                } else {
                    //设置 title 的顶部间距
                    rHeight -= mTitlePara
                }
                var wordCount = 0
                var subStr: String? = null
                // TODO: 2018/11/13 当paragraph太长（如paragraph.length() == 37599，而正常为小于300）时，下面的while将循环过多次，而且代码是在主线程中执行，所以会引发ANR
                while (paragraph!!.isNotEmpty()) {
                    //当前空间，是否容得下一行文字
                    if (showTitle) {
                        rHeight -= mTitlePaint!!.textSize.toInt()
                    } else {
                        rHeight -= mTextPaint!!.textSize.toInt()
                    }
                    // 一页已经填充满了，创建 TextPage
                    if (rHeight <= 0) {
                        // 创建Page
                        val page = TxtPage()
                        page.position = pages.size
                        page.title = chapter.title
                        page.lines = ArrayList(lines)
                        page.titleLines = titleLinesCount
                        pages.add(page)
                        // 重置Lines
                        lines.clear()
                        rHeight = mVisibleHeight
                        titleLinesCount = 0
                        continue
                    }

                    //测量一行占用的字节数
                    if (showTitle) {
                        wordCount = mTitlePaint!!.breakText(paragraph,
                                true, mVisibleWidth.toFloat(), null)
                    } else {
                        wordCount = mTextPaint!!.breakText(paragraph,
                                true, mVisibleWidth.toFloat(), null)
                    }

                    subStr = paragraph.substring(0, wordCount)
                    if (subStr != "\n") {
                        //将一行字节，存储到lines中
                        lines.add(subStr)

                        //设置段落间距
                        if (showTitle) {
                            titleLinesCount += 1
                            rHeight -= mTitleInterval
                        } else {
                            rHeight -= mTextInterval
                        }
                    }
                    //裁剪
                    paragraph = paragraph.substring(wordCount)
                }

                //增加段落的间距
                if (!showTitle && lines.size != 0) {
                    rHeight = rHeight - mTextPara + mTextInterval
                }

                if (showTitle) {
                    rHeight = rHeight - mTitlePara + mTitleInterval
                    showTitle = false
                }
                paragraph = br.readLine()
            }

            if (lines.size != 0) {
                //创建Page
                val page = TxtPage()
                page.position = pages.size
                page.title = chapter.title
                page.lines = ArrayList(lines)
                page.titleLines = titleLinesCount
                pages.add(page)
                //重置Lines
                lines.clear()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return pages
    }


    /**
     * @return:获取初始显示的页面
     */
    private fun getCurPage(pos: Int): TxtPage {
        if (mPageChangeListener != null) {
            mPageChangeListener!!.onPageChange(pos)
        }
        return mCurPageList!![pos]
    }

    /**
     * 根据当前状态，决定是否能够翻页
     *
     * @return
     */
    private fun canTurnPage(): Boolean {

        if (!isChapterListPrepare) {
            return false
        }

        if (pageStatus == STATUS_PARSE_ERROR || pageStatus == STATUS_PARING) {
            return false
        } else if (pageStatus == STATUS_ERROR) {
            pageStatus = STATUS_LOADING
        }
        return true
    }

    /**
     * //如果上次退出时阅读的章节mCurChapterPos大于小说章节（可能是换源的时候出了点问题），清空BookRecord
     */
    protected fun checkRecordValid() {
        if (chapterPos >= collBook.chaptersCount || chapterPos < 0) {
            chapterPos = 0
            mLastChapterPos = chapterPos
            collBook.chapter = 0
            collBook.pagePos = 0
            BookRepository.instance.insertOrUpdateCollBook(collBook)
            ToastUtils.show("上次阅读章节大于小说总章节，检查是否换源的时候出了问题")
        }
    }

    /*****************************************interface */

    interface OnPageChangeListener {
        /**
         * 作用：章节切换的时候进行回调
         *
         * @param pos:切换章节的序号
         */
        fun onChapterChange(pos: Int)

        /**
         * 作用：请求加载章节内容
         *
         * @param requestChapters:需要下载的章节列表
         */
        fun requestChapters(requestChapters: List<TxtChapter>)

        /**
         * 作用：章节目录加载完成时候回调
         *
         * @param chapters：返回章节目录
         */
        fun onCategoryFinish(chapters: List<TxtChapter>?)

        /**
         * 作用：章节页码数量改变之后的回调。==> 字体大小的调整，或者是否关闭虚拟按钮功能都会改变页面的数量。
         *
         * @param count:页面的数量
         */
        fun onPageCountChange(count: Int)

        /**
         * 作用：当页面改变的时候回调
         *
         * @param pos:当前的页面的序号
         */
        fun onPageChange(pos: Int)
    }

    companion object {

        // 当前页面的状态
        const val STATUS_LOADING = 1         // 正在加载
        const val STATUS_FINISH = 2          // 加载完成
        const val STATUS_ERROR = 3           // 加载错误 (一般是网络加载情况)
        const val STATUS_EMPTY = 4           // 空数据
        const val STATUS_PARING = 5          // 正在解析 (装载本地数据)
        const val STATUS_PARSE_ERROR = 6     // 本地文件解析错误(暂未被使用)
        const val STATUS_CATEGORY_EMPTY = 7  // 获取到的目录为空
        // 默认的显示参数配置
        private const val DEFAULT_MARGIN_HEIGHT = 28
        private const val DEFAULT_MARGIN_WIDTH = 15
        private const val DEFAULT_TIP_SIZE = 12
        private const val EXTRA_TITLE_SIZE = 4
    }
}
