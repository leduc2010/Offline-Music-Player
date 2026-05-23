package com.duc.offlinemusicplayer.domain.model

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val contentUri: String,
    val albumArtUri: String = "",
    val dateAddedSec: Long,
    val folderPath: String,
    val isFavorite: Boolean = false,
)