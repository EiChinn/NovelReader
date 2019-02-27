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
import com.bumptech.glide.Glide
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.CommentBean
import com.example.newbiechen.ireader.model.bean.ReviewDetailBean
import com.example.newbiechen.ireader.ui.adapter.CommentAdapter
import com.example.newbiechen.ireader.ui.adapter.GodCommentAdapter
import com.example.newbiechen.ireader.utils.Constant
import com.example.newbiechen.ireader.utils.StringUtils
import com.example.newbiechen.ireader.widget.BookTextView
import com.example.newbiechen.ireader.widget.EasyRatingBar
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration
import com.example.newbiechen.ireader.widget.transform.CircleTransform

class ReviewDetailHeader(val context: Context, private val commentAdapter: CommentAdapter) : WholeAdapter.ItemView {
    @JvmField var ivPortrait: ImageView? = null
    @JvmField var tvName: TextView? = null
    @JvmField var tvTime: TextView? = null
    @JvmField var tvTitle: TextView? = null
    @JvmField var btvContent: BookTextView? = null
    @JvmField var ivBookCover: ImageView? = null
    @JvmField var tvBookName: TextView? = null
    @JvmField var erbBookRate: EasyRatingBar? = null
    @JvmField var tvHelpfulCount: TextView? = null
    @JvmField var tvUnhelpfulCount: TextView? = null

    @JvmField var tvBestComment: TextView? = null
    @JvmField var rvBestComments: RecyclerView? = null
    @JvmField var tvCommentCount: TextView? = null

    var godCommentAdapter: GodCommentAdapter? = null
    var reviewDetailBean: ReviewDetailBean? = null
    var godCommentList: List<CommentBean>? = null
    override fun onCreateView(parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.header_disc_review_detail, parent, false)
        ivPortrait = view.findViewById(R.id.disc_detail_iv_portrait)
        tvName = view.findViewById(R.id.disc_detail_tv_name)
        tvTime = view.findViewById(R.id.disc_detail_tv_time)
        tvTitle = view.findViewById(R.id.disc_detail_tv_title)
        btvContent = view.findViewById(R.id.disc_detail_btv_content)
        ivBookCover = view.findViewById(R.id.review_detail_iv_book_cover)
        tvBookName = view.findViewById(R.id.review_detail_tv_book_name)
        erbBookRate = view.findViewById(R.id.review_detail_erb_rate)
        tvHelpfulCount = view.findViewById(R.id.review_detail_tv_helpful_count)
        tvUnhelpfulCount = view.findViewById(R.id.review_detail_tv_unhelpful_count)
        tvBestComment = view.findViewById(R.id.disc_detail_tv_best_comment)
        rvBestComments = view.findViewById(R.id.disc_detail_rv_best_comments)
        tvCommentCount = view.findViewById(R.id.disc_detail_tv_comment_count)
        return view
    }

    override fun onBindView(view: View) {
        //如果没有值就直接返回
        if (reviewDetailBean == null || godCommentList == null) {
            return
        }

        val authorBean = reviewDetailBean!!.author
        //头像
        Glide.with(context)
                .load(Constant.IMG_BASE_URL + authorBean.avatar)
                .placeholder(R.drawable.ic_loadding)
                .error(R.drawable.ic_load_error)
                .transform(CircleTransform(context!!))
                .into(ivPortrait!!)
        //名字
        tvName!!.text = authorBean.nickname
        //日期
        tvTime!!.text = StringUtils.dateConvert(reviewDetailBean!!.created,
                Constant.FORMAT_BOOK_DATE)
        //标题
        tvTitle!!.text = reviewDetailBean!!.title
        //内容
        btvContent!!.text = reviewDetailBean!!.content
        //设置书籍的点击事件
        btvContent!!.setOnBookClickListener(object : BookTextView.OnBookClickListener {
            override fun onBookClick(bookName: String) {
                Log.d("ReviewDetailFragment", "onBindView: $bookName")
            }
        })
        val bookBean = reviewDetailBean!!.book
        //书籍封面
        Glide.with(context)
                .load(Constant.IMG_BASE_URL + bookBean.cover)
                .placeholder(R.drawable.ic_book_loading)
                .error(R.drawable.ic_load_error)
                .fitCenter()
                .into(ivBookCover!!)
        //书名
        tvBookName!!.text = bookBean.title
        //对书的打分
        erbBookRate!!.setRating(reviewDetailBean!!.rating)
        //帮助度
        val bookHelpfulBean = reviewDetailBean!!.helpful
        //有用
        tvHelpfulCount!!.text = bookHelpfulBean.yes.toString() + ""
        //没用
        tvUnhelpfulCount!!.text = bookHelpfulBean.no.toString() + ""
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

        //设置评论数
        if (commentAdapter.getItems().isEmpty()) {
            tvCommentCount!!.text = context.resources.getString(R.string.nb_comment_empty_comment)
        } else {
            val (_, _, _, floor) = commentAdapter.getItems()[0]
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

    fun setCommentDetail(bean: ReviewDetailBean) {
        reviewDetailBean = bean
    }
}