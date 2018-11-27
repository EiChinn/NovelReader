package com.example.newbiechen.ireader.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.BOOK_ID
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.BookSourcesBean
import com.example.newbiechen.ireader.presenter.ChangeSourcePresenter
import com.example.newbiechen.ireader.presenter.contract.ChangeSourceContract
import com.example.newbiechen.ireader.ui.adapter.ChangeSourceAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPActivity
import kotlinx.android.synthetic.main.activity_change_source.*

class ChangeSourceActivity : BaseMVPActivity<ChangeSourceContract.Presenter>(), ChangeSourceContract.View {

    private lateinit var bookId: String
    private lateinit var currentSourceName: String
    private lateinit var adapter: ChangeSourceAdapter

    override fun getContentId() = R.layout.activity_change_source

    override fun bindPresenter(): ChangeSourceContract.Presenter = ChangeSourcePresenter()

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        if (savedInstanceState != null) {
            savedInstanceState.getString(BOOK_ID)?.let { bookId = it }
            savedInstanceState.getString(CURRENT_SOURCE_NAME)?.let { currentSourceName = it }
        } else {
            bookId = intent.getStringExtra(BOOK_ID)
            currentSourceName = intent.getStringExtra(CURRENT_SOURCE_NAME)
        }
    }

    override fun initWidget() {
        super.initWidget()
        rv_book_sources.layoutManager = LinearLayoutManager(this)
        adapter = ChangeSourceAdapter(currentSourceName)
        rv_book_sources.adapter = adapter
    }

    override fun initClick() {
        super.initClick()
        adapter.setOnItemClickListener { _, pos ->
            val source = adapter.getItem(pos)
            val intent = Intent()
            intent.putExtra(CURRENT_SOURCE_NAME, source.name)
            intent.putExtra(CURRENT_SOURCE_BOOK_ID, source._id)
            setResult(RESULT_CODE, intent)
            finish()
        }
    }

    override fun processLogic() {
        super.processLogic()
        mPresenter.loadSources(bookId)
    }

    override fun showSource(bookChapterList: List<BookSourcesBean>) {
        adapter.refreshItems(bookChapterList)

    }

    override fun showError() {

    }

    override fun complete() {
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putString(BOOK_ID, bookId)
        outState?.putString(CURRENT_SOURCE_NAME, currentSourceName)
    }

    override fun onDestroy() {
        rv_book_sources.adapter = null
        adapter.setOnItemClickListener(null)
        super.onDestroy()
    }

    companion object {
        internal const val CURRENT_SOURCE_NAME = "current_source_name"
        internal const val CURRENT_SOURCE_BOOK_ID = "current_source_book_id"

        internal const val REQUEST_CODE = 0xff01
        internal const val RESULT_CODE = 0xff02
    }

}