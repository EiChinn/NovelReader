package com.example.newbiechen.ireader.ui.activity

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.RxBus
import com.example.newbiechen.ireader.event.SelectorEvent
import com.example.newbiechen.ireader.model.flag.BookDistillate
import com.example.newbiechen.ireader.model.flag.BookSelection.*
import com.example.newbiechen.ireader.model.flag.BookSort
import com.example.newbiechen.ireader.model.flag.BookType
import com.example.newbiechen.ireader.model.flag.CommunityType
import com.example.newbiechen.ireader.ui.base.BaseActivity
import com.example.newbiechen.ireader.ui.fragment.DiscCommentFragment
import com.example.newbiechen.ireader.ui.fragment.DiscHelpsFragment
import com.example.newbiechen.ireader.ui.fragment.DiscReviewFragment
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.widget.SelectorView
import kotlinx.android.synthetic.main.activity_book_discussion.*

/**
 * Created by newbiechen on 17-4-17.
 * 书籍讨论
 */

class BookDiscussionActivity : BaseActivity(), SelectorView.OnItemSelectedListener {

    /**********************Params */
    //当前的讨论组
    private var mType: CommunityType? = null
    //每个讨论组中的选择分类
    private var mBookSort = BookSort.DEFAULT
    private var mDistillate = BookDistillate.ALL
    private var mBookType = BookType.ALL

    /*****************************init method */
    override fun getContentId() = R.layout.activity_book_discussion

    override fun initData(savedInstanceState: Bundle?) {
        mType = if (savedInstanceState != null) {
            savedInstanceState.getSerializable(EXTRA_COMMUNITY) as CommunityType
        } else {
            intent.getSerializableExtra(EXTRA_COMMUNITY) as CommunityType
        }
    }

    override fun setUpToolbar(toolbar: Toolbar) {
        supportActionBar!!.title = mType!!.getTypeName()
    }

    /******************************click method */
    override fun initClick() {
        super.initClick()
        book_discussion_sv_selector.setOnItemSelectedListener(this)
    }

    override fun onItemSelected(type: Int, pos: Int) {
        //转换器
        when (type) {
            0 -> mDistillate = BookDistillate.values()[pos]
            1 -> if (book_discussion_sv_selector.childCount == 2) {
                //当size = 2的时候，就会到Sort这里。
                mBookSort = BookSort.values()[pos]
            } else if (book_discussion_sv_selector.childCount == 3) {
                mBookType = BookType.values()[pos]
            }
            2 -> mBookSort = BookSort.values()[pos]
            else -> {
            }
        }

        RxBus.getInstance()
                .post(Constant.MSG_SELECTOR, SelectorEvent(mDistillate, mBookType, mBookSort))
    }

    /*******************************logic method */
    override fun processLogic() {
        val fragment = when (mType) {
            CommunityType.REVIEW -> { // 书评区
                setUpSelectorView(TYPE_SECOND)
                DiscReviewFragment()
            }
            CommunityType.HELP -> { // 书荒帮助区
                setUpSelectorView(TYPE_FIRST)
                DiscHelpsFragment()
            } else -> {
                setUpSelectorView(TYPE_FIRST)
                DiscCommentFragment.newInstance(mType!!)
            }
        }

        supportFragmentManager.beginTransaction()
                .add(R.id.book_discussion_fl, fragment)
                .commit()
    }

    private fun setUpSelectorView(type: Int) {
        if (type == TYPE_FIRST) {
            book_discussion_sv_selector.setSelectData(DISTILLATE.getTypeParams(), SORT_TYPE.getTypeParams())
        } else {
            book_discussion_sv_selector.setSelectData(DISTILLATE.getTypeParams(),
                    BOOK_TYPE.getTypeParams(), SORT_TYPE.getTypeParams())
        }
    }

    /** */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(EXTRA_COMMUNITY, mType)
    }

    companion object {
        /****************************Constant */
        const val EXTRA_COMMUNITY = "extra_community"
        private const val TYPE_FIRST = 0
        private const val TYPE_SECOND = 1
    }
}
