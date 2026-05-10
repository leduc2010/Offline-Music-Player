package com.duc.offlinemusicplayer.presentation.base

import androidx.lifecycle.ViewModel
import com.duc.offlinemusicplayer.presentation.viewmodel.NavigationViewModel
import com.duc.offlinemusicplayer.presentation.viewmodel.SharedViewModel

abstract class BaseVM : ViewModel() {
    lateinit var sharedViewModel: SharedViewModel
    lateinit var navigationVM: NavigationViewModel
}
