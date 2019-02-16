package com.example.newbiechen.ireader.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ScrollView
import android.widget.Scroller

/**
 * Created by newbiechen on 17-4-23.
 */

class ReboundScrollView : ScrollView {
    private var mContentView: View? = null
    private var mViewOriginRect: Rect? = null
    private var mScroller: Scroller? = null

    private var canPullUp = false
    private var canPullDown = false

    private var mStartY: Int = 0

    private var isMove = false

    /**
     * 判断是否滚动到顶部
     * contentView < 屏幕 - 滑动大小
     */

    private val isCanPullDown: Boolean
        get() = scrollY <= 0

    /**
     * 判断是否滚动到底部
     * contentView < 屏幕 + 滑动大小
     */
    private val isCanPullUp: Boolean
        get() = mContentView!!.height <= height + scrollY

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        mScroller = Scroller(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mScroller = Scroller(context)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            //获取包裹的View
            mContentView = getChildAt(0)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (mContentView == null) {
            return
        }
        if (mViewOriginRect == null) {
            mViewOriginRect = Rect()
            mViewOriginRect!!.set(mContentView!!.left, mContentView!!.top,
                    mContentView!!.right, mContentView!!.bottom)
        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (mContentView == null) {
            return super.dispatchTouchEvent(ev)
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartY = ev.y.toInt()

                canPullDown = isCanPullDown
                canPullUp = isCanPullUp
            }
            MotionEvent.ACTION_MOVE -> if (!canPullDown && !canPullUp) {
                canPullDown = isCanPullDown
                canPullUp = isCanPullUp
            } else {
                //首先判断，需要滑动吗，找到不需要的并排除
                val currentY = ev.y.toInt()
                //表示可以进行滑动，计算滑动值 (下滑为负，上划为正)
                val deltaY = mStartY - currentY
                //以下情况才真正可以进行滑动
                val shouldMove = (canPullDown && deltaY < 0
                        || canPullUp && deltaY > 0 || canPullDown && canPullUp)

                if (shouldMove) {
                    val moveValue = (deltaY * MOVE_FACTOR).toInt()
                    mContentView!!.layout(mViewOriginRect!!.left, mViewOriginRect!!.top - moveValue,
                            mViewOriginRect!!.right, mViewOriginRect!!.bottom - moveValue)
                    isMove = true
                }
            }
            MotionEvent.ACTION_UP -> if (isMove) {
                val distance = mViewOriginRect!!.top - mContentView!!.top
                //移动到初始时候的位置
                mScroller!!.startScroll(0, mContentView!!.top, 0, distance, SCROLL_TIME)
                invalidate()

                canPullDown = false
                canPullUp = false
                isMove = false
            }
            else -> {
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller!!.computeScrollOffset()) {
            val value = mScroller!!.currY
            mContentView!!.layout(mViewOriginRect!!.left, mViewOriginRect!!.top + value, mViewOriginRect!!.right,
                    mViewOriginRect!!.bottom + value)
            postInvalidate()
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mScroller = null
        mContentView = null
    }

    companion object {
        //阻尼因子
        private const val MOVE_FACTOR = 0.5f
        private const val SCROLL_TIME = 800
    }
}
