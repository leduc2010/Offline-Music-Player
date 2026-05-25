package com.duc.offlinemusicplayer.domain.repository

import com.duc.offlinemusicplayer.domain.model.VisualizerCategory
import com.duc.offlinemusicplayer.domain.model.VisualizerItem
import kotlinx.coroutines.flow.Flow

interface VisualizerRepository {
    fun getCategories(): Flow<List<VisualizerCategory>>
    fun getVisualizerItems(): Flow<List<VisualizerItem>>
    fun observeDownloadedIds(): Flow<Set<String>>
    fun observeAppliedId(): Flow<String?>
    suspend fun downloadVisualizer(id: String)
    suspend fun applyVisualizer(id: String?)
}
