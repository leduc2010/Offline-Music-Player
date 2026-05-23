package com.duc.offlinemusicplayer.domain.model

data class Artist(
    val name: String,
    val songCount: Int,
    val albumCount: Int,
    val albumArtUri: String = "",
    val sampleSongUri: String = "",
)
