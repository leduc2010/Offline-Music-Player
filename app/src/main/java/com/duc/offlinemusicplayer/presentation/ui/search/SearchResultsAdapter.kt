package com.duc.offlinemusicplayer.presentation.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.databinding.ItemCollectionBinding
import com.duc.offlinemusicplayer.databinding.ItemSearchSectionHeaderBinding
import com.duc.offlinemusicplayer.databinding.ItemSongBinding
import com.duc.offlinemusicplayer.domain.model.Album
import com.duc.offlinemusicplayer.domain.model.Artist
import com.duc.offlinemusicplayer.domain.model.Folder
import com.duc.offlinemusicplayer.domain.model.Playlist
import com.duc.offlinemusicplayer.domain.model.SearchTab
import com.duc.offlinemusicplayer.domain.model.Song
import com.duc.offlinemusicplayer.utils.loadImage

class SearchResultsAdapter(
    private val onSongClick: (Song) -> Unit,
    private val onSongFavoriteClick: (Song) -> Unit,
    private val onSongMoreClick: (Song) -> Unit,
    private val onPlaylistClick: (Playlist) -> Unit,
    private val onAlbumClick: (Album) -> Unit,
    private val onArtistClick: (Artist) -> Unit,
    private val onFolderClick: (Folder) -> Unit,
    private val onViewAllClick: (SearchTab) -> Unit,
) : ListAdapter<SearchItem, RecyclerView.ViewHolder>(DIFF) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is SearchItem.Header -> TYPE_HEADER
        is SearchItem.SongRow -> TYPE_SONG
        is SearchItem.PlaylistRow -> TYPE_PLAYLIST
        is SearchItem.AlbumRow -> TYPE_ALBUM
        is SearchItem.ArtistRow -> TYPE_ARTIST
        is SearchItem.FolderRow -> TYPE_FOLDER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderVH(ItemSearchSectionHeaderBinding.inflate(inflater, parent, false))
            TYPE_SONG -> SongVH(ItemSongBinding.inflate(inflater, parent, false))
            else -> CollectionVH(ItemCollectionBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is SearchItem.Header -> (holder as HeaderVH).bind(item)
            is SearchItem.SongRow -> (holder as SongVH).bind(item.song)
            is SearchItem.PlaylistRow -> (holder as CollectionVH).bindPlaylist(item.playlist)
            is SearchItem.AlbumRow -> (holder as CollectionVH).bindAlbum(item.album)
            is SearchItem.ArtistRow -> (holder as CollectionVH).bindArtist(item.artist)
            is SearchItem.FolderRow -> (holder as CollectionVH).bindFolder(item.folder)
        }
    }

    inner class HeaderVH(private val b: ItemSearchSectionHeaderBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: SearchItem.Header) {
            b.tvSection.setText(sectionTitleRes(item.category))
            b.tvViewAll.setOnClickListener { onViewAllClick(item.category) }
        }
    }

    inner class SongVH(private val b: ItemSongBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(song: Song) {
            b.tvTitle.text = song.title
            b.tvArtist.text = song.artist.ifBlank { "Unknown" }
            val artSource = song.albumArtUri.ifBlank { song.contentUri }
            b.ivAlbumArt.loadImage(
                url = artSource,
                placeholder = R.drawable.ic_music_note,
                centerCrop = true,
                cornerRadiusDp = 8f,
            )
            b.ivFavorite.isSelected = song.isFavorite
            b.root.setOnClickListener { onSongClick(song) }
            b.ivFavorite.setOnClickListener { onSongFavoriteClick(song) }
            b.ivMore.setOnClickListener { onSongMoreClick(song) }
        }
    }

    inner class CollectionVH(private val b: ItemCollectionBinding) : RecyclerView.ViewHolder(b.root) {
        private fun setIconPadding(dp: Int) {
            val px = (dp * b.root.context.resources.displayMetrics.density).toInt()
            b.ivIcon.setPadding(px, px, px, px)
        }

        fun bindPlaylist(p: Playlist) {
            setIconPadding(10)
            b.ivIcon.setBackgroundResource(R.drawable.bg_collection_icon)
            b.ivIcon.setImageResource(R.drawable.ic_playlist)
            b.ivIcon.imageTintList = android.content.res.ColorStateList.valueOf(
                b.root.context.getColor(R.color.green_primary)
            )
            b.tvName.text = p.name
            b.tvSubtitle.text = b.root.context.getString(R.string.songs_count_format, p.songCount)
            b.root.setOnClickListener { onPlaylistClick(p) }
            b.ivMore.setOnClickListener { onPlaylistClick(p) }
        }

        fun bindAlbum(a: Album) {
            val artSource = when {
                a.albumArtUri.isNotBlank() -> a.albumArtUri
                a.sampleSongUri.isNotBlank() -> a.sampleSongUri
                else -> null
            }
            if (!artSource.isNullOrBlank()) {
                setIconPadding(0)
                b.ivIcon.imageTintList = null
                b.ivIcon.loadImage(
                    url = artSource,
                    placeholder = R.drawable.ic_music_note,
                    centerCrop = true,
                    cornerRadiusDp = 8f,
                )
            } else {
                setIconPadding(10)
                b.ivIcon.setBackgroundResource(R.drawable.bg_collection_icon)
                b.ivIcon.setImageResource(R.drawable.ic_music_note)
                b.ivIcon.imageTintList = android.content.res.ColorStateList.valueOf(
                    b.root.context.getColor(R.color.green_primary)
                )
            }
            b.tvName.text = a.name.ifBlank { "Unknown" }
            b.tvSubtitle.text = b.root.context.getString(
                R.string.album_subtitle_format, a.songCount, a.artist.ifBlank { "Unknown" }
            )
            b.root.setOnClickListener { onAlbumClick(a) }
            b.ivMore.setOnClickListener { onAlbumClick(a) }
        }

        fun bindArtist(a: Artist) {
            val artSource = when {
                a.albumArtUri.isNotBlank() -> a.albumArtUri
                a.sampleSongUri.isNotBlank() -> a.sampleSongUri
                else -> null
            }
            if (!artSource.isNullOrBlank()) {
                setIconPadding(0)
                b.ivIcon.imageTintList = null
                b.ivIcon.loadImage(
                    url = artSource,
                    placeholder = R.drawable.ic_music_note,
                    centerCrop = true,
                    cornerRadiusDp = 8f,
                )
            } else {
                setIconPadding(10)
                b.ivIcon.setBackgroundResource(R.drawable.bg_collection_icon)
                b.ivIcon.setImageResource(R.drawable.ic_music_note)
                b.ivIcon.imageTintList = android.content.res.ColorStateList.valueOf(
                    b.root.context.getColor(R.color.green_primary)
                )
            }
            b.tvName.text = a.name.ifBlank { "Unknown" }
            b.tvSubtitle.text = b.root.context.getString(
                R.string.artist_subtitle_format, a.songCount, a.albumCount
            )
            b.root.setOnClickListener { onArtistClick(a) }
            b.ivMore.setOnClickListener { onArtistClick(a) }
        }

        fun bindFolder(f: Folder) {
            setIconPadding(10)
            b.ivIcon.setBackgroundResource(R.drawable.bg_collection_icon)
            b.ivIcon.setImageResource(R.drawable.ic_folder)
            b.ivIcon.imageTintList = android.content.res.ColorStateList.valueOf(
                b.root.context.getColor(R.color.green_primary)
            )
            b.tvName.text = f.name
            b.tvSubtitle.text = b.root.context.getString(R.string.songs_count_format, f.songCount)
            b.root.setOnClickListener { onFolderClick(f) }
            b.ivMore.setOnClickListener { onFolderClick(f) }
        }
    }

    private fun sectionTitleRes(tab: SearchTab): Int = when (tab) {
        SearchTab.SONGS -> R.string.songs
        SearchTab.PLAYLISTS -> R.string.playlists
        SearchTab.ALBUMS -> R.string.albums
        SearchTab.ARTISTS -> R.string.artists
        SearchTab.FOLDERS -> R.string.folders
        SearchTab.ALL -> R.string.tab_all
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_SONG = 1
        private const val TYPE_PLAYLIST = 2
        private const val TYPE_ALBUM = 3
        private const val TYPE_ARTIST = 4
        private const val TYPE_FOLDER = 5

        private val DIFF = object : DiffUtil.ItemCallback<SearchItem>() {
            override fun areItemsTheSame(o: SearchItem, n: SearchItem): Boolean = when {
                o is SearchItem.Header && n is SearchItem.Header -> o.category == n.category
                o is SearchItem.SongRow && n is SearchItem.SongRow -> o.song.id == n.song.id
                o is SearchItem.PlaylistRow && n is SearchItem.PlaylistRow -> o.playlist.id == n.playlist.id
                o is SearchItem.AlbumRow && n is SearchItem.AlbumRow -> o.album.name == n.album.name
                o is SearchItem.ArtistRow && n is SearchItem.ArtistRow -> o.artist.name == n.artist.name
                o is SearchItem.FolderRow && n is SearchItem.FolderRow -> o.folder.path == n.folder.path
                else -> false
            }

            override fun areContentsTheSame(o: SearchItem, n: SearchItem): Boolean = o == n
        }
    }
}
