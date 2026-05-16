package com.duc.offlinemusicplayer.domain.repository

import com.duc.offlinemusicplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun observeSongs(): Flow<List<Song>>

    suspend fun refreshSongs()

    suspend fun searchSongs(query: String): List<Song>

    suspend fun getSongsByFolder(folderPath: String): List<Song>

    suspend fun getAllFolders(): List<String>
}