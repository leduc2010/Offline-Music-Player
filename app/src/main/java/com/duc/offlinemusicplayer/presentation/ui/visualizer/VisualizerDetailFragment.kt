package com.duc.offlinemusicplayer.presentation.ui.visualizer

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.databinding.FragmentVisualizerDetailBinding
import com.duc.offlinemusicplayer.domain.model.VisualizerItem
import com.duc.offlinemusicplayer.presentation.base.BaseFragment
import com.duc.offlinemusicplayer.presentation.utils.safeOnClickListener
import dagger.hilt.android.AndroidEntryPoint

import androidx.navigation.fragment.navArgs

@AndroidEntryPoint
class VisualizerDetailFragment : BaseFragment<FragmentVisualizerDetailBinding, VisualizerViewModel>() {

    private val args: VisualizerDetailFragmentArgs by navArgs()
    private var visualizerId: String = ""
    private var currentItem: VisualizerItem? = null

    override fun getClassVM(): Class<VisualizerViewModel> = VisualizerViewModel::class.java

    override fun initViewBinding(): FragmentVisualizerDetailBinding =
        FragmentVisualizerDetailBinding.inflate(layoutInflater)

    override fun initView() {
        visualizerId = args.visualizerId

        mBinding.btnBack.safeOnClickListener {
            navigationViewModel.back()
        }

        // Live visualizer view should play/animate
        mBinding.visualizerView.setPlaying(true)

        // Observers
        mViewModel.visualizerItems.observe(viewLifecycleOwner) { items ->
            currentItem = items.firstOrNull { it.id == visualizerId }
            bindVisualizerDetails()
        }

        mViewModel.downloadedIds.observe(viewLifecycleOwner) {
            updateActionButtonState()
        }

        mViewModel.appliedId.observe(viewLifecycleOwner) {
            updateActionButtonState()
        }

        mViewModel.downloadProgress.observe(viewLifecycleOwner) { progressMap ->
            val progress = progressMap[visualizerId]
            if (progress != null) {
                mBinding.btnAction.visibility = View.GONE
                mBinding.progressBar.visibility = View.VISIBLE
                mBinding.progressBar.progress = progress
            } else {
                mBinding.progressBar.visibility = View.GONE
                mBinding.btnAction.visibility = View.VISIBLE
                updateActionButtonState()
            }
        }
    }

    private fun bindVisualizerDetails() {
        val item = currentItem ?: return

        mBinding.tvTitle.text = item.name
        mBinding.tvDescription.text = item.description
        
        // Format size: e.g. "106 Kb"
        val kbSize = item.size / 1024
        mBinding.tvSize.text = "${kbSize} Kb"

        // Format downloads
        mBinding.tvDownloads.text = item.downloadCount.toString()

        // Background
        val bgUrl = item.config["background"] as? String ?: item.thumbnail
        Glide.with(this)
            .load(bgUrl)
            .placeholder(R.drawable.shimmer_placeholder)
            .into(mBinding.ivBackground)

        // Setup Visualizer Canvas
        mBinding.visualizerView.setConfig(item.type, item.config)

        updateActionButtonState()
    }

    private fun updateActionButtonState() {
        val item = currentItem ?: return
        val downloaded = mViewModel.downloadedIds.value.orEmpty().contains(item.id)
        val applied = mViewModel.appliedId.value == item.id

        when {
            applied -> {
                mBinding.btnAction.isEnabled = false
                mBinding.btnAction.text = "Applied"
                mBinding.btnAction.setBackgroundResource(R.drawable.bg_button_selector) // Outlined/Disabled style
            }
            downloaded -> {
                mBinding.btnAction.isEnabled = true
                mBinding.btnAction.text = "Apply"
                mBinding.btnAction.setBackgroundResource(R.drawable.bg_button_primary)
                mBinding.btnAction.safeOnClickListener {
                    mViewModel.applyVisualizer(item.id)
                }
            }
            else -> {
                mBinding.btnAction.isEnabled = true
                mBinding.btnAction.text = "Download"
                mBinding.btnAction.setBackgroundResource(R.drawable.bg_button_primary)
                mBinding.btnAction.safeOnClickListener {
                    mViewModel.downloadVisualizer(item.id)
                }
            }
        }
    }
}
