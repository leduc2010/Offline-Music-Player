package com.duc.offlinemusicplayer.presentation.ui.visualizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.databinding.FragmentVisualizerBinding
import com.duc.offlinemusicplayer.databinding.ItemVisualizerBinding
import com.duc.offlinemusicplayer.databinding.ItemVisualizerTabBinding
import com.duc.offlinemusicplayer.domain.model.VisualizerCategory
import com.duc.offlinemusicplayer.domain.model.VisualizerItem
import com.duc.offlinemusicplayer.presentation.base.BaseFragment
import com.duc.offlinemusicplayer.presentation.utils.safeOnClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VisualizerFragment : BaseFragment<FragmentVisualizerBinding, VisualizerViewModel>() {

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var visualizerAdapter: VisualizerAdapter

    override fun getClassVM(): Class<VisualizerViewModel> = VisualizerViewModel::class.java

    override fun initViewBinding(): FragmentVisualizerBinding =
        FragmentVisualizerBinding.inflate(layoutInflater)

    override fun initView() {
        mBinding.btnClose.safeOnClickListener {
            navigationViewModel.back()
        }

        mBinding.btnMyVisualizer.safeOnClickListener {
            navigationViewModel.navigate(VisualizerFragmentDirections.actionVisualizerToMyVisualizer())
        }

        // Setup Category Tabs Adapter
        categoryAdapter = CategoryAdapter { category ->
            mViewModel.selectCategory(category.tag)
        }
        mBinding.rvCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvCategories.adapter = categoryAdapter

        // Setup Visualizer Grid Adapter
        visualizerAdapter = VisualizerAdapter { item ->
            navigationViewModel.navigate(VisualizerFragmentDirections.actionVisualizerToDetail(item.id))
        }
        mBinding.rvVisualizer.layoutManager = GridLayoutManager(requireContext(), 3)
        mBinding.rvVisualizer.adapter = visualizerAdapter

        // Observers
        mViewModel.categories.observe(viewLifecycleOwner) {
            categoryAdapter.submitList(it)
        }

        mViewModel.selectedCategoryTag.observe(viewLifecycleOwner) { activeTag ->
            categoryAdapter.setSelectedTag(activeTag)
            filterAndSubmitItems()
        }

        mViewModel.visualizerItems.observe(viewLifecycleOwner) {
            filterAndSubmitItems()
        }
    }

    private fun filterAndSubmitItems() {
        val activeTag = mViewModel.selectedCategoryTag.value ?: "trending"
        val allItems = mViewModel.visualizerItems.value.orEmpty()
        val filtered = allItems.filter { it.categoryTags.contains(activeTag) }
        visualizerAdapter.submitList(filtered)
    }

    // --- Inner Category Adapter ---
    private inner class CategoryAdapter(
        private val onTabClick: (VisualizerCategory) -> Unit
    ) : RecyclerView.Adapter<CategoryAdapter.CategoryVH>() {

        private var list = emptyList<VisualizerCategory>()
        private var selectedTag: String = "trending"

        fun submitList(newList: List<VisualizerCategory>) {
            list = newList
            notifyDataSetChanged()
        }

        fun setSelectedTag(tag: String) {
            selectedTag = tag
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVH {
            val binding = ItemVisualizerTabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CategoryVH(binding)
        }

        override fun onBindViewHolder(holder: CategoryVH, position: Int) {
            val item = list[position]
            holder.binding.tvTagName.text = item.name

            val isSelected = item.tag == selectedTag
            if (isSelected) {
                holder.binding.tvTagName.setTextColor(requireContext().getColor(R.color.white))
            } else {
                holder.binding.tvTagName.setTextColor(requireContext().getColor(R.color.text_secondary))
            }
            holder.binding.indicator.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE

            holder.itemView.setOnClickListener { onTabClick(item) }
        }

        override fun getItemCount(): Int = list.size

        inner class CategoryVH(val binding: ItemVisualizerTabBinding) : RecyclerView.ViewHolder(binding.root)
    }

    // --- Inner Visualizer Grid Adapter ---
    private inner class VisualizerAdapter(
        private val onItemClick: (VisualizerItem) -> Unit
    ) : RecyclerView.Adapter<VisualizerAdapter.VisualizerVH>() {

        private var list = emptyList<VisualizerItem>()

        fun submitList(newList: List<VisualizerItem>) {
            list = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisualizerVH {
            val binding = ItemVisualizerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return VisualizerVH(binding)
        }

        override fun onBindViewHolder(holder: VisualizerVH, position: Int) {
            val item = list[position]
            holder.binding.tvName.text = item.name

            Glide.with(holder.itemView.context)
                .load(item.thumbnail)
                .placeholder(R.drawable.shimmer_placeholder)
                .into(holder.binding.ivThumbnail)

            holder.itemView.setOnClickListener { onItemClick(item) }
        }

        override fun getItemCount(): Int = list.size

        inner class VisualizerVH(val binding: ItemVisualizerBinding) : RecyclerView.ViewHolder(binding.root)
    }
}
