package com.duc.offlinemusicplayer.data.mapper

import com.duc.offlinemusicplayer.data.source.local.db.entity.SongEntity
import com.duc.offlinemusicplayer.domain.model.Song

fun SongEntity.toDomain(): Song {
    return Song(
        id = id,
        title = title,
        artist = artist,
        album = album,
        durationMs = durationMs,
        contentUri = contentUri,
        albumArtUri = albumArtUri,
        dateAddedSec = dateAddedSec,
        folderPath = folderPath,
        isFavorite = isFavorite,
    )
}

fun Song.toEntity(): SongEntity {
    return SongEntity(
        id = id,
        title = title,
        artist = artist,
        album = album,
        durationMs = durationMs,
        contentUri = contentUri,
        albumArtUri = albumArtUri,
        dateAddedSec = dateAddedSec,
        folderPath = folderPath,
        isFavorite = isFavorite,
    )
}