package dev.kelocen.loadstatus.button

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import dev.kelocen.loadstatus.R
import kotlin.properties.Delegates

/**
 * A [View] subclass for the custom button.
 */
class LoadingButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
) :
        View(context, attrs, defStyleAttr) {
    private var widthSize = 0f
    private var heightSize = 0f
    private var animationButtonWidth = 0f
    private var animationCircleAngle = 0f
    private var defaultPaintColor = 0
    private var loadingPaintColor = 0
    private var textPaintColor = 0
    private var loadingCircleColor = 0
    private var defaultButtonText: String
    private var loadingButtonText: String

    /**
     * A [Paint] object for the button style.
     */
    private var customButtonPaint = Paint()

    /**
     * A [Paint] object for the text style.
     */
    private var textPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        textSize = 60f
        textAlign = Paint.Align.CENTER
    }

    /**
     * A [Paint] object for the loading circle style.
     */
    private var customCirclePaint = Paint().apply {
        isAntiAlias = true
        isDither = true
    }

    /**
     * A [ButtonState] object by [Delegates] to observe the button state.
     */
    var buttonState: ButtonState by Delegates.observable(
            ButtonState.Completed) { _, _, newButtonState ->
        if (newButtonState == ButtonState.Clicked) {
            buttonState = ButtonState.Loading
        } else if (newButtonState == ButtonState.Loading) {
            updateAnimationWidth()
            updateAnimationAngle()
        }
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            textPaintColor = getColor(R.styleable.LoadingButton_paintDefaultText, 0)
            defaultPaintColor = getColor(R.styleable.LoadingButton_paintDefaultButton, 0)
            loadingPaintColor = getColor(R.styleable.LoadingButton_paintLoadingButton, 0)
            loadingCircleColor = getColor(R.styleable.LoadingButton_paintLoadingCircle, 0)
        }
        defaultButtonText = resources.getString(R.string.main_label_default_button_text)
        loadingButtonText = resources.getString(R.string.main_label_loading_button_text)
        textPaint.color = textPaintColor
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawLoadingButton(canvas)
        drawLoadingCircle(canvas)
    }

    /**
     * Draws the [LoadingButton] using the given [Canvas] and [buttonState].
     */
    private fun drawLoadingButton(canvas: Canvas?) {
        customButtonPaint.color = defaultPaintColor
        canvas?.drawRect(drawButtonRectF(), customButtonPaint)
        if (buttonState == ButtonState.Completed) {
            drawButtonText(canvas, defaultButtonText)
        } else if (buttonState == ButtonState.Loading) {
            customButtonPaint.color = loadingPaintColor
            canvas?.drawRect(drawButtonRectF(right = animationButtonWidth), customButtonPaint)
            drawButtonText(canvas, loadingButtonText)
        }
    }

    /**
     * Returns the [RectF] for the [LoadingButton].
     */
    private fun drawButtonRectF(
            left: Float = 0f,
            top: Float = 0f,
            right: Float = widthSize,
            bottom: Float = heightSize,
    ): RectF {
        return RectF(left, top, right, bottom)
    }

    /**
     * Draws the text for the [LoadingButton] with the given [Canvas].
     */
    private fun drawButtonText(canvas: Canvas?, buttonText: String) {
        canvas?.drawText(buttonText,
                         (widthSize / 2),
                         (heightSize / 2) - ((textPaint.ascent() + textPaint.descent()) / 2),
                         textPaint)
    }

    /**
     * Uses a [ValueAnimator] to animate the [LoadingButton] by updating the [animationButtonWidth].
     */
    private fun updateAnimationWidth() {
        ValueAnimator.ofFloat(0f, widthSize).apply {
            duration = 1750
            interpolator = LinearInterpolator()
            addUpdateListener {
                animationButtonWidth = it.animatedValue as Float
                if (animationButtonWidth == widthSize) {
                    buttonState = ButtonState.Completed
                }
                invalidate()
            }
        }.start()
    }

    /**
     * Draws the loading circle for the [LoadingButton] with the given [Canvas].
     */
    private fun drawLoadingCircle(canvas: Canvas?) {
        if (buttonState == ButtonState.Loading) {
            customCirclePaint.color = loadingCircleColor
            canvas?.drawArc(drawCircleRectF(), 0f, animationCircleAngle, true, customCirclePaint)
        }
    }

    /**
     * Returns the [RectF] used for the loading circle.
     */
    private fun drawCircleRectF(
            left: Float = widthSize / 2 + (textPaint.measureText(loadingButtonText) / 1.9f),
            top: Float = heightSize / 2 + textPaint.ascent() - ((textPaint.ascent() + textPaint.descent()) / 2),
            right: Float = widthSize / 2 + (textPaint.measureText(loadingButtonText) * 0.795f),
            bottom: Float = heightSize / 2 + textPaint.descent() - ((textPaint.ascent() + textPaint.descent()) / 2),
    ): RectF {
        return RectF(left, top, right, bottom)
    }

    /**
     * Uses a [ValueAnimator] to animate the loading circle by updating the [animationCircleAngle].
     */
    private fun updateAnimationAngle() {
        ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 1700
            interpolator = LinearInterpolator()
            addUpdateListener {
                animationCircleAngle = it.animatedValue as Float
                if (animationCircleAngle >= 360f) {
                    buttonState = ButtonState.Completed
                }
                invalidate()
            }
        }.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
                MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        widthSize = w.toFloat()
        heightSize = h.toFloat()
        setMeasuredDimension(w, h)
    }
}