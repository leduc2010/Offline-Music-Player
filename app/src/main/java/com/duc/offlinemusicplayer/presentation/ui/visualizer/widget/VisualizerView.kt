package com.duc.offlinemusicplayer.presentation.ui.visualizer.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class VisualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var type: String = "bar" // "bar", "wave", "blob"
    private var config: Map<String, Any> = emptyMap()

    private val mainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private var isPlaying: Boolean = false
    private var animationTime: Float = 0f
    private val randomAmplitudes = FloatArray(100) { (0.3f + Math.random() * 0.7f).toFloat() }

    init {
        // Set Layer Type to Software to ensure compatibility with shadow layers if neon glow is used
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun setConfig(type: String, config: Map<String, Any>) {
        this.type = type
        this.config = config
        initPaint()
        invalidate()
    }

    fun setPlaying(playing: Boolean) {
        if (this.isPlaying != playing) {
            this.isPlaying = playing
            invalidate()
        }
    }

    private fun initPaint() {
        val colorHex = config["color"] as? String ?: "#00FFC4"
        val parsedColor = runCatching { Color.parseColor(colorHex) }.getOrDefault(Color.CYAN)

        mainPaint.color = parsedColor
        strokePaint.color = parsedColor

        // Neon Glow effect
        val glowRadius = 15f
        mainPaint.setShadowLayer(glowRadius, 0f, 0f, parsedColor)
        strokePaint.setShadowLayer(glowRadius, 0f, 0f, parsedColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width == 0 || height == 0) return

        when (type) {
            "bar" -> drawBarVisualizer(canvas)
            "wave" -> drawWaveVisualizer(canvas)
            "blob" -> drawBlobVisualizer(canvas)
        }

        // Keep animating at 60fps
        if (isPlaying) {
            animationTime += 0.05f
            postInvalidateOnAnimation()
        } else {
            // Gentle breathing idle state
            animationTime += 0.008f
            postInvalidateOnAnimation()
        }
    }

    private fun drawBarVisualizer(canvas: Canvas) {
        val barCountDouble = config["barCount"] as? Double ?: 24.0
        val barCount = barCountDouble.toInt()
        val barWidthDouble = config["barWidth"] as? Double ?: 8.0
        val barWidth = (barWidthDouble.toFloat() * resources.displayMetrics.density).coerceAtLeast(4f)
        val roundCornerDouble = config["roundCorner"] as? Double ?: 12.0
        val roundCorner = roundCornerDouble.toFloat() * resources.displayMetrics.density

        val gap = (width - (barCount * barWidth)) / (barCount + 1)

        for (i in 0 until barCount) {
            val randomFactor = randomAmplitudes[i % randomAmplitudes.size]
            val waveSpeed = if (isPlaying) 1.5f else 0.4f
            val modulation = sin((animationTime * waveSpeed + i * 0.4f).toDouble()).toFloat()
            val normalizedHeight = (modulation + 1f) / 2f // 0 to 1
            
            val maxBarHeight = height * 0.7f
            val minBarHeight = height * 0.1f
            val barHeight = minBarHeight + (maxBarHeight - minBarHeight) * normalizedHeight * randomFactor

            val left = gap + i * (barWidth + gap)
            val right = left + barWidth
            val bottom = height.toFloat()
            val top = bottom - barHeight

            canvas.drawRoundRect(left, top, right, bottom, roundCorner, roundCorner, mainPaint)
        }
    }

    private fun drawWaveVisualizer(canvas: Canvas) {
        val waveCountDouble = config["waveCount"] as? Double ?: 3.0
        val waveCount = waveCountDouble.toInt()
        val lineThicknessDouble = config["lineThickness"] as? Double ?: 5.0
        val lineThickness = lineThicknessDouble.toFloat() * resources.displayMetrics.density
        val amplitudeScale = (config["amplitudeScale"] as? Double ?: 0.8).toFloat()
        val mirror = config["mirror"] as? Boolean ?: true

        strokePaint.strokeWidth = lineThickness
        
        val centerY = height / 2f
        val maxAmp = height * 0.4f * amplitudeScale

        for (w in 0 until waveCount) {
            val path = Path()
            val wavePhase = animationTime * (if (isPlaying) 1.2f else 0.3f) + w * 1.5f
            val speedFactor = 1f - (w * 0.15f)

            path.moveTo(0f, centerY)
            for (x in 0..width step 10) {
                val angle = (x.toDouble() / width) * 2 * Math.PI * 1.5 + wavePhase
                val sinVal = sin(angle).toFloat()
                val fade = sin((x.toDouble() / width) * Math.PI).toFloat() // zero at ends
                val y = centerY + sinVal * maxAmp * fade * speedFactor * randomAmplitudes[(x / 10) % randomAmplitudes.size]

                path.lineTo(x.toFloat(), y)
            }

            canvas.drawPath(path, strokePaint)

            if (mirror) {
                val mirrorPath = Path()
                mirrorPath.moveTo(0f, centerY)
                for (x in 0..width step 10) {
                    val angle = (x.toDouble() / width) * 2 * Math.PI * 1.5 + wavePhase
                    val sinVal = sin(angle).toFloat()
                    val fade = sin((x.toDouble() / width) * Math.PI).toFloat()
                    val y = centerY - sinVal * maxAmp * fade * speedFactor * randomAmplitudes[(x / 10) % randomAmplitudes.size]

                    mirrorPath.lineTo(x.toFloat(), y)
                }
                canvas.drawPath(mirrorPath, strokePaint)
            }
        }
    }

    private fun drawBlobVisualizer(canvas: Canvas) {
        val baseRadiusDouble = config["baseRadius"] as? Double ?: 0.4
        val maxRadiusDouble = config["maxRadius"] as? Double ?: 0.7
        val strokeWidthDouble = config["strokeWidth"] as? Double ?: 3.0
        val strokeWidth = strokeWidthDouble.toFloat() * resources.displayMetrics.density
        val pointCountDouble = config["pointCount"] as? Double ?: 90.0
        val pointCount = pointCountDouble.toInt()
        val amplitude = (config["amplitude"] as? Double ?: 0.6).toFloat()
        val pulseDamping = (config["pulseDamping"] as? Double ?: 0.15).toFloat()

        strokePaint.strokeWidth = strokeWidth

        val cx = width / 2f
        val cy = height / 2f
        val maxRadiusSize = Math.min(width, height) / 2f
        val baseRadius = maxRadiusSize * baseRadiusDouble.toFloat()
        val deltaRadius = maxRadiusSize * (maxRadiusDouble.toFloat() - baseRadiusDouble.toFloat())

        val path = Path()

        val pulseSpeed = if (isPlaying) 2.0f else 0.5f
        val pulse = sin((animationTime * pulseSpeed).toDouble()).toFloat() * pulseDamping

        for (i in 0 until pointCount) {
            val angleDeg = (i.toFloat() / pointCount) * 360f
            val angleRad = Math.toRadians(angleDeg.toDouble())

            val waveSpeed = if (isPlaying) 1.5f else 0.4f
            val modulation = sin((animationTime * waveSpeed + i * 0.25f).toDouble()).toFloat()
            val randomFactor = randomAmplitudes[i % randomAmplitudes.size]

            val currentRadius = baseRadius + deltaRadius * (1f + pulse) + modulation * baseRadius * amplitude * 0.15f * randomFactor

            val x = cx + currentRadius * cos(angleRad).toFloat()
            val y = cy + currentRadius * sin(angleRad).toFloat()

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()

        // Semitransparent fill for premium styling
        mainPaint.alpha = 40 // ~15% opacity
        canvas.drawPath(path, mainPaint)
        
        mainPaint.alpha = 255 // Reset alpha
        canvas.drawPath(path, strokePaint)
    }
}
