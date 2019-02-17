package com.example.newbiechen.ireader.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.CommentBean
import com.example.newbiechen.ireader.model.bean.HelpsDetailBean
import com.example.newbiechen.ireader.presenter.HelpsDetailPresenter
import com.example.newbiechen.ireader.presenter.contract.HelpsDetailContract
import com.example.newbiechen.ireader.ui.adapter.CommentAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPFragment
import com.example.newbiechen.ireader.widget.adapter.LoadMoreView
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_refresh_list.*

/**
 * Created by newbiechen on 17-4-30.
 */

class HelpsDetailFragment : BaseMVPFragment<HelpsDetailContract.View, HelpsDetailContract.Presenter>(), HelpsDetailContract.View {

    /** */
    private var mCommentAdapter: CommentAdapter? = null
    private var mDetailHeader: HelpsDetailHeader? = null
    /***********params */
    private var mDetailId: String? = null
    private var start = 0
    private val limit = 30

    override fun getContentId(): Int {
        return R.layout.fragment_refresh_list
    }

    override fun bindPresenter(): HelpsDetailContract.Presenter {
        return HelpsDetailPresenter()
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
        mDetailHeader = HelpsDetailHeader(context!!, mCommentAdapter!!)
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
        mPresenter.refreshHelpsDetail(mDetailId!!, start, limit)
    }

    override fun finishRefresh(helpsDetail: HelpsDetailBean,
                               bestComments: List<CommentBean>,
                               comments: List<CommentBean>) {
        //加载
        start = comments.size
        mDetailHeader!!.setCommentDetail(helpsDetail)
        mDetailHeader!!.godCommentList = bestComments
        mCommentAdapter!!.refreshItems(comments)
    }

    override fun finishLoad(comments: List<CommentBean>) {
        start += comments.size
        mCommentAdapter!!.addItems(comments)
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

    /** */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_DETAIL_ID, mDetailId)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDetailHeader!!.detailUnbinder != null) {
            mDetailHeader!!.detailUnbinder!!.unbind()
        }
    }

    companion object {
        private val TAG = "HelpsDetailFragment"
        private val EXTRA_DETAIL_ID = "extra_detail_id"

        fun newInstance(detailId: String): Fragment {
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_DETAIL_ID, detailId)
            val fragment = HelpsDetailFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}


