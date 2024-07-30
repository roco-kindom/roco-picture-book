package com.lanier.roco.picturebook.widget.rv

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class EqualDivider(
    private val divider: Drawable,
    private val spanCount: Int
) : RecyclerView.ItemDecoration() {

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawHorizontalDividers(c, parent)
        drawVerticalDividers(c, parent)
    }

    private fun drawHorizontalDividers(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.left - params.leftMargin
            val right = child.right + params.rightMargin
            val top = child.bottom + params.bottomMargin
            val bottom = top + divider.intrinsicHeight
            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }

    private fun drawVerticalDividers(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.top - params.topMargin
            val bottom = child.bottom + params.bottomMargin
            val left = child.right + params.rightMargin
            val right = left + divider.intrinsicWidth
            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        outRect.left = column * divider.intrinsicWidth / spanCount
        outRect.right = divider.intrinsicWidth - (column + 1) * divider.intrinsicWidth / spanCount
        if (position >= spanCount) {
            outRect.top = divider.intrinsicHeight
        } else {
            outRect.top = 0
        }
        outRect.bottom = 0
    }
}