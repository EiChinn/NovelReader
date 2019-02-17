package com.example.newbiechen.ireader.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.RxBus
import com.example.newbiechen.ireader.event.*
import com.example.newbiechen.ireader.model.bean.CollBookBean
import com.example.newbiechen.ireader.model.local.BookRepository
import com.example.newbiechen.ireader.model.local.Void
import com.example.newbiechen.ireader.presenter.BookShelfPresenter
import com.example.newbiechen.ireader.presenter.contract.BookShelfContract
import com.example.newbiechen.ireader.ui.activity.ReadActivity
import com.example.newbiechen.ireader.ui.adapter.CollBookAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPFragment
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.utils.RxUtils
import com.example.newbiechen.ireader.utils.ToastUtils
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_bookshelf.*
import java.io.File

/**
 * Created by newbiechen on 17-4-15.
 */

class BookShelfFragment : BaseMVPFragment<BookShelfContract.View, BookShelfContract.Presenter>(), BookShelfContract.View {

    /** */
    private var mCollBookAdapter: CollBookAdapter? = null
    private var mFooterItem: FooterItemView? = null

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
        setUpAdapter()
    }

    private fun setUpAdapter() {
        //添加Footer
        mCollBookAdapter = CollBookAdapter()
        book_shelf_rv_content.setLayoutManager(LinearLayoutManager(context))
        book_shelf_rv_content.addItemDecoration(DividerItemDecoration(context!!))
        book_shelf_rv_content.setAdapter(mCollBookAdapter)
    }

    override fun initClick() {
        super.initClick()
        //同步书籍
        val AsyncBookDisp = RxBus.getInstance()
                .toObservable(SyncBookEvent::class.java)
                .subscribe(
                        { event -> mPresenter.refreshCollBooks() },
                        { error -> showErrorTip(error.toString()) }
                )
        addDisposable(AsyncBookDisp)

        //推荐书籍
        val recommendDisp = RxBus.getInstance()
                .toObservable(RecommendBookEvent::class.java)
                .subscribe { (sex) ->
                    book_shelf_rv_content.startRefresh()
                    mPresenter.loadRecommendBooks(sex)
                }
        addDisposable(recommendDisp)

        val donwloadDisp = RxBus.getInstance()
                .toObservable(DownloadMessage::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (message) ->
                    //使用Toast提示
                    ToastUtils.show(message)
                }
        addDisposable(donwloadDisp)

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
                                .compose<com.example.newbiechen.ireader.model.local.Void>(SingleTransformer<com.example.newbiechen.ireader.model.local.Void, Void> { RxUtils.toSimpleSingle(it) })
                                .subscribe { Void ->
                                    mCollBookAdapter!!.removeItem(collBook)
                                    progressDialog.dismiss()
                                }
                    } else {
                        //弹出一个Dialog
                        val tipDialog = AlertDialog.Builder(context!!)
                                .setTitle("您的任务正在加载")
                                .setMessage("先请暂停任务再进行删除")
                                .setPositiveButton("确定") { dialog, which -> dialog.dismiss() }.create()
                        tipDialog.show()
                    }
                }
        addDisposable(deleteDisp)

        book_shelf_rv_content.setOnRefreshListener { mPresenter.updateCollBooks(mCollBookAdapter!!.getItems()) }

        mCollBookAdapter!!.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                //如果是本地文件，首先判断这个文件是否存在
                val collBook = mCollBookAdapter!!.getItem(pos)
                if (collBook.isLocal()) {
                    //id表示本地文件的路径
                    val path = collBook.cover
                    val file = File(path)
                    //判断这个本地文件是否存在
                    if (file.exists() && file.length() != 0L) {
                        ReadActivity.startActivity(context!!,
                                mCollBookAdapter!!.getItem(pos), true)
                    } else {
                        val tip = context!!.getString(R.string.nb_bookshelf_book_not_exist)
                        //提示(从目录中移除这个文件)
                        AlertDialog.Builder(context!!)
                                .setTitle(resources.getString(R.string.nb_common_tip))
                                .setMessage(tip)
                                .setPositiveButton(resources.getString(R.string.nb_common_sure)
                                ) { dialog, which -> deleteBook(collBook) }
                                .setNegativeButton(resources.getString(R.string.nb_common_cancel), null)
                                .show()
                    }
                } else {
                    ReadActivity.startActivity(context!!,
                            mCollBookAdapter!!.getItem(pos), true)
                }
            }

        })

        mCollBookAdapter!!.setOnItemLongClickListener(object : BaseListAdapter.OnItemLongClickListener {
            override fun onItemLongClick(view: View, pos: Int): Boolean {
                //开启Dialog,最方便的Dialog,就是AlterDialog
                openItemDialog(mCollBookAdapter!!.getItem(pos))
                return true
            }

        })
    }

    override fun processLogic() {
        super.processLogic()

    }

    private fun openItemDialog(collBook: CollBookBean) {
        val menus: Array<String>
        if (collBook.isLocal()) {
            menus = resources.getStringArray(R.array.nb_menu_local_book)
        } else {
            menus = resources.getStringArray(R.array.nb_menu_net_book)
        }
        val collBookDialog = AlertDialog.Builder(context!!)
                .setTitle(collBook.title)
                .setAdapter(ArrayAdapter(context!!,
                        android.R.layout.simple_list_item_1, menus)
                ) { dialog, which -> onItemMenuClick(menus[which], collBook) }
                .setNegativeButton(null, null)
                .setPositiveButton(null, null)
                .create()

        collBookDialog.show()
    }

    private fun onItemMenuClick(which: String, collBook: CollBookBean) {
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

    private fun downloadBook(collBook: CollBookBean) {
        //创建任务
        mPresenter.createDownloadTask(collBook)
    }

    /**
     * 默认删除本地文件
     *
     * @param collBook
     */
    private fun deleteBook(collBook: CollBookBean) {
        if (collBook.isLocal()) {
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
                            BookRepository.instance.deleteBookRecord(collBook._id)

                            //从Adapter中删除
                            mCollBookAdapter!!.removeItem(collBook)
                            progressDialog.dismiss()
                        } else {
                            BookRepository.instance.deleteCollBook(collBook)
                            BookRepository.instance.deleteBookRecord(collBook._id)
                            //从Adapter中删除
                            mCollBookAdapter!!.removeItem(collBook)
                        }
                    }
                    .setNegativeButton(resources.getString(R.string.nb_common_cancel), null)
                    .show()
        } else {
            RxBus.getInstance().post(DeleteTaskEvent(collBook))
        }
    }

    /*******************************************************************8 */

    override fun showError() {

        if (book_shelf_rv_content.isRefreshing) {
            book_shelf_rv_content.finishRefresh()
        }

    }

    override fun complete() {
        if (mCollBookAdapter!!.itemCount > 0 && mFooterItem == null) {
            mFooterItem = FooterItemView()
            mCollBookAdapter!!.addFooterView(mFooterItem!!)
        }

        if (book_shelf_rv_content.isRefreshing) {
            book_shelf_rv_content.finishRefresh()
        }
    }

    override fun finishRefresh(collBookBeans: List<CollBookBean>) {
        if (!collBookBeans.isEmpty()) {
            mCollBookAdapter!!.refreshItems(collBookBeans)
            //如果是初次进入，则更新书籍信息
            if (isInit) {
                isInit = false
                book_shelf_rv_content.startRefresh()
                book_shelf_rv_content.post { mPresenter.updateCollBooks(mCollBookAdapter!!.getItems()) }
            }
        }

    }

    override fun finishUpdate() {
        //重新从数据库中获取数据
        mCollBookAdapter!!.refreshItems(BookRepository
                .instance.collBooks)
    }

    override fun showErrorTip(error: String) {
        book_shelf_rv_content.setTip(error)
        book_shelf_rv_content.showTip()
    }

    /** */
    internal inner class FooterItemView : WholeAdapter.ItemView {
        override fun onCreateView(parent: ViewGroup): View {
            val view = LayoutInflater.from(context)
                    .inflate(R.layout.footer_book_shelf, parent, false)
            view.setOnClickListener { v ->
                //设置RxBus回调
            }
            return view
        }

        override fun onBindView(view: View) {}
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
        book_shelf_rv_content.setOnRefreshListener(null)
        book_shelf_rv_content.setAdapter(null)
        super.onDestroy()
    }
}
