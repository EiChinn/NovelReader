package com.example.newbiechen.ireader.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.flag.BookSortListType
import com.example.newbiechen.ireader.ui.base.BaseTabActivity
import com.example.newbiechen.ireader.ui.fragment.BookSortListFragment
import com.example.newbiechen.ireader.viewmodel.BookCategoryDetailViewModel
import com.example.newbiechen.ireader.viewmodel.InjectorUtils
import java.util.*

class BookCategoryDetailActivity : BaseTabActivity() {
    private lateinit var viewModel: BookCategoryDetailViewModel
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        viewModel = ViewModelProviders.of(this,
                InjectorUtils.provideBookCategoryDetailViewModelFactory())
                .get(BookCategoryDetailViewModel::class.java)
        viewModel.major = intent.getStringExtra("category")
        viewModel.gender = intent.getStringExtra("gender")

    }
    override fun createTabFragments(): MutableList<Fragment> {
        val fragments = ArrayList<Fragment>()
        for (type in BookSortListType.values()) {
            fragments.add(BookSortListFragment.newInstance(viewModel.gender, viewModel.major, type))
        }
        return fragments
    }

    override fun createTabTitles(): MutableList<String> {
        val titles = ArrayList<String>()
        for (type in BookSortListType.values()) {
            titles.add(type.typeName)
        }
        return titles
    }

    override fun getContentId() = R.layout.activity_book_category_detail

}
