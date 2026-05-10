package com.duc.offlinemusicplayer.presentation.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.duc.offlinemusicplayer.R

class PillTabIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var totalTabs: Int = 3
        set(value) {
            field = value
            invalidate()
        }

    var currentTab: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.neutral_700)
    }

    private val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.primary_light)
    }

    private val dotRadius = dpToPx(4f)
    private val pillHeight = dpToPx(8f)
    private val pillWidth = dpToPx(32f)
    private val spacing = dpToPx(4f)
    private val dotRect = RectF()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = ((totalTabs - 1) * (dotRadius * 2 + spacing) + pillWidth).toInt()
        val height = pillHeight.toInt()
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var startX = 0f

        for (i in 0 until totalTabs) {
            if (i == currentTab) {
                dotRect.set(startX, 0f, startX + pillWidth, pillHeight)
                canvas.drawRoundRect(dotRect, pillHeight / 2, pillHeight / 2, pillPaint)
                startX += pillWidth + spacing
            } else {
                val cx = startX + dotRadius
                val cy = pillHeight / 2
                canvas.drawCircle(cx, cy, dotRadius, dotPaint)
                startX += dotRadius * 2 + spacing
            }
        }
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
}
