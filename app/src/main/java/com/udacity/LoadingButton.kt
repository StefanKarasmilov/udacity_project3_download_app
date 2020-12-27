package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private var loadingValue = 0f
    private var text: String = "Download"
    private val textBounds = Rect()

    private val rectForCircle: RectF = RectF(0f, 0f, 0f, 0f)
    private val valueAnimator = ValueAnimator.ofFloat(0f, 100f)

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        if (buttonState == ButtonState.Clicked) {
            startAnimation()
        } else if (buttonState == ButtonState.Completed) {
            loadingValue = 0f
        }

        invalidate()
    }

    private var paintBar: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var paintProgress: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 70.0f
        typeface = Typeface.create("", Typeface.NORMAL)
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    init {
        paintBar.color = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
        paintProgress.color = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.LoadingButton)
            val buttonColor = resources.getColor(typedArray.getResourceId(R.styleable.LoadingButton_buttonColor, R.color.colorPrimary), null)
            val progressColor = resources.getColor(typedArray.getResourceId(R.styleable.LoadingButton_progressColor, R.color.colorPrimaryDark), null)

            paintBar.color = buttonColor
            paintProgress.color = progressColor

            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paintBar)
        paint.color = Color.WHITE

        canvas.drawRect(
            0f,
            0f,
            (0f + ((widthSize * loadingValue / 100).toInt())),
            heightSize.toFloat(),
            paintProgress
        )

        drawText(canvas)

        paint.color = Color.YELLOW
        paint.style = Paint.Style.FILL

        canvas.drawArc(
            rectForCircle,
            0f,
           (loadingValue / 100) * 360f,
            true,
            paint
        )
    }

    private fun startAnimation() {
        valueAnimator.apply {
            interpolator = LinearInterpolator()
            duration = 1500
            addUpdateListener {
                val animValue = it.animatedValue as Float
                loadingValue = animValue

                if (animValue == 100f && buttonState == ButtonState.Completed) {
                    loadingValue = 0f
                }
                invalidate()
            }
        }

        valueAnimator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        rectForCircle.set(0f, 0f, 100f, 100f)
        rectForCircle.offset(
            width.toFloat() - 200f,
            height.toFloat() / 4
        )
    }

    private fun drawText(canvas: Canvas) {
        text = when (buttonState == ButtonState.Clicked) {
            true -> "We are loading"
            false -> "Download"
        }

        paint.getTextBounds(text, 0, text.length, textBounds)

        val xPos = canvas.width / 2
        val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2)

        canvas.drawText(text, xPos.toFloat(), yPos, paint)
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}