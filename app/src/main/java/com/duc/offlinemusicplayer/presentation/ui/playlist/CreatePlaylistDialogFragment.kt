package com.duc.offlinemusicplayer.presentation.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.duc.offlinemusicplayer.databinding.DialogCreatePlaylistBinding

class CreatePlaylistDialogFragment(
    private val initialName: String = "",
    private val onConfirm: (String) -> Unit
) : DialogFragment() {

    private var _binding: DialogCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If editing/renaming, pre-populate name
        if (initialName.isNotBlank()) {
            binding.tvDialogTitle.text = "Rename Playlist"
            binding.edtPlaylistName.setText(initialName)
            binding.btnCreate.text = "Save"
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnCreate.setOnClickListener {
            val name = binding.edtPlaylistName.text.toString().trim()
            if (name.isNotBlank()) {
                onConfirm(name)
                dismiss()
            }
        }

        // Auto focus edit text and show keyboard
        binding.edtPlaylistName.requestFocus()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        // Set dialog background to transparent so our rounded corners show
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
