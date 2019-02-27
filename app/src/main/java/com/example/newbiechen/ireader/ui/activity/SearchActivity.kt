package com.example.newbiechen.ireader.ui.activity

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.packages.SearchBooksBean
import com.example.newbiechen.ireader.presenter.SearchPresenter
import com.example.newbiechen.ireader.presenter.contract.SearchContract
import com.example.newbiechen.ireader.ui.adapter.KeyWordAdapter
import com.example.newbiechen.ireader.ui.adapter.SearchBookAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPActivity
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_refresh_list.*

/**
 * Created by newbiechen on 17-4-24.
 */

class SearchActivity : BaseMVPActivity<SearchContract.View, SearchContract.Presenter>(), SearchContract.View {

    private var mKeyWordAdapter: KeyWordAdapter? = null
    private var mSearchAdapter: SearchBookAdapter? = null

    private var isTag: Boolean = false
    private var mHotTagList: List<String>? = null
    private var mTagStart = 0

    override fun getContentId(): Int {
        return R.layout.activity_search
    }

    override fun bindPresenter(): SearchContract.Presenter {
        return SearchPresenter()
    }

    override fun initWidget() {
        super.initWidget()
        setUpAdapter()
        refresh_layout!!.background = ContextCompat.getDrawable(this, R.color.white)
    }

    private fun setUpAdapter() {
        mKeyWordAdapter = KeyWordAdapter()
        mSearchAdapter = SearchBookAdapter()

        refresh_rv_content!!.layoutManager = LinearLayoutManager(this)
        refresh_rv_content!!.addItemDecoration(DividerItemDecoration(this))
    }

    override fun initClick() {
        super.initClick()

        //退出
        search_iv_back!!.setOnClickListener { v -> onBackPressed() }

        //输入框
        search_et_input!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' } == "") {
                    //隐藏delete按钮和关键字显示内容
                    if (search_iv_delete!!.visibility == View.VISIBLE) {
                        search_iv_delete!!.visibility = View.INVISIBLE
                        refresh_layout!!.visibility = View.INVISIBLE
                        //删除全部视图
                        mKeyWordAdapter!!.clear()
                        mSearchAdapter!!.clear()
                        refresh_rv_content!!.removeAllViews()
                    }
                    return
                }
                //显示delete按钮
                if (search_iv_delete!!.visibility == View.INVISIBLE) {
                    search_iv_delete!!.visibility = View.VISIBLE
                    refresh_layout!!.visibility = View.VISIBLE
                    //默认是显示完成状态
                    refresh_layout!!.showFinish()
                }
                //搜索
                val query = s.toString().trim { it <= ' ' }
                if (isTag) {
                    refresh_layout!!.showLoading()
                    mPresenter.searchBook(query)
                    isTag = false
                } else {
                    //传递
                    mPresenter.searchKeyWord(query)
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        //键盘的搜索
        search_et_input!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            //修改回车键功能
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                searchBook()
                return@OnKeyListener true
            }
            false
        })

        //进行搜索
        search_iv_search!!.setOnClickListener { v -> searchBook() }

        //删除字
        search_iv_delete!!.setOnClickListener { v ->
            search_et_input!!.setText("")
            toggleKeyboard()
        }

        //点击查书
        mKeyWordAdapter!!.setOnItemClickListener { view, pos ->
            //显示正在加载
            refresh_layout!!.showLoading()
            val book = mKeyWordAdapter!!.getItem(pos)
            mPresenter.searchBook(book)
            toggleKeyboard()
        }

        //Tag的点击事件
        search_tg_hot!!.setOnTagClickListener { tag ->
            isTag = true
            search_et_input!!.setText(tag)
        }

        //Tag的刷新事件
        search_book_tv_refresh_hot!!.setOnClickListener { v -> refreshTag() }

        //书本的点击事件
        mSearchAdapter!!.setOnItemClickListener { view, pos ->
            val bookId = mSearchAdapter!!.getItem(pos)._id
            BookDetailActivity.startActivity(this, bookId)
        }
    }

    private fun searchBook() {
        val query = search_et_input!!.text.toString().trim { it <= ' ' }
        if (query != "") {
            refresh_layout!!.visibility = View.VISIBLE
            refresh_layout!!.showLoading()
            mPresenter.searchBook(query)
            //显示正在加载
            refresh_layout!!.showLoading()
            toggleKeyboard()
        }
    }

    private fun toggleKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun processLogic() {
        super.processLogic()
        //默认隐藏
        refresh_layout!!.visibility = View.GONE
        //获取热词
        mPresenter.searchHotWord()
    }

    override fun showError() {}

    override fun complete() {

    }

    override fun finishHotWords(hotWords: List<String>) {
        mHotTagList = hotWords
        refreshTag()
    }

    private fun refreshTag() {
        var last = mTagStart + TAG_LIMIT
        if (mHotTagList!!.size <= last) {
            mTagStart = 0
            last = TAG_LIMIT
        }
        val tags = mHotTagList!!.subList(mTagStart, last)
        search_tg_hot!!.setTags(tags)
        mTagStart += TAG_LIMIT
    }

    override fun finishKeyWords(keyWords: List<String>) {
        if (keyWords.isEmpty()) refresh_layout!!.visibility = View.INVISIBLE
        mKeyWordAdapter!!.refreshItems(keyWords)
        if (refresh_rv_content!!.adapter !is KeyWordAdapter) {
            refresh_rv_content!!.adapter = mKeyWordAdapter
        }
    }

    override fun finishBooks(books: List<SearchBooksBean>) {
        mSearchAdapter!!.refreshItems(books)
        if (books.isEmpty()) {
            refresh_layout!!.showEmpty()
        } else {
            //显示完成
            refresh_layout!!.showFinish()
        }
        //加载
        if (refresh_rv_content!!.adapter !is SearchBookAdapter) {
            refresh_rv_content!!.adapter = mSearchAdapter
        }
    }

    override fun errorBooks() {
        refresh_layout!!.showEmpty()
    }

    override fun onBackPressed() {
        if (refresh_layout!!.visibility == View.VISIBLE) {
            search_et_input!!.setText("")
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val TAG_LIMIT = 8
    }
}
