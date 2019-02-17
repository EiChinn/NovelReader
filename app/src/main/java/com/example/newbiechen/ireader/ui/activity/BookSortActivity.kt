package com.example.newbiechen.ireader.ui.activity

import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.packages.BookSortPackage
import com.example.newbiechen.ireader.model.bean.packages.BookSubSortPackage
import com.example.newbiechen.ireader.presenter.BookSortPresenter
import com.example.newbiechen.ireader.presenter.contract.BookSortContract
import com.example.newbiechen.ireader.ui.adapter.BookSortAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPActivity
import com.example.newbiechen.ireader.widget.RefreshLayout
import com.example.newbiechen.ireader.widget.itemdecoration.DividerGridItemDecoration

/**
 * Created by newbiechen on 17-4-23.
 * 分类选择
 *
 */

class BookSortActivity : BaseMVPActivity<BookSortContract.View, BookSortContract.Presenter>(), BookSortContract.View {

    @BindView(R.id.book_sort_rl_refresh)
    @JvmField internal var mRlRefresh: RefreshLayout? = null
    @BindView(R.id.book_sort_rv_boy)
    @JvmField internal var mRvBoy: RecyclerView? = null
    @BindView(R.id.book_sort_rv_girl)
    @JvmField internal var mRvGirl: RecyclerView? = null

    private var mBoyAdapter: BookSortAdapter? = null
    private var mGirlAdapter: BookSortAdapter? = null

    private var mSubSortPackage: BookSubSortPackage? = null
    /**********************init */
    override fun getContentId(): Int {
        return R.layout.activity_book_sort
    }

    override fun setUpToolbar(toolbar: Toolbar) {
        super.setUpToolbar(toolbar)
        supportActionBar!!.title = resources.getString(R.string.nb_fragment_find_sort)
    }

    override fun initWidget() {
        super.initWidget()
        setUpAdapter()
    }

    private fun setUpAdapter() {
        mBoyAdapter = BookSortAdapter()
        mGirlAdapter = BookSortAdapter()

        val itemDecoration = DividerGridItemDecoration(this, R.drawable.shape_divider_row, R.drawable.shape_divider_col)

        mRvBoy!!.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        mRvBoy!!.addItemDecoration(itemDecoration)
        mRvBoy!!.adapter = mBoyAdapter

        mRvGirl!!.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        mRvGirl!!.addItemDecoration(itemDecoration)
        mRvGirl!!.adapter = mGirlAdapter
    }

    override fun bindPresenter(): BookSortContract.Presenter {
        return BookSortPresenter()
    }

    override fun initClick() {
        super.initClick()
        mBoyAdapter!!.setOnItemClickListener { view, pos ->
            val subSortBean = mSubSortPackage!!.male[pos]
            //上传
            BookSortListActivity.startActivity(this, "male", subSortBean)
        }
        mGirlAdapter!!.setOnItemClickListener { view, pos ->
            val subSortBean = mSubSortPackage!!.female[pos]
            //上传
            BookSortListActivity.startActivity(this, "female", subSortBean)
        }
    }

    /*********************logic */

    override fun processLogic() {
        super.processLogic()

        mRlRefresh!!.showLoading()
        mPresenter.refreshSortBean()
    }

    /***********************rewrite */
    override fun finishRefresh(sortPackage: BookSortPackage, subSortPackage: BookSubSortPackage) {
        if (sortPackage == null || sortPackage.male.isEmpty() || sortPackage.female.isEmpty()) {
            mRlRefresh!!.showEmpty()
        } else {
            mBoyAdapter!!.refreshItems(sortPackage.male)
            mGirlAdapter!!.refreshItems(sortPackage.female)
        }
        mSubSortPackage = subSortPackage
    }

    override fun showError() {
        mRlRefresh!!.showError()
    }

    override fun complete() {
        mRlRefresh!!.showFinish()
    }

    companion object {
        /*******************Constant */
        private const val SPAN_COUNT = 3
    }
}
