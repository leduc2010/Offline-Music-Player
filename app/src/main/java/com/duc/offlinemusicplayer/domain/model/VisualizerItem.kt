package com.duc.offlinemusicplayer.domain.model

data class VisualizerItem(
    val id: String,
    val name: String,
    val categoryTags: List<String>,
    val type: String, // "bar", "wave", "blob"
    val thumbnail: String,
    val description: String,
    val size: Long,
    val downloadCount: Long,
    val config: Map<String, Any>
)
