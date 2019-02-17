package com.example.newbiechen.ireader.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.RxBus
import com.example.newbiechen.ireader.event.SelectorEvent
import com.example.newbiechen.ireader.model.bean.BookHelpsBean
import com.example.newbiechen.ireader.model.flag.BookDistillate
import com.example.newbiechen.ireader.model.flag.BookSort
import com.example.newbiechen.ireader.model.flag.CommunityType
import com.example.newbiechen.ireader.presenter.DiscHelpsPresenter
import com.example.newbiechen.ireader.presenter.contract.DiscHelpsContract
import com.example.newbiechen.ireader.ui.activity.DiscDetailActivity
import com.example.newbiechen.ireader.ui.adapter.DiscHelpsAdapter
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

class DiscHelpsFragment : BaseMVPFragment<DiscHelpsContract.View, DiscHelpsContract.Presenter>(), DiscHelpsContract.View {
    /******************Object */
    private var mDiscHelpsAdapter: DiscHelpsAdapter? = null
    /******************Params */
    private var mBookSort = BookSort.DEFAULT
    private var mDistillate = BookDistillate.ALL
    private var mStart = 0
    private val mLimited = 20

    /************************init method */
    override fun getContentId(): Int {
        return R.layout.fragment_scroll_refresh_list
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        if (savedInstanceState != null) {
            mBookSort = savedInstanceState.getSerializable(BUNDLE_SORT) as BookSort
            mDistillate = savedInstanceState.getSerializable(BUNDLE_DISTILLATE) as BookDistillate
        }
    }

    override fun initWidget(savedInstanceState: Bundle?) {
        setUpAdapter()
    }

    private fun setUpAdapter() {
        scroll_refresh_rv_content.setLayoutManager(LinearLayoutManager(context))
        scroll_refresh_rv_content.addItemDecoration(DividerItemDecoration(context!!))
        mDiscHelpsAdapter = DiscHelpsAdapter(context!!, WholeAdapter.Options())
        scroll_refresh_rv_content.setAdapter(mDiscHelpsAdapter)
    }

    /******************************click method */
    override fun initClick() {
        scroll_refresh_rv_content.setOnRefreshListener { startRefresh() }
        mDiscHelpsAdapter!!.setOnLoadMoreListener(object : LoadMoreView.OnLoadMoreListener {
            override fun onLoadMore() {
                mPresenter.loadingBookHelps(mBookSort, mStart, mLimited, mDistillate)
            }
        })

        mDiscHelpsAdapter!!.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                val bean = mDiscHelpsAdapter!!.getItem(pos)
                DiscDetailActivity.startActivity(context, CommunityType.HELP, bean._id)
            }

        })

        val eventDispo = RxBus.getInstance()
                .toObservable(Constant.MSG_SELECTOR, SelectorEvent::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (distillate, _, sort) ->
                    mBookSort = sort
                    mDistillate = distillate
                    startRefresh()
                }
        addDisposable(eventDispo)
    }

    override fun bindPresenter(): DiscHelpsContract.Presenter {
        return DiscHelpsPresenter()
    }

    /*****************************logic method */
    override fun processLogic() {
        super.processLogic()

        scroll_refresh_rv_content.startRefresh()
        mPresenter.firstLoading(mBookSort, mStart, mLimited, mDistillate)
    }

    private fun startRefresh() {
        mStart = 0
        mPresenter.refreshBookHelps(mBookSort, mStart, mLimited, mDistillate)
    }

    /**************************rewrite method */
    override fun finishRefresh(beans: List<BookHelpsBean>) {
        mDiscHelpsAdapter!!.refreshItems(beans)
        mStart = beans.size
        scroll_refresh_rv_content.isRefreshing = false
    }

    override fun finishLoading(beans: List<BookHelpsBean>) {
        mDiscHelpsAdapter!!.addItems(beans)
        mStart += beans.size
    }

    override fun showErrorTip() {
        scroll_refresh_rv_content.showTip()
    }

    override fun showError() {
        mDiscHelpsAdapter!!.showLoadError()
    }

    override fun complete() {
        scroll_refresh_rv_content.finishRefresh()
    }

    /** */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(BUNDLE_SORT, mBookSort)
        outState.putSerializable(BUNDLE_DISTILLATE, mDistillate)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.saveBookHelps(mDiscHelpsAdapter!!.getItems())
    }

    companion object {
        private val BUNDLE_SORT = "bundle_sort"
        private val BUNDLE_DISTILLATE = "bundle_distillate"
    }
}
