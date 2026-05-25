package com.duc.offlinemusicplayer.domain.model

data class Playlist(
    val id: Long,
    val name: String,
    val songCount: Int,
    val createdAt: Long,
    val isPinned: Boolean = false,
    val pinnedAt: Long = 0L,
)
