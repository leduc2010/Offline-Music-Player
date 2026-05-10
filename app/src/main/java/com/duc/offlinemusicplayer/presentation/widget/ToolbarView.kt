package com.duc.offlinemusicplayer.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.databinding.ViewToolbarBinding
import com.duc.offlinemusicplayer.presentation.utils.dpToPx

class ToolbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    val binding =
        ViewToolbarBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        context.withStyledAttributes(attrs, R.styleable.ToolbarView) {
            val title = getString(R.styleable.ToolbarView_title)
            binding.tvTitle.text = title

            val showBackIcon = getBoolean(R.styleable.ToolbarView_showBackIcon, true)
            if (!showBackIcon) {
                hideIconBack()
            }
        }   
    }

    fun setOnBackClickListener(listener: () -> Unit) {
        binding.btnBack.setOnClickListener { listener.invoke() }
    }

    fun hideIconBack(){
        binding.btnBack.visibility = GONE
        setPadding(20.dpToPx(), paddingTop, paddingEnd, paddingBottom)
    }
}
