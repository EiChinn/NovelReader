package com.example.newbiechen.ireader.ui.activity

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.RxBus
import com.example.newbiechen.ireader.event.BookSubSortEvent
import com.example.newbiechen.ireader.model.bean.BookTagBean
import com.example.newbiechen.ireader.model.flag.BookListType
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.ui.adapter.HorizonTagAdapter
import com.example.newbiechen.ireader.ui.adapter.TagGroupAdapter
import com.example.newbiechen.ireader.ui.base.BaseTabActivity
import com.example.newbiechen.ireader.ui.fragment.BookListFragment
import com.example.newbiechen.ireader.utils.LogUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_book_list.*
import java.util.*

/**
 * 主题书单
 */

class BookListActivity : BaseTabActivity() {
    private var mHorizonTagAdapter: HorizonTagAdapter? = null
    private var mTagGroupAdapter: TagGroupAdapter? = null
    private var mTopInAnim: Animation? = null
    private var mTopOutAnim: Animation? = null
    /************Params */
    private val mDisposable = CompositeDisposable()

    override fun getContentId(): Int {
        return R.layout.activity_book_list
    }

    override fun createTabFragments(): MutableList<Fragment> {
        val fragments = ArrayList<Fragment>(BookListType.values().size)
        for (type in BookListType.values()) {
            fragments.add(BookListFragment.newInstance(type))
        }
        return fragments
    }

    override fun createTabTitles(): MutableList<String> {
        val titles = ArrayList<String>(BookListType.values().size)
        for (type in BookListType.values()) {
            titles.add(type.getTypeName())
        }
        return titles
    }

    override fun setUpToolbar(toolbar: Toolbar) {
        super.setUpToolbar(toolbar)
        supportActionBar!!.title = "主题书单"
    }

    override fun initWidget() {
        super.initWidget()
        initTag()
    }

    private fun initTag() {
        //横向的
        mHorizonTagAdapter = HorizonTagAdapter()
        val tagManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        book_list_rv_tag_horizon!!.layoutManager = tagManager
        book_list_rv_tag_horizon!!.adapter = mHorizonTagAdapter

        //筛选框
        mTagGroupAdapter = TagGroupAdapter(book_list_rv_tag_filter!!, 4)
        book_list_rv_tag_filter!!.adapter = mTagGroupAdapter
    }

    override fun initClick() {
        super.initClick()
        //滑动的Tag
        mHorizonTagAdapter!!.setOnItemClickListener { _, pos ->
            val bookSort = mHorizonTagAdapter!!.getItem(pos)
            RxBus.getInstance().post(BookSubSortEvent(bookSort))
        }

        //筛选
        book_list_cb_filter!!.setOnCheckedChangeListener { _, checked ->
            if (mTopInAnim == null || mTopOutAnim == null) {
                mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in)
                mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out)
            }

            if (checked) {
                book_list_rv_tag_filter!!.visibility = View.VISIBLE
                book_list_rv_tag_filter!!.startAnimation(mTopInAnim)
            } else {
                book_list_rv_tag_filter!!.startAnimation(mTopOutAnim)
                book_list_rv_tag_filter!!.visibility = View.GONE
            }
        }

        //筛选列表
        mTagGroupAdapter!!.setOnChildItemListener { _, groupPos, childPos ->
            val bean = mTagGroupAdapter!!.getChildItem(groupPos, childPos)
            //是否已存在
            val tags = mHorizonTagAdapter!!.getItems()
            var isExist = false
            for (i in tags.indices) {
                if (bean == tags[i]) {
                    mHorizonTagAdapter!!.setCurrentSelected(i)
                    book_list_rv_tag_horizon!!.layoutManager!!.scrollToPosition(i)
                    isExist = true
                }
            }
            if (!isExist) {
                //添加到1的位置,保证全本的位置
                mHorizonTagAdapter!!.addItem(1, bean)
                mHorizonTagAdapter!!.setCurrentSelected(1)
                book_list_rv_tag_horizon!!.layoutManager!!.scrollToPosition(1)
            }
            book_list_cb_filter!!.isChecked = false
        }
    }


    override fun processLogic() {
        super.processLogic()
        refreshTag()
    }

    private fun refreshTag() {
        val refreshDispo = RemoteRepository.instance
                .bookTags
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { tagBeans ->
                            refreshHorizonTag(tagBeans)
                            refreshGroupTag(tagBeans.toMutableList())
                        },
                        { e -> LogUtils.e(e.toString()) }
                )
        mDisposable.add(refreshDispo)
    }

    private fun refreshHorizonTag(tagBeans: List<BookTagBean>) {
        val randomTag = ArrayList<String>(RANDOM_COUNT)
        randomTag.add("全本")
        var caculator = 0
        //随机获取4,5个。
        val tagBeanCount = tagBeans.size
        for (i in 0 until tagBeanCount) {
            val tags = tagBeans[i].tags
            val tagCount = tags.size
            for (j in 0 until tagCount) {
                if (caculator < RANDOM_COUNT) {
                    randomTag.add(tags[j])
                    ++caculator
                } else {
                    break
                }
            }
            if (caculator >= RANDOM_COUNT) {
                break
            }
        }
        mHorizonTagAdapter!!.addItems(randomTag)
    }

    private fun refreshGroupTag(tagBeans: MutableList<BookTagBean>) {
        //由于数据还有根据性别分配，所以需要加上去
        val bean = BookTagBean(resources.getString(R.string.nb_tag_sex),
                Arrays.asList(resources.getString(R.string.nb_tag_boy), resources.getString(R.string.nb_tag_girl)))
        tagBeans.add(0, bean)

        mTagGroupAdapter!!.refreshItems(tagBeans)
    }

    companion object {
        private const val RANDOM_COUNT = 5
    }
}
