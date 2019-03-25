package com.example.newbiechen.ireader.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.ui.adapter.LeaderBoardBookAdapter
import com.example.newbiechen.ireader.viewmodel.InjectorUtils
import com.example.newbiechen.ireader.viewmodel.LeaderBoardBookViewModel
import com.example.newbiechen.ireader.widget.dialog.LoadingDialogHelper
import kotlinx.android.synthetic.main.fragment_leader_board.*
import org.jetbrains.anko.support.v4.toast

private const val LEADER_BOARD_ID = "leaderBoardId"

class LeaderBoardFragment : Fragment() {
    private var leaderBoardId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            leaderBoardId = it.getString(LEADER_BOARD_ID)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leader_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProviders.of(this,
                InjectorUtils.provideLeaderBoardBookReposityFactory())
                .get(LeaderBoardBookViewModel::class.java)
        viewModel.id.value = leaderBoardId
        viewModel.isRequestInProgress.observe(this, Observer {
            if (it) {
                LoadingDialogHelper.showLoadingDialog(context!!)
            } else {
                LoadingDialogHelper.closeLoadingDialog()
            }
        })
        viewModel.toastMsg.observe(this, Observer { toast(it) })

        viewModel.data.observe(this, Observer {
            if (rv_books.adapter == null) {
                rv_books.layoutManager = LinearLayoutManager(context)
                rv_books.adapter = LeaderBoardBookAdapter(it)
            } else {
                (rv_books.adapter as LeaderBoardBookAdapter).setNewData(it)
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param leaderBoardId
         * @return A new instance of fragment LeaderBoardFragment.
         */
        @JvmStatic
        fun newInstance(leaderBoardId: String) =
                LeaderBoardFragment().apply {
                    arguments = Bundle().apply {
                        putString(LEADER_BOARD_ID, leaderBoardId)
                    }
                }
    }
}
