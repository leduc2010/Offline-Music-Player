package com.duc.offlinemusicplayer.data.repository

import com.duc.offlinemusicplayer.data.mapper.toDomain
import com.duc.offlinemusicplayer.data.source.local.db.SongDao
import com.duc.offlinemusicplayer.data.source.local.media.MediaStoreScanner
import com.duc.offlinemusicplayer.domain.model.Album
import com.duc.offlinemusicplayer.domain.model.Artist
import com.duc.offlinemusicplayer.domain.model.Folder
import com.duc.offlinemusicplayer.domain.model.Song
import com.duc.offlinemusicplayer.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val songDao: SongDao,
    private val mediaStoreScanner: MediaStoreScanner,
) : MusicRepository {

    override fun observeSongs(): Flow<List<Song>> =
        songDao.observeSongs().map { songs -> songs.map { it.toDomain() } }

    override fun observeFavorites(): Flow<List<Song>> =
        songDao.observeFavorites().map { songs -> songs.map { it.toDomain() } }

    override suspend fun refreshSongs() {
        val scannedSongs = mediaStoreScanner.scanSongs()
        songDao.clearSongs()
        if (scannedSongs.isNotEmpty()) songDao.upsertSongs(scannedSongs)
    }

    override suspend fun searchSongs(query: String): List<Song> =
        songDao.searchSongs(query.trim()).map { it.toDomain() }

    override suspend fun getSongsByFolder(folderPath: String): List<Song> =
        songDao.getSongsByFolder(folderPath).map { it.toDomain() }

    override suspend fun getAllFolders(): List<String> = songDao.getAllFolders()

    override suspend fun setFavorite(songId: Long, isFavorite: Boolean) =
        songDao.setFavorite(songId, isFavorite)

    override suspend fun getAllAlbums(): List<Album> =
        songDao.getAllAlbums().map { it.toDomain() }

    override suspend fun searchAlbums(query: String): List<Album> =
        songDao.searchAlbums(query.trim()).map { it.toDomain() }

    override suspend fun getAllArtists(): List<Artist> =
        songDao.getAllArtists().map { it.toDomain() }

    override suspend fun searchArtists(query: String): List<Artist> =
        songDao.searchArtists(query.trim()).map { it.toDomain() }

    override suspend fun getAllFoldersWithCount(): List<Folder> =
        songDao.getAllFoldersWithCount().map { it.toDomain() }

    override suspend fun searchFolders(query: String): List<Folder> =
        songDao.searchFolders(query.trim()).map { it.toDomain() }
}
