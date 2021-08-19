package dev.kelocen.loadstatus.button

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import dev.kelocen.loadstatus.R
import kotlin.properties.Delegates

/**
 * A [View] subclass for the loading button.
 */
class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
) :
    View(context, attrs, defStyleAttr) {
    private var widthSize = 0f
    private var heightSize = 0f
    private var animationWidthSize = 0f
    private var defaultPaintColor = 0
    private var loadingPaintColor = 0
    private var textPaintColor = 0
    private var defaultButtonText: String
    private var loadingButtonText: String

    /**
     * A [Paint] object for the button color and style.
     */
    private var customButtonPaint = Paint().apply {
        color = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
    }

    /**
     * A [Paint] object for the text color and style.
     */
    private var textPaint = Paint().apply {
        color = ResourcesCompat.getColor(resources, R.color.white, null)
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
        textSize = 50f
        textAlign = Paint.Align.CENTER
    }

    /**
     * A [ButtonState] object by [Delegates] to observe the button state.
     */
    var buttonState: ButtonState by Delegates.observable(
            ButtonState.Completed) { _, _, newButtonState ->
        if (newButtonState == ButtonState.Clicked) {
            buttonState = ButtonState.Loading
        } else if (newButtonState == ButtonState.Loading) {
            getAnimationWidthSize()
        }
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            defaultPaintColor = getColor(R.styleable.LoadingButton_defaultButtonPaint, 0)
            loadingPaintColor = getColor(R.styleable.LoadingButton_loadingButtonPaint, 0)
            textPaintColor = getColor(R.styleable.LoadingButton_defaultTextPaint, 0)
        }
        defaultButtonText = resources.getString(R.string.label_default_button_text)
        loadingButtonText = resources.getString(R.string.label_loading_button_text)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawLoadingButton(canvas)
    }

    /**
     * Draws the [LoadingButton] using the given [Canvas] and [buttonState].
     */
    private fun drawLoadingButton(canvas: Canvas?) {
        if (buttonState == ButtonState.Completed) {
            customButtonPaint.color = defaultPaintColor
            canvas?.drawRect(drawButtonRectF(), customButtonPaint)
            drawButtonText(canvas, defaultButtonText)
        } else if (buttonState == ButtonState.Loading) {
            customButtonPaint.color = loadingPaintColor
            canvas?.drawRect(drawButtonRectF(right = animationWidthSize), customButtonPaint)
            drawButtonText(canvas, loadingButtonText)
        }
    }

    /**
     * Returns the [RectF] for the [LoadingButton].
     */
    private fun drawButtonRectF(
            left: Float = 0f, top: Float = 0f, right: Float = widthSize,
            bottom: Float = heightSize,
    ): RectF {
        return RectF(left, top, right, bottom)
    }

    /**
     * Draws the text for the [LoadingButton] with the given [Canvas].
     */
    private fun drawButtonText(canvas: Canvas?, buttonText: String) {
        canvas?.drawText(buttonText, (widthSize / 2),
                (heightSize / 2) - ((textPaint.descent() + textPaint.ascent() / 2)), textPaint)
    }

    /**
     * Uses a [ValueAnimator] to animate the [LoadingButton] by updating the [animationWidthSize].
     *
     */
    private fun getAnimationWidthSize() {
        ValueAnimator.ofFloat(0f, widthSize).apply {
            duration = 1400
            interpolator = LinearInterpolator()
            addUpdateListener {
                animationWidthSize = it.animatedValue as Float
                if (animationWidthSize >= widthSize) {
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