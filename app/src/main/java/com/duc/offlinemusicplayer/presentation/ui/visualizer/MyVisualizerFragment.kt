package com.duc.offlinemusicplayer.presentation.ui.visualizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.databinding.FragmentMyVisualizerBinding
import com.duc.offlinemusicplayer.databinding.ItemVisualizerBinding
import com.duc.offlinemusicplayer.domain.model.VisualizerItem
import com.duc.offlinemusicplayer.presentation.base.BaseFragment
import com.duc.offlinemusicplayer.presentation.utils.safeOnClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyVisualizerFragment : BaseFragment<FragmentMyVisualizerBinding, VisualizerViewModel>() {

    private lateinit var visualizerAdapter: MyVisualizerAdapter

    override fun getClassVM(): Class<VisualizerViewModel> = VisualizerViewModel::class.java

    override fun initViewBinding(): FragmentMyVisualizerBinding =
        FragmentMyVisualizerBinding.inflate(layoutInflater)

    override fun initView() {
        mBinding.btnBack.safeOnClickListener {
            navigationViewModel.back()
        }

        visualizerAdapter = MyVisualizerAdapter { item ->
            navigationViewModel.navigate(MyVisualizerFragmentDirections.actionMyVisualizerToDetail(item.id))
        }

        mBinding.rvMyVisualizer.layoutManager = GridLayoutManager(requireContext(), 3)
        mBinding.rvMyVisualizer.adapter = visualizerAdapter

        // Observe both downloaded IDs and total visualizer items
        mViewModel.downloadedIds.observe(viewLifecycleOwner) {
            updateFilteredList()
        }

        mViewModel.visualizerItems.observe(viewLifecycleOwner) {
            updateFilteredList()
        }
    }

    private fun updateFilteredList() {
        val downloaded = mViewModel.downloadedIds.value.orEmpty()
        val allItems = mViewModel.visualizerItems.value.orEmpty()
        val filtered = allItems.filter { downloaded.contains(it.id) }
        visualizerAdapter.submitList(filtered)
    }

    // --- Inner Grid Adapter ---
    private inner class MyVisualizerAdapter(
        private val onItemClick: (VisualizerItem) -> Unit
    ) : RecyclerView.Adapter<MyVisualizerAdapter.MyVisualizerVH>() {

        private var list = emptyList<VisualizerItem>()

        fun submitList(newList: List<VisualizerItem>) {
            list = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVisualizerVH {
            val binding = ItemVisualizerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MyVisualizerVH(binding)
        }

        override fun onBindViewHolder(holder: MyVisualizerVH, position: Int) {
            val item = list[position]
            holder.binding.tvName.text = item.name

            Glide.with(holder.itemView.context)
                .load(item.thumbnail)
                .placeholder(R.drawable.shimmer_placeholder)
                .into(holder.binding.ivThumbnail)

            holder.itemView.setOnClickListener { onItemClick(item) }
        }

        override fun getItemCount(): Int = list.size

        inner class MyVisualizerVH(val binding: ItemVisualizerBinding) : RecyclerView.ViewHolder(binding.root)
    }
}
