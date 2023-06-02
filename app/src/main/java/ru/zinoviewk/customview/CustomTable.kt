package ru.zinoviewk.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

private const val HORIZONTAL_MARGIN = 10
private const val VERTICAL_MARGIN = 10


class CustomTable @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private val columns = listOf(
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday",
    )

    private val paint = Paint().apply {
        this.color = Color.GRAY
    }

    private val cellTextPaint = Paint().apply {
        this.color = Color.BLACK
        this.textSize = 25f
    }

    private val textHeightRect = Rect()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val y0 = 100f
        var prevCellX = 0f
        columns.forEachIndexed { index, column ->
            val tWidth = cellTextPaint.measureText(column)
            val tHeight = textHeightRect.apply {
                cellTextPaint.getTextBounds(column, 0, column.lastIndex, textHeightRect)
            }.height()
            val endX = prevCellX + tWidth + 2 * HORIZONTAL_MARGIN;
            canvas?.drawLine(prevCellX, y0, endX, y0, paint)
            canvas?.drawText(column, prevCellX + HORIZONTAL_MARGIN, y0 + tHeight + VERTICAL_MARGIN, cellTextPaint)
            canvas?.drawLine(endX, y0, endX, y0 + (columns.size * 20), paint)
            prevCellX = endX
        }


        invalidate()
    }
}