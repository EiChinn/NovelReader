package com.example.newbiechen.ireader.ui.activity

import android.os.Bundle
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.flag.BookListType
import com.example.newbiechen.ireader.viewmodel.BookListViewModel
import com.example.newbiechen.ireader.viewmodel.InjectorUtils
import com.example.newbiechen.ireader.widget.dialog.LoadingDialogHelper
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_book_list.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.toast

/**
 * 主题书单
 */

class BookListActivity : AppCompatActivity() {

    private lateinit var viewModel: BookListViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)
        initToolbar()
        initTabCategory()
        viewModel = ViewModelProviders.of(this,
                InjectorUtils.provideBookListViewModelFactory())
                .get(BookListViewModel::class.java)
        viewModel.isRequestInProgress.observe(this, Observer {
            if (it) {
                LoadingDialogHelper.showLoadingDialog(this)
            } else {
                LoadingDialogHelper.closeLoadingDialog()
            }
        })
        viewModel.toastMsg.observe(this, Observer { toast(it) })
        viewModel.fetchTags()
        viewModel.tags?.observe(this, Observer { list ->
            viewModel.big5Tags.postValue(list.filter { it.type == MultiTagItem.TYPE_TAG }.take(5).map { it.tag }.toMutableList())
        })
        viewModel.big5Tags.observe(this, Observer {list ->
            tl_tag.removeAllTabs()
            list.forEach {
                tl_tag.addTab(tl_tag.newTab().setText(it))
            }
            tl_tag.clearOnTabSelectedListeners()
            tl_tag.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        viewModel.currentTag.postValue(it.text.toString())
                    }
                }

            })
        })

        tv_filter.setOnClickListener {
            showTagsPopupWindow()
        }
    }

    private fun showTagsPopupWindow() {
        val popupWindow = PopupWindow()
        val contentView = layoutInflater.inflate(R.layout.popup_book_list_tags, null)
        val rv_tags = contentView.findViewById<RecyclerView>(R.id.rv_tags)
        val adapter = FilterTagAdapter(viewModel.tags!!.value!!) {
            viewModel.refreshBig5Tags(it)
            popupWindow.dismiss()
        }
        rv_tags.layoutManager = GridLayoutManager(this, 4)
        adapter.setSpanSizeLookup { _, i ->
            return@setSpanSizeLookup if (adapter.data[i].type == MultiTagItem.TYPE_CATEGORY) 4 else 1
        }
        rv_tags.adapter = adapter

        popupWindow.contentView = contentView
        popupWindow.width = WindowManager.LayoutParams.MATCH_PARENT
        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.showAsDropDown(tv_filter)
    }

    class MultiTagItem(val type: Int, val tag: String) : MultiItemEntity{
        override fun getItemType() = type

        companion object {
            const val TYPE_CATEGORY = 0xff01
            const val TYPE_TAG = 0xff02
        }

    }

    class FilterTagAdapter(data: List<MultiTagItem>, val selectTag: (String) -> Unit) : BaseMultiItemQuickAdapter<MultiTagItem, BaseViewHolder>(data) {
        init {
            addItemType(MultiTagItem.TYPE_CATEGORY, R.layout.item_book_category_label)
            addItemType(MultiTagItem.TYPE_TAG, R.layout.item_book_list_tag)
        }
        override fun convert(helper: BaseViewHolder, item: MultiTagItem) {
            when (item.type) {
                MultiTagItem.TYPE_CATEGORY -> {
                    helper.setText(R.id.tv_label, item.tag)
                }
                MultiTagItem.TYPE_TAG -> {
                    helper.setText(R.id.btn_tag, item.tag)
                    helper.setOnClickListener(R.id.btn_tag) {
                        selectTag(item.tag)
                    }
                }
            }
        }

    }

    private fun initToolbar() {
        toolbar.title = "主题书单"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initTabCategory() {
        BookListType.values().forEach {
            tl_category.addTab(tl_category.newTab().setText(it.typeName))
        }
        tl_category.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewModel.currentCategory.postValue(BookListType.getNetName(it.text.toString()))
                }
            }

        })
    }
}
