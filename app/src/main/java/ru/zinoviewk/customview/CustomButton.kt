package ru.zinoviewk.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

private const val BUTTON_WIDTH = 200
private const val BUTTON_HEIGHT = 100

class CustomButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val buttonPaint = Paint().apply {
        this.color = Color.BLUE
    }

    private val buttonTextPaint = Paint().apply {
        this.color = Color.WHITE
        this.textSize = 25f
        this.typeface = Typeface.DEFAULT_BOLD
    }

    private val buttonRect = Rect()
    private val buttonTextRect = Rect()
    private var block: (() -> Unit)? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (buttonRect.isEmpty) {
            val leftX = (width / 2 - BUTTON_WIDTH / 2)
            val topY = (height / 2 - BUTTON_HEIGHT / 2)
            val rightX = (width / 2 + BUTTON_WIDTH / 2)
            val bottomY = (height / 2 + BUTTON_HEIGHT / 2)
            buttonRect.apply {
                this.left = leftX
                this.top = topY
                this.right = rightX
                this.bottom = bottomY
            }
        }

        canvas?.drawRect(buttonRect, buttonPaint)
        drawButtonText(canvas, "Tap me");
    }

    fun onClickListener(block: () -> Unit) {
        this.block = block
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {

            MotionEvent.ACTION_UP -> {
//                val x = event.x
//                val y = event.y
//                if (x >= buttonRect.left && x <= buttonRect.right && y >= buttonRect.top && y <= buttonRect.bottom) {
//                    this.block?.invoke()
//                    return true;
//                }
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val y = event.y
                if ((x >= buttonRect.left && x <= buttonRect.right && y >= buttonRect.top && y <= buttonRect.bottom) || true) {
                    updateButtonPosition(x, y)
                    return true;
                }
            }
        }

        return true;
    }

    private fun updateButtonPosition(x: Float, y: Float) {

        val dx = BUTTON_WIDTH - (buttonRect.right - x)
        log("($x, $dx), diff - ${dx}")
        //val dy = BUTTON_HEIGHT - (buttonRect.bottom - y)
        buttonRect.left = (x - dx).toInt()
        buttonRect.top = y.toInt()
        buttonRect.bottom = buttonRect.top + BUTTON_HEIGHT
        buttonRect.right = buttonRect.left + BUTTON_WIDTH

//        val rightX = buttonRect.right
//        val leftX = buttonRect.left
//        val topY = buttonRect.top
//        val bottomY = buttonRect.bottom
        invalidate()
    }


    private fun drawButtonText(canvas: Canvas?, text: String) {
        val textWidth = buttonTextPaint.measureText(text)
        val textHeight = buttonTextRect.apply {
            buttonTextPaint.getTextBounds(text, 0, text.length, buttonTextRect)
        }.height()

        val centerButtonByX = buttonRect.left + BUTTON_WIDTH / 2 - textWidth / 2
        val centerButtonByY = buttonRect.top + BUTTON_HEIGHT / 2 + textHeight / 2

        canvas?.drawText(text, centerButtonByX, centerButtonByY.toFloat(), buttonTextPaint)
    }
}
