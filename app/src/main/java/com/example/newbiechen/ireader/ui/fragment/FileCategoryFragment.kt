package com.example.newbiechen.ireader.ui.fragment

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.local.BookRepository
import com.example.newbiechen.ireader.ui.adapter.FileSystemAdapter
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.utils.FileStack
import com.example.newbiechen.ireader.utils.FileUtils
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_file_category.*
import java.io.File
import java.io.FileFilter
import java.util.*

/**
 * Created by newbiechen on 17-5-27.
 */

class FileCategoryFragment : BaseFileFragment() {

    private var mFileStack: FileStack? = null

    val fileCount: Int
        get() {
            var count = 0
            val entrys = mAdapter!!.checkMap.entries
            for (entry in entrys) {
                if (!entry.key.isDirectory) {
                    ++count
                }
            }
            return count
        }

    override fun getContentId(): Int {
        return R.layout.fragment_file_category
    }

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        mFileStack = FileStack()
        setUpAdapter()
    }

    private fun setUpAdapter() {
        mAdapter = FileSystemAdapter()
        file_category_rv_content.layoutManager = LinearLayoutManager(context)
        file_category_rv_content.addItemDecoration(DividerItemDecoration(context!!))
        file_category_rv_content.adapter = mAdapter
    }

    override fun initClick() {
        super.initClick()
        mAdapter!!.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                val file = mAdapter!!.getItem(pos)
                if (file.isDirectory) {
                    //保存当前信息。
                    val snapshot = FileStack.FileSnapshot()
                    snapshot.filePath = file_category_tv_path.text.toString()
                    snapshot.files = ArrayList(mAdapter!!.getItems())
                    snapshot.scrollOffset = file_category_rv_content.computeVerticalScrollOffset()
                    mFileStack!!.push(snapshot)
                    //切换下一个文件
                    toggleFileTree(file)
                } else {

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
            }

        })

        file_category_tv_back_last.setOnClickListener { v ->
            val snapshot = mFileStack!!.pop()
            val oldScrollOffset = file_category_rv_content.computeHorizontalScrollOffset()
            if (snapshot != null){
                file_category_tv_path.text = snapshot.filePath
                mAdapter!!.refreshItems(snapshot.files!!)
                file_category_rv_content.scrollBy(0, snapshot.scrollOffset - oldScrollOffset)
                //反馈
                if (mListener != null) {
                    mListener!!.onCategoryChanged()
                }
            }



        }

    }

    override fun processLogic() {
        super.processLogic()
        val root = Environment.getExternalStorageDirectory()
        toggleFileTree(root)
    }

    private fun toggleFileTree(file: File) {
        //路径名
        file_category_tv_path.text = getString(R.string.nb_file_path, file.path)
        //获取数据
        val files = file.listFiles(SimpleFileFilter())
        //转换成List
        val rootFiles = Arrays.asList(*files)
        //排序
        Collections.sort(rootFiles, FileComparator())
        //加入
        mAdapter!!.refreshItems(rootFiles)
        //反馈
        if (mListener != null) {
            mListener!!.onCategoryChanged()
        }
    }

    inner class FileComparator : Comparator<File> {
        override fun compare(o1: File, o2: File): Int {
            if (o1.isDirectory && o2.isFile) {
                return -1
            }
            return if (o2.isDirectory && o1.isFile) {
                1
            } else o1.name.compareTo(o2.name, ignoreCase = true)
        }
    }

    inner class SimpleFileFilter : FileFilter {
        override fun accept(pathname: File): Boolean {
            if (pathname.name.startsWith(".")) {
                return false
            }
            //文件夹内部数量为0
            if (pathname.isDirectory && pathname.list().isEmpty()) {
                return false
            }

            /**
             * 现在只支持TXT文件的显示
             */
            //文件内容为空,或者不以txt为开头
            return !(!pathname.isDirectory && (pathname.length() == 0L || !pathname.name.endsWith(FileUtils.SUFFIX_TXT)))
        }
    }
}
