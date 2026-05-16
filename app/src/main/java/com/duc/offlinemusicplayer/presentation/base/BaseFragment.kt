package com.duc.offlinemusicplayer.presentation.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.duc.offlinemusicplayer.data.source.local.pref.PreferenceHelper
import com.duc.offlinemusicplayer.presentation.utils.AppLog
import com.duc.offlinemusicplayer.presentation.utils.findNavControllerSafely
import com.duc.offlinemusicplayer.presentation.utils.setLocale
import com.duc.offlinemusicplayer.presentation.viewmodel.NavigationViewModel
import com.duc.offlinemusicplayer.presentation.viewmodel.SharedViewModel
import javax.inject.Inject

abstract class BaseFragment<VB : ViewBinding, VM : BaseVM> : Fragment() {

    val TAG = javaClass.simpleName

    @Inject
    lateinit var pref: PreferenceHelper
    lateinit var mViewModel: VM
    private var _binding: VB? = null
    protected val mBinding get() = _binding!!

    open var hasBackPress = true

    protected val sharedViewModel: SharedViewModel by activityViewModels()
    protected val navigationViewModel: NavigationViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        AppLog.lifeCircle(TAG, "onAttach:")
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppLog.lifeCircle(TAG, "onCreateView:")
        requireContext().setLocale(pref.currentLanguage.code)
        _binding = initViewBinding()
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AppLog.lifeCircle(TAG, "onViewCreated:")
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProvider(this)[getClassVM()]
        mViewModel.sharedViewModel = sharedViewModel
        mViewModel.navigationVM = navigationViewModel
        initView()
    }

    override fun onResume() {
        AppLog.lifeCircle(TAG, "onResume:")
        super.onResume()
        if (hasBackPress) handleBackPress()
    }

    override fun onDestroyView() {
        AppLog.lifeCircle(TAG, "onDestroyView:")
        _binding = null
        super.onDestroyView()
    }

    protected abstract fun getClassVM(): Class<VM>
    protected abstract fun initViewBinding(): VB
    protected abstract fun initView()

    private fun handleBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            onBackPressed()
        }
    }

    open fun onBackPressed() {
        findNavControllerSafely()?.navigateUp()
    }

    protected fun setOnBackPressedDispatcher(onBackPress: (() -> Unit)? = null) {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            onBackPress?.invoke()
        }
    }
}
