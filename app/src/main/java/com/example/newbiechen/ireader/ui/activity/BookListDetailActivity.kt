package com.example.newbiechen.ireader.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.newbiechen.ireader.App
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.BookListDetailBean
import com.example.newbiechen.ireader.model.bean.DetailBookBean
import com.example.newbiechen.ireader.model.bean.DetailBooksBean
import com.example.newbiechen.ireader.presenter.BookListDetailPresenter
import com.example.newbiechen.ireader.presenter.contract.BookListDetailContract
import com.example.newbiechen.ireader.ui.adapter.BookListDetailAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPActivity
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import com.example.newbiechen.ireader.widget.transform.CircleTransform
import kotlinx.android.synthetic.main.activity_refresh_list.*
import kotlinx.android.synthetic.main.header_book_list_detail.*
import java.util.*

/**
 * Created by newbiechen on 17-5-1.
 */

class BookListDetailActivity : BaseMVPActivity<BookListDetailContract.View, BookListDetailContract.Presenter>(), BookListDetailContract.View {
    private var mDetailAdapter: BookListDetailAdapter? = null
    private var mDetailHeader: DetailHeader? = null
    private var mBooksList: List<DetailBooksBean>? = null
    /***********params */
    private var mDetailId: String? = null
    private var start = 0
    private val limit = 20

    private val bookList: List<DetailBookBean>
        get() {
            var end = start + limit
            if (end > mBooksList!!.size) {
                end = mBooksList!!.size
            }
            val books = ArrayList<DetailBookBean>(limit)
            for (i in start until end) {
                books.add(mBooksList!![i].book)
            }
            return books
        }

    override fun getContentId(): Int {
        return R.layout.activity_refresh_list
    }

    override fun bindPresenter(): BookListDetailContract.Presenter {
        return BookListDetailPresenter()
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mDetailId = if (savedInstanceState != null) {
            savedInstanceState.getString(EXTRA_DETAIL_ID)
        } else {
            intent.getStringExtra(EXTRA_DETAIL_ID)
        }
    }

    override fun setUpToolbar(toolbar: Toolbar) {
        super.setUpToolbar(toolbar)
        supportActionBar!!.title = "书单详情"
    }

    override fun initWidget() {
        super.initWidget()
        setUpAdapter()
    }

    private fun setUpAdapter() {
        mDetailAdapter = BookListDetailAdapter(this, WholeAdapter.Options())
        mDetailHeader = DetailHeader()
        mDetailAdapter!!.addHeaderView(mDetailHeader!!)

        refresh_rv_content!!.layoutManager = LinearLayoutManager(this)
        refresh_rv_content!!.addItemDecoration(DividerItemDecoration(this))
        refresh_rv_content!!.adapter = mDetailAdapter
    }

    override fun initClick() {
        super.initClick()
        mDetailAdapter!!.setOnLoadMoreListener { loadBook() }
        mDetailAdapter!!.setOnItemClickListener { v, p -> onItemClick(v, p) }
    }

    protected fun onItemClick(v: View, p: Int) {
        val book = mBooksList!![p].book
        BookDetailActivity.startActivity(this, book._id)
    }

    override fun processLogic() {
        super.processLogic()
        refresh_layout!!.showLoading()
        mPresenter.refreshBookListDetail(mDetailId!!)
    }

    override fun finishRefresh(bean: BookListDetailBean) {
        mDetailHeader!!.setBookListDetail(bean)
        mBooksList = bean.books
        refreshBook()
    }

    private fun refreshBook() {
        start = 0
        val books = bookList
        mDetailAdapter!!.refreshItems(books)
        start = books.size
    }

    private fun loadBook() {
        val books = bookList
        mDetailAdapter!!.addItems(books)
        start += books.size
    }

    override fun showError() {
        refresh_layout!!.showError()
    }

    override fun complete() {
        refresh_layout!!.showFinish()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_DETAIL_ID, mDetailId)
    }


    internal inner class DetailHeader : WholeAdapter.ItemView {
        var detailBean: BookListDetailBean? = null

        override fun onCreateView(parent: ViewGroup): View {
            return LayoutInflater.from(parent.context)
                    .inflate(R.layout.header_book_list_detail, parent, false)
        }

        override fun onBindView(view: View) {
            //如果没有值就直接返回
            if (detailBean == null) {
                return
            }
            //标题
            book_list_info_tv_title!!.text = detailBean!!.title
            //描述
            book_list_detail_tv_desc!!.text = detailBean!!.desc
            //头像
            Glide.with(App.getInstance())
                    .load(Constant.IMG_BASE_URL + detailBean!!.author.avatar)
                    .placeholder(R.drawable.ic_loadding)
                    .error(R.drawable.ic_load_error)
                    .transform(CircleTransform(App.getInstance()))
                    .into(book_list_info_iv_cover!!)
            //作者
            book_list_info_tv_author!!.text = detailBean!!.author.nickname
        }

        fun setBookListDetail(bean: BookListDetailBean) {
            detailBean = bean
        }
    }

    companion object {

        private const val EXTRA_DETAIL_ID = "extra_detail_id"

        fun startActivity(context: Context, detailId: String) {
            val intent = Intent(context, BookListDetailActivity::class.java)
            intent.putExtra(EXTRA_DETAIL_ID, detailId)
            context.startActivity(intent)
        }
    }
}
