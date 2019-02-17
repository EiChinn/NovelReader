package com.example.newbiechen.ireader.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.RxBus
import com.example.newbiechen.ireader.event.BookSubSortEvent
import com.example.newbiechen.ireader.model.bean.SortBookBean
import com.example.newbiechen.ireader.model.flag.BookSortListType
import com.example.newbiechen.ireader.presenter.BookSortListPresenter
import com.example.newbiechen.ireader.presenter.contract.BookSortListContract
import com.example.newbiechen.ireader.ui.activity.BookDetailActivity
import com.example.newbiechen.ireader.ui.adapter.BookSortListAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPFragment
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.widget.adapter.LoadMoreView
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_refresh_list.*

/**
 * Created by newbiechen on 17-5-3.
 */

class BookSortListFragment : BaseMVPFragment<BookSortListContract.View, BookSortListContract.Presenter>(), BookSortListContract.View {

    /** */
    internal lateinit var mBookSortListAdapter: BookSortListAdapter
    /** */
    private var mGender: String? = null
    private var mMajor: String? = null
    private var mType: BookSortListType? = null
    private var mMinor = ""
    private var mStart = 0
    private val mLimit = 20

    override fun getContentId(): Int {
        return R.layout.fragment_refresh_list
    }

    override fun bindPresenter(): BookSortListContract.Presenter {
        return BookSortListPresenter()
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        if (savedInstanceState != null) {
            mGender = savedInstanceState.getString(EXTRA_GENDER)
            mMajor = savedInstanceState.getString(EXTRA_MAJOR)
            mType = savedInstanceState.getSerializable(EXTRA_TYPE) as BookSortListType
        } else {
            mGender = arguments!!.getString(EXTRA_GENDER)
            mMajor = arguments!!.getString(EXTRA_MAJOR)
            mType = arguments!!.getSerializable(EXTRA_TYPE) as BookSortListType
        }
    }

    override fun initClick() {
        super.initClick()
        mBookSortListAdapter.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                val bookId = mBookSortListAdapter.getItem(pos)._id
                BookDetailActivity.startActivity(context, bookId)
            }

        })

        mBookSortListAdapter.setOnLoadMoreListener(object : LoadMoreView.OnLoadMoreListener {
            override fun onLoadMore() {
                mPresenter.loadSortBook(mGender!!, mType!!, mMajor!!, mMinor, mStart, mLimit)
            }
        })

        //子类的切换
        val disposable = RxBus.getInstance()
                .toObservable(BookSubSortEvent::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (bookSubSort) ->
                    mMinor = bookSubSort
                    refresh_layout.showLoading()
                    mStart = 0
                    mPresenter.refreshSortBook(mGender!!, mType!!, mMajor!!, mMinor, mStart, mLimit)
                }
        addDisposable(disposable)
    }

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        setUpAdapter()
    }

    private fun setUpAdapter() {
        mBookSortListAdapter = BookSortListAdapter(context!!, WholeAdapter.Options())

        refresh_rv_content.layoutManager = LinearLayoutManager(context)
        refresh_rv_content.addItemDecoration(DividerItemDecoration(context!!))
        refresh_rv_content.adapter = mBookSortListAdapter
    }

    override fun processLogic() {
        super.processLogic()
        refresh_layout.showLoading()
        mPresenter.refreshSortBook(mGender!!, mType!!, mMajor!!, mMinor, mStart, mLimit)
    }

    override fun finishRefresh(beans: List<SortBookBean>) {
        if (beans.isEmpty()) {
            refresh_layout.showEmpty()
            return
        }
        mBookSortListAdapter.refreshItems(beans)
        mStart = beans.size
    }

    override fun finishLoad(beans: List<SortBookBean>) {
        mBookSortListAdapter.addItems(beans)
        mStart += beans.size
    }

    override fun showError() {
        refresh_layout.showError()
    }

    override fun showLoadError() {
        mBookSortListAdapter.showLoadError()
    }

    override fun complete() {
        refresh_layout.showFinish()
    }

    /** */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_GENDER, mGender)
        outState.putString(EXTRA_MAJOR, mMajor)
        outState.putSerializable(EXTRA_TYPE, mType)
    }

    companion object {
        private const val EXTRA_GENDER = "extra_gender"
        private const val EXTRA_TYPE = "extra_type"
        private const val EXTRA_MAJOR = "extra_major"

        fun newInstance(gender: String, major: String, type: BookSortListType): Fragment {
            val bundle = Bundle()
            bundle.putString(EXTRA_GENDER, gender)
            bundle.putString(EXTRA_MAJOR, major)
            bundle.putSerializable(EXTRA_TYPE, type)
            val fragment = BookSortListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
