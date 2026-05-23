package com.duc.offlinemusicplayer.data.repository

import com.duc.offlinemusicplayer.data.mapper.toDomain
import com.duc.offlinemusicplayer.data.source.local.db.PlaylistDao
import com.duc.offlinemusicplayer.data.source.local.db.entity.PlaylistEntity
import com.duc.offlinemusicplayer.data.source.local.db.entity.PlaylistSongEntity
import com.duc.offlinemusicplayer.domain.model.Playlist
import com.duc.offlinemusicplayer.domain.model.Song
import com.duc.offlinemusicplayer.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
) : PlaylistRepository {

    override fun observeAllPlaylists(): Flow<List<Playlist>> =
        playlistDao.observeAllPlaylists().map { list -> list.map { it.toDomain() } }

    override suspend fun createPlaylist(name: String): Long =
        playlistDao.insertPlaylist(PlaylistEntity(name = name, createdAt = System.currentTimeMillis()))

    override suspend fun deletePlaylist(id: Long) = playlistDao.deletePlaylist(id)

    override suspend fun addSongToPlaylist(playlistId: Long, songId: Long) =
        playlistDao.addSongToPlaylist(PlaylistSongEntity(playlistId, songId, System.currentTimeMillis()))

    override suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) =
        playlistDao.removeSongFromPlaylist(playlistId, songId)

    override fun observeSongsInPlaylist(playlistId: Long): Flow<List<Song>> =
        playlistDao.observeSongsInPlaylist(playlistId).map { list -> list.map { it.toDomain() } }

    override suspend fun searchPlaylists(query: String): List<Playlist> =
        playlistDao.searchPlaylists(query.trim()).map { it.toDomain() }
}
