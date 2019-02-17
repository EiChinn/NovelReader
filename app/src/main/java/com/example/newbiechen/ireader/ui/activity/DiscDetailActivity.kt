package com.example.newbiechen.ireader.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.flag.CommunityType
import com.example.newbiechen.ireader.ui.base.BaseActivity
import com.example.newbiechen.ireader.ui.fragment.CommentDetailFragment
import com.example.newbiechen.ireader.ui.fragment.HelpsDetailFragment
import com.example.newbiechen.ireader.ui.fragment.ReviewDetailFragment

/**
 * Created by newbiechen on 17-4-22.
 */

class DiscDetailActivity : BaseActivity() {
    private var mCommentType: CommunityType? = null
    private var mDetailId: String? = null

    override fun getContentId(): Int {
        return R.layout.activity_discussion_detail
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        if (savedInstanceState != null) {
            mCommentType = savedInstanceState.getSerializable(EXTRA_COMMENT_TYPE) as CommunityType
            mDetailId = savedInstanceState.getString(EXTRA_DETAIL_ID)
        } else {
            mCommentType = intent.getSerializableExtra(EXTRA_COMMENT_TYPE) as CommunityType
            mDetailId = intent.getStringExtra(EXTRA_DETAIL_ID)
        }
    }

    override fun setUpToolbar(toolbar: Toolbar) {
        super.setUpToolbar(toolbar)
        supportActionBar!!.title = "详情"
    }

    override fun processLogic() {
        super.processLogic()
        var fragment: Fragment? = null
        when (mCommentType) {
            CommunityType.REVIEW -> fragment = ReviewDetailFragment.newInstance(mDetailId!!)
            CommunityType.HELP ->
                //因为HELP中的布局内容完全一致，所以就直接用了。
                fragment = HelpsDetailFragment.newInstance(mDetailId!!)
            else -> fragment = CommentDetailFragment.newInstance(mDetailId!!)
        }
        supportFragmentManager.beginTransaction()
                .add(R.id.discussion_detail_fl, fragment)
                .commit()
    }

    /*****************************lifecycler */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(EXTRA_COMMENT_TYPE, mCommentType)
        outState.putString(EXTRA_DETAIL_ID, mDetailId)
    }

    companion object {

        private const val EXTRA_COMMENT_TYPE = "extra_comment_type"
        private const val EXTRA_DETAIL_ID = "extra_detail_id"
        fun startActivity(context: Context, communityType: CommunityType,
                          detailId: String) {
            val intent = Intent(context, DiscDetailActivity::class.java)
            intent.putExtra(EXTRA_COMMENT_TYPE, communityType)
            intent.putExtra(EXTRA_DETAIL_ID, detailId)
            context.startActivity(intent)
        }
    }
}
