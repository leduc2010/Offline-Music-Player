package com.duc.offlinemusicplayer.presentation.base

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.duc.offlinemusicplayer.data.source.local.pref.PreferenceHelper
import com.duc.offlinemusicplayer.presentation.utils.setLocale
import javax.inject.Inject

abstract class BaseActivity<VB : ViewBinding, VM : ViewModel> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val mBinding get() = _binding!!

    lateinit var mViewModel: VM

    @Inject
    lateinit var pref: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(pref.currentLanguage.code)
        enableEdgeToEdge()
        _binding = initViewBinding()
        setContentView(_binding!!.root)
        mViewModel = ViewModelProvider(this)[getClassVM()]
        initView()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    protected abstract fun getClassVM(): Class<VM>
    protected abstract fun initViewBinding(): VB
    protected abstract fun initView()
}
