package com.example.newbiechen.ireader.ui.adapter


import androidx.recyclerview.widget.RecyclerView
import com.example.newbiechen.ireader.model.bean.BookTagBean
import com.example.newbiechen.ireader.ui.adapter.view.TagChildHolder
import com.example.newbiechen.ireader.ui.adapter.view.TagGroupHolder
import com.example.newbiechen.ireader.ui.base.adapter.GroupAdapter
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder
import java.util.*

/**
 * Created by newbiechen on 17-5-5.
 * BookListTagGroup
 */

class TagGroupAdapter(recyclerView: RecyclerView, spanSize: Int) : GroupAdapter<String, String>(recyclerView, spanSize) {
    private val mBookTagList = ArrayList<BookTagBean>()

    override fun getGroupCount(): Int {
        return mBookTagList.size
    }

    override fun getChildCount(groupPos: Int): Int {
        val tagList = mBookTagList[groupPos].tags
        return tagList.size
    }

    override fun getGroupItem(groupPos: Int): String {
        return mBookTagList[groupPos].name
    }

    override fun getChildItem(groupPos: Int, childPos: Int): String {
        val tagList = getChildItems(groupPos)
        return tagList[childPos]
    }

    override fun createGroupViewHolder(): IViewHolder<String> {
        //是个TextView
        return TagGroupHolder()
    }

    override fun createChildViewHolder(): IViewHolder<String> {
        //是个TextView
        return TagChildHolder()
    }

    private fun getChildItems(groupPos: Int): List<String> {
        return mBookTagList[groupPos].tags
    }

    fun refreshItems(bookTags: List<BookTagBean>) {
        mBookTagList.clear()
        mBookTagList.addAll(bookTags)
        notifyDataSetChanged()
    }

}
