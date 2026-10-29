package id.ac.stmi.vehiclerent.util

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import java.text.NumberFormat
import java.util.Locale

/**
 * Functional Micro-Interactions & Motion Utilities
 * Adheres to strict anti-slop guidelines (MOTION 2 / Liveliness Dial 2).
 * Inspired by Motion-Primitives, ReactBits, and Anime.js.
 */
object MotionUtils {

    private val idLocale = Locale("id", "ID")
    private val numberFormat = NumberFormat.getNumberInstance(idLocale)

    /**
     * Animates numeric values rolling up or down smoothly over 300ms.
     * Prevents abrupt visual jumps when calculating totals or claims.
     */
    fun animateNumberRolling(
        textView: TextView,
        startVal: Long,
        endVal: Long,
        prefix: String = "Rp ",
        suffix: String = "",
        duration: Long = 300L
    ) {
        if (startVal == endVal) {
            textView.text = "$prefix${numberFormat.format(endVal)}$suffix"
            return
        }

        val animator = ValueAnimator.ofFloat(startVal.toFloat(), endVal.toFloat()).apply {
            this.duration = duration
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener { animation ->
                val current = (animation.animatedValue as Float).toLong()
                textView.text = "$prefix${numberFormat.format(current)}$suffix"
            }
        }
        animator.start()
    }

    /**
     * Animates integer counter (e.g. Metric cards on Beranda)
     */
    fun animateIntRolling(
        textView: TextView,
        startVal: Int,
        endVal: Int,
        prefix: String = "",
        suffix: String = "",
        duration: Long = 300L
    ) {
        if (startVal == endVal) {
            textView.text = "$prefix$endVal$suffix"
            return
        }

        val animator = ValueAnimator.ofInt(startVal, endVal).apply {
            this.duration = duration
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener { animation ->
                val current = animation.animatedValue as Int
                textView.text = "$prefix$current$suffix"
            }
        }
        animator.start()
    }

    /**
     * Animates decimal floating numbers (e.g. 5.5 Jam Pengerjaan)
     */
    fun animateDoubleRolling(
        textView: TextView,
        startVal: Double,
        endVal: Double,
        prefix: String = "",
        suffix: String = " Jam Pengerjaan",
        duration: Long = 300L
    ) {
        if (startVal == endVal) {
            textView.text = String.format(Locale.US, "%s%.1f%s", prefix, endVal, suffix)
            return
        }

        val animator = ValueAnimator.ofFloat(startVal.toFloat(), endVal.toFloat()).apply {
            this.duration = duration
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener { animation ->
                val current = animation.animatedValue as Float
                textView.text = String.format(Locale.US, "%s%.1f%s", prefix, current, suffix)
            }
        }
        animator.start()
    }

    /**
     * Tactile Press Feedback:
     * Subtle compression to 0.985 scale on touch down, spring back to 1.0 on release.
     */
    fun applyTactilePressFeedback(view: View) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate()
                        .scaleX(0.985f)
                        .scaleY(0.985f)
                        .setDuration(100L)
                        .setInterpolator(DecelerateInterpolator())
                        .start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(100L)
                        .setInterpolator(DecelerateInterpolator())
                        .start()
                }
            }
            false // Return false so onClickListeners still receive the event
        }
    }

    /**
     * Smoothly transitions the stroke border color of a card (e.g. Workshop two-way branching)
     * over 250ms with ArgbEvaluator.
     */
    fun animateBorderColor(
        view: View,
        fromColor: Int,
        toColor: Int,
        fillColor: Int,
        strokeWidthPx: Int = 4,
        cornerRadiusPx: Float = 36f,
        duration: Long = 250L
    ) {
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(fillColor)
            cornerRadius = cornerRadiusPx
            setStroke(strokeWidthPx, fromColor)
        }
        view.background = drawable

        val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor).apply {
            this.duration = duration
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener { animator ->
                val animatedColor = animator.animatedValue as Int
                drawable.setStroke(strokeWidthPx, animatedColor)
            }
        }
        colorAnimator.start()
    }
}

