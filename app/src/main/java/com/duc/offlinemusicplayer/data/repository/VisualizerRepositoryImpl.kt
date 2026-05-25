package com.duc.offlinemusicplayer.data.repository

import com.duc.offlinemusicplayer.data.source.local.pref.PreferenceHelper
import com.duc.offlinemusicplayer.data.source.remote.cloud.FirebaseMgr
import com.duc.offlinemusicplayer.domain.model.VisualizerCategory
import com.duc.offlinemusicplayer.domain.model.VisualizerItem
import com.duc.offlinemusicplayer.domain.repository.VisualizerRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisualizerRepositoryImpl @Inject constructor(
    private val firebaseMgr: FirebaseMgr,
    private val pref: PreferenceHelper
) : VisualizerRepository {

    private val gson = Gson()

    private val fallbackCategories = listOf(
        VisualizerCategory("trending", "Trending"),
        VisualizerCategory("top_new", "Top New"),
        VisualizerCategory("top_premium", "Top Premium"),
        VisualizerCategory("edm", "EDM"),
        VisualizerCategory("anime", "Anime"),
        VisualizerCategory("chill", "Chill")
    )

    private val fallbackItems = listOf(
        VisualizerItem(
            id = "v01",
            name = "Chill Bass",
            categoryTags = listOf("trending", "chill", "top_new"),
            type = "bar",
            thumbnail = "https://cdn.wallpapersafari.com/49/76/6lIRVM.jpg",
            description = "Gentle visualizer bars reacting smoothly to deep frequencies, perfect for relaxing lo-fi tunes.",
            size = 204800,
            downloadCount = 12300,
            config = mapOf(
                "background" to "https://i.pinimg.com/originals/e1/78/73/e17873381a302d3fabd5ce3d330eccdf.gif",
                "color" to "#00FFC4", // HSL-tailored vibrant neon green/cyan
                "barCount" to 28.0,
                "barWidth" to 8.0,
                "direction" to "up",
                "roundCorner" to 12.0,
                "smoothFactor" to 0.3
            )
        ),
        VisualizerItem(
            id = "v02",
            name = "Car Bass",
            categoryTags = listOf("trending", "edm"),
            type = "wave",
            thumbnail = "https://wallpapers.com/images/hd/chill-anime-phone-c8dd9rv865w9hc9e.jpg",
            description = "High energy stereo waves bouncing to punchy kicks. Ideal for EDM and phonk beats.",
            size = 158000,
            downloadCount = 228960,
            config = mapOf(
                "background" to "https://i.pinimg.com/originals/e1/78/73/e17873381a302d3fabd5ce3d330eccdf.gif",
                "color" to "#FF007F", // Neon pink
                "waveCount" to 3.0,
                "lineThickness" to 5.0,
                "amplitudeScale" to 0.85,
                "smooth" to true,
                "mirror" to true
            )
        ),
        VisualizerItem(
            id = "v03",
            name = "Anime Spirit Glow",
            categoryTags = listOf("anime", "top_new"),
            type = "blob",
            thumbnail = "https://cdn.leansoft-ai.com/ls33-offline-music-player/Anime%20Spirit%20Glow.webp",
            description = "Energetic glowing radial blob inspired by magical anime effects and spiritual elements.",
            size = 142000,
            downloadCount = 8900,
            config = mapOf(
                "background" to "https://cdn.leansoft-ai.com/ls33-offline-music-player/Anime%20Spirit%20Glow.webp",
                "color" to "#00FFFF", // Neon cyan
                "baseRadius" to 0.4,
                "maxRadius" to 0.7,
                "strokeWidth" to 4.0,
                "pointCount" to 90.0,
                "amplitude" to 0.6,
                "pulseDamping" to 0.15,
                "smoothFactor" to 0.7
            )
        ),
        VisualizerItem(
            id = "v04",
            name = "Deep Space Wave",
            categoryTags = listOf("chill", "top_premium"),
            type = "wave",
            thumbnail = "https://wallpapers.com/images/hd/starry-night-anime-phone-q6v8zdx4v39w5ex8.jpg",
            description = "Serene layered landscape wave visualizer depicting astronomical gravity fluctuations.",
            size = 184500,
            downloadCount = 15400,
            config = mapOf(
                "background" to "https://wallpapers.com/images/hd/starry-night-anime-phone-q6v8zdx4v39w5ex8.jpg",
                "color" to "#9D00FF", // Purple neon
                "waveCount" to 4.0,
                "lineThickness" to 4.0,
                "amplitudeScale" to 0.7,
                "smooth" to true,
                "mirror" to false
            )
        ),
        VisualizerItem(
            id = "v05",
            name = "Synthwave Grid",
            categoryTags = listOf("edm", "trending"),
            type = "bar",
            thumbnail = "https://wallpapersafari.com/images/neon-grid-wallpaper.jpg",
            description = "Futuristic retro-grid bars reacting dynamically to fast retro-synth soundwaves.",
            size = 230000,
            downloadCount = 42100,
            config = mapOf(
                "background" to "https://i.pinimg.com/originals/a5/d8/d5/a5d8d5dfd1db48ec42661858c49e29a9.gif",
                "color" to "#FF8A00", // Retro Orange
                "barCount" to 32.0,
                "barWidth" to 6.0,
                "direction" to "up",
                "roundCorner" to 6.0,
                "smoothFactor" to 0.25
            )
        )
    )

    private val downloadedIdsFlow = MutableStateFlow<Set<String>>(loadDownloadedIds())
    private val appliedIdFlow = MutableStateFlow<String?>(loadAppliedId())

    override fun getCategories(): Flow<List<VisualizerCategory>> = flow {
        val json = runCatching { firebaseMgr.getString("visualizer_categories") }.getOrNull()
        if (json.isNullOrBlank() || json == "[]") {
            emit(fallbackCategories)
        } else {
            val list: List<VisualizerCategory> = gson.fromJson(json, object : TypeToken<List<VisualizerCategory>>() {}.type)
            emit(list)
        }
    }

    override fun getVisualizerItems(): Flow<List<VisualizerItem>> = flow {
        val json = runCatching { firebaseMgr.getString("visualizer_items") }.getOrNull()
        if (json.isNullOrBlank() || json == "[]") {
            emit(fallbackItems)
        } else {
            val list: List<VisualizerItem> = gson.fromJson(json, object : TypeToken<List<VisualizerItem>>() {}.type)
            emit(list)
        }
    }

    override fun observeDownloadedIds(): Flow<Set<String>> = downloadedIdsFlow

    override fun observeAppliedId(): Flow<String?> = appliedIdFlow

    override suspend fun downloadVisualizer(id: String) {
        val current = loadDownloadedIds().toMutableSet()
        current.add(id)
        pref.downloadedVisualizerIdsJson = gson.toJson(current)
        downloadedIdsFlow.value = current
    }

    override suspend fun applyVisualizer(id: String?) {
        pref.appliedVisualizerId = id ?: ""
        appliedIdFlow.value = if (id.isNullOrEmpty()) null else id
    }

    private fun loadDownloadedIds(): Set<String> {
        val json = pref.downloadedVisualizerIdsJson
        return if (json.isNullOrEmpty()) {
            // First time, v01 and v02 can be pre-downloaded as samples
            setOf("v01", "v02")
        } else {
            gson.fromJson(json, object : TypeToken<Set<String>>() {}.type)
        }
    }

    private fun loadAppliedId(): String? {
        val id = pref.appliedVisualizerId
        return if (id.isEmpty()) "v01" else id // default apply Chill Bass (v01)
    }
}
