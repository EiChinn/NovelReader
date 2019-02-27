package com.example.newbiechen.ireader.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.CommentBean
import com.example.newbiechen.ireader.model.bean.ReviewDetailBean
import com.example.newbiechen.ireader.presenter.ReviewDetailPresenter
import com.example.newbiechen.ireader.presenter.contract.ReviewDetailContract
import com.example.newbiechen.ireader.ui.adapter.CommentAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPFragment
import com.example.newbiechen.ireader.widget.adapter.LoadMoreView
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_refresh_list.*

/**
 * Created by newbiechen on 17-4-30.
 */

class ReviewDetailFragment : BaseMVPFragment<ReviewDetailContract.View, ReviewDetailContract.Presenter>(), ReviewDetailContract.View {
    /** */
    private var mCommentAdapter: CommentAdapter? = null
    private var mDetailHeader: ReviewDetailHeader? = null
    /***********params */
    private var mDetailId: String? = null
    private var start = 0
    private val limit = 30

    override fun getContentId(): Int {
        return R.layout.fragment_refresh_list
    }

    override fun bindPresenter(): ReviewDetailContract.Presenter {
        return ReviewDetailPresenter()
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        if (savedInstanceState != null) {
            mDetailId = savedInstanceState.getString(EXTRA_DETAIL_ID)
        } else {
            mDetailId = arguments!!.getString(EXTRA_DETAIL_ID)
        }
    }

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {

        mCommentAdapter = CommentAdapter(context!!, WholeAdapter.Options())
        mDetailHeader = ReviewDetailHeader(context!!, mCommentAdapter!!)
        mCommentAdapter!!.addHeaderView(mDetailHeader!!)

        refresh_rv_content.layoutManager = LinearLayoutManager(context)
        refresh_rv_content.addItemDecoration(DividerItemDecoration(context!!))
        refresh_rv_content.adapter = mCommentAdapter
    }

    override fun initClick() {
        super.initClick()
        mCommentAdapter!!.setOnLoadMoreListener(object : LoadMoreView.OnLoadMoreListener {
            override fun onLoadMore() {
                mPresenter.loadComment(mDetailId!!, start, limit)
            }
        })
    }

    override fun processLogic() {
        super.processLogic()
        //获取数据啦
        refresh_layout.showLoading()
        mPresenter.refreshReviewDetail(mDetailId!!, start, limit)
    }

    override fun finishRefresh(reviewDetail: ReviewDetailBean,
                               bestComments: List<CommentBean>, comments: List<CommentBean>) {
        start = comments.size
        mDetailHeader!!.setCommentDetail(reviewDetail)
        mDetailHeader!!.godCommentList = bestComments
        mCommentAdapter!!.refreshItems(comments)
    }

    override fun finishLoad(comments: List<CommentBean>) {
        mCommentAdapter!!.addItems(comments)
        start += comments.size
    }

    override fun showError() {
        refresh_layout.showError()
    }

    override fun showLoadError() {
        mCommentAdapter!!.showLoadError()
    }

    override fun complete() {
        refresh_layout.showFinish()
    }

    companion object {
        private const val EXTRA_DETAIL_ID = "extra_detail_id"

        fun newInstance(detailId: String): Fragment {
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_DETAIL_ID, detailId)
            val fragment = ReviewDetailFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
