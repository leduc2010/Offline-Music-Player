package com.duc.offlinemusicplayer.presentation.ui.language

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.duc.offlinemusicplayer.databinding.FragmentLanguageBinding
import com.duc.offlinemusicplayer.presentation.base.BaseFragment
import com.duc.offlinemusicplayer.presentation.utils.safeOnClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageFragment : BaseFragment<FragmentLanguageBinding, LanguageViewModel>() {

    private lateinit var languageAdapter: LanguageAdapter
    private var selectedLanguageCode: String = "en"

    override fun getClassVM(): Class<LanguageViewModel> = LanguageViewModel::class.java

    override fun initViewBinding(): FragmentLanguageBinding =
        FragmentLanguageBinding.inflate(layoutInflater)

    override fun initView() {
        val current = mViewModel.getCurrentLanguage()
        selectedLanguageCode = current.code

        mBinding.toolbar.setOnBackClickListener {
            navigationViewModel.back()
        }

        mBinding.btnOKContainer.visibility = View.VISIBLE
        mBinding.btnOK.visibility = View.GONE
        mBinding.btnTick.visibility = View.VISIBLE

        languageAdapter = LanguageAdapter(
            onItemClick = { item ->
                selectedLanguageCode = item.code
            },
            selectedLanguageCode = selectedLanguageCode
        )

        mBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mBinding.recyclerView.adapter = languageAdapter
        languageAdapter.setItems(mViewModel.listLanguage)

        mBinding.handClick.visibility = View.GONE

        mBinding.btnTick.safeOnClickListener { applyLanguage(current.code) }
        mBinding.btnOK.safeOnClickListener { applyLanguage(current.code) }
    }

    private fun applyLanguage(currentCode: String) {
        if (selectedLanguageCode == currentCode) {
            navigationViewModel.back()
            return
        }
        val selected = mViewModel.listLanguage
            .firstOrNull { it.code == selectedLanguageCode } ?: return
        mViewModel.changeLanguage(selected)
        mViewModel.setNeedReinit(true)
        navigationViewModel.back()
    }
}
