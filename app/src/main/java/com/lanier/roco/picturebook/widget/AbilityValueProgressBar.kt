package com.lanier.roco.picturebook.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class AbilityValueProgressBar @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attributeSet, defStyleAttr) {

    private val backgroundPaint = Paint().apply {
        color = Color.rgb(180, 180, 180)
    }
    private val progressPaint = Paint()

    private var animator: ValueAnimator? = null

    var maxProgress = 200
        set(value) {
            if (value < 100) {
                field = 100
                return
            }
            field = value
        }

    private var mProgress: Float = 0f

    var progress: Int = 0
        set(value) {
            field = value
            animateProgress(value)
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        progressPaint.strokeWidth = max(h / 2f, 24f)
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        canvas.drawLine(0f, height / 2f, width.toFloat(), height / 2f, backgroundPaint)
        canvas.drawLine(0f, height / 2f, mProgress, height / 2f, progressPaint)
    }

    fun modifyProgressPaint(paint: Paint, reDraw: Boolean = true) {
        progressPaint.set(paint)
        if (reDraw.not()) return
//        invalidate()
        animateProgress(progress)
    }

    fun modifyBackgroundPaint(paint: Paint, reDraw: Boolean = true) {
        backgroundPaint.set(paint)
        if (reDraw.not()) return
//        invalidate()
        animateProgress(progress)
    }

    private fun animateProgress(toProgress: Int) {
        animator?.cancel()
        val target = (toProgress * 1f / maxProgress) * width
        animator = ValueAnimator.ofFloat(mProgress, target).apply {
            duration = 1000
            addUpdateListener { animation ->
                mProgress = animation.animatedValue as Float
                invalidate()
            }
            postDelayed({ start() }, 300)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
        animator = null
    }
}