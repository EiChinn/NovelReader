package com.example.newbiechen.ireader.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.ui.adapter.LeaderBoardBookAdapter
import com.example.newbiechen.ireader.viewmodel.InjectorUtils
import com.example.newbiechen.ireader.viewmodel.LeaderBoardBookViewModel
import com.example.newbiechen.ireader.widget.dialog.LoadingDialogHelper
import kotlinx.android.synthetic.main.activity_single_leader_board_book.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.toast

class SingleLeaderBoardBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_leader_board_book)
        val title = intent.getStringExtra("extra_bill_name")
        initToolbar(title)
        val viewModel = ViewModelProviders.of(this,
                InjectorUtils.provideLeaderBoardBookReposityFactory())
                .get(LeaderBoardBookViewModel::class.java)
        viewModel.id.value = intent.getStringExtra("extra_bill_id")
        viewModel.isRequestInProgress.observe(this, Observer {
            if (it) {
                LoadingDialogHelper.showLoadingDialog(this)
            } else {
                LoadingDialogHelper.closeLoadingDialog()
            }
        })
        viewModel.toastMsg.observe(this, Observer { toast(it) })
        viewModel.data.observe(this, Observer {
            if (rv_book.adapter == null) {
                rv_book.layoutManager = LinearLayoutManager(this)
                rv_book.adapter = LeaderBoardBookAdapter(it)
            } else {
                (rv_book.adapter as LeaderBoardBookAdapter).setNewData(it)
            }
        })
    }

    private fun initToolbar(title: String) {
        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}
