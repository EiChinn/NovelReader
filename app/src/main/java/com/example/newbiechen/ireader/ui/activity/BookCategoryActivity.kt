package com.example.newbiechen.ireader.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.ui.adapter.BookCategoryAdapter
import com.example.newbiechen.ireader.ui.adapter.BookCategoryMultiItemEntity
import com.example.newbiechen.ireader.viewmodel.BookCategoryViewModel
import com.example.newbiechen.ireader.viewmodel.InjectorUtils
import com.example.newbiechen.ireader.widget.dialog.LoadingDialogHelper
import kotlinx.android.synthetic.main.activity_book_category.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class BookCategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_category)
        val viewModel = ViewModelProviders.of(this,
                InjectorUtils.provideBookCategoryViewModelFactory())
                .get(BookCategoryViewModel::class.java)
        viewModel.bookCategory.observe(this, Observer { data ->
            if (rv_book_category.adapter == null) {
                rv_book_category.layoutManager = GridLayoutManager(this, 3)
                val adapter = BookCategoryAdapter(data)
                adapter.setSpanSizeLookup { _, i ->
                    return@setSpanSizeLookup if (data[i].itemType == BookCategoryMultiItemEntity.ITEM_LABEL) 3 else 1
                }
                adapter.setOnItemClickListener { _, _, position ->
                    startActivity<BookCategoryDetailActivity>(
                            "category" to adapter.data[position].category.name,
                            "gender" to  adapter.data[position].gender
                    )
                }
                rv_book_category.adapter = adapter
            } else {
                (rv_book_category.adapter as BookCategoryAdapter).setNewData(data)
            }
        })
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

    }
}
