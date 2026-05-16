package com.duc.offlinemusicplayer.data.repository

import com.duc.offlinemusicplayer.data.mapper.toDomain
import com.duc.offlinemusicplayer.data.source.local.db.SongDao
import com.duc.offlinemusicplayer.data.source.local.media.MediaStoreScanner
import com.duc.offlinemusicplayer.domain.model.Song
import com.duc.offlinemusicplayer.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val songDao: SongDao,
    private val mediaStoreScanner: MediaStoreScanner,
) : MusicRepository {

    override fun observeSongs(): Flow<List<Song>> {
        return songDao.observeSongs().map { songs -> songs.map { it.toDomain() } }
    }

    override suspend fun refreshSongs() {
        val scannedSongs = mediaStoreScanner.scanSongs()
        songDao.clearSongs()
        if (scannedSongs.isNotEmpty()) {
            songDao.upsertSongs(scannedSongs)
        }
    }

    override suspend fun searchSongs(query: String): List<Song> {
        return songDao.searchSongs(query.trim()).map { it.toDomain() }
    }

    override suspend fun getSongsByFolder(folderPath: String): List<Song> {
        return songDao.getSongsByFolder(folderPath).map { it.toDomain() }
    }

    override suspend fun getAllFolders(): List<String> {
        return songDao.getAllFolders()
    }

}