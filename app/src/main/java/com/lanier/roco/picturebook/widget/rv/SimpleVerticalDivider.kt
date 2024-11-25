package com.lanier.roco.picturebook.widget.rv

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Desc:
 * Author:  幻弦让叶
 * Date:    2024/11/26 01:54
 */
class SimpleVerticalDivider(
    private val color: Int,
    private val thickness: Int,
) : RecyclerView.ItemDecoration() {

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        this.color = this@SimpleVerticalDivider.color
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val itemCount = parent.adapter?.itemCount ?: 0

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            if (position == RecyclerView.NO_POSITION || position == itemCount - 1) continue

            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + thickness

            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        if (position != RecyclerView.NO_POSITION && position < itemCount - 1) {
            outRect.bottom = thickness
        }
    }
}