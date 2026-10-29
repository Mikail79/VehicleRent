package id.ac.stmi.vehiclerent.util

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import java.text.NumberFormat
import java.util.Locale

/**
 * Functional Micro-Interactions & Kinetic Motion Utilities
 * Adheres to strict anti-slop guidelines (Liveliness Dial: MOTION 2 / RHYTHM 2).
 * Inspired by Motion-Primitives, ReactBits, and Anime.js.
 */
object MotionUtils {

    private val idLocale = Locale("id", "ID")
    private val numberFormat = NumberFormat.getNumberInstance(idLocale)

    /**
     * Animates numeric values rolling up or down smoothly.
     * Prevents abrupt visual jumps when calculating totals or claims.
     */
    fun animateNumberRolling(
        textView: TextView,
        startVal: Long,
        endVal: Long,
        prefix: String = "Rp ",
        suffix: String = "",
        duration: Long = 450L
    ) {
        if (startVal == endVal && startVal > 0) {
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
        duration: Long = 450L
    ) {
        if (startVal == endVal && startVal > 0) {
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
        duration: Long = 450L
    ) {
        if (startVal == endVal && startVal > 0.0) {
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
     * Highly Visible Kinetic Press Feedback (Aura / ReactBits / 21st.dev):
     * Compresses to 0.94x with gentle alpha reduction on touch-down,
     * triggers device haptic tick, and springs back with a subtle overshoot on release.
     */
    fun applyTactilePressFeedback(view: View) {
        try {
            view.stateListAnimator = null
        } catch (_: Exception) {}

        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    v.animate()
                        .scaleX(0.935f)
                        .scaleY(0.935f)
                        .alpha(0.85f)
                        .setDuration(90L)
                        .setInterpolator(DecelerateInterpolator())
                        .start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .alpha(1.0f)
                        .setDuration(160L)
                        .setInterpolator(OvershootInterpolator(2.2f))
                        .start()
                }
            }
            false // Return false so click listeners still receive events normally
        }
    }

    /**
     * Staggered Cascade Entrance Animation (Motion-Primitives / Anime.js):
     * Views smoothly glide upwards from +36dp and fade in sequentially with a 45ms step delay.
     */
    fun staggeredCascade(views: List<View>, baseDelayMs: Long = 45L) {
        if (views.isEmpty()) return

        val density = views[0].resources.displayMetrics.density
        val slideDistance = density * 36f

        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = slideDistance

            // Post to ensure initial layout is ready before running animation
            view.postDelayed({
                view.animate()
                    .alpha(1.0f)
                    .translationY(0f)
                    .setDuration(360L)
                    .setInterpolator(FastOutSlowInInterpolator())
                    .start()
            }, (index * baseDelayMs).coerceAtMost(600L))
        }
    }

    /**
     * Smooth Capacity Ratio Bar Weight Expansion Animation:
     * Sweeps the multi-segment bar smoothly from 0 to full capacity on launch and updates.
     */
    fun animateMultiSegmentWeights(
        vReady: View,
        vRented: View,
        vWorkshop: View,
        targetReady: Int,
        targetRented: Int,
        targetWorkshop: Int,
        duration: Long = 550L
    ) {
        val animator = ValueAnimator.ofFloat(0.05f, 1.0f).apply {
            this.duration = duration
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener { animation ->
                val fraction = animation.animatedValue as Float
                val lpReady = vReady.layoutParams as? LinearLayout.LayoutParams ?: return@addUpdateListener
                val lpRented = vRented.layoutParams as? LinearLayout.LayoutParams ?: return@addUpdateListener
                val lpWorkshop = vWorkshop.layoutParams as? LinearLayout.LayoutParams ?: return@addUpdateListener

                lpReady.weight = (targetReady * fraction).coerceAtLeast(0.1f)
                lpRented.weight = (targetRented * fraction).coerceAtLeast(0.1f)
                lpWorkshop.weight = (targetWorkshop * fraction).coerceAtLeast(0.1f)

                vReady.layoutParams = lpReady
                vRented.layoutParams = lpRented
                vWorkshop.layoutParams = lpWorkshop
            }
        }
        animator.start()
    }

    /**
     * Pulse confirmation animation (scale 1.0 -> 1.05 -> 1.0) for status updates,
     * release actions, and active filter sector switching.
     */
    fun pulseHighlight(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        view.animate()
            .scaleX(1.05f)
            .scaleY(1.05f)
            .setDuration(120L)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                view.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(180L)
                    .setInterpolator(OvershootInterpolator(1.8f))
                    .start()
            }
            .start()
    }

    /**
     * Smoothly transitions the stroke border color of a card
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
