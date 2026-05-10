package com.duc.offlinemusicplayer.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.TypedValue
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

object GlideUtils {

    @SuppressLint("CheckResult")
    fun loadImage(
        imageView: ImageView,
        url: Any?,
        placeholder: Int = 0,
        noCache: Boolean = false,
        centerCrop: Boolean = true,
        cornerRadiusDp: Float = 0f,
        useShimmer: Boolean = false
    ) {
        if (url == null) return
        val context = imageView.context
        val shimmerDrawable = if (useShimmer) createShimmerPlaceholder() else null

        val requestOptions = RequestOptions()
            .diskCacheStrategy(if (noCache) DiskCacheStrategy.NONE else DiskCacheStrategy.ALL)
            .placeholder(if (useShimmer) shimmerDrawable else context.getDrawable(placeholder))

        val transformations = mutableListOf<Transformation<Bitmap>>()
        if (centerCrop) transformations.add(CenterCrop())
        if (cornerRadiusDp > 0) {
            transformations.add(RoundedCorners(dpToPx(context, cornerRadiusDp).toInt()))
        }

        if (transformations.isNotEmpty()) {
            requestOptions.transform(*transformations.toTypedArray())
        }

        Glide.with(context)
            .load(url)
            .apply(requestOptions)
            .into(imageView)
    }

    @SuppressLint("CheckResult")
    fun loadImageAsGif(
        imageView: ImageView,
        url: Any?,
        placeholder: Int = 0,
        noCache: Boolean = false,
        centerCrop: Boolean = true,
        cornerRadiusDp: Float = 0f,
        useShimmer: Boolean = false
    ) {
        if (url == null) return
        val context = imageView.context
        val shimmerDrawable = if (useShimmer) createShimmerPlaceholder() else null

        val requestOptions = RequestOptions()
            .diskCacheStrategy(if (noCache) DiskCacheStrategy.NONE else DiskCacheStrategy.ALL)
            .placeholder(if (useShimmer) shimmerDrawable else context.getDrawable(placeholder))

        val transformations = mutableListOf<Transformation<Bitmap>>()
        if (centerCrop) transformations.add(CenterCrop())
        if (cornerRadiusDp > 0) {
            transformations.add(RoundedCorners(dpToPx(context, cornerRadiusDp).toInt()))
        }

        if (transformations.isNotEmpty()) {
            requestOptions.transform(*transformations.toTypedArray())
        }

        Glide.with(context)
            .asGif()
            .load(url)
            .apply(requestOptions)
            .into(imageView)
    }

    fun createShimmerPlaceholder(): ShimmerDrawable {
        val shimmer = Shimmer.AlphaHighlightBuilder()
            .setDuration(1000)
            .setBaseAlpha(0.7f)
            .setHighlightAlpha(1.0f)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()

        return ShimmerDrawable().apply {
            setShimmer(shimmer)
        }
    }

    private fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
    }
}

// Extension functions for easier usage
fun ImageView.loadImage(
    url: Any?,
    placeholder: Int = 0,
    noCache: Boolean = false,
    centerCrop: Boolean = true,
    cornerRadiusDp: Float = 0f,
    useShimmer: Boolean = false
) {
    GlideUtils.loadImage(this, url, placeholder, noCache, centerCrop, cornerRadiusDp, useShimmer)
}

fun ImageView.loadImageAsGif(
    url: Any?,
    placeholder: Int = 0,
    noCache: Boolean = false,
    centerCrop: Boolean = true,
    cornerRadiusDp: Float = 0f,
    useShimmer: Boolean = false
) {
    GlideUtils.loadImageAsGif(this, url, placeholder, noCache, centerCrop, cornerRadiusDp, useShimmer)
}
