package com.duc.offlinemusicplayer.data.mapper

import com.duc.offlinemusicplayer.data.source.local.db.entity.AlbumQueryResult
import com.duc.offlinemusicplayer.data.source.local.db.entity.ArtistQueryResult
import com.duc.offlinemusicplayer.data.source.local.db.entity.FolderQueryResult
import com.duc.offlinemusicplayer.data.source.local.db.PlaylistWithCount
import com.duc.offlinemusicplayer.domain.model.Album
import com.duc.offlinemusicplayer.domain.model.Artist
import com.duc.offlinemusicplayer.domain.model.Folder
import com.duc.offlinemusicplayer.domain.model.Playlist

fun AlbumQueryResult.toDomain() = Album(
    name = name,
    artist = artist,
    songCount = songCount,
    albumArtUri = albumArtUri,
    sampleSongUri = sampleSongUri,
)

fun ArtistQueryResult.toDomain() = Artist(
    name = name,
    songCount = songCount,
    albumCount = albumCount,
    albumArtUri = albumArtUri,
    sampleSongUri = sampleSongUri,
)

fun FolderQueryResult.toDomain() = Folder(
    path = path,
    name = path.trimEnd('/').substringAfterLast('/').ifBlank { path },
    songCount = songCount,
)

fun PlaylistWithCount.toDomain() = Playlist(
    id = id,
    name = name,
    songCount = songCount,
    createdAt = createdAt,
)
