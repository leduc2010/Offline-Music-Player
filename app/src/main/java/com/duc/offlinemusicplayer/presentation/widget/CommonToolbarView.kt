package com.duc.offlinemusicplayer.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.duc.offlinemusicplayer.R

class CommonToolbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : Toolbar(context, attrs, defStyleAttr) {

    private val btnStart: ImageButton
    private val btnEnd: ImageButton
    private val tvTitle: TextView

    init {
        inflate(context, R.layout.view_common_toolbar, this)
        btnStart = findViewById(R.id.btnStart)
        btnEnd = findViewById(R.id.btnEnd)
        tvTitle = findViewById(R.id.tvTitle)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.CommonToolbarView)
        val title = ta.getString(R.styleable.CommonToolbarView_ctvTitle).orEmpty()
        val titleStyle = ta.getResourceId(R.styleable.CommonToolbarView_ctvTitleTextStyle, R.style.Body_Large_M_20)
        val showStart = ta.getBoolean(R.styleable.CommonToolbarView_ctvShowStartIcon, true)
        val showEnd = ta.getBoolean(R.styleable.CommonToolbarView_ctvShowEndIcon, true)
        val startSrc = ta.getResourceId(R.styleable.CommonToolbarView_ctvStartIconSrc, R.drawable.ic_back)
        val endSrc = ta.getResourceId(R.styleable.CommonToolbarView_ctvEndIconSrc, R.drawable.ic_search)
        ta.recycle()

        tvTitle.text = title
        tvTitle.setTextAppearance(titleStyle)
        btnStart.setImageResource(startSrc)
        btnEnd.setImageResource(endSrc)
        btnStart.visibility = if (showStart) VISIBLE else GONE
        btnEnd.visibility = if (showEnd) VISIBLE else GONE
    }

    fun setTitleText(text: CharSequence) { tvTitle.text = text }
    fun setOnStartClick(listener: OnClickListener?) { btnStart.setOnClickListener(listener) }
    fun setOnEndClick(listener: OnClickListener?) { btnEnd.setOnClickListener(listener) }
    fun showStartIcon(show: Boolean) { btnStart.visibility = if (show) VISIBLE else GONE }
    fun showEndIcon(show: Boolean) { btnEnd.visibility = if (show) VISIBLE else GONE }
}
