package com.example.newbiechen.ireader.widget.itemdecoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class DividerGridItemDecoration(context: Context, @DrawableRes widthDividerRes: Int,
                                @DrawableRes heightDividerRes: Int) : RecyclerView.ItemDecoration() {
    private val mWidthDivider = context.getDrawable(widthDividerRes)
    private val mHeightDivider = context.getDrawable(heightDividerRes)

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawHorizontal(c, parent)
        drawVertical(c, parent)
    }

    private fun getSpanCount(parent: RecyclerView): Int {
        // 列数
        var spanCount = -1
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            spanCount = layoutManager.spanCount
        } else if (layoutManager is StaggeredGridLayoutManager) {
            spanCount = layoutManager
                    .spanCount
        }
        return spanCount
    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                    .layoutParams as RecyclerView.LayoutParams
            val left = child.left - params.leftMargin
            val right = (child.right + params.rightMargin
                    + mWidthDivider.intrinsicWidth)
            val top = child.bottom + params.bottomMargin
            val bottom = top + mWidthDivider.intrinsicHeight
            mWidthDivider.setBounds(left, top, right, bottom)
            mWidthDivider.draw(c)
        }
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child
                    .layoutParams as RecyclerView.LayoutParams
            val top = child.top - params.topMargin
            val bottom = child.bottom + params.bottomMargin
            val left = child.right + params.rightMargin
            val right = left + mHeightDivider.intrinsicWidth

            mHeightDivider.setBounds(left, top, right, bottom)
            mHeightDivider.draw(c)
        }
    }

    private fun isLastColumn(parent: RecyclerView, pos: Int, spanCount: Int, childCount: Int): Boolean {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) {
                return true
            }
        } else if (layoutManager is StaggeredGridLayoutManager) {

            val orientation = layoutManager
                    .orientation
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // 如果是最后一列，则不需要绘制右边
                if ((pos + 1) % spanCount == 0) {
                    return true
                }
            } else {
                val newChildCount = childCount - childCount % spanCount
                // 如果是最后一列，则不需要绘制右边
                if (pos >= newChildCount)
                    return true
            }
        }
        return false
    }

    private fun isLastRaw(parent: RecyclerView, pos: Int, spanCount: Int, childCount: Int): Boolean {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            val newChildCount = childCount - childCount % spanCount
            if (pos >= newChildCount)
            // 如果是最后一行，则不需要绘制底部
                return true
        } else if (layoutManager is StaggeredGridLayoutManager) {

            val orientation = layoutManager
                    .orientation
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                val newChildCount = childCount - childCount % spanCount
                // 如果是最后一行，则不需要绘制底部
                if (pos >= newChildCount)
                    return true
            } else {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true
                }
            }
        }
        return false
    }

    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        val spanCount = getSpanCount(parent)
        val childCount = parent.adapter!!.itemCount
        // 如果是最后一行，则不需要绘制底部
        when {
            isLastRaw(parent, itemPosition, spanCount, childCount) -> outRect.set(0, 0, mHeightDivider.intrinsicWidth, 0)
            isLastColumn(parent, itemPosition, spanCount, childCount) -> outRect.set(0, 0, 0, mWidthDivider.intrinsicHeight)
            else -> outRect.set(0, 0, mHeightDivider.intrinsicWidth,
                    mWidthDivider.intrinsicHeight)
        }// 如果是最后一列，则不需要绘制右边
    }
}