package com.example.newbiechen.ireader.widget.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes

class LoadMoreView(context: Context, @LayoutRes loadMoreId: Int, @LayoutRes errorId: Int,
                   @LayoutRes noMoreId: Int) : FrameLayout(context) {

    companion object {
        private const val TYPE_HIDE = 0
        const val TYPE_LOAD_MORE = 1
        const val TYPE_NO_MORE = 2
        const val TYPE_LOAD_ERROR = 3
    }

    private lateinit var mLoadMoreView: View
    private lateinit var mErrorView: View
    private lateinit var mNoMoreView: View
    private lateinit var mListener: OnLoadMoreListener
    private var mStatus = TYPE_HIDE

    init {
        mLoadMoreView = inflateId(loadMoreId)
        mErrorView = inflateId(errorId)
        mNoMoreView = inflateId(noMoreId)

        addView(mLoadMoreView)
        addView(mErrorView)
        addView(mNoMoreView)

        refreshView()

        mErrorView.setOnClickListener { v -> setLoadMore() }
    }

    private fun inflateId(id: Int): View {
        return LayoutInflater.from(context).inflate(id, this, false)
    }

    fun refreshView() {
        when (mStatus) {
            TYPE_HIDE -> setHide()
            TYPE_LOAD_MORE -> setLoadMore()
            TYPE_NO_MORE -> setLoadNoMore()
            TYPE_LOAD_ERROR -> setLoadError()

        }
    }

    fun setLoadMoreStatus(status: Int) {
        mStatus = status
        refreshView()
    }

    private fun setHide(){
        mLoadMoreView.visibility = View.GONE
        mErrorView.visibility = View.GONE
        mNoMoreView.visibility = View.GONE
    }
    private fun setLoadMore(){
        mLoadMoreView.visibility = View.VISIBLE
        mErrorView.visibility = View.GONE
        mNoMoreView.visibility = View.GONE
        mListener?.onLoadMore()
    }
    private fun setLoadError(){
        mLoadMoreView.visibility = View.GONE
        mErrorView.visibility = View.VISIBLE
        mNoMoreView.visibility = View.GONE
    }
    private fun setLoadNoMore(){
        mLoadMoreView.visibility = View.GONE
        mErrorView.visibility = View.GONE
        mNoMoreView.visibility = View.VISIBLE
    }
    fun setOnLoadMoreListener(listener: OnLoadMoreListener) {
        mListener = listener
    }
    interface OnLoadMoreListener {
        fun onLoadMore()
    }

}