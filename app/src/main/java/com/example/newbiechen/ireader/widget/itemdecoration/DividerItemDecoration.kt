package com.example.newbiechen.ireader.widget.itemdecoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    companion object {
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
        private const val VERTICAL_LIST = LinearLayoutManager.VERTICAL
    }

    private val mDrawable: Drawable?

    init {
        val a = context.obtainStyledAttributes(ATTRS)
        mDrawable = a.getDrawable(0)
        a.recycle()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView) {
        if (getLayoutManagerType(parent) == VERTICAL_LIST) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    private fun getLayoutManagerType(parent: RecyclerView): Int {
        val manager = parent.layoutManager as? LinearLayoutManager
                ?: throw IllegalArgumentException("only supply linearLayoutManager")

        return manager.orientation
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                    .layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + (mDrawable?.intrinsicHeight ?: 0)
            mDrawable?.setBounds(left, top, right, bottom)
            mDrawable?.draw(c)
        }
    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop
        val bottom = parent.height - parent.paddingBottom

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                    .layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            val right = left + (mDrawable?.intrinsicHeight ?: 0)
            mDrawable?.setBounds(left, top, right, bottom)
            mDrawable?.draw(c)
        }
    }

    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        if (getLayoutManagerType(parent) == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mDrawable?.intrinsicHeight ?: 0)
        } else {
            outRect.set(0, 0, mDrawable?.intrinsicWidth ?: 0, 0)
        }
    }
}