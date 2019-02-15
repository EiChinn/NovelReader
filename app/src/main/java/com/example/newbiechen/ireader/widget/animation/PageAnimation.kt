package com.example.newbiechen.ireader.widget.animation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Scroller

/**
 * Created by ei_chinn on 19-02-15.
 * 翻页动画抽象类
 */
abstract class PageAnimation(w: Int, h: Int, marginWidth: Int, marginHeight: Int, view: View, listener: OnPageChangeListener) {
    //正在使用的View
    @JvmField protected var mView: View?
    //滑动装置
    @JvmField internal val mScroller: Scroller
    //监听器
    @JvmField protected val mListener: OnPageChangeListener
    //移动方向
    @JvmField internal var mDirection = Direction.NONE
    @JvmField internal var isRunning = false
    //屏幕的尺寸
    @JvmField internal val mScreenWidth: Int
    @JvmField internal val mScreenHeight: Int
    //屏幕的间距
    internal val mMarginWidth: Int
    @JvmField internal val mMarginHeight: Int
    //视图的尺寸
    @JvmField internal val mViewWidth: Int
    @JvmField internal val mViewHeight: Int
    //起始点
    @JvmField internal var mStartX: Float = 0.0f
    @JvmField internal var mStartY: Float = 0.0f
    //触碰点
    @JvmField internal var mTouchX: Float = 0.0f
    @JvmField internal var mTouchY: Float = 0.0f
    //上一个触碰点
    internal var mLastX: Float = 0.0f
    @JvmField internal var mLastY: Float = 0.0f

    init {
        mScreenWidth = w
        mScreenHeight = h

        mMarginWidth = marginWidth
        mMarginHeight = marginHeight

        mViewWidth = mScreenWidth - mMarginWidth * 2
        mViewHeight = mScreenHeight - mMarginHeight * 2

        mView = view
        mListener = listener

        mScroller = Scroller(mView!!.context, LinearInterpolator())
    }

    constructor(w: Int, h: Int, view: View, listener: OnPageChangeListener) : this(w, h, 0, 0, view, listener)

    open fun setDirection(direction: Direction) {
        mDirection = direction
    }
    open fun setStartPoint(x: Float, y: Float) {
        mStartX = x
        mStartY = y

        mLastX = mStartX
        mLastY = mStartY
    }
    open fun setTouchPoint(x: Float, y: Float) {
        mLastX = mTouchX
        mLastY = mTouchY

        mTouchX = x
        mTouchY = y
    }

    /**
     * 开启翻页动画
     */
    open fun startAnim() {
        if (isRunning) {
            return
        }
        isRunning = true
    }

    fun clear() {
        mView = null
    }

    /**
     * 点击事件的处理
     * @param event
     */
    abstract fun onTouchEvent(event: MotionEvent): Boolean
    /**
     * 绘制图形
     * @param canvas
     */
    abstract fun draw(canvas: Canvas)
    /**
     * 滚动动画
     * 必须放在computeScroll()方法中执行
     */
    abstract fun scrollAnim()
    /**
     * 取消动画
     */
    abstract fun abortAnim()


    /**
     * 获取内容显示版面
     */
    abstract fun getNextBitmap(): Bitmap
    /**
     * 获取背景板
     * @return
     */
    abstract fun getBgBitmap(): Bitmap

    interface OnPageChangeListener {
        fun hasPrev(): Boolean
        fun hasNext(): Boolean
        fun pageCancel()
    }

    enum class Direction(isHorizontal: Boolean) {
        NONE(true),NEXT(true), PRE(true), UP(false), DOWN(false)
    }
}