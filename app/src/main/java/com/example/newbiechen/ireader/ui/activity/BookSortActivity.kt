package com.example.newbiechen.ireader.ui.activity

import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.packages.BookSortPackage
import com.example.newbiechen.ireader.model.bean.packages.BookSubSortPackage
import com.example.newbiechen.ireader.presenter.BookSortPresenter
import com.example.newbiechen.ireader.presenter.contract.BookSortContract
import com.example.newbiechen.ireader.ui.adapter.BookSortAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPActivity
import com.example.newbiechen.ireader.widget.itemdecoration.DividerGridItemDecoration
import kotlinx.android.synthetic.main.activity_book_sort.*

/**
 * Created by newbiechen on 17-4-23.
 * 分类选择
 *
 */

class BookSortActivity : BaseMVPActivity<BookSortContract.View, BookSortContract.Presenter>(), BookSortContract.View {

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

        book_sort_rv_boy!!.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        book_sort_rv_boy!!.addItemDecoration(itemDecoration)
        book_sort_rv_boy!!.adapter = mBoyAdapter

        book_sort_rv_girl!!.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        book_sort_rv_girl!!.addItemDecoration(itemDecoration)
        book_sort_rv_girl!!.adapter = mGirlAdapter
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

        book_sort_rl_refresh!!.showLoading()
        mPresenter.refreshSortBean()
    }

    /***********************rewrite */
    override fun finishRefresh(sortPackage: BookSortPackage, subSortPackage: BookSubSortPackage) {
        if (sortPackage == null || sortPackage.male.isEmpty() || sortPackage.female.isEmpty()) {
            book_sort_rl_refresh!!.showEmpty()
        } else {
            mBoyAdapter!!.refreshItems(sortPackage.male)
            mGirlAdapter!!.refreshItems(sortPackage.female)
        }
        mSubSortPackage = subSortPackage
    }

    override fun showError() {
        book_sort_rl_refresh!!.showError()
    }

    override fun complete() {
        book_sort_rl_refresh!!.showFinish()
    }

    companion object {
        /*******************Constant */
        private const val SPAN_COUNT = 3
    }
}
