package com.example.newbiechen.ireader.widget

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.example.newbiechen.ireader.R

/**
 * Created by newbiechen on 17-4-29.
 * 1. 找到改写TextView内容的方法:是哪个 0 0，个人猜测是setText()
 * 2. 找到文章中存在《》的位置:
 * 3. 设置ForeSpan
 * 4. 添加点击事件
 */
class BookTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatTextView(context, attrs, defStyleAttr) {
    private var mBookListener: OnBookClickListener? = null
    private var bookColor = 0

    init {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BookTextView)
        bookColor = typedArray.getColor(R.styleable.BookTextView_bookColor, ContextCompat.getColor(getContext(), R.color.light_coffee))
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        val value = text.toString()
        var indexStart = 0
        var indexEnd = 0

        indexStart = value.indexOf("《", indexStart)
        indexEnd = value.indexOf("》", indexEnd)
        val builder = SpannableStringBuilder(value)
        while (indexStart != -1 || indexEnd != -1) {
            val start = indexStart + 1
            val end = indexEnd
            builder.setSpan(ForegroundColorSpan(bookColor), indexStart, indexEnd + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            builder.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    mBookListener?.let {
                        val bookName = value.substring(start, end)
                        it.onBookClick(bookName)
                    }
                }
            }, indexStart, indexEnd + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            indexStart = value.indexOf("《", indexStart + 1)
            indexEnd = value.indexOf("》", indexEnd + 1)
        }
        this.movementMethod = LinkMovementMethod.getInstance()
        super.setText(text, type)
    }

    fun setOnBookClickListener(listener: OnBookClickListener) {
        mBookListener = listener
    }

    interface OnBookClickListener {
        fun onBookClick(bookName: String)
    }
}