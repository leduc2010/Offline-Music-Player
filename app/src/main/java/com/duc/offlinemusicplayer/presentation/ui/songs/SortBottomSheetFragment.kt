package com.duc.offlinemusicplayer.presentation.ui.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.duc.offlinemusicplayer.databinding.BottomSheetSortBinding
import com.duc.offlinemusicplayer.domain.model.SortOrder
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortBottomSheetFragment(
    private val initial: SortOrder,
    private val onApply: (SortOrder) -> Unit,
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSortBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetSortBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val (sortBy, isAsc) = decompose(initial)
        when (sortBy) {
            SortBy.ALPHABETICAL -> binding.rbAlphabetical.isChecked = true
            SortBy.DATE_ADDED -> binding.rbDateAdded.isChecked = true
            SortBy.DURATION -> binding.rbDuration.isChecked = true
        }
        if (isAsc) binding.rbAtoZ.isChecked = true else binding.rbZtoA.isChecked = true

        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnApply.setOnClickListener {
            val sb = when (binding.rgSortBy.checkedRadioButtonId) {
                binding.rbDateAdded.id -> SortBy.DATE_ADDED
                binding.rbDuration.id -> SortBy.DURATION
                else -> SortBy.ALPHABETICAL
            }
            val asc = binding.rgOrder.checkedRadioButtonId == binding.rbAtoZ.id
            onApply(compose(sb, asc))
            dismiss()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private enum class SortBy { ALPHABETICAL, DATE_ADDED, DURATION }

    private fun decompose(order: SortOrder): Pair<SortBy, Boolean> = when (order) {
        SortOrder.ALPHABETICAL_ASC -> SortBy.ALPHABETICAL to true
        SortOrder.ALPHABETICAL_DESC -> SortBy.ALPHABETICAL to false
        SortOrder.DATE_ADDED_ASC -> SortBy.DATE_ADDED to true
        SortOrder.DATE_ADDED_DESC -> SortBy.DATE_ADDED to false
        SortOrder.DURATION_ASC -> SortBy.DURATION to true
        SortOrder.DURATION_DESC -> SortBy.DURATION to false
    }

    private fun compose(sb: SortBy, asc: Boolean): SortOrder = when (sb) {
        SortBy.ALPHABETICAL -> if (asc) SortOrder.ALPHABETICAL_ASC else SortOrder.ALPHABETICAL_DESC
        SortBy.DATE_ADDED -> if (asc) SortOrder.DATE_ADDED_ASC else SortOrder.DATE_ADDED_DESC
        SortBy.DURATION -> if (asc) SortOrder.DURATION_ASC else SortOrder.DURATION_DESC
    }
}
