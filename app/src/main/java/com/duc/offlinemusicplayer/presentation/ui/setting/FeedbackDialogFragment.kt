package com.duc.offlinemusicplayer.presentation.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.databinding.DialogLeansoftFeedbackBinding

class FeedbackDialogFragment : DialogFragment() {

    private var _binding: DialogLeansoftFeedbackBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLeansoftFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFeedback.setOnClickListener {
            val feedback = binding.edtFeedback.text?.toString()?.trim().orEmpty()
            if (feedback.isEmpty()) {
                Toast.makeText(requireContext(), R.string.feedback, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
