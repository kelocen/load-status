package dev.kelocen.loadstatus.button

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
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
    private var defaultButtonText: String? = null
    private var loadingButtonText: String? = null
    private val pass: Unit = Unit // Placeholder for empty blocks

    /**
     * A [Paint] object for the default button style.
     */
    private var defaultButtonPaint = Paint().apply {
        isAntiAlias = true
    }

    /**
     * A [Paint] object for the loading button style.
     */
    private var loadingButtonPaint = Paint().apply {
        isAntiAlias = true
    }

    /**
     * A [Paint] object for the text style.
     */
    private var textPaint = Paint().apply {
        isAntiAlias = true
        textSize = 60f
        textAlign = Paint.Align.CENTER
    }

    /**
     * A [Paint] object for the loading circle style.
     */
    private var loadingCirclePaint = Paint().apply {
        isAntiAlias = true
        isDither = true
    }

    /**
     * A [ValueAnimator] to animate the [LoadingButton] by updating the [animationButtonWidth].
     */
    private var buttonAnimator = ValueAnimator.ofFloat().apply {
        duration = 700
        interpolator = AccelerateInterpolator()
        addUpdateListener {
            animationButtonWidth = it.animatedValue as Float
            invalidate()
        }
    }

    /**
     * A [ValueAnimator] to animate the loading circle by updating the [animationCircleAngle].
     */
    private var circleAnimator = ValueAnimator.ofFloat().apply {
        duration = 1200
        interpolator = AccelerateInterpolator()
        repeatMode = ValueAnimator.RESTART
        repeatCount = 3
        addUpdateListener {
            animationCircleAngle = it.animatedValue as Float
            invalidate()
        }
    }

    /**
     * A [ButtonState] object by [Delegates] to observe the button state and update the animation.
     */
    var buttonState: ButtonState by Delegates.observable(ButtonState.Reset) { _, _, newButtonState ->
        when (newButtonState) {
            ButtonState.Clicked -> {
                buttonAnimator.setFloatValues(0f, widthSize)
                circleAnimator.setFloatValues(0f, 360f)
                invalidate()
            }
            ButtonState.Loading -> {
                if (!circleAnimator.isRunning) {
                    buttonAnimator.start()
                    circleAnimator.start()
                }
            }
            ButtonState.Completed -> {
                if (circleAnimator.isRunning) {
                    circleAnimator.end()
                    buttonAnimator.end()
                }
                invalidate()
            }
            ButtonState.Reset -> {
                invalidate()
            }
        }
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            defaultButtonPaint.color = getColor(R.styleable.LoadingButton_buttonDefaultPaint, 0)
            loadingCirclePaint.color = getColor(R.styleable.LoadingButton_buttonCirclePaint, 0)
            loadingButtonPaint.color = getColor(R.styleable.LoadingButton_buttonLoadingPaint, 0)
            textPaint.color = getColor(R.styleable.LoadingButton_buttonTextPaint, 0)
            defaultButtonText = getString(R.styleable.LoadingButton_buttonDefaultText)
            loadingButtonText = getString(R.styleable.LoadingButton_buttonLoadingText)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        updateButtonState()
        drawLoadingButton(canvas)
    }

    /**
     * Updates the button state based on the state of [circleAnimator].
     */
    private fun updateButtonState() {
        if (!circleAnimator.isRunning) {
            when (buttonState) {
                ButtonState.Clicked -> {
                    buttonState = ButtonState.Loading
                }
                ButtonState.Loading -> {
                    buttonState = ButtonState.Completed
                }
                ButtonState.Completed -> {
                    buttonState = ButtonState.Reset
                }
                ButtonState.Reset -> {
                    pass
                }
            }
        }
    }

    /**
     * Draws the [LoadingButton] using the [buttonState] and given [Canvas].
     */
    private fun drawLoadingButton(canvas: Canvas?) {
        if (buttonState == ButtonState.Reset) {
            drawDefaultLoadingButton(canvas)
        } else if (buttonState == ButtonState.Loading) {
            drawInProgressLoadingButton(canvas)
        }
    }

    /**
     * Draws the default loading button using the given [Canvas].
     */
    private fun drawDefaultLoadingButton(canvas: Canvas?) {
        canvas?.drawRect(drawButtonRectF(), defaultButtonPaint)
        drawButtonText(canvas, defaultButtonText)
    }

    /**
     * Draws the animated loading button using the given [Canvas].
     */
    private fun drawInProgressLoadingButton(canvas: Canvas?) {
        canvas?.drawRect(drawButtonRectF(), defaultButtonPaint)
        canvas?.drawRect(drawButtonRectF(right = animationButtonWidth), loadingButtonPaint)
        drawButtonText(canvas, loadingButtonText)
        drawLoadingCircle(canvas)
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
    private fun drawButtonText(canvas: Canvas?, buttonText: String?) {
        canvas?.drawText(buttonText.toString(),
                         (widthSize / 2),
                         (heightSize / 2) - ((textPaint.ascent() + textPaint.descent()) / 2),
                         textPaint)
    }

    /**
     * Draws the loading circle for the [LoadingButton] with the given [Canvas].
     */
    private fun drawLoadingCircle(canvas: Canvas?) {
        canvas?.drawArc(drawCircleRectF(),
                        0f,
                        animationCircleAngle,
                        true,
                        loadingCirclePaint)
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