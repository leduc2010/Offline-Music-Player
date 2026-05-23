package com.duc.offlinemusicplayer.data.source.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.duc.offlinemusicplayer.data.source.local.db.entity.PlaylistEntity
import com.duc.offlinemusicplayer.data.source.local.db.entity.PlaylistSongEntity
import com.duc.offlinemusicplayer.data.source.local.db.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Query(
        """
        SELECT p.*, COUNT(ps.songId) AS songCount
        FROM playlists p
        LEFT JOIN playlist_songs ps ON p.id = ps.playlistId
        GROUP BY p.id
        ORDER BY p.name COLLATE NOCASE ASC
        """
    )
    fun observeAllPlaylists(): Flow<List<PlaylistWithCount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(entity: PlaylistEntity): Long

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deletePlaylist(id: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(entity: PlaylistSongEntity)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)

    @Query(
        """
        SELECT s.* FROM songs s
        INNER JOIN playlist_songs ps ON s.id = ps.songId
        WHERE ps.playlistId = :playlistId
        ORDER BY ps.addedAt ASC
        """
    )
    fun observeSongsInPlaylist(playlistId: Long): Flow<List<SongEntity>>

    @Query(
        """
        SELECT p.*, COUNT(ps.songId) AS songCount
        FROM playlists p
        LEFT JOIN playlist_songs ps ON p.id = ps.playlistId
        WHERE p.name LIKE '%' || :query || '%'
        GROUP BY p.id
        ORDER BY p.name COLLATE NOCASE ASC
        """
    )
    suspend fun searchPlaylists(query: String): List<PlaylistWithCount>
}

// Query result holder for playlist + song count
data class PlaylistWithCount(
    val id: Long,
    val name: String,
    val createdAt: Long,
    val songCount: Int,
)
