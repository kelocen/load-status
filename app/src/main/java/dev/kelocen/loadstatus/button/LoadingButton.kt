package dev.kelocen.loadstatus.button

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
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
        duration = 3000
        interpolator = OvershootInterpolator(.1f)
    }

    /**
     * A [ValueAnimator] to animate the loading circle by updating the [animationCircleAngle].
     *
     * This [ValueAnimator] is responsible for indicating an ongoing download process, and will
     * continue until the download is completed.
     */
    private var circleAnimator = ValueAnimator.ofFloat().apply {
        duration = 3000
        interpolator = OvershootInterpolator(.1f)
        repeatMode = ValueAnimator.RESTART
        repeatCount = ValueAnimator.INFINITE
    }

    /**
     * An [AnimatorSet] for [buttonAnimator] and [circleAnimator].
     */
    private var loadAnimatorSet = AnimatorSet().apply {
        play(buttonAnimator).with(circleAnimator)
        buttonAnimator.addUpdateListener { bAnimator ->
            animationButtonWidth = bAnimator.animatedValue as Float
        }
        circleAnimator.addUpdateListener { cAnimator ->
            animationCircleAngle = cAnimator.animatedValue as Float
            invalidate()
        }
    }

    /**
     * A [ButtonState] by [Delegates.observable] to update the animation based on the button state.
     */
    var buttonState: ButtonState by Delegates.observable(
            ButtonState.Reset) { _, _, newButtonState ->
        when (newButtonState) {
            ButtonState.Clicked -> {
                buttonAnimator.setFloatValues(0f, widthSize)  // Assign values here after onMeasure
                circleAnimator.setFloatValues(0f, 360f)       // is called and widthSize is updated
                invalidate()
            }
            ButtonState.Loading -> {
                if (!loadAnimatorSet.isRunning) {
                    loadAnimatorSet.start()
                }
            }
            ButtonState.Completed -> {
                if (loadAnimatorSet.isRunning) {
                    loadAnimatorSet.end()
                }
            }
            ButtonState.Reset -> {
                invalidate()
            }
        }
    }

    /**
     * A [Runnable] used by [postDelayed] to reset the [ButtonState].
     *
     * This [Runnable] is used to create a small delay between drawing the finished loading button
     * and drawing the default loading button.
     */
    private var loadingRunnable = Runnable { buttonState = ButtonState.Reset }

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
                ButtonState.Clicked -> buttonState = ButtonState.Loading
                ButtonState.Loading -> pass // Assigned by MainActivity
                ButtonState.Completed -> postDelayed(loadingRunnable, 500)
                ButtonState.Reset -> pass
            }
        }
    }

    /**
     * Draws the [LoadingButton] using the [buttonState] and given [Canvas].
     */
    private fun drawLoadingButton(canvas: Canvas?) {
        when (buttonState) {
            ButtonState.Reset -> drawDefaultLoadingButton(canvas)
            ButtonState.Completed -> drawCompletedLoadingButton(canvas)
            ButtonState.Loading -> drawInProgressLoadingButton(canvas)
            ButtonState.Clicked -> pass
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
     * Draws the completed loading button using the given [Canvas].
     *
     * This method provides a visual indication that the download has completed by drawing a full
     * loading button and circle when the download has finished.
     */
    private fun drawCompletedLoadingButton(canvas: Canvas?) {
        canvas?.drawRect(drawButtonRectF(), defaultButtonPaint)
        canvas?.drawRect(drawButtonRectF(right = widthSize), loadingButtonPaint)
        drawButtonText(canvas, loadingButtonText)
        drawLoadingCircle(canvas, startAngle = 360f)
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
                         (heightSize / 2) - getDistToCenter(),
                         textPaint)
    }

    /**
     * Returns the distance to the center of the text.
     */
    private fun getDistToCenter(): Float {
        return (textPaint.ascent() + textPaint.descent()) / 2
    }

    /**
     * Draws the loading circle for the [LoadingButton] with the given [Canvas].
     */
    private fun drawLoadingCircle(canvas: Canvas?, startAngle: Float = 0f) {
        canvas?.drawArc(drawCircleRectF(),
                        startAngle,
                        animationCircleAngle,
                        true,
                        loadingCirclePaint)
    }

    /**
     * Returns the [RectF] used for the loading circle.
     */
    private fun drawCircleRectF(
            left: Float = widthSize / 2 + (textPaint.measureText(loadingButtonText) / 1.9f),
            top: Float = (heightSize / 2) + textPaint.ascent() - getDistToCenter(),
            right: Float = widthSize / 2 + (textPaint.measureText(loadingButtonText) * 0.795f),
            bottom: Float = (heightSize / 2) + textPaint.descent() - getDistToCenter(),
    ): RectF {
        return RectF(left, top, right, bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)
        widthSize = w.toFloat()
        heightSize = h.toFloat()
        setMeasuredDimension(w, h)
    }
}