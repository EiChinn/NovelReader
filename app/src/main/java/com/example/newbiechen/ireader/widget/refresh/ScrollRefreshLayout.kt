package com.example.newbiechen.ireader.widget.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.newbiechen.ireader.R

/**
 * Created by newbiechen on 17-4-18.
 * 自带错误提示、数据为空、下拉刷新的控件
 */

abstract class ScrollRefreshLayout (
        context: Context, attrs: AttributeSet? = null) : SwipeRefreshLayout(context, attrs) {
    private var mFlContent: FrameLayout? = null
    private var mTvTip: TextView? = null
    private var mEmptyView: View? = null
    private var mContentView: View? = null
    private var mTipOpenAnim: Animation? = null
    private var mTopCloseAnim: Animation? = null
    /** */
    @LayoutRes
    private var mEmptyId = R.layout.view_empty

    /****************************abstract method */
    abstract fun getContentView(parent: ViewGroup): View

    /****************************public method */
    /*需要自己关闭*/
    private fun toggleTip() {
        initAnim()
        cancelAnim()
        if (mTvTip!!.visibility == View.GONE) {
            mTvTip!!.visibility = View.VISIBLE
            mTvTip!!.startAnimation(mTipOpenAnim)
        } else {
            mTvTip!!.startAnimation(mTopCloseAnim)
            mTvTip!!.visibility = View.GONE
        }
    }

    private fun initAnim() {
        if (mTipOpenAnim == null || mTopCloseAnim == null) {
            mTipOpenAnim = AnimationUtils.loadAnimation(context, R.anim.slide_top_in)
            mTopCloseAnim = AnimationUtils.loadAnimation(context, R.anim.slide_top_out)

            mTipOpenAnim!!.fillAfter = true
            mTopCloseAnim!!.fillAfter = true
        }
    }

    fun setTip(str: String) {
        mTvTip!!.text = str
    }

    /*自动关闭*/
    fun showTip() {
        //自动关闭
        toggleTip()
        val runnable = {
            mTvTip!!.startAnimation(mTopCloseAnim)
            mTvTip!!.visibility = View.GONE
        }
        mTvTip!!.removeCallbacks(runnable)
        if (mTvTip!!.visibility == View.VISIBLE) {
            mTvTip!!.postDelayed(runnable, 2000)
        }
    }

    private fun cancelAnim() {
        if (mTipOpenAnim!!.hasStarted()) {
            mTipOpenAnim!!.cancel()
        }
        if (mTopCloseAnim!!.hasStarted()) {
            mTipOpenAnim!!.cancel()
        }
    }

    /********************************protected method */
    protected fun showEmptyView() {
        mEmptyView!!.visibility = View.VISIBLE
    }

    protected fun hideEmptyView() {
        mEmptyView!!.visibility = View.GONE
    }

    init {
        initAttrs(attrs)
        initView()
    }

    private fun initAttrs(attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.ScrollRefreshLayout)
        val emptyId = array.getResourceId(R.styleable.ScrollRefreshLayout_layout_scroll_empty, ATTR_NULL)

        if (emptyId != ATTR_NULL) mEmptyId = emptyId
    }

    private fun initView() {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.layout_scroll_refresh, this, false)
        addView(view)
        //init View
        mFlContent = view.findViewById(R.id.scroll_refresh_fl_content)


        mTvTip = view.findViewById(R.id.scroll_refresh_tv_tip)

        mEmptyView = inflateId(mFlContent, mEmptyId)
        mFlContent!!.addView(mEmptyView)

        mContentView = getContentView(mFlContent!!)

        //默认不显示
        if (mContentView != null) {
            val params = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            mFlContent!!.addView(mContentView, params)
            mContentView!!.visibility = View.GONE
        }
    }

    private fun inflateId(parent: ViewGroup?, @LayoutRes id: Int): View {
        return LayoutInflater.from(context)
                .inflate(id, parent, false)
    }

    override fun onDetachedFromWindow() {
        mContentView = null
        mEmptyView = null
        mFlContent?.removeAllViews()
        mFlContent = null
        super.onDetachedFromWindow()
    }

    companion object {
        private const val ATTR_NULL = -1
    }
}
/******************************init **************************************8 */
