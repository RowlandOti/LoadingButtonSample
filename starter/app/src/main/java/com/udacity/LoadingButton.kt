package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat


private const val DEFAULT_STROKE_WIDTH = 5f
private const val DEFAULT_CIRCLE_RADIUS = 16f
private const val DEFAULT_START_ANGLE = 90f
private const val DEFAULT_INITIAL_ANGLE = 0f

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var loadingCircleInitialAngle: Float
    private var loadingCircleStrokeWidth: Float
    private var loadingCircleRadius: Float
    private var widthSize = 0
    private var heightSize = 0

    // Styleable attributes
    private var isAnimateLayout = true
    private var buttonTextColor = 0
    private var buttonDownloadColor = 0
    private var buttonDownloadingColor = 0

    private var loadingCircleColor = 0

    private var textPaint: Paint
    private var circlePaint: Paint

    private var valueAnimator: ValueAnimator? = null
    private var circularAnimator: ValueAnimator? = null
    private var loadingCircleAnimatedInitialAngle: Float
    private var buttonDownloadingAnimatedColor: Int


    init {
        val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.LoadingButton,
                defStyleAttr,
                R.style.AppTheme
        )
        try {
            loadingCircleInitialAngle = a.getDimension(
                    R.styleable.LoadingButton_cb_loading_circle_initial_angle,
                    DEFAULT_INITIAL_ANGLE
            )
            loadingCircleStrokeWidth = a.getDimension(
                    R.styleable.LoadingButton_cb_loading_circle_stroke_width,
                    DEFAULT_STROKE_WIDTH
            )
            loadingCircleRadius = a.getDimension(
                    R.styleable.LoadingButton_cb_loading_circle_radius,
                    DEFAULT_CIRCLE_RADIUS
            )
            isAnimateLayout = a.getBoolean(R.styleable.LoadingButton_cb_is_animate_layout, true)
            buttonDownloadColor = a.getColor(
                    R.styleable.LoadingButton_cb_btn_normal_bg_color,
                    ContextCompat.getColor(context, R.color.colorPrimary)
            )
            buttonDownloadingColor = a.getColor(
                    R.styleable.LoadingButton_cb_btn_loading_bg_color,
                    ContextCompat.getColor(context, R.color.colorPrimaryDark)
            )
            buttonTextColor = a.getColor(
                    R.styleable.LoadingButton_cb_tv_text_color,
                    ContextCompat.getColor(context, android.R.color.black)
            )

            loadingCircleColor = a.getColor(
                    R.styleable.LoadingButton_cb_loading_circle_color,
                    ContextCompat.getColor(context, R.color.colorAccent)
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

        circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = loadingCircleColor
            strokeWidth = loadingCircleStrokeWidth
        }

        loadingCircleAnimatedInitialAngle = loadingCircleInitialAngle
        buttonDownloadingAnimatedColor = buttonDownloadColor

        valueAnimator = ObjectAnimator.ofArgb(
                this,
                "backgroundColor", buttonDownloadColor, buttonDownloadingColor
        )
        valueAnimator?.duration = 500
        valueAnimator?.repeatCount = ValueAnimator.INFINITE
        valueAnimator?.repeatMode = ObjectAnimator.REVERSE
        valueAnimator?.interpolator = DecelerateInterpolator()
        valueAnimator?.disableViewDuringAnimation(this)

        circularAnimator = ValueAnimator.ofInt(1, 360)
        circularAnimator?.interpolator = DecelerateInterpolator()
        circularAnimator?.duration = 500
        circularAnimator?.repeatCount = ValueAnimator.INFINITE
        circularAnimator?.addUpdateListener {
            loadingCircleAnimatedInitialAngle = (circularAnimator?.animatedValue as Int).toFloat()
            invalidate()
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackgroundColor(canvas)
        drawText(canvas)
        drawCircle(canvas)
    }

    private fun drawBackgroundColor(canvas: Canvas) {
        canvas.drawColor(buttonDownloadingAnimatedColor)
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
    }

    private fun drawCircle(canvas: Canvas) {
        val text = context.getString(R.string.download_msg)

        val centerX = measuredWidth / 2f + textPaint.measureText(text)
        val centerY = measuredHeight / 2f

        //canvas.drawCircle(centerX, centerY, loadingCircleRadius, circlePaint)

        val r = RectF()
        r.left = centerX - loadingCircleStrokeWidth -  loadingCircleRadius
        r.top = centerY - loadingCircleStrokeWidth -  loadingCircleRadius
        r.right = centerX - loadingCircleStrokeWidth -  loadingCircleRadius + loadingCircleRadius * 2
        r.bottom = centerY - loadingCircleStrokeWidth -  loadingCircleRadius + loadingCircleRadius * 2

        canvas.drawArc(r, DEFAULT_START_ANGLE, loadingCircleAnimatedInitialAngle, false, circlePaint);
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

    fun setState(state: ButtonState) {
        when (state) {
            ButtonState.Clicked -> {

            }
            ButtonState.Loading -> {
                if (isAnimateLayout) {
                    valueAnimator?.start()
                    circularAnimator?.start()
                }
            }
            ButtonState.Completed -> {
                valueAnimator?.end()
                circularAnimator?.end()

                buttonDownloadingAnimatedColor = buttonDownloadColor
                loadingCircleAnimatedInitialAngle = loadingCircleInitialAngle
            }
        }
    }

    private fun ValueAnimator.disableViewDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })

        addUpdateListener { buttonDownloadingAnimatedColor = valueAnimator?.animatedValue as Int }
    }
}