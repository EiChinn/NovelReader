package com.example.newbiechen.ireader.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.RxBus
import com.example.newbiechen.ireader.db.entity.CollBook
import com.example.newbiechen.ireader.event.DeleteResponseEvent
import com.example.newbiechen.ireader.event.DownloadMessage
import com.example.newbiechen.ireader.event.SyncBookEvent
import com.example.newbiechen.ireader.model.local.BookRepository
import com.example.newbiechen.ireader.presenter.BookShelfPresenter
import com.example.newbiechen.ireader.presenter.contract.BookShelfContract
import com.example.newbiechen.ireader.ui.activity.ReadActivity
import com.example.newbiechen.ireader.ui.adapter.CollBookAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPFragment
import com.example.newbiechen.ireader.utils.RxUtils
import com.example.newbiechen.ireader.utils.ToastUtils
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_bookshelf.*
import org.jetbrains.anko.support.v4.toast
import java.io.File

/**
 * Created by newbiechen on 17-4-15.
 */

class BookShelfFragment : BaseMVPFragment<BookShelfContract.View, BookShelfContract.Presenter>(), BookShelfContract.View{

    /** */
    private var mCollBookAdapter: CollBookAdapter? = null

    //是否是第一次进入
    private var isInit = true

    override fun getContentId(): Int {
        return R.layout.fragment_bookshelf
    }

