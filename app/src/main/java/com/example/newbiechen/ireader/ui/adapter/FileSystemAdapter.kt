package com.example.newbiechen.ireader.ui.adapter

import com.example.newbiechen.ireader.model.local.BookRepository
import com.example.newbiechen.ireader.ui.adapter.view.FileHolder
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder
import java.io.File
import java.util.*

/**
 * Created by newbiechen on 17-5-27.
 */

class FileSystemAdapter : BaseListAdapter<File>() {
    //记录item是否被选中的Map
    val checkMap = HashMap<File, Boolean>()
    var checkedCount = 0
        private set

    val checkableCount: Int
        get() {
            val files = getItems()
            var count = 0
            for (file in files) {
                if (!isFileLoaded(file.absolutePath) && file.isFile)
                    ++count
            }
            return count
        }

    val checkedFiles: List<File>
        get() {
            val files = ArrayList<File>()
            val entrys = checkMap.entries
            for ((key, value) in entrys) {
                if (value) {
                    files.add(key)
                }
            }
            return files
        }

    override fun createViewHolder(viewType: Int): IViewHolder<File> {
        return FileHolder(checkMap)
    }

    override fun refreshItems(list: List<File>) {
        checkMap.clear()
        for (file in list) {
            checkMap[file] = false
        }
        super.refreshItems(list)
    }

    override fun addItem(value: File) {
        checkMap[value] = false
        super.addItem(value)
    }

    override fun addItem(index: Int, value: File) {
        checkMap[value] = false
        super.addItem(index, value)
    }

    override fun addItems(values: List<File>) {
        for (file in values) {
            checkMap[file] = false
        }
        super.addItems(values)
    }

    override fun removeItem(value: File) {
        checkMap.remove(value)
        super.removeItem(value)
    }

    override fun removeItems(value: List<File>) {
        //删除在HashMap中的文件
        for (file in value) {
            checkMap.remove(file)
            //因为，能够被移除的文件，肯定是选中的
            --checkedCount
        }
        //删除列表中的文件
        super.removeItems(value)
    }

    //设置点击切换
    fun setCheckedItem(pos: Int) {
        val file = getItem(pos)
        if (isFileLoaded(file.absolutePath)) return

        val isSelected = checkMap[file]!!
        if (isSelected) {
            checkMap[file] = false
            --checkedCount
        } else {
            checkMap[file] = true
            ++checkedCount
        }
        notifyDataSetChanged()
    }

    fun setCheckedAll(isChecked: Boolean) {
        val entrys = checkMap.entries
        checkedCount = 0
        for (entry in entrys) {
            //必须是文件，必须没有被收藏
            if (entry.key.isFile && !isFileLoaded(entry.key.absolutePath)) {
                entry.setValue(isChecked)
                //如果选中，则增加点击的数量
                if (isChecked) {
                    ++checkedCount
                }
            }
        }
        notifyDataSetChanged()
    }

    private fun isFileLoaded(id: String): Boolean {
        //如果是已加载的文件，则点击事件无效。
        return BookRepository.getInstance().getCollBook(id) != null
    }

    fun getItemIsChecked(pos: Int): Boolean {
        val file = getItem(pos)
        return checkMap[file]!!
    }
}
