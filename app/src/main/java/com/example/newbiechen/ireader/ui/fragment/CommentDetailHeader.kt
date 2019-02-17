package com.example.newbiechen.ireader.ui.fragment

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bumptech.glide.Glide
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.CommentBean
import com.example.newbiechen.ireader.model.bean.CommentDetailBean
import com.example.newbiechen.ireader.ui.adapter.CommentAdapter
import com.example.newbiechen.ireader.ui.adapter.GodCommentAdapter
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.StringUtils
import com.example.newbiechen.ireader.widget.BookTextView
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import com.example.newbiechen.ireader.widget.transform.CircleTransform

class CommentDetailHeader(val context: Context, private val commentAdapter: CommentAdapter) : WholeAdapter.ItemView {
    @BindView(R.id.disc_detail_iv_portrait)
    @JvmField var ivPortrait: ImageView? = null
    @BindView(R.id.disc_detail_tv_name)
    @JvmField var tvName: TextView? = null
    @BindView(R.id.disc_detail_tv_time)
    @JvmField var tvTime: TextView? = null
    @BindView(R.id.disc_detail_tv_title)
    @JvmField var tvTitle: TextView? = null
    @BindView(R.id.disc_detail_btv_content)
    @JvmField var btvContent: BookTextView? = null
    @BindView(R.id.disc_detail_tv_best_comment)
    @JvmField var tvBestComment: TextView? = null
    @BindView(R.id.disc_detail_rv_best_comments)
    @JvmField var rvBestComments: RecyclerView? = null
    @BindView(R.id.disc_detail_tv_comment_count)
    @JvmField var tvCommentCount: TextView? = null

    var godCommentAdapter: GodCommentAdapter? = null
    var commentDetailBean: CommentDetailBean? = null
    var godCommentList: List<CommentBean>? = null
    var detailUnbinder: Unbinder? = null
    override fun onCreateView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context)
                .inflate(R.layout.header_disc_detail, parent, false)
    }

    override fun onBindView(view: View) {
        if (detailUnbinder == null) {
            detailUnbinder = ButterKnife.bind(this, view)
        }
        //如果没有值就直接返回
        if (commentDetailBean == null || godCommentList == null) {
            return
        }
        //头像
        Glide.with(context)
                .load(Constant.IMG_BASE_URL + commentDetailBean!!.author.avatar)
                .placeholder(R.drawable.ic_loadding)
                .error(R.drawable.ic_load_error)
                .transform(CircleTransform(context!!))
                .into(ivPortrait!!)
        //名字
        tvName!!.text = commentDetailBean!!.author.nickname
        //日期
        tvTime!!.text = StringUtils.dateConvert(commentDetailBean!!.created, Constant.FORMAT_BOOK_DATE)
        //标题
        tvTitle!!.text = commentDetailBean!!.title
        //内容
        btvContent!!.text = commentDetailBean!!.content
        //设置书籍的点击事件
        btvContent!!.setOnBookClickListener(object : BookTextView.OnBookClickListener {
            override fun onBookClick(bookName: String) {
                Log.d("CommentDetailFragment", "onBindView: $bookName")
            }
        })
        //设置神评论
        if (godCommentList!!.isEmpty()) {
            tvBestComment!!.visibility = View.GONE
            rvBestComments!!.visibility = View.GONE
        } else {
            tvBestComment!!.visibility = View.VISIBLE
            rvBestComments!!.visibility = View.VISIBLE
            //初始化RecyclerView
            initRecyclerView()
            godCommentAdapter!!.refreshItems(godCommentList!!)
        }

        if (commentAdapter.getItems().isEmpty()) {
            tvCommentCount!!.text = context.resources.getString(R.string.nb_comment_empty_comment)
        } else {
            val (_, _, _, floor) = commentAdapter.getItems()[0]
            //评论数
            tvCommentCount!!.text = context.resources
                    .getString(R.string.nb_comment_comment_count, floor)
        }
    }

    private fun initRecyclerView() {
        if (godCommentAdapter != null) return
        godCommentAdapter = GodCommentAdapter()
        rvBestComments!!.layoutManager = LinearLayoutManager(context)
        rvBestComments!!.addItemDecoration(DividerItemDecoration(context!!))
        rvBestComments!!.adapter = godCommentAdapter
    }

    fun setCommentDetail(bean: CommentDetailBean) {
        commentDetailBean = bean
    }
}