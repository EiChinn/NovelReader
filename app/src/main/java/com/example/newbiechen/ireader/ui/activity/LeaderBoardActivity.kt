package com.example.newbiechen.ireader.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.ui.adapter.LeaderBoardAdapter
import com.example.newbiechen.ireader.viewmodel.InjectorUtils
import com.example.newbiechen.ireader.viewmodel.LeaderBoardViewModel
import com.example.newbiechen.ireader.widget.dialog.LoadingDialogHelper
import kotlinx.android.synthetic.main.activity_leader_board.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.toast

class LeaderBoardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_board)
        initToolbar()
        val viewModel = ViewModelProviders.of(this,
                InjectorUtils.provideLeaderBoardRepositoryFactory()).get(LeaderBoardViewModel::class.java)
        viewModel.dataWrapper.isRequestInProgress.observe(this, Observer {
            if (it) {
                LoadingDialogHelper.showLoadingDialog(this)
            } else {
                LoadingDialogHelper.closeLoadingDialog()
            }
        })
        viewModel.dataWrapper.toastMsg.observe(this, Observer { toast(it) })

        viewModel.dataWrapper.data.observe(this, Observer {
            if (rv_leader_board.adapter == null) {
                rv_leader_board.layoutManager = LinearLayoutManager(this)
                rv_leader_board.adapter = LeaderBoardAdapter(it) { bean ->
                    if (bean.isCollapse) {
                        OtherBillBookActivity.startActivity(this, bean.title, bean._id)
                    } else {
                        BillBookActivity.startActivity(this, bean._id, bean.monthRank, bean.totalRank)
                    }
                }
            } else {
                (rv_leader_board.adapter as LeaderBoardAdapter).setNewData(it)
            }
        })
    }

    private fun initToolbar() {
        toolbar.title = "排行榜"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

    }
}