    override fun bindPresenter(): BookShelfContract.Presenter {
        return BookShelfPresenter()
    }

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener { mPresenter.updateCollBooks(mCollBookAdapter!!.data) }
        setUpAdapter()
    }

    private fun setUpAdapter() {
        //添加Footer
        mCollBookAdapter = CollBookAdapter(context!!)
        mCollBookAdapter!!.emptyView = layoutInflater.inflate(R.layout.view_empty_book_shelf, null)
        rv_coll_books.layoutManager = LinearLayoutManager(context)
        rv_coll_books.addItemDecoration(DividerItemDecoration(context!!))
        rv_coll_books.adapter = mCollBookAdapter
    }

    override fun initClick() {
        super.initClick()
        //同步书籍
        val syncBookDisp = RxBus.getInstance()
                .toObservable(SyncBookEvent::class.java)
                .subscribe(
                        { mPresenter.refreshCollBooks() },
                        { error -> showErrorTip(error.toString()) }
                )
        addDisposable(syncBookDisp)

        val downloadDisp = RxBus.getInstance()
                .toObservable(DownloadMessage::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (message) ->
                    //使用Toast提示
                    ToastUtils.show(message)
                }
        addDisposable(downloadDisp)

        //删除书籍 (写的丑了点)
        val deleteDisp = RxBus.getInstance()
                .toObservable(DeleteResponseEvent::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (isDelete, collBook) ->
                    if (isDelete) {
                        val progressDialog = ProgressDialog(context)
                        progressDialog.setMessage("正在删除中")
                        progressDialog.show()
                        BookRepository.instance.deleteCollBookInRx(collBook)
                                .compose<com.example.newbiechen.ireader.model.local.Void> { RxUtils.toSimpleSingle(it) }
                                .subscribe { _ ->
                                    mPresenter.refreshCollBooks()
                                    progressDialog.dismiss()
                                }
                    } else {
                        //弹出一个Dialog
                        val tipDialog = AlertDialog.Builder(context!!)
                                .setTitle("您的任务正在加载")
                                .setMessage("先请暂停任务再进行删除")
                                .setPositiveButton("确定") { dialog, _ -> dialog.dismiss() }.create()
                        tipDialog.show()
                    }
                }
        addDisposable(deleteDisp)

        mCollBookAdapter?.setOnItemClickListener { adapter, view, position ->
            //如果是本地文件，首先判断这个文件是否存在
            val collBook = mCollBookAdapter!!.getItem(position)!!
            if (collBook.isLocal) {
                //id表示本地文件的路径
                val path = collBook.cover
                val file = File(path)
                //判断这个本地文件是否存在
                if (file.exists() && file.length() != 0L) {
                    ReadActivity.startActivity(context!!, collBook, true)
                } else {
                    val tip = context!!.getString(R.string.nb_bookshelf_book_not_exist)
                    //提示(从目录中移除这个文件)
                    AlertDialog.Builder(context!!)
                            .setTitle(resources.getString(R.string.nb_common_tip))
                            .setMessage(tip)
                            .setPositiveButton(resources.getString(R.string.nb_common_sure)
                            ) { _, _ -> deleteBook(collBook) }
                            .setNegativeButton(resources.getString(R.string.nb_common_cancel), null)
                            .show()
                }
            } else {
                ReadActivity.startActivity(context!!, collBook, true)
            }
        }

        mCollBookAdapter?.setOnItemLongClickListener { _, _, position ->
            //开启Dialog,最方便的Dialog,就是AlterDialog
            openItemDialog(mCollBookAdapter!!.getItem(position)!!)
            true
        }
    }

    private fun openItemDialog(collBook: CollBook) {
        val menus: Array<String> = resources.getStringArray(R.array.nb_menu_net_book)
        val collBookDialog = AlertDialog.Builder(context!!)
                .setTitle(collBook.title)
                .setAdapter(ArrayAdapter(context!!,
                        android.R.layout.simple_list_item_1, menus)
                ) { _, which -> onItemMenuClick(menus[which], collBook) }
                .setNegativeButton(null, null)
                .setPositiveButton(null, null)
                .create()

        collBookDialog.show()
    }

    private fun onItemMenuClick(which: String, collBook: CollBook) {
        when (which) {
            //置顶
            "置顶" -> {
            }
            //缓存
            "缓存" ->
                //2. 进行判断，如果CollBean中状态为未更新。那么就创建Task，加入到Service中去。
                //3. 如果状态为finish，并且isUpdate为true，那么就根据chapter创建状态
                //4. 如果状态为finish，并且isUpdate为false。
                downloadBook(collBook)
            //删除
            "删除" -> deleteBook(collBook)
            //批量管理
            "批量管理" -> {
            }
            else -> {
            }
        }
    }

    private fun downloadBook(collBook: CollBook) {
        //创建任务
        mPresenter.createDownloadTask(collBook)
    }

    /**
     * 默认删除本地文件
     *
     * @param collBook
     */
    private fun deleteBook(collBook: CollBook) {
        if (collBook.isLocal) {
            val view = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_delete, null)
            val cb = view.findViewById<View>(R.id.delete_cb_select) as CheckBox
            AlertDialog.Builder(context!!)
                    .setTitle("删除文件")
                    .setView(view)
                    .setPositiveButton(resources.getString(R.string.nb_common_sure)) { dialog, which ->
                        val isSelected = cb.isSelected
                        if (isSelected) {
                            val progressDialog = ProgressDialog(context)
                            progressDialog.setMessage("正在删除中")
                            progressDialog.show()
                            //删除
                            val file = File(collBook.cover)
                            if (file.exists()) file.delete()
                            BookRepository.instance.deleteCollBook(collBook)

                            //从Adapter中删除
                            mPresenter.refreshCollBooks()
                            progressDialog.dismiss()
                        } else {
                            BookRepository.instance.deleteCollBook(collBook)
                            //从Adapter中删除
                            mPresenter.refreshCollBooks()
                        }
                    }
                    .setNegativeButton(resources.getString(R.string.nb_common_cancel), null)
                    .show()
        } else {
            RxBus.getInstance().post(DeleteResponseEvent(true, collBook))
        }
    }

    /*******************************************************************8 */

    override fun showError() {
        finishRefresh()
    }

    private fun finishRefresh() {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun complete() {
        finishRefresh()
    }

    override fun finishRefresh(collBooks: List<CollBook>) {
        if (!collBooks.isEmpty()) {
            mCollBookAdapter!!.setNewData(collBooks)

            //如果是初次进入，则更新书籍信息
            if (isInit) {
                isInit = false
                swipeRefreshLayout.postDelayed({
                    swipeRefreshLayout.isRefreshing = true
                    mPresenter.updateCollBooks(mCollBookAdapter!!.data)
                }, 100)
            }
        }

    }

    override fun finishUpdate() {
        //重新从数据库中获取数据
        mCollBookAdapter!!.setNewData(BookRepository
                .instance.getAllCollBooks())
    }

    override fun showErrorTip(error: String) {
       toast(error)
    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint) {//当前fragement显示且在登录状态下才显示引导页
            refreshCollBooks()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isResumed) {//当前fragement显示且在登录状态下才显示引导页
            refreshCollBooks()
        }
    }

    private fun refreshCollBooks() {
        mPresenter.refreshCollBooks()
    }

    override fun onDestroy() {
        rv_coll_books?.adapter = null
        super.onDestroy()
    }
}
