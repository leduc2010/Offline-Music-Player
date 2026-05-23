package com.duc.offlinemusicplayer.presentation.ui.songs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.databinding.ItemSongBinding
import com.duc.offlinemusicplayer.domain.model.Song
import com.duc.offlinemusicplayer.utils.loadImage

class SongListAdapter(
    private val onSongClick: (Song, Int) -> Unit,
    private val onFavoriteClick: (Song) -> Unit,
    private val onMoreClick: (Song) -> Unit,
) : ListAdapter<Song, SongListAdapter.VH>(DIFF) {

    var currentPlayingId: Long = -1L
        set(value) {
            val old = field
            field = value
            // Refresh affected rows only
            currentList.indexOfFirst { it.id == old }.takeIf { it >= 0 }?.let { notifyItemChanged(it) }
            currentList.indexOfFirst { it.id == value }.takeIf { it >= 0 }?.let { notifyItemChanged(it) }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.tvTitle.text = song.title
            binding.tvArtist.text = song.artist.ifBlank { "Unknown" }

            val isPlaying = song.id == currentPlayingId
            val titleColor = if (isPlaying) R.color.green_primary else R.color.white
            binding.tvTitle.setTextColor(binding.root.context.getColor(titleColor))

            val artSource = if (song.albumArtUri.isNotBlank()) song.albumArtUri else song.contentUri
            binding.ivAlbumArt.loadImage(
                url = artSource,
                placeholder = R.drawable.ic_music_note,
                centerCrop = true,
                cornerRadiusDp = 8f,
            )

            binding.ivFavorite.isSelected = song.isFavorite

            binding.root.setOnClickListener { onSongClick(song, bindingAdapterPosition) }
            binding.ivFavorite.setOnClickListener { onFavoriteClick(song) }
            binding.ivMore.setOnClickListener { onMoreClick(song) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(o: Song, n: Song) = o.id == n.id
            override fun areContentsTheSame(o: Song, n: Song) = o == n
        }
    }
}
