package com.duc.offlinemusicplayer.domain.repository

import com.duc.offlinemusicplayer.domain.model.Album
import com.duc.offlinemusicplayer.domain.model.Artist
import com.duc.offlinemusicplayer.domain.model.Folder
import com.duc.offlinemusicplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun observeSongs(): Flow<List<Song>>
    fun observeFavorites(): Flow<List<Song>>
    suspend fun refreshSongs()
    suspend fun searchSongs(query: String): List<Song>
    suspend fun getSongsByFolder(folderPath: String): List<Song>
    suspend fun getAllFolders(): List<String>
    suspend fun setFavorite(songId: Long, isFavorite: Boolean)

    suspend fun getAllAlbums(): List<Album>
    suspend fun searchAlbums(query: String): List<Album>

    suspend fun getAllArtists(): List<Artist>
    suspend fun searchArtists(query: String): List<Artist>

    suspend fun getAllFoldersWithCount(): List<Folder>
    suspend fun searchFolders(query: String): List<Folder>
}