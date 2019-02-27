package com.example.newbiechen.ireader.ui.activity

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.db.entity.CollBook
import com.example.newbiechen.ireader.model.local.BookRepository
import com.example.newbiechen.ireader.ui.base.BaseTabActivity
import com.example.newbiechen.ireader.ui.fragment.BaseFileFragment
import com.example.newbiechen.ireader.ui.fragment.BaseFileFragment.OnFileCheckedListener
import com.example.newbiechen.ireader.ui.fragment.FileCategoryFragment
import com.example.newbiechen.ireader.ui.fragment.LocalBookFragment
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.MD5Utils
import com.example.newbiechen.ireader.utils.StringUtils
import com.example.newbiechen.ireader.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_file_system.*
import java.io.File
import java.util.*

/**
 * Created by newbiechen on 17-5-27.
 */

class FileSystemActivity : BaseTabActivity() {

    private lateinit var mLocalFragment: LocalBookFragment
    private lateinit var mCategoryFragment: FileCategoryFragment
    private var mCurFragment: BaseFileFragment? = null

    private val mListener = object : OnFileCheckedListener {
        override fun onItemCheckedChange(isChecked: Boolean) {
            changeMenuStatus()
        }

        override fun onCategoryChanged() {
            //状态归零
            mCurFragment!!.setCheckedAll(false)
            //改变菜单
            changeMenuStatus()
            //改变是否能够全选
            changeCheckedAllStatus()
        }
    }

    override fun createTabFragments(): MutableList<Fragment> {
        mLocalFragment = LocalBookFragment()
        mCategoryFragment = FileCategoryFragment()
        return mutableListOf(mLocalFragment, mCategoryFragment)
    }

    override fun createTabTitles() = mutableListOf("智能导入", "手机目录")

    override fun getContentId(): Int {
        return R.layout.activity_file_system
    }

    override fun setUpToolbar(toolbar: Toolbar) {
        super.setUpToolbar(toolbar)
        supportActionBar!!.title = "本机导入"
    }

    override fun initClick() {
        super.initClick()
        file_system_cb_selected_all!!.setOnClickListener {
            //设置全选状态
            val isChecked = file_system_cb_selected_all!!.isChecked
            mCurFragment!!.setCheckedAll(isChecked)
            //改变菜单状态
            changeMenuStatus()
        }

        mVp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    mCurFragment = mLocalFragment
                } else {
                    mCurFragment = mCategoryFragment
                }
                //改变菜单状态
                changeMenuStatus()
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        file_system_btn_add_book!!.setOnClickListener {
            //获取选中的文件
            val files = mCurFragment!!.checkedFiles
            //转换成CollBook,并存储
            val collBooks = convertCollBook(files!!)
            BookRepository.instance.insertOrUpdateCollBooks(collBooks)
            //设置HashMap为false
            mCurFragment!!.setCheckedAll(false)
            //改变菜单状态
            changeMenuStatus()
            //改变是否可以全选
            changeCheckedAllStatus()
            //提示加入书架成功
            ToastUtils.show(resources.getString(R.string.nb_file_add_succeed, collBooks.size))

        }

        file_system_btn_delete!!.setOnClickListener {
            //弹出，确定删除文件吗。
            AlertDialog.Builder(this)
                    .setTitle("删除文件")
                    .setMessage("确定删除文件吗?")
                    .setPositiveButton(resources.getString(R.string.nb_common_sure)) { _, _ ->
                        //删除选中的文件
                        mCurFragment!!.deleteCheckedFiles()
                        //提示删除文件成功
                        ToastUtils.show("删除文件成功")
                    }
                    .setNegativeButton(resources.getString(R.string.nb_common_cancel), null)
                    .show()
        }

        mLocalFragment.setOnFileCheckedListener(mListener)
        mCategoryFragment.setOnFileCheckedListener(mListener)
    }

    override fun processLogic() {
        super.processLogic()
        mCurFragment = mLocalFragment
    }

    /**
     * 将文件转换成CollBook
     * @param files:需要加载的文件列表
     * @return
     */
    private fun convertCollBook(files: List<File>): List<CollBook> {
        val collBooks = ArrayList<CollBook>(files.size)
        for (file in files) {
            //判断文件是否存在
            if (!file.exists()) continue

            val collBook = CollBook()
            collBook.bookId = MD5Utils.strToMd5By16(file.absolutePath)
            collBook.title = file.name.replace(".txt", "")
            collBook.author = ""
            collBook.shortIntro = "无"
            collBook.cover = file.absolutePath
            collBook.isLocal = true
            collBook.lastChapter = "开始阅读"
            collBook.updated = StringUtils.dateConvert(file.lastModified(), Constant.FORMAT_BOOK_DATE)
            collBook.lastRead = StringUtils.dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE)
            collBooks.add(collBook)
        }
        return collBooks
    }

    /**
     * 改变底部选择栏的状态
     */
    private fun changeMenuStatus() {

        //点击、删除状态的设置
        if (mCurFragment!!.checkedCount == 0) {
            file_system_btn_add_book!!.text = getString(R.string.nb_file_add_shelf)
            //设置某些按钮的是否可点击
            setMenuClickable(false)

            if (file_system_cb_selected_all!!.isChecked) {
                mCurFragment!!.setChecked(false)
                file_system_cb_selected_all!!.isChecked = mCurFragment!!.isCheckedAll()
            }

        } else {
            file_system_btn_add_book!!.text = getString(R.string.nb_file_add_shelves, mCurFragment!!.checkedCount)
            setMenuClickable(true)

            //全选状态的设置

            //如果选中的全部的数据，则判断为全选
            if (mCurFragment!!.checkedCount == mCurFragment!!.checkableCount) {
                //设置为全选
                mCurFragment!!.setChecked(true)
                file_system_cb_selected_all!!.isChecked = mCurFragment!!.isCheckedAll()
            } else if (mCurFragment!!.isCheckedAll()) {
                mCurFragment!!.setChecked(false)
                file_system_cb_selected_all!!.isChecked = mCurFragment!!.isCheckedAll()
            }//如果曾今是全选则替换
        }

        //重置全选的文字
        if (mCurFragment!!.isCheckedAll()) {
            file_system_cb_selected_all!!.text = "取消"
        } else {
            file_system_cb_selected_all!!.text = "全选"
        }

    }

    private fun setMenuClickable(isClickable: Boolean) {

        //设置是否可删除
        file_system_btn_delete!!.isEnabled = isClickable
        file_system_btn_delete!!.isClickable = isClickable

        //设置是否可添加书籍
        file_system_btn_add_book!!.isEnabled = isClickable
        file_system_btn_add_book!!.isClickable = isClickable
    }

    /**
     * 改变全选按钮的状态
     */
    private fun changeCheckedAllStatus() {
        //获取可选择的文件数量
        val count = mCurFragment!!.checkableCount

        //设置是否能够全选
        if (count > 0) {
            file_system_cb_selected_all!!.isClickable = true
            file_system_cb_selected_all!!.isEnabled = true
        } else {
            file_system_cb_selected_all!!.isClickable = false
            file_system_cb_selected_all!!.isEnabled = false
        }
    }
}
