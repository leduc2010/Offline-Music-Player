package com.duc.offlinemusicplayer.data.source.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.duc.offlinemusicplayer.data.source.local.db.entity.SongEntity

@Database(
    entities = [SongEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}