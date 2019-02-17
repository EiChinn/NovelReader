package com.example.newbiechen.ireader.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.RxBus
import com.example.newbiechen.ireader.event.SelectorEvent
import com.example.newbiechen.ireader.model.bean.BookReviewBean
import com.example.newbiechen.ireader.model.flag.BookDistillate
import com.example.newbiechen.ireader.model.flag.BookSort
import com.example.newbiechen.ireader.model.flag.BookType
import com.example.newbiechen.ireader.model.flag.CommunityType
import com.example.newbiechen.ireader.presenter.DiscReviewPresenter
import com.example.newbiechen.ireader.presenter.contract.DiscReviewContract
import com.example.newbiechen.ireader.ui.activity.DiscDetailActivity
import com.example.newbiechen.ireader.ui.adapter.DiscReviewAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPFragment
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.widget.adapter.LoadMoreView
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_scroll_refresh_list.*

/**
 * Created by newbiechen on 17-4-21.
 */

class DiscReviewFragment : BaseMVPFragment<DiscReviewContract.View, DiscReviewContract.Presenter>(), DiscReviewContract.View {
    /*******************Object */
    private var mDiscReviewAdapter: DiscReviewAdapter? = null
    /*******************Params */
    private var mBookSort = BookSort.DEFAULT
    private var mBookType = BookType.ALL
    private var mDistillate = BookDistillate.ALL
    private var mStart = 0
    private val mLimited = 20

    /**********************init method */
    override fun getContentId(): Int {
        return R.layout.fragment_scroll_refresh_list
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        if (savedInstanceState != null) {
            mBookType = savedInstanceState.getSerializable(BUNDLE_BOOK) as BookType
            mBookSort = savedInstanceState.getSerializable(BUNDLE_SORT) as BookSort
            mDistillate = savedInstanceState.getSerializable(BUNDLE_DISTILLATE) as BookDistillate
        }
    }

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        setUpAdapter()
    }

    private fun setUpAdapter() {
        scroll_refresh_rv_content.setLayoutManager(LinearLayoutManager(context))
        scroll_refresh_rv_content.addItemDecoration(DividerItemDecoration(context!!))
        mDiscReviewAdapter = DiscReviewAdapter(context!!, WholeAdapter.Options())
        scroll_refresh_rv_content.setAdapter(mDiscReviewAdapter)
    }

    override fun bindPresenter(): DiscReviewContract.Presenter {
        return DiscReviewPresenter()
    }

    /*************************click method */

    override fun initClick() {
        super.initClick()

        scroll_refresh_rv_content.setOnRefreshListener { refreshData() }
        mDiscReviewAdapter!!.setOnLoadMoreListener(object : LoadMoreView.OnLoadMoreListener {
            override fun onLoadMore() {
                mPresenter.loadingBookReview(mBookSort, mBookType, mStart, mLimited, mDistillate)
            }
        }
        )
        mDiscReviewAdapter!!.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                val bean = mDiscReviewAdapter!!.getItem(pos)
                val detailId = bean._id
                DiscDetailActivity.startActivity(context!!, CommunityType.REVIEW, detailId)
            }

        })

        addDisposable(RxBus.getInstance()
                .toObservable(Constant.MSG_SELECTOR, SelectorEvent::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (distillate, type, sort) ->
                    mBookSort = sort
                    mBookType = type
                    mDistillate = distillate
                    refreshData()
                })
    }

    /****************************logic method */
    override fun processLogic() {
        super.processLogic()
        //首次自动刷新
        scroll_refresh_rv_content.startRefresh()
        mPresenter.firstLoading(mBookSort, mBookType, mStart, mLimited, mDistillate)
    }

    private fun refreshData() {
        mStart = 0
        scroll_refresh_rv_content.startRefresh()
        mPresenter.refreshBookReview(mBookSort, mBookType, mStart, mLimited, mDistillate)
    }

    /****************************rewrite method */
    override fun finishRefresh(beans: List<BookReviewBean>) {
        mDiscReviewAdapter!!.refreshItems(beans)
        mStart = beans.size
    }

    override fun finishLoading(beans: List<BookReviewBean>) {
        mDiscReviewAdapter!!.addItems(beans)
        mStart += beans.size
    }

    override fun showErrorTip() {
        scroll_refresh_rv_content.showTip()
    }


    override fun showError() {
        mDiscReviewAdapter!!.showLoadError()
    }

    override fun complete() {
        scroll_refresh_rv_content.finishRefresh()
    }

    /****************************lifecycle method */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(BUNDLE_BOOK, mBookType)
        outState.putSerializable(BUNDLE_SORT, mBookSort)
        outState.putSerializable(BUNDLE_DISTILLATE, mDistillate)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.saveBookReview(mDiscReviewAdapter!!.getItems())
    }

    companion object {
        private val BUNDLE_BOOK = "bundle_book"
        private val BUNDLE_SORT = "bundle_sort"
        private val BUNDLE_DISTILLATE = "bundle_distillate"
    }
}
