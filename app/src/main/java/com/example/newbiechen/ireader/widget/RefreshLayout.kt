package com.example.newbiechen.ireader.widget

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.newbiechen.ireader.R

class RefreshLayout(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    companion object {
        private const val STATUS_LOADING = 0
        private const val STATUS_FINISH = 1
        private const val STATUS_ERROR = 2
        private const val STATUS_EMPTY = 3
    }

    private var mEmptyViewId = 0
    private var mErrorViewId = 0
    private var mLoadingViewId = 0

    private var mEmptyView: View? = null
    private var mErrorView: View? = null
    private var mLoadingView: View? = null
    private var mContentView: View? = null

    private var mListener: OnReloadingListener? = null
    private var mStatus = 0

    init {
        initAttrs(attrs)
        initView()
    }

    private fun initAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RefreshLayout)
        mEmptyViewId = typedArray.getResourceId(R.styleable.RefreshLayout_layout_refresh_empty, R.layout.view_empty)
        mErrorViewId = typedArray.getResourceId(R.styleable.RefreshLayout_layout_refresh_error, R.layout.view_net_error)
        mLoadingViewId = typedArray.getResourceId(R.styleable.RefreshLayout_layout_refresh_loading, R.layout.view_loading)

        typedArray.recycle()
    }

    private fun initView() {

        //添加在empty、error、loading 情况下的布局
        mEmptyView = inflateView(mEmptyViewId)
        mErrorView = inflateView(mErrorViewId)
        mLoadingView = inflateView(mLoadingViewId)

        addView(mEmptyView!!)
        addView(mErrorView!!)
        addView(mLoadingView!!)

        //设置监听器
        mErrorView?.setOnClickListener { view ->
            mListener?.let {
                toggleStatus(STATUS_LOADING)
                it.onReload()
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        toggleStatus(STATUS_LOADING)
    }

    override fun onViewAdded(child: View) {
        super.onViewAdded(child)
        if (childCount == 4) {
            mContentView = child
        }
    }

    //除了自带的数据，保证子类只能够添加一个子View
    override fun addView(child: View) {
        if (childCount > 4) {
            throw IllegalStateException("RefreshLayout can host only one direct child")
        }
        super.addView(child)
    }

    override fun addView(child: View, index: Int) {
        if (childCount > 4) {
            throw IllegalStateException("RefreshLayout can host only one direct child")
        }

        super.addView(child, index)
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        if (childCount > 4) {
            throw IllegalStateException("RefreshLayout can host only one direct child")
        }

        super.addView(child, params)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (childCount > 4) {
            throw IllegalStateException("RefreshLayout can host only one direct child")
        }

        super.addView(child, index, params)
    }

    fun showLoading() {
        if (mStatus != STATUS_LOADING) {
            toggleStatus(STATUS_LOADING)
        }
    }

    fun showFinish() {
        if (mStatus == STATUS_LOADING) {
            toggleStatus(STATUS_FINISH)
        }
    }

    fun showError() {
        if (mStatus != STATUS_ERROR) {
            toggleStatus(STATUS_ERROR)
        }
    }

    fun showEmpty() {
        if (mStatus != STATUS_EMPTY) {
            toggleStatus(STATUS_EMPTY)
        }
    }

    //视图根据状态切换
    private fun toggleStatus(status: Int) {
        when (status) {
            STATUS_LOADING -> {
                mLoadingView?.visibility = View.VISIBLE
                mEmptyView?.visibility = View.GONE
                mErrorView?.visibility = View.GONE
                mContentView?.visibility = View.GONE
            }
            STATUS_FINISH -> {
                mContentView?.visibility = View.VISIBLE
                mLoadingView?.visibility = View.GONE
                mEmptyView?.visibility = View.GONE
                mErrorView?.visibility = View.GONE
            }
            STATUS_ERROR -> {
                mErrorView?.visibility = View.VISIBLE
                mLoadingView?.visibility = View.GONE
                mEmptyView?.visibility = View.GONE
                mContentView?.visibility = View.GONE
            }
            STATUS_EMPTY -> {
                mEmptyView?.visibility = View.VISIBLE
                mErrorView?.visibility = View.GONE
                mLoadingView?.visibility = View.GONE
                mContentView?.visibility = View.GONE
            }
        }
        mStatus = status
    }

    private fun inflateView(id: Int): View {
        return LayoutInflater.from(context)
                .inflate(id, this, false)
    }

    fun setOnReloadingListener(listener: OnReloadingListener) {
        mListener = listener
    }

    //数据存储
    override fun onSaveInstanceState(): Parcelable? {
        val superParcel = super.onSaveInstanceState()
        val savedState = SavedState(superParcel)
        savedState.status = mStatus
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.getSuperState())
        //刷新状态
        toggleStatus(savedState.status)
    }

    // kotlin 的 class 可以没有 primary constructor
    class SavedState : View.BaseSavedState {
        var status: Int = 0

        constructor(superState: Parcelable) : super(superState)

        private constructor(inParcel: Parcel) : super(inParcel) {
            status = inParcel.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(status)
        }

        companion object {

            @JvmField val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }


    //添加错误重新加载的监听
    interface OnReloadingListener {
        fun onReload()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mErrorView = null
        mLoadingView = null
        mContentView = null
    }

}