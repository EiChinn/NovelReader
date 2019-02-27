package com.example.newbiechen.ireader.ui.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.db.entity.CollBook
import com.example.newbiechen.ireader.model.bean.BookDetailBean
import com.example.newbiechen.ireader.model.bean.BookListBean
import com.example.newbiechen.ireader.model.bean.HotCommentBean
import com.example.newbiechen.ireader.model.local.BookRepository
import com.example.newbiechen.ireader.presenter.BookDetailPresenter
import com.example.newbiechen.ireader.presenter.contract.BookDetailContract
import com.example.newbiechen.ireader.ui.adapter.BookListAdapter
import com.example.newbiechen.ireader.ui.adapter.HotCommentAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPActivity
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.StringUtils
import com.example.newbiechen.ireader.utils.ToastUtils
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_book_detail.*

/**
 * Created by newbiechen on 17-5-4.
 */

class BookDetailActivity : BaseMVPActivity<BookDetailContract.View, BookDetailContract.Presenter>(), BookDetailContract.View {

    /** */
    private var mHotCommentAdapter: HotCommentAdapter? = null
    private var mBookListAdapter: BookListAdapter? = null
    private var mCollBook: CollBook? = null
    private var mProgressDialog: ProgressDialog? = null
    /** */
    private var mBookId: String? = null
    private var isBriefOpen = false
    private var isCollected = false

    override fun getContentId(): Int {
        return R.layout.activity_book_detail
    }

    override fun bindPresenter(): BookDetailContract.Presenter {
        return BookDetailPresenter()
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        if (savedInstanceState != null) {
            mBookId = savedInstanceState.getString(EXTRA_BOOK_ID)
        } else {
            mBookId = intent.getStringExtra(EXTRA_BOOK_ID)
        }
        //        mBookId = "5afaa58c75c0345b392dca9a";
    }

    override fun setUpToolbar(toolbar: Toolbar) {
        super.setUpToolbar(toolbar)
        supportActionBar!!.title = "书籍详情"
    }

    override fun initClick() {
        super.initClick()

        //可伸缩的TextView
        book_detail_tv_brief!!.setOnClickListener {
            if (isBriefOpen) {
                book_detail_tv_brief!!.maxLines = 4
                isBriefOpen = false
            } else {
                book_detail_tv_brief!!.maxLines = 8
                isBriefOpen = true
            }
        }

        book_list_tv_chase!!.setOnClickListener {
            //点击存储
            if (isCollected) {
                //放弃点击
                BookRepository.instance
                        .deleteCollBookInRx(mCollBook!!)

                book_list_tv_chase!!.text = resources.getString(R.string.nb_book_detail_chase_update)

                //修改背景
                val drawable = resources.getDrawable(R.drawable.selector_btn_book_list)
                book_list_tv_chase!!.background = drawable
                //设置图片
                book_list_tv_chase!!.setCompoundDrawables(ContextCompat.getDrawable(this, R.drawable.ic_book_list_add), null, null, null)

                isCollected = false
            } else {
                mPresenter.addToBookShelf(mCollBook!!)
                book_list_tv_chase!!.text = resources.getString(R.string.nb_book_detail_give_up)

                //修改背景
                val drawable = resources.getDrawable(R.drawable.shape_common_gray_corner)
                book_list_tv_chase!!.background = drawable
                //设置图片
                book_list_tv_chase!!.setCompoundDrawables(ContextCompat.getDrawable(this, R.drawable.ic_book_list_delete), null, null, null)

                isCollected = true
            }
        }

        book_detail_tv_read!!.setOnClickListener {
            startActivityForResult(Intent(this, ReadActivity::class.java)
                    .putExtra(ReadActivity.EXTRA_IS_COLLECTED, isCollected)
                    .putExtra(ReadActivity.EXTRA_COLL_BOOK, mCollBook), REQUEST_READ)
        }


    }

    override fun processLogic() {
        super.processLogic()
        refresh_layout!!.showLoading()
        mPresenter.refreshBookDetail(mBookId!!)
    }

