package ru.zinoviewk.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.icu.util.Measure
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import androidx.annotation.BinderThread
import androidx.annotation.MainThread
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


private const val RADIUS = 50
private const val SWEEP_COEFFICIENT = 3.6f
private const val CHECK_MARK_OFFSET_X = 15f
private const val CHECK_MARK_OFFSET_Y = 15f

class ImageLoader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val circlePaint = Paint().apply {
        this.color = Color.BLUE
        this.style = Paint.Style.STROKE;
        this.strokeWidth = 5f;
    }

    private val rectF by lazy {
        val centerX = width / 2
        val centerY = height / 2

        RectF(
            (centerX - RADIUS).toFloat(),
            (centerY - RADIUS).toFloat(),
            (centerX + RADIUS).toFloat(),
            (centerY + RADIUS).toFloat()
        )
    }

    private var progress: Float = 100f
    private var isDownloaded = false

    private val checkMarkPath = Path().apply {
        val centerX = width / 2
        val centerY = height / 2

        val x0 = centerX.toFloat()
        val y0 = centerY.toFloat()
        var currX = x0 - RADIUS + 5
        var currY = y0 + 5
        this.moveTo(currX, currY)
        currX += RADIUS - 5
        currY += RADIUS - 10
        this.lineTo(currX, currY)
        currY = centerY - RADIUS.toFloat() + 10 * (RADIUS / 2f * PI / 180).toFloat()
        currX = centerX + RADIUS / 2.toFloat()
        this.lineTo(currX, currY)
    }

    private val failurePath = Path().apply {
        val centerX = width / 2
        val centerY = height / 2

        val x0 = centerX.toFloat()
        val y0 = centerY.toFloat()
        this.moveTo(centerX - 20f, centerX - RADIUS.toFloat())
        var currX = x0 - RADIUS + 5
        var currY = y0 + 5
        this.moveTo(currX, currY)
        currX += RADIUS - 5
        currY += RADIUS - 10
        this.lineTo(currX, currY)
        currY = centerY - RADIUS.toFloat() + 10 * (RADIUS / 2f * PI / 180).toFloat()
        currX = centerX + RADIUS / 2.toFloat()
        this.lineTo(currX, currY)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
    }

    @UiThread
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val path = Path().apply {
            val centerX = width / 2
            val centerY = height / 2

            val x0 = centerX.toFloat()
            val y0 = centerY.toFloat()
            this.moveTo(centerX - 40f, centerY - RADIUS + 40.toFloat())
            this.lineTo(centerX + 20f, centerY + RADIUS.toFloat())
        }
        canvas?.drawPath(path, circlePaint)
        updateProgress(canvas)
//        if(isDownloaded)
            //updateProgress(canvas)
//        else
//            canvas?.drawPath(checkMarkPath)
    }

    private fun updateProgress(canvas: Canvas?) {
        canvas!!.drawArc(
            rectF,
            -90f,
            progress * SWEEP_COEFFICIENT,
            false,
            circlePaint
        )
    }

    fun setProgress(progress: Float) {
        log("setProgress $progress")
        require(progress >= 0);
        isDownloaded = progress == 100f
        this.progress = progress
        invalidate()
    }
}