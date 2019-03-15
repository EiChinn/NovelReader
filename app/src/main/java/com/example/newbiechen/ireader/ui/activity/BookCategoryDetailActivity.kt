package com.example.newbiechen.ireader.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.flag.BookSortListType
import com.example.newbiechen.ireader.ui.adapter.BookCategoryDetailAdapter
import com.example.newbiechen.ireader.viewmodel.BookCategoryDetailViewModel
import com.example.newbiechen.ireader.viewmodel.InjectorUtils
import com.example.newbiechen.ireader.widget.dialog.LoadingDialogHelper
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_book_category_detail.*
import org.jetbrains.anko.toast

class BookCategoryDetailActivity : AppCompatActivity() {
    private lateinit var viewModel: BookCategoryDetailViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_category_detail)
        viewModel = ViewModelProviders.of(this,
                InjectorUtils.provideBookCategoryDetailViewModelFactory())
                .get(BookCategoryDetailViewModel::class.java)

        rv_books.layoutManager = LinearLayoutManager(this)
        rv_books.adapter = BookCategoryDetailAdapter(this, null)
        viewModel.books.observe(this, Observer {
            Log.i("tag", "------------------------------------------------------------------")
            (rv_books.adapter as BookCategoryDetailAdapter).setNewData(it)
        })

        for (type in BookSortListType.values()) {
            tab_tl_indicator.addTab(tab_tl_indicator.newTab().setText(type.typeName))
        }

        tab_tl_indicator.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.currentType.postValue(tab.text.toString())
            }

        })
        viewModel.currentType.value = BookSortListType.HOT.netName
        viewModel.major = intent.getStringExtra("category")
        viewModel.gender = intent.getStringExtra("gender")
        viewModel.fetchMins()
        viewModel.isRequestInProgress.observe(this, Observer {
            if (it) {
                LoadingDialogHelper.showLoadingDialog(this)
            } else {
                LoadingDialogHelper.closeLoadingDialog()
            }
        })
        viewModel.toastMsg.observe(this, Observer {
            toast(it)
        })

        viewModel.mins.observe(this, Observer {
            if (it.isNotEmpty()) {
                tl_category.removeAllTabs()
                it.forEach { category ->
                    tl_category.addTab(tl_category.newTab().setText(category))
                }
                tl_category.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabReselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                    }

                    override fun onTabSelected(tab: TabLayout.Tab) {
                        viewModel.currentMinor.postValue(tab.text.toString())
                    }

                })

                viewModel.currentMinor.postValue("")
            }

        })

        viewModel.currentType.observe(this, Observer {
            viewModel.fetBooks()
        })
        viewModel.currentMinor.observe(this, Observer {
            viewModel.fetBooks()
        })



    }
}
