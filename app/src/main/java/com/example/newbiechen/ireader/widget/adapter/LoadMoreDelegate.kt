package com.example.newbiechen.ireader.widget.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

class LoadMoreDelegate(context: Context, options: WholeAdapter.Options) : WholeAdapter.ItemView {
    private val mLoadMoreView: LoadMoreView = LoadMoreView(context, options.loadMoreId, options.errorId, options.noMoreId)

    init {
        mLoadMoreView.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    override fun onCreateView(parent: ViewGroup?): View {
        return mLoadMoreView
    }

    override fun onBindView(view: View?) {
        (view as LoadMoreView).refreshView()
    }

    fun setLoadMoreStatus(status: Int) {
        mLoadMoreView.setLoadMoreStatus(status)
    }

    fun setOnLoadMoreListener(listener: LoadMoreView.OnLoadMoreListener) {
        mLoadMoreView.setOnLoadMoreListener(listener)
    }

}