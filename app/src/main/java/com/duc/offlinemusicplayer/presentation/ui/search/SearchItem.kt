package com.duc.offlinemusicplayer.presentation.ui.search

import com.duc.offlinemusicplayer.domain.model.Album
import com.duc.offlinemusicplayer.domain.model.Artist
import com.duc.offlinemusicplayer.domain.model.Folder
import com.duc.offlinemusicplayer.domain.model.Playlist
import com.duc.offlinemusicplayer.domain.model.SearchTab
import com.duc.offlinemusicplayer.domain.model.Song

sealed class SearchItem {
    data class Header(val category: SearchTab) : SearchItem()
    data class SongRow(val song: Song) : SearchItem()
    data class PlaylistRow(val playlist: Playlist) : SearchItem()
    data class AlbumRow(val album: Album) : SearchItem()
    data class ArtistRow(val artist: Artist) : SearchItem()
    data class FolderRow(val folder: Folder) : SearchItem()
}