    override fun finishRefresh(bean: BookDetailBean) {
        //封面
        Glide.with(this)
                .load(Constant.IMG_BASE_URL + bean.cover)
                .placeholder(R.drawable.ic_book_loading)
                .error(R.drawable.ic_load_error)
                .centerCrop()
                .into(book_detail_iv_cover!!)
        //书名
        book_detail_tv_title!!.text = bean.title
        //作者
        book_detail_tv_author!!.text = bean.author
        //类型
        book_detail_tv_type!!.text = bean.majorCate

        //总字数
        book_detail_tv_word_count!!.text = resources.getString(R.string.nb_book_word, bean.wordCount / 10000)
        //更新时间
        book_detail_tv_lately_update!!.text = StringUtils.dateConvert(bean.updated, Constant.FORMAT_BOOK_DATE)
        //追书人数
        book_detail_tv_follower_count!!.text = bean.followerCount.toString() + ""
        //存留率
        book_detail_tv_retention!!.text = bean.retentionRatio + "%"
        //日更字数
        book_detail_tv_day_word_count!!.text = bean.serializeWordCount.toString() + ""
        //简介
        book_detail_tv_brief!!.text = bean.longIntro
        //社区
        book_detail_tv_community!!.text = resources.getString(R.string.nb_book_detail_community, bean.title)
        //帖子数
        book_detail_tv_posts_count!!.text = resources.getString(R.string.nb_book_detail_posts_count, bean.postCount)
        mCollBook = BookRepository.instance.getCollBook(bean._id)

        //判断是否收藏
        if (mCollBook != null) {
            isCollected = true

            book_list_tv_chase!!.text = resources.getString(R.string.nb_book_detail_give_up)
            //修改背景
            val drawable = resources.getDrawable(R.drawable.shape_common_gray_corner)
            book_list_tv_chase!!.background = drawable
            //设置图片
            book_list_tv_chase!!.setCompoundDrawables(ContextCompat.getDrawable(this, R.drawable.ic_book_list_delete), null, null, null)
            book_detail_tv_read!!.text = "继续阅读"
        } else {
            mCollBook = bean.getCollBook()
        }
    }

    override fun finishHotComment(beans: List<HotCommentBean>) {
        if (beans.isEmpty()) {
            return
        }
        mHotCommentAdapter = HotCommentAdapter()
        book_detail_rv_hot_comment!!.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                //与外部ScrollView滑动冲突
                return false
            }
        }
        book_detail_rv_hot_comment!!.addItemDecoration(DividerItemDecoration(this))
        book_detail_rv_hot_comment!!.adapter = mHotCommentAdapter
        mHotCommentAdapter!!.addItems(beans)
    }

    override fun finishRecommendBookList(beans: List<BookListBean>) {
        if (beans.isEmpty()) {
            book_list_tv_recommend_book_list!!.visibility = View.GONE
            return
        }
        //推荐书单列表
        mBookListAdapter = BookListAdapter(this, null)
        book_detail_rv_recommend_book_list!!.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                //与外部ScrollView滑动冲突
                return false
            }
        }
        book_detail_rv_recommend_book_list!!.addItemDecoration(DividerItemDecoration(this))
        book_detail_rv_recommend_book_list!!.adapter = mBookListAdapter
        mBookListAdapter!!.addItems(beans)
    }

    override fun waitToBookShelf() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setTitle("正在添加到书架中")
        }
        mProgressDialog!!.show()
    }

    override fun errorToBookShelf() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
            mProgressDialog = null
        }
        ToastUtils.show("加入书架失败，请检查网络")
    }

    override fun succeedToBookShelf() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
            mProgressDialog = null
        }
        ToastUtils.show("加入书架成功")
    }

    override fun onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
            mProgressDialog = null
        }
        super.onDestroy()
    }

    override fun showError() {
        refresh_layout!!.showError()
    }

    override fun complete() {
        refresh_layout!!.showFinish()
    }

    /** */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_BOOK_ID, mBookId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //如果进入阅读页面收藏了，页面结束的时候，就需要返回改变收藏按钮
        if (requestCode == REQUEST_READ) {
            if (data == null) {
                return
            }

            isCollected = data.getBooleanExtra(RESULT_IS_COLLECTED, false)

            if (isCollected) {
                book_list_tv_chase!!.text = resources.getString(R.string.nb_book_detail_give_up)
                //修改背景
                val drawable = resources.getDrawable(R.drawable.shape_common_gray_corner)
                book_list_tv_chase!!.background = drawable
                //设置图片
                book_list_tv_chase!!.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_book_list_delete), null, null, null)
                book_detail_tv_read!!.text = "继续阅读"
            }
        }
    }

    companion object {
        const val RESULT_IS_COLLECTED = "result_is_collected"

        private const val EXTRA_BOOK_ID = "extra_book_id"

        private const val REQUEST_READ = 1

        fun startActivity(context: Context, bookId: String) {
            val intent = Intent(context, BookDetailActivity::class.java)
            intent.putExtra(EXTRA_BOOK_ID, bookId)
            context.startActivity(intent)
        }
    }
}
