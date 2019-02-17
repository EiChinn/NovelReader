package com.example.newbiechen.ireader.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
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
import com.example.newbiechen.ireader.widget.RefreshLayout
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import com.example.newbiechen.ireader.widget.transform.CircleTransform
import java.util.*

/**
 * Created by newbiechen on 17-5-1.
 */

class BookListDetailActivity : BaseMVPActivity<BookListDetailContract.View, BookListDetailContract.Presenter>(), BookListDetailContract.View {
    @BindView(R.id.refresh_layout)
    @JvmField internal var mRefreshLayout: RefreshLayout? = null
    @BindView(R.id.refresh_rv_content)
    @JvmField internal var mRvContent: RecyclerView? = null
    /** */
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
        if (savedInstanceState != null) {
            mDetailId = savedInstanceState.getString(EXTRA_DETAIL_ID)
        } else {
            mDetailId = intent.getStringExtra(EXTRA_DETAIL_ID)
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

        mRvContent!!.layoutManager = LinearLayoutManager(this)
        mRvContent!!.addItemDecoration(DividerItemDecoration(this))
        mRvContent!!.adapter = mDetailAdapter
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
        mRefreshLayout!!.showLoading()
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
        mRefreshLayout!!.showError()
    }

    override fun complete() {
        mRefreshLayout!!.showFinish()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_DETAIL_ID, mDetailId)
    }


    internal inner class DetailHeader : WholeAdapter.ItemView {
        @BindView(R.id.book_list_info_tv_title)
        @JvmField var tvTitle: TextView? = null
        @BindView(R.id.book_list_detail_tv_desc)
        @JvmField var tvDesc: TextView? = null
        @BindView(R.id.book_list_info_iv_cover)
        @JvmField var ivPortrait: ImageView? = null
        @BindView(R.id.book_list_detail_tv_create)
        @JvmField var tvCreate: TextView? = null
        @BindView(R.id.book_list_info_tv_author)
        @JvmField var tvAuthor: TextView? = null
        @BindView(R.id.book_list_detail_tv_share)
        @JvmField var tvShare: TextView? = null

        var detailBean: BookListDetailBean? = null

        var detailUnbinder: Unbinder? = null
        override fun onCreateView(parent: ViewGroup): View {
            return LayoutInflater.from(parent.context)
                    .inflate(R.layout.header_book_list_detail, parent, false)
        }

        override fun onBindView(view: View) {
            if (detailUnbinder == null) {
                detailUnbinder = ButterKnife.bind(this, view)
            }
            //如果没有值就直接返回
            if (detailBean == null) {
                return
            }
            //标题
            tvTitle!!.text = detailBean!!.title
            //描述
            tvDesc!!.text = detailBean!!.desc
            //头像
            Glide.with(App.getInstance())
                    .load(Constant.IMG_BASE_URL + detailBean!!.author.avatar)
                    .placeholder(R.drawable.ic_loadding)
                    .error(R.drawable.ic_load_error)
                    .transform(CircleTransform(App.getInstance()))
                    .into(ivPortrait!!)
            //作者
            tvAuthor!!.text = detailBean!!.author.nickname
        }

        fun setBookListDetail(bean: BookListDetailBean) {
            detailBean = bean
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (mDetailHeader!!.detailUnbinder != null) {
            mDetailHeader!!.detailUnbinder!!.unbind()
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
