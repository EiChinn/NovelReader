package com.example.newbiechen.ireader.ui.fragment

import com.example.newbiechen.ireader.ui.adapter.FileSystemAdapter
import com.example.newbiechen.ireader.ui.base.BaseFragment

import java.io.File

/**
 * Created by newbiechen on 17-7-10.
 * FileSystemActivity的基础Fragment类
 */

abstract class BaseFileFragment : BaseFragment() {

    protected var mAdapter: FileSystemAdapter? = null
    protected var mListener: OnFileCheckedListener? = null
    private var isCheckedAll: Boolean = false

    //获取被选中的数量
    val checkedCount: Int
        get() = if (mAdapter == null) 0 else mAdapter!!.checkedCount

    //获取被选中的文件列表
    val checkedFiles: List<File>?
        get() = if (mAdapter != null) mAdapter!!.checkedFiles else null


    //获取可点击的文件的数量
    val checkableCount: Int
        get() = if (mAdapter == null) 0 else mAdapter!!.checkableCount

    //设置当前列表为全选
    fun setCheckedAll(checkedAll: Boolean) {
        if (mAdapter == null) return

        isCheckedAll = checkedAll
        mAdapter!!.setCheckedAll(checkedAll)
    }

    fun setChecked(checked: Boolean) {
        isCheckedAll = checked
    }

    //当前fragment是否全选
    fun isCheckedAll(): Boolean {
        return isCheckedAll
    }

    /**
     * 删除选中的文件
     */
    fun deleteCheckedFiles() {
        //删除选中的文件
        val files = checkedFiles
        //删除显示的文件列表
        mAdapter!!.removeItems(files!!)
        //删除选中的文件
        for (file in files) {
            if (file.exists()) {
                file.delete()
            }
        }
    }

    //设置文件点击监听事件
    fun setOnFileCheckedListener(listener: OnFileCheckedListener) {
        mListener = listener
    }

    //文件点击监听
    interface OnFileCheckedListener {
        fun onItemCheckedChange(isChecked: Boolean)
        fun onCategoryChanged()
    }
}
