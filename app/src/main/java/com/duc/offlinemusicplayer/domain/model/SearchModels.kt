package com.duc.offlinemusicplayer.domain.model

enum class SearchTab(val titleResName: String) {
    ALL("tab_all"),
    SONGS("songs"),
    PLAYLISTS("playlists"),
    ALBUMS("albums"),
    ARTISTS("artists"),
    FOLDERS("folders"),
}

data class SearchResults(
    val songs: List<Song> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val folders: List<Folder> = emptyList(),
) {
    val isEmpty: Boolean
        get() = songs.isEmpty() && playlists.isEmpty() && albums.isEmpty() && artists.isEmpty() && folders.isEmpty()
}
