package com.duc.offlinemusicplayer.data.source.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duc.offlinemusicplayer.data.source.local.db.entity.AlbumQueryResult
import com.duc.offlinemusicplayer.data.source.local.db.entity.ArtistQueryResult
import com.duc.offlinemusicplayer.data.source.local.db.entity.FolderQueryResult
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

    // Favorites
    @Query("UPDATE songs SET isFavorite = :fav WHERE id = :id")
    suspend fun setFavorite(id: Long, fav: Boolean)

    @Query("SELECT * FROM songs WHERE isFavorite = 1 ORDER BY title COLLATE NOCASE ASC")
    fun observeFavorites(): Flow<List<SongEntity>>

    // Albums derived from songs
    @Query(
        """
        SELECT album AS name, artist, COUNT(*) AS songCount, MIN(albumArtUri) AS albumArtUri, MIN(contentUri) AS sampleSongUri
        FROM songs
        GROUP BY album
        ORDER BY album COLLATE NOCASE ASC
        """
    )
    suspend fun getAllAlbums(): List<AlbumQueryResult>

    @Query(
        """
        SELECT album AS name, artist, COUNT(*) AS songCount, MIN(albumArtUri) AS albumArtUri, MIN(contentUri) AS sampleSongUri
        FROM songs
        WHERE album LIKE '%' || :query || '%'
        GROUP BY album
        ORDER BY album COLLATE NOCASE ASC
        """
    )
    suspend fun searchAlbums(query: String): List<AlbumQueryResult>

    // Artists derived from songs
    @Query(
        """
        SELECT artist AS name, COUNT(*) AS songCount, COUNT(DISTINCT album) AS albumCount, MIN(albumArtUri) AS albumArtUri, MIN(contentUri) AS sampleSongUri
        FROM songs
        GROUP BY artist
        ORDER BY artist COLLATE NOCASE ASC
        """
    )
    suspend fun getAllArtists(): List<ArtistQueryResult>

    @Query(
        """
        SELECT artist AS name, COUNT(*) AS songCount, COUNT(DISTINCT album) AS albumCount, MIN(albumArtUri) AS albumArtUri, MIN(contentUri) AS sampleSongUri
        FROM songs
        WHERE artist LIKE '%' || :query || '%'
        GROUP BY artist
        ORDER BY artist COLLATE NOCASE ASC
        """
    )
    suspend fun searchArtists(query: String): List<ArtistQueryResult>

    // Folders with count
    @Query(
        """
        SELECT folderPath AS path, COUNT(*) AS songCount
        FROM songs
        WHERE folderPath != ''
        GROUP BY folderPath
        ORDER BY folderPath COLLATE NOCASE ASC
        """
    )
    suspend fun getAllFoldersWithCount(): List<FolderQueryResult>

    @Query(
        """
        SELECT folderPath AS path, COUNT(*) AS songCount
        FROM songs
        WHERE folderPath LIKE '%' || :query || '%' AND folderPath != ''
        GROUP BY folderPath
        ORDER BY folderPath COLLATE NOCASE ASC
        """
    )
    suspend fun searchFolders(query: String): List<FolderQueryResult>
}