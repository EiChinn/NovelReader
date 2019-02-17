package com.example.newbiechen.ireader.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.RxBus
import com.example.newbiechen.ireader.event.BookSubSortEvent
import com.example.newbiechen.ireader.model.bean.BookListBean
import com.example.newbiechen.ireader.model.flag.BookListType
import com.example.newbiechen.ireader.presenter.BookListPresenter
import com.example.newbiechen.ireader.presenter.contract.BookListContract
import com.example.newbiechen.ireader.ui.activity.BookListDetailActivity
import com.example.newbiechen.ireader.ui.adapter.BookListAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPFragment
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.widget.adapter.LoadMoreView
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_refresh_list.*

/**
 * Created by newbiechen on 17-5-1.
 * 书单页面
 */

class BookListFragment : BaseMVPFragment<BookListContract.View, BookListContract.Presenter>(), BookListContract.View {
    /** */
    private var mBookListAdapter: BookListAdapter? = null
    /** */
    private var mBookListType: BookListType? = null
    private var mTag: String? = ""
    private var mStart = 0
    private val mLimit = 20

    override fun getContentId(): Int {
        return R.layout.fragment_refresh_list
    }

    override fun bindPresenter(): BookListContract.Presenter {
        return BookListPresenter()
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        if (savedInstanceState != null) {
            mBookListType = savedInstanceState.getSerializable(EXTRA_BOOK_LIST_TYPE) as BookListType
            mTag = savedInstanceState.getString(BUNDLE_BOOK_TAG)
        } else {
            mBookListType = arguments!!.getSerializable(EXTRA_BOOK_LIST_TYPE) as BookListType
        }
    }

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        setUpAdapter()
    }


    override fun initClick() {
        super.initClick()
        mBookListAdapter!!.setOnLoadMoreListener(object : LoadMoreView.OnLoadMoreListener {
            override fun onLoadMore() {
                mPresenter.loadBookList(mBookListType!!, mTag!!, mStart, mLimit)
            }
        })
        mBookListAdapter!!.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                val (_id) = mBookListAdapter!!.getItem(pos)
                BookListDetailActivity.startActivity(context, _id)
            }

        })

        val disposable = RxBus.getInstance()
                .toObservable(BookSubSortEvent::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (bookSubSort) ->
                    mTag = bookSubSort
                    showRefresh()
                }
        addDisposable(disposable)
    }

    override fun processLogic() {
        super.processLogic()
        showRefresh()
    }

    private fun showRefresh() {
        mStart = 0
        refresh_layout.showLoading()
        mPresenter.refreshBookList(mBookListType!!, mTag!!, mStart, mLimit)
    }

    private fun setUpAdapter() {
        refresh_rv_content.layoutManager = LinearLayoutManager(context)
        refresh_rv_content.addItemDecoration(DividerItemDecoration(context!!))
        mBookListAdapter = BookListAdapter(context!!, WholeAdapter.Options())
        refresh_rv_content.adapter = mBookListAdapter
    }

    override fun finishRefresh(beans: List<BookListBean>) {
        mBookListAdapter!!.refreshItems(beans)
        mStart = beans.size
    }

    override fun finishLoading(beans: List<BookListBean>) {
        mBookListAdapter!!.addItems(beans)
        mStart += beans.size
    }

    override fun showLoadError() {
        mBookListAdapter!!.showLoadError()
    }

    override fun showError() {
        refresh_layout.showError()
    }

    override fun complete() {
        refresh_layout.showFinish()
    }

    /** */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(EXTRA_BOOK_LIST_TYPE, mBookListType)
        outState.putSerializable(BUNDLE_BOOK_TAG, mTag)
    }

    companion object {
        private val EXTRA_BOOK_LIST_TYPE = "extra_book_list_type"
        private val BUNDLE_BOOK_TAG = "bundle_book_tag"

        fun newInstance(bookListType: BookListType): Fragment {
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_BOOK_LIST_TYPE, bookListType)
            val fragment = BookListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
