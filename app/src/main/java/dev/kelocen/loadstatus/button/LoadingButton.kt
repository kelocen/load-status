package dev.kelocen.loadstatus.button

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import dev.kelocen.loadstatus.R
import kotlin.properties.Delegates

/**
 * A [View] subclass for the loading button.
 */
class LoadingButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private val valueAnimator = ValueAnimator()

    private lateinit var canvas: Canvas
    private lateinit var bitmap: Bitmap

    /**
     * A [Paint] object for the default button color.
     */
    private var defaultButtonPaint = Paint().apply {
        color = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
    }

    /**
     * A [Paint] object for the loading button color.
     */
    private var loadingButtonPaint = Paint().apply {
        color = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
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
    private var buttonState: ButtonState by Delegates.observable(
            ButtonState.Completed) { _, _, new ->
    }

    init {

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawDefaultButton(canvas)
    }

    /**
     * Draws the default loading button.
     */
    private fun drawDefaultButton(canvas: Canvas?) {
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), defaultButtonPaint)
        canvas?.drawText(resources.getString(R.string.loading_button_text),
                (widthSize / 2).toFloat(),
                (heightSize / 2) - ((textPaint.descent() + textPaint.ascent() / 2)), textPaint)
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