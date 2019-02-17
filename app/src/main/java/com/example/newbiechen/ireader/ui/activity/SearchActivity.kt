package com.example.newbiechen.ireader.ui.activity

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.packages.SearchBooksBean
import com.example.newbiechen.ireader.presenter.SearchPresenter
import com.example.newbiechen.ireader.presenter.contract.SearchContract
import com.example.newbiechen.ireader.ui.adapter.KeyWordAdapter
import com.example.newbiechen.ireader.ui.adapter.SearchBookAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPActivity
import com.example.newbiechen.ireader.widget.RefreshLayout
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import me.gujun.android.taggroup.TagGroup

/**
 * Created by newbiechen on 17-4-24.
 */

class SearchActivity : BaseMVPActivity<SearchContract.View, SearchContract.Presenter>(), SearchContract.View {

    @BindView(R.id.search_iv_back)
    @JvmField internal var mIvBack: ImageView? = null
    @BindView(R.id.search_et_input)
    @JvmField internal var mEtInput: EditText? = null
    @BindView(R.id.search_iv_delete)
    @JvmField internal var mIvDelete: ImageView? = null
    @BindView(R.id.search_iv_search)
    @JvmField internal var mIvSearch: ImageView? = null
    @BindView(R.id.search_book_tv_refresh_hot)
    @JvmField internal var mTvRefreshHot: TextView? = null
    @BindView(R.id.search_tg_hot)
    @JvmField internal var mTgHot: TagGroup? = null
    /*    @BindView(R.id.search_rv_history)
    RecyclerView mRvHistory;*/
    @BindView(R.id.refresh_layout)
    @JvmField internal var mRlRefresh: RefreshLayout? = null
    @BindView(R.id.refresh_rv_content)
    @JvmField internal var mRvSearch: RecyclerView? = null

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
        mRlRefresh!!.background = ContextCompat.getDrawable(this, R.color.white)
    }

    private fun setUpAdapter() {
        mKeyWordAdapter = KeyWordAdapter()
        mSearchAdapter = SearchBookAdapter()

        mRvSearch!!.layoutManager = LinearLayoutManager(this)
        mRvSearch!!.addItemDecoration(DividerItemDecoration(this))
    }

    override fun initClick() {
        super.initClick()

        //退出
        mIvBack!!.setOnClickListener { v -> onBackPressed() }

        //输入框
        mEtInput!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' } == "") {
                    //隐藏delete按钮和关键字显示内容
                    if (mIvDelete!!.visibility == View.VISIBLE) {
                        mIvDelete!!.visibility = View.INVISIBLE
                        mRlRefresh!!.visibility = View.INVISIBLE
                        //删除全部视图
                        mKeyWordAdapter!!.clear()
                        mSearchAdapter!!.clear()
                        mRvSearch!!.removeAllViews()
                    }
                    return
                }
                //显示delete按钮
                if (mIvDelete!!.visibility == View.INVISIBLE) {
                    mIvDelete!!.visibility = View.VISIBLE
                    mRlRefresh!!.visibility = View.VISIBLE
                    //默认是显示完成状态
                    mRlRefresh!!.showFinish()
                }
                //搜索
                val query = s.toString().trim { it <= ' ' }
                if (isTag) {
                    mRlRefresh!!.showLoading()
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
        mEtInput!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            //修改回车键功能
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                searchBook()
                return@OnKeyListener true
            }
            false
        })

        //进行搜索
        mIvSearch!!.setOnClickListener { v -> searchBook() }

        //删除字
        mIvDelete!!.setOnClickListener { v ->
            mEtInput!!.setText("")
            toggleKeyboard()
        }

        //点击查书
        mKeyWordAdapter!!.setOnItemClickListener { view, pos ->
            //显示正在加载
            mRlRefresh!!.showLoading()
            val book = mKeyWordAdapter!!.getItem(pos)
            mPresenter.searchBook(book)
            toggleKeyboard()
        }

        //Tag的点击事件
        mTgHot!!.setOnTagClickListener { tag ->
            isTag = true
            mEtInput!!.setText(tag)
        }

        //Tag的刷新事件
        mTvRefreshHot!!.setOnClickListener { v -> refreshTag() }

        //书本的点击事件
        mSearchAdapter!!.setOnItemClickListener { view, pos ->
            val bookId = mSearchAdapter!!.getItem(pos)._id
            BookDetailActivity.startActivity(this, bookId)
        }
    }

    private fun searchBook() {
        val query = mEtInput!!.text.toString().trim { it <= ' ' }
        if (query != "") {
            mRlRefresh!!.visibility = View.VISIBLE
            mRlRefresh!!.showLoading()
            mPresenter.searchBook(query)
            //显示正在加载
            mRlRefresh!!.showLoading()
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
        mRlRefresh!!.visibility = View.GONE
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
        mTgHot!!.setTags(tags)
        mTagStart += TAG_LIMIT
    }

    override fun finishKeyWords(keyWords: List<String>) {
        if (keyWords.size == 0) mRlRefresh!!.visibility = View.INVISIBLE
        mKeyWordAdapter!!.refreshItems(keyWords)
        if (mRvSearch!!.adapter !is KeyWordAdapter) {
            mRvSearch!!.adapter = mKeyWordAdapter
        }
    }

    override fun finishBooks(books: List<SearchBooksBean>) {
        mSearchAdapter!!.refreshItems(books)
        if (books.size == 0) {
            mRlRefresh!!.showEmpty()
        } else {
            //显示完成
            mRlRefresh!!.showFinish()
        }
        //加载
        if (mRvSearch!!.adapter !is SearchBookAdapter) {
            mRvSearch!!.adapter = mSearchAdapter
        }
    }

    override fun errorBooks() {
        mRlRefresh!!.showEmpty()
    }

    override fun onBackPressed() {
        if (mRlRefresh!!.visibility == View.VISIBLE) {
            mEtInput!!.setText("")
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val TAG_LIMIT = 8
    }
}
