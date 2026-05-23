package com.duc.offlinemusicplayer.data.source.local.db.entity

// Query result holders — not @Entity, used for derived GROUP BY queries

data class AlbumQueryResult(
    val name: String,
    val artist: String,
    val songCount: Int,
    val albumArtUri: String,
    val sampleSongUri: String,
)

data class ArtistQueryResult(
    val name: String,
    val songCount: Int,
    val albumCount: Int,
    val albumArtUri: String,
    val sampleSongUri: String,
)

data class FolderQueryResult(
    val path: String,
    val songCount: Int,
)
