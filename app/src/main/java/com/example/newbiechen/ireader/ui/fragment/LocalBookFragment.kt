package com.example.newbiechen.ireader.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.local.BookRepository
import com.example.newbiechen.ireader.ui.adapter.FileSystemAdapter
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.utils.media.MediaStoreHelper
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_local_book.*
import java.io.File

/**
 * Created by newbiechen on 17-5-27.
 * 本地书籍
 */

class LocalBookFragment : BaseFileFragment() {

    override fun getContentId(): Int {
        return R.layout.fragment_local_book
    }

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        setUpAdapter()
    }

    private fun setUpAdapter() {
        mAdapter = FileSystemAdapter()
        local_book_rv_content.layoutManager = LinearLayoutManager(context)
        local_book_rv_content.addItemDecoration(DividerItemDecoration(context!!))
        local_book_rv_content.adapter = mAdapter
    }

    override fun initClick() {
        super.initClick()
        mAdapter!!.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                //如果是已加载的文件，则点击事件无效。
                val id = mAdapter!!.getItem(pos).absolutePath
                if (BookRepository.instance.getCollBook(id) == null) {
                    //点击选中
                    mAdapter!!.setCheckedItem(pos)

                    //反馈
                    if (mListener != null) {
                        mListener!!.onItemCheckedChange(mAdapter!!.getItemIsChecked(pos))
                    }
                }


            }

        })
    }

    override fun processLogic() {
        super.processLogic()
        MediaStoreHelper.getAllBookFile(activity!!, object : MediaStoreHelper.MediaResultCallback {
            override fun onResultCallback(files: List<File>) {
                if (files.isEmpty()) {
                    refresh_layout.showEmpty()
                } else {
                    mAdapter!!.refreshItems(files)
                    refresh_layout.showFinish()
                    //反馈
                    if (mListener != null) {
                        mListener!!.onCategoryChanged()
                    }
                }
            }
        })
    }
}
