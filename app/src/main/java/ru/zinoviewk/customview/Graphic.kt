package ru.zinoviewk.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

private const val DIVIDER_SPACE_IN_PX = 40
private const val HALF_DIVIDER_HEIGHT = 7
private const val HALF_DIVIDER_WIDTH = 7
private const val DEFAULT_AMOUNT_OF_POINTS = 2000

private const val FUNCTION_NAME_BLOCK_X = 300
private const val FUNCTION_NAME_BLOCK_Y = 300

private class Border(
    val minX: Float = Float.MIN_VALUE,
    val maxX: Float = Float.MAX_VALUE,
    val minY: Float = Float.MIN_VALUE,
    val maxY: Float = Float.MAX_VALUE,
)

class Graphic @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val axisPaint = Paint().apply {
        this.color = Color.BLACK
        this.strokeWidth = 2.0f
    }

    private val rect = Rect()

    private val axisDividerPaint = Paint().apply {
        this.color = Color.BLACK
        this.strokeWidth = 1.0f
    }

    private val dividerTextPaint = Paint().apply {
        this.color = Color.BLACK
        this.strokeWidth = 3.0f
        this.textSize = 15f
    }

    private val functionPaint = Paint().apply {
        this.color = Color.RED
        this.strokeWidth = 5.0f
    }

    private val functionNamePaint = Paint().apply {
        this.strokeWidth = 2.0f
        this.textSize = 45f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        log("ondraw")
        // draw X axis
        canvas?.drawLine(
            0.0f,
            (height / 2).toFloat(),
            width.toFloat(),
            (height / 2).toFloat(),
            axisPaint
        )

        // draw Y axis
        canvas?.drawLine(
            (width / 2).toFloat(),
            0f,
            (width / 2).toFloat(),
            height.toFloat(),
            axisPaint
        )

        val centerX = width / 2

        // draw x dividers from center till the x = 0
        var currX = centerX - DIVIDER_SPACE_IN_PX
        while (currX >= 0) {
            canvas?.drawLine(
                currX.toFloat(),
                (height / 2 - HALF_DIVIDER_HEIGHT).toFloat(),
                currX.toFloat(),
                (height / 2 + HALF_DIVIDER_HEIGHT).toFloat(),
                axisDividerPaint
            )
            currX -= DIVIDER_SPACE_IN_PX
        }

        // draw x dividers from center till the x = width
        currX = centerX + DIVIDER_SPACE_IN_PX
        while (currX <= width) {
            canvas?.drawLine(
                currX.toFloat(),
                (height / 2 - HALF_DIVIDER_HEIGHT).toFloat(),
                currX.toFloat(),
                (height / 2 + HALF_DIVIDER_HEIGHT).toFloat(),
                axisDividerPaint
            )
            currX += DIVIDER_SPACE_IN_PX
        }

        val centerY = height / 2

        // draw y dividers from center till the y = 0
        var currY = centerY - DIVIDER_SPACE_IN_PX
        while (currY >= 0) {
            canvas?.drawLine(
                (width / 2 - HALF_DIVIDER_WIDTH).toFloat(),
                currY.toFloat(),
                (width / 2 + HALF_DIVIDER_WIDTH).toFloat(),
                currY.toFloat(),
                axisDividerPaint
            )
            currY -= DIVIDER_SPACE_IN_PX
        }

        // draw y dividers from center till the y = height
        currY = centerY + DIVIDER_SPACE_IN_PX
        while (currY <= height) {
            canvas?.drawLine(
                (width / 2 - HALF_DIVIDER_WIDTH).toFloat(),
                currY.toFloat(),
                (width / 2 + HALF_DIVIDER_WIDTH).toFloat(),
                currY.toFloat(),
                axisDividerPaint
            )
            currY += DIVIDER_SPACE_IN_PX
        }

        // divider numbers in range (width/2, 0)
        var currTextX = centerX - DIVIDER_SPACE_IN_PX
        var currValue = -1
        while (currTextX >= 0) {
            val text = currValue.toString()

            val textWidth = dividerTextPaint.measureText(text)
            val textHeight = rect.apply {
                dividerTextPaint.getTextBounds(currValue.toString(), 0, text.length, this)
            }.height()

            canvas?.drawText(
                text,
                (currTextX - textWidth / 2).toFloat(),
                (height / 2 + textHeight * 2).toFloat(),
                dividerTextPaint
            )
            currTextX -= DIVIDER_SPACE_IN_PX

            currValue--
        }

//        // divider numbers in range (width / 2, width)
        currTextX = centerX + DIVIDER_SPACE_IN_PX
        currValue = 1
        while (currTextX <= width) {
            val text = currValue.toString()

            val textWidth = dividerTextPaint.measureText(text)
            val textHeight = rect.apply {
                dividerTextPaint.getTextBounds(currValue.toString(), 0, text.length, this)
            }.height()

            canvas?.drawText(
                text,
                (currTextX - textWidth / 2).toFloat(),
                (height / 2 + textHeight * 2).toFloat(),
                dividerTextPaint
            )
            currTextX += DIVIDER_SPACE_IN_PX
            currValue++
        }

        // divider numbers form (height/2, 0)

        var currTextY = centerY - DIVIDER_SPACE_IN_PX
        currValue = 1
        var maxTextWidth = dividerTextPaint.measureText(currValue.toString())

        for (i in currTextY downTo 0) {
            maxTextWidth = max(maxTextWidth, dividerTextPaint.measureText(currValue.toString()))
            currValue++
        }

        currValue = 1

        while (currTextY >= 0) {
            val text = currValue.toString()

            val textWidth = dividerTextPaint.measureText(text)
            val textHeight = rect.apply {
                dividerTextPaint.getTextBounds(currValue.toString(), 0, text.length, this)
            }.height()

            var offsetFromCenterY = maxTextWidth
            if (textWidth == maxTextWidth)
                offsetFromCenterY += 10
            else offsetFromCenterY -= 5

            canvas?.drawText(
                text,
                (width / 2 - offsetFromCenterY).toFloat(),
                (currTextY + textHeight / 2).toFloat(),
                dividerTextPaint
            )
            currTextY -= DIVIDER_SPACE_IN_PX
            currValue++
        }

        // divider numbers form (height/2, height)

        currTextY = centerY + DIVIDER_SPACE_IN_PX
        currValue = -1
        maxTextWidth = dividerTextPaint.measureText(currValue.toString())

        for (i in currTextY downTo 0) {
            maxTextWidth = max(maxTextWidth, dividerTextPaint.measureText(currValue.toString()))
            currValue--
        }

        currValue = -1

        while (currTextY <= height) {
            val text = currValue.toString()

            val textWidth = dividerTextPaint.measureText(text)
            val textHeight = rect.apply {
                dividerTextPaint.getTextBounds(currValue.toString(), 0, text.length, this)
            }.height()

            var offsetFromCenterY = maxTextWidth
            if (textWidth == maxTextWidth)
                offsetFromCenterY += 10
            else offsetFromCenterY -= 5

            canvas?.drawText(
                text,
                (width / 2 - offsetFromCenterY).toFloat(),
                (currTextY + textHeight / 2).toFloat(),
                dividerTextPaint
            )
            currTextY += DIVIDER_SPACE_IN_PX
            currValue--
        }

        drawFunction(
            canvas,
            color = Color.RED,
            functionName = "ax^2",
            function = ::parabolY
        )
        drawFunction(
            canvas,
            color = Color.BLACK,
            functionName = "sin(5x)",
            function = ::sinY
        )
        drawFunction(
            canvas,
            color = Color.BLUE,
            countOfPoints = 200_000,
            functionName = "tan(10x)",
            border = tanBorder,
            function = ::tanY
        )
        drawFunction(
            canvas,
            color = Color.GREEN,
            functionName = "cos(0.3x) - 5",
            function = ::cosY
        )
    }

    private val tanBorder = Border(
        minX = -13f,
        maxX = 13f,
        minY = -10f,
        maxY = 10f,
    )

    private val a = 1
    private val b = 0
    private fun parabolY(x: Float): Float {
        return a * x * x + b
    }

    private fun sinY(x: Float): Float {
        return sin(5 * x)
    }

    private fun tanY(x: Float): Float {
        return kotlin.math.tan(10 * x)
    }

    private fun cosY(x: Float): Float {
        return (cos(0.3 * x) - 5).toFloat()
    }

    private fun drawFunction(
        canvas: Canvas?,
        color: Int,
        countOfPoints: Int = DEFAULT_AMOUNT_OF_POINTS,
        functionName: String,
        border: Border = Border(),
        function: (Float) -> Float,
    ) {
        functionPaint.color = color
        var currX = -20f
        val x = 20f
        val step = (x - currX) / countOfPoints

        while (currX <= x) {
            val y = function(currX)
            val realX = width / 2 + (currX * DIVIDER_SPACE_IN_PX)
            val realY = height / 2 - (y * DIVIDER_SPACE_IN_PX)

            // check if parameters is not bound of border
            log("($x, $y)")
            if (currX < border.minX || currX > border.maxX || y < border.minY || y > border.maxY) {
                currX += step
                continue
            }

            if (realX >= width / 2 + 10) { // the first coordinate quarter + 10 offset
                if (realY >= FUNCTION_NAME_BLOCK_Y) {
                    // if does not intersect the name function area
                    canvas?.drawPoint(realX, realY, functionPaint)
                }
            } else {
                canvas?.drawPoint(realX, realY, functionPaint)
            }
            currX += step
        }

        drawFunctionName(canvas, functionName, color)
    }

    private var functionNameY = -1

    private fun drawFunctionName(canvas: Canvas?, name: String, color: Int) {
        val text = "y = $name";

        functionNamePaint.color = color
        val functionNameWidth = functionNamePaint.measureText(text)
        val functionNameHeight = rect.apply {
            functionNamePaint.getTextBounds(text, 0, text.length, this)
        }.height()

        val functionNameX = width / 2 + 20

        // init for the first time
        if (functionNameY == -1) functionNameY = functionNameHeight

        // Check if fun name can be placed to function name block by X
        val isFitByX = (width - 10) - (functionNameX + functionNameWidth) >= 0

        // Check if fun name can be placed to function name block by Y
        val isFitByY = FUNCTION_NAME_BLOCK_Y - (functionNameY + functionNameHeight) >= 0
        if (isFitByX && isFitByY) {
            canvas?.drawText(
                text,
                functionNameX.toFloat(),
                functionNameY.toFloat(),
                functionNamePaint
            )
            functionNameY += functionNameHeight
        }
    }

}