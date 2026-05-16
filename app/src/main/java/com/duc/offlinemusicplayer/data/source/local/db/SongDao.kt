package com.duc.offlinemusicplayer.data.source.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duc.offlinemusicplayer.data.source.local.db.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs ORDER BY title COLLATE NOCASE ASC")
    fun observeSongs(): Flow<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSongs(songs: List<SongEntity>)

    @Query("DELETE FROM songs")
    suspend fun clearSongs()

    @Query(
        """
        SELECT * FROM songs
        WHERE title LIKE '%' || :query || '%'
           OR artist LIKE '%' || :query || '%'
           OR album LIKE '%' || :query || '%'
        ORDER BY title COLLATE NOCASE ASC
        """
    )
    suspend fun searchSongs(query: String): List<SongEntity>

    @Query(
        """
        SELECT * FROM songs
        WHERE folderPath = :folderPath
        ORDER BY title COLLATE NOCASE ASC
        """
    )
    suspend fun getSongsByFolder(folderPath: String): List<SongEntity>

    @Query(
        """
        SELECT DISTINCT folderPath
        FROM songs
        WHERE folderPath != ''
        ORDER BY folderPath COLLATE NOCASE ASC
        """
    )
    suspend fun getAllFolders(): List<String>
}