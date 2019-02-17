package com.example.newbiechen.ireader.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.RxBus
import com.example.newbiechen.ireader.event.SelectorEvent
import com.example.newbiechen.ireader.model.bean.BookCommentBean
import com.example.newbiechen.ireader.model.flag.BookDistillate
import com.example.newbiechen.ireader.model.flag.BookSort
import com.example.newbiechen.ireader.model.flag.CommunityType
import com.example.newbiechen.ireader.presenter.DiscCommentPresenter
import com.example.newbiechen.ireader.presenter.contract.DiscCommentContract
import com.example.newbiechen.ireader.ui.activity.DiscDetailActivity
import com.example.newbiechen.ireader.ui.adapter.DiscCommentAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPFragment
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.widget.adapter.LoadMoreView
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_scroll_refresh_list.*

/**
 * DiscussionCommentFragment
 * Created by newbiechen on 17-4-17.
 * 讨论组中的评论区，包括Comment、Girl、Origin
 * 1. 初始化RecyclerView
 * 2. 初始化视图和逻辑的交互
 */

class DiscCommentFragment : BaseMVPFragment<DiscCommentContract.View, DiscCommentContract.Presenter>(), DiscCommentContract.View {

    /************************object */
    private var mDiscCommentAdapter: DiscCommentAdapter? = null

    /*************************Params */
    private var mBlock = CommunityType.COMMENT
    private var mBookSort = BookSort.DEFAULT
    private var mDistillate = BookDistillate.ALL
    private var mStart = 0
    private val mLimited = 20

    /** */

    override fun getContentId(): Int {
        return R.layout.fragment_scroll_refresh_list
    }

    override fun initData(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            mBlock = savedInstanceState.getSerializable(BUNDLE_BLOCK) as CommunityType
            mBookSort = savedInstanceState.getSerializable(BUNDLE_SORT) as BookSort
            mDistillate = savedInstanceState.getSerializable(BUNDLE_DISTILLATE) as BookDistillate
        } else {
            mBlock = arguments!!.getSerializable(EXTRA_BLOCK) as CommunityType
        }
    }

    override fun initWidget(savedInstanceState: Bundle?) {
        setUpAdapter()
    }

    private fun setUpAdapter() {
        scroll_refresh_rv_content.setLayoutManager(LinearLayoutManager(context))
        scroll_refresh_rv_content.addItemDecoration(DividerItemDecoration(context!!))
        mDiscCommentAdapter = DiscCommentAdapter(context!!, WholeAdapter.Options())
        scroll_refresh_rv_content.setAdapter(mDiscCommentAdapter)
    }

    /******************************init click method */

    override fun initClick() {
        //下滑刷新
        scroll_refresh_rv_content.setOnRefreshListener { refreshData() }
        //上滑加载
        mDiscCommentAdapter!!.setOnLoadMoreListener(object : LoadMoreView.OnLoadMoreListener {
            override fun onLoadMore() {
                mPresenter.loadingComment(mBlock, mBookSort, mStart, mLimited, mDistillate)
            }
        })
        mDiscCommentAdapter!!.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                val bean = mDiscCommentAdapter!!.getItem(pos)
                val detailId = bean._id
                DiscDetailActivity.startActivity(context, mBlock, detailId)
            }
        })
        //选择刷新
        addDisposable(RxBus.getInstance()
                .toObservable(Constant.MSG_SELECTOR, SelectorEvent::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (distillate, _, sort) ->
                    mBookSort = sort
                    mDistillate = distillate
                    refreshData()
                })
    }

    override fun bindPresenter(): DiscCommentPresenter {
        return DiscCommentPresenter()
    }

    /*******************************logic */
    override fun processLogic() {
        super.processLogic()
        //首次加载数据
        scroll_refresh_rv_content.startRefresh()
        mPresenter.firstLoading(mBlock, mBookSort, mStart, mLimited, mDistillate)
    }

    private fun refreshData() {
        mStart = 0
        scroll_refresh_rv_content.startRefresh()
        mPresenter.refreshComment(mBlock, mBookSort, mStart, mLimited, mDistillate)
    }

    /********************************rewrite method */

    override fun finishRefresh(beans: List<BookCommentBean>) {
        mDiscCommentAdapter!!.refreshItems(beans)
        mStart = beans.size
    }

    override fun finishLoading(beans: List<BookCommentBean>) {
        mDiscCommentAdapter!!.addItems(beans)
        mStart += beans.size
    }

    override fun showErrorTip() {
        scroll_refresh_rv_content.showTip()
    }

    override fun showError() {
        mDiscCommentAdapter!!.showLoadError()
    }

    override fun complete() {
        scroll_refresh_rv_content.finishRefresh()
    }

    /****************************save */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(BUNDLE_BLOCK, mBlock)
        outState.putSerializable(BUNDLE_SORT, mBookSort)
        outState.putSerializable(BUNDLE_DISTILLATE, mDistillate)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.saveComment(mDiscCommentAdapter!!.getItems())
    }

    companion object {
        private const val EXTRA_BLOCK = "extra_block"
        private const val BUNDLE_BLOCK = "bundle_block"
        private const val BUNDLE_SORT = "bundle_sort"
        private const val BUNDLE_DISTILLATE = "bundle_distillate"

        /****************************open method */
        fun newInstance(block: CommunityType): Fragment {
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_BLOCK, block)
            val fragment = DiscCommentFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
