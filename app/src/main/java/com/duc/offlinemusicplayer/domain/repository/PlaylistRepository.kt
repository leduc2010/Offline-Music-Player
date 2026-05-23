package com.duc.offlinemusicplayer.domain.repository

import com.duc.offlinemusicplayer.domain.model.Playlist
import com.duc.offlinemusicplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun observeAllPlaylists(): Flow<List<Playlist>>
    suspend fun createPlaylist(name: String): Long
    suspend fun deletePlaylist(id: Long)
    suspend fun addSongToPlaylist(playlistId: Long, songId: Long)
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)
    fun observeSongsInPlaylist(playlistId: Long): Flow<List<Song>>
    suspend fun searchPlaylists(query: String): List<Playlist>
}
