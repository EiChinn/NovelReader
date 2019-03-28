package com.example.newbiechen.ireader.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.ui.fragment.LeaderBoardFragment
import kotlinx.android.synthetic.main.activity_multi_leader_board_book.*
import kotlinx.android.synthetic.main.toolbar.*

class MultiLeaderBoardBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_leader_board_book)
        initToolbar(intent.getStringExtra("title"))
        initTab()


    }

    private fun initToolbar(title: String) {
        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initTab() {
        val titles = arrayOf("周榜", "月榜", "总榜")
        val fragments = arrayOf(
                LeaderBoardFragment.newInstance(intent.getStringExtra("weekId")),
                LeaderBoardFragment.newInstance(intent.getStringExtra("monthId")),
                LeaderBoardFragment.newInstance(intent.getStringExtra("totalId"))
        )
        vp_leader_board.offscreenPageLimit = 2
        vp_leader_board.adapter = LeaderBoardTabAdapter(supportFragmentManager, fragments, titles)
        tl_leader_board.setupWithViewPager(vp_leader_board)

    }

    class LeaderBoardTabAdapter(fm: FragmentManager, private val fragments: Array<out Fragment>, private val titles: Array<String>) : FragmentPagerAdapter(fm) {
        override fun getCount() = fragments.size

        override fun getItem(position: Int) = fragments[position]

        override fun getPageTitle(position: Int) = titles[position]

    }
}
