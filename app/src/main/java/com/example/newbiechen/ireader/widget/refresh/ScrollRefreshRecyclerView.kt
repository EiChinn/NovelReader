package com.example.newbiechen.ireader.widget.refresh

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by newbiechen on 17-4-18.
 */

class ScrollRefreshRecyclerView : ScrollRefreshLayout {

    private var mRecyclerView: RecyclerView? = null

    /**************************public method */

    fun setLayoutManager(manager: RecyclerView.LayoutManager) {
        mRecyclerView!!.layoutManager = manager
    }

    fun addItemDecoration(decoration: RecyclerView.ItemDecoration) {
        mRecyclerView!!.addItemDecoration(decoration)
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        if (mRecyclerView != null) {
            mRecyclerView!!.adapter = adapter
        }
        adapter?.registerAdapterDataObserver(MyAdapterDataObserver())
    }

    /**
     * 刚进入的时候不点击界面，自动刷新
     */
    fun startRefresh() {
        mRecyclerView!!.post { isRefreshing = true }
    }

    fun finishRefresh() {
        mRecyclerView!!.post { isRefreshing = false }
    }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    /*************************init */
    override fun getContentView(parent: ViewGroup): View {
        mRecyclerView = RecyclerView(context)
        return mRecyclerView!!
    }

    /**************************inner class  */
    internal inner class MyAdapterDataObserver : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            update()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            update()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            super.onItemRangeChanged(positionStart, itemCount, payload)
            update()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            update()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            update()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            update()
        }

        private fun update() {
            val count = mRecyclerView!!.adapter!!.itemCount
            if (count == 0) {
                showEmptyView()
                mRecyclerView!!.visibility = View.GONE
            } else if (mRecyclerView!!.visibility == View.GONE) {
                hideEmptyView()
                mRecyclerView!!.visibility = View.VISIBLE
            }
        }
    }

    override fun onDetachedFromWindow() {
        mRecyclerView!!.adapter = null
        mRecyclerView = null
        setOnRefreshListener(null)
        super.onDetachedFromWindow()

    }

    companion object {

        private const val TAG = "RefreshRecyclerView"



        /****************************inner method */
        fun log(str: String) {
            Log.d(TAG, str)
        }
    }
}
