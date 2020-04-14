package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
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

    private var valueAnimator: ValueAnimator? = null

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
                    ContextCompat.getColor(context,R.color.colorPrimary)
            )
            buttonDownloadingColor = a.getColor(
                    R.styleable.LoadingButton_cb_tv_background_color,
                    ContextCompat.getColor(context,R.color.colorPrimaryDark)
            )
            buttonTextColor = a.getColor(
                    R.styleable.LoadingButton_cb_tv_text_color,
                    ContextCompat.getColor(context, android.R.color.black)
            )
        } finally {
            a.recycle()
        }

        textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.LEFT
            textSize = 55.0f
            color = buttonTextColor
            typeface = Typeface.create("", Typeface.BOLD)
        }

        valueAnimator = ObjectAnimator.ofArgb(
                this,
                "backgroundColor", buttonDownloadColor, buttonDownloadingColor
        )
        valueAnimator?.duration = 500
        valueAnimator?.repeatCount = ValueAnimator.INFINITE
        valueAnimator?.repeatMode = ObjectAnimator.REVERSE
        valueAnimator?.interpolator = DecelerateInterpolator()
        valueAnimator?.disableViewDuringAnimation(this)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(buttonDownloadColor)
        drawText(canvas)
    }

    private fun drawText(canvas: Canvas) {
        val text = context.getString(R.string.download_msg)
        val r = Rect()
        canvas.getClipBounds(r)
        val cHeight: Int = r.height()
        val cWidth: Int = r.width()
        textPaint.getTextBounds(text, 0, text.length, r)
        val x: Float = cWidth / 2f - r.width() / 2f - r.left
        val y: Float = cHeight / 2f + r.height() / 2f - r.bottom
        canvas.drawText(text, x, y, textPaint)

       /* canvas.drawText(
                context.getString(R.string.download_msg),
                widthSize  / 2f,
                heightSize  / 2f,
                textPaint
        )*/
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

    override fun performClick(): Boolean {
        setState(ButtonState.Loading)
        return super.performClick()
    }

    private fun setState(state: ButtonState) {
        when (state) {
            ButtonState.Clicked -> {

            }
            ButtonState.Loading -> {
                valueAnimator?.start()
            }
            ButtonState.Completed -> {
                valueAnimator?.end()
            }
        }

        invalidate()
    }

    private fun ValueAnimator.disableViewDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                //view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                //view.isEnabled = true
            }
        })

        addUpdateListener(object: ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator?) {
                setBackgroundColor(valueAnimator?.animatedValue as Int)
            }

        })
    }

}