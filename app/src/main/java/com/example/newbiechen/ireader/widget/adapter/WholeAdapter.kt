package com.example.newbiechen.ireader.widget.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import java.util.*

abstract class  WholeAdapter<T>() : BaseListAdapter<T>() {
    companion object {
        private const val TYPE_ITEM = 0
    }

    private var mLoadDelegate: LoadMoreDelegate? = null
    private val mHeaderList = ArrayList<ItemView>(2)
    private val mFooterList = ArrayList<ItemView>(2)

    constructor(context: Context, options: Options?) : this(){
        options?.let {
            mLoadDelegate = LoadMoreDelegate(context, options)
            mFooterList.add(mLoadDelegate!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) super.onCreateViewHolder(parent, viewType) else createOtherViewHolder(parent, viewType)
    }

    private fun createOtherViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View? = null
        mHeaderList.forEach {
            if (viewType == it.hashCode()) {
                view = it.onCreateView(parent)
            }
        }
        mFooterList.forEach {
            if (viewType == it.hashCode()) {
                view = it.onCreateView(parent)
            }
        }

        return object : RecyclerView.ViewHolder(view!!){}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            position < mHeaderList.size -> mHeaderList[position].onBindView(holder.itemView)
            position < mHeaderList.size + getItemSize() -> super.onBindViewHolder(holder, position - mHeaderList.size)
            else -> mFooterList[position - mHeaderList.size - getItemSize()].onBindView(holder.itemView)

        }

    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position < mHeaderList.size -> mHeaderList[position].hashCode()
            position < mHeaderList.size + getItemSize() -> TYPE_ITEM
            else -> mFooterList[position - mHeaderList.size - getItemSize()].hashCode()

        }

    }

    override fun getItemCount(): Int {
        return mHeaderList.size + getItemSize() + mFooterList.size
    }

    fun addHeaderView(itemView: ItemView) {
        mHeaderList.add(itemView)
    }
    fun addFooterView(itemView: ItemView) {
        if (mLoadDelegate != null) {
            mFooterList.add(mFooterList.size - 1, itemView)

        } else {
            mHeaderList.add(itemView)
        }
    }

    override fun addItems(values: List<@JvmSuppressWildcards T>) {
        if (values.isEmpty()) {
            mLoadDelegate?.setLoadMoreStatus(LoadMoreView.TYPE_NO_MORE)

        }
        super.addItems(values)
    }

    fun setOnLoadMoreListener(listener: LoadMoreView.OnLoadMoreListener) {
        checkLoadMoreExist()
        mLoadDelegate?.setOnLoadMoreListener(listener)
    }
    fun setOnLoadMoreListener(listener: () -> Unit) {
        checkLoadMoreExist()
        mLoadDelegate?.setOnLoadMoreListener(listener)
    }

    private fun checkLoadMoreExist() {
        if (mLoadDelegate == null)
            throw IllegalArgumentException("you must setting LoadMore Option")
    }

    override fun refreshItems(list: List<@JvmSuppressWildcards T>) {
        mLoadDelegate?.setLoadMoreStatus(LoadMoreView.TYPE_LOAD_MORE)
        super.refreshItems(list)
    }

    fun showLoadError() {
        checkLoadMoreExist()
        mLoadDelegate?.setLoadMoreStatus(LoadMoreView.TYPE_LOAD_ERROR)
        notifyDataSetChanged()
    }


    class Options{
        @LayoutRes val loadMoreId = R.layout.view_load_more
        @LayoutRes val errorId = R.layout.view_error
        @LayoutRes val noMoreId = R.layout.view_nomore
    }
    interface ItemView {
        fun onCreateView(parent: ViewGroup): View
        fun onBindView(view: View)
    }
}