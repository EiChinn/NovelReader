package com.example.newbiechen.ireader.ui.activity

import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
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
import java.io.File
import java.util.*

/**
 * Created by newbiechen on 17-5-27.
 */

class FileSystemActivity : BaseTabActivity() {

    @BindView(R.id.file_system_cb_selected_all)
    @JvmField internal var mCbSelectAll: CheckBox? = null
    @BindView(R.id.file_system_btn_delete)
    @JvmField internal var mBtnDelete: Button? = null
    @BindView(R.id.file_system_btn_add_book)
    @JvmField internal var mBtnAddBook: Button? = null

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
        mCbSelectAll!!.setOnClickListener { view ->
            //设置全选状态
            val isChecked = mCbSelectAll!!.isChecked
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

        mBtnAddBook!!.setOnClickListener { v ->
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

        mBtnDelete!!.setOnClickListener { v ->
            //弹出，确定删除文件吗。
            AlertDialog.Builder(this)
                    .setTitle("删除文件")
                    .setMessage("确定删除文件吗?")
                    .setPositiveButton(resources.getString(R.string.nb_common_sure)) { dialog, which ->
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
            mBtnAddBook!!.text = getString(R.string.nb_file_add_shelf)
            //设置某些按钮的是否可点击
            setMenuClickable(false)

            if (mCbSelectAll!!.isChecked) {
                mCurFragment!!.setChecked(false)
                mCbSelectAll!!.isChecked = mCurFragment!!.isCheckedAll()
            }

        } else {
            mBtnAddBook!!.text = getString(R.string.nb_file_add_shelves, mCurFragment!!.checkedCount)
            setMenuClickable(true)

            //全选状态的设置

            //如果选中的全部的数据，则判断为全选
            if (mCurFragment!!.checkedCount == mCurFragment!!.checkableCount) {
                //设置为全选
                mCurFragment!!.setChecked(true)
                mCbSelectAll!!.isChecked = mCurFragment!!.isCheckedAll()
            } else if (mCurFragment!!.isCheckedAll()) {
                mCurFragment!!.setChecked(false)
                mCbSelectAll!!.isChecked = mCurFragment!!.isCheckedAll()
            }//如果曾今是全选则替换
        }

        //重置全选的文字
        if (mCurFragment!!.isCheckedAll()) {
            mCbSelectAll!!.text = "取消"
        } else {
            mCbSelectAll!!.text = "全选"
        }

    }

    private fun setMenuClickable(isClickable: Boolean) {

        //设置是否可删除
        mBtnDelete!!.isEnabled = isClickable
        mBtnDelete!!.isClickable = isClickable

        //设置是否可添加书籍
        mBtnAddBook!!.isEnabled = isClickable
        mBtnAddBook!!.isClickable = isClickable
    }

    /**
     * 改变全选按钮的状态
     */
    private fun changeCheckedAllStatus() {
        //获取可选择的文件数量
        val count = mCurFragment!!.checkableCount

        //设置是否能够全选
        if (count > 0) {
            mCbSelectAll!!.isClickable = true
            mCbSelectAll!!.isEnabled = true
        } else {
            mCbSelectAll!!.isClickable = false
            mCbSelectAll!!.isEnabled = false
        }
    }
}
