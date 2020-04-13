package com.udacity

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    // Styleable attributes
    private var isAnimateLayout = true
    private var buttonTextColor = 0
    private var buttonDownloadColor = 0
    private var buttonDownloadingColor = 0

    private lateinit var textPaint: Paint

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }


    init {
        val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.LoadingButton,
                defStyleAttr,
                R.style.AppTheme
        )
        try {
            isAnimateLayout = a.getBoolean(R.styleable.LoadingButton_cb_is_animate_layout, true)
            buttonDownloadColor = a.getColor(
                    R.styleable.LoadingButton_cb_tv_background_color,
                    resources.getColor(R.color.colorPrimary)
            )
            buttonDownloadingColor = a.getColor(
                    R.styleable.LoadingButton_cb_tv_background_color,
                    resources.getColor(R.color.colorPrimaryDark)
            )
            buttonTextColor = a.getColor(
                    R.styleable.LoadingButton_cb_tv_text_color,
                    resources.getColor(android.R.color.black)
            )
        } finally {
            a.recycle()
        }

        textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = 55.0f
            color = buttonTextColor
            typeface = Typeface.create("", Typeface.BOLD)
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(buttonDownloadColor)
        canvas.drawText(
                context.getString(R.string.download_msg),
                widthSize / 2f,
                heightSize / 2f,
                textPaint
        )
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

    private fun setState(state: ButtonState) {
        when (state) {
            ButtonState.Clicked -> {

            }
            ButtonState.Loading -> {

            }
            ButtonState.Completed -> {

            }
        }

        invalidate()
    }

    private fun animateView() {
        super.animate()
        valueAnimator.addUpdateListener {

        }
        valueAnimator.repeatMode = ValueAnimator.RESTART
        valueAnimator.setIntValues()
        valueAnimator.start()


        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), buttonDownloadColor, buttonDownloadingColor)
        colorAnimation.duration = 250 // milliseconds
        colorAnimation.repeatMode = ValueAnimator.REVERSE

        colorAnimation.addUpdateListener { animator -> this.setBackgroundColor(animator.animatedValue as Int) }
        colorAnimation.start()
    }

}