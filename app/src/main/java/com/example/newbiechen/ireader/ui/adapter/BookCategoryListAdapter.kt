package com.example.newbiechen.ireader.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.SortBookBean
import com.example.newbiechen.ireader.ui.activity.BookDetailActivity
import com.example.newbiechen.ireader.utils.Constant

class BookCategoryListAdapter : PagedListAdapter<SortBookBean, RecyclerView.ViewHolder>(BOOK_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BookCategoryViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BookCategoryViewHolder).bind(getItem(position))
    }

    class BookCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val portraitIv = view.findViewById<ImageView>(R.id.book_brief_iv_portrait)
        private val titleTv = view.findViewById<TextView>(R.id.book_brief_tv_title)
        private val authorTv = view.findViewById<TextView>(R.id.book_brief_tv_author)
        private val shortInfoTv = view.findViewById<TextView>(R.id.book_brief_tv_brief)
        private val msgTv = view.findViewById<TextView>(R.id.book_brief_tv_msg)
        private var book: SortBookBean? = null

        init {
            view.setOnClickListener {
                book?.let {
                    BookDetailActivity.startActivity(view.context, it._id)
                }
            }
        }

        fun bind(book: SortBookBean?) {
            book?.let {
                this.book = it
                Glide.with(portraitIv.context).load(Constant.IMG_BASE_URL + book.cover)
                        .placeholder(R.drawable.ic_default_portrait)
                        .error(R.drawable.ic_load_error)
                        .fitCenter()
                        .into(portraitIv)
                titleTv.text = book.title
                authorTv.text = book.author
                shortInfoTv.text = book.shortIntro
                msgTv.text = "${book.latelyFollower}人在追 | ${book.retentionRatio}读者留存"
            }


        }

        companion object {
            fun create(parent: ViewGroup): BookCategoryViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book_brief, parent, false)
                return BookCategoryViewHolder(view)
            }
        }
    }

    companion object {
        val BOOK_COMPARATOR = object : DiffUtil.ItemCallback<SortBookBean>() {
            override fun areItemsTheSame(oldItem: SortBookBean, newItem: SortBookBean): Boolean {
                return oldItem._id == newItem._id
            }

            override fun areContentsTheSame(oldItem: SortBookBean, newItem: SortBookBean): Boolean {
                return oldItem._id == newItem._id
            }

        }
    }
}