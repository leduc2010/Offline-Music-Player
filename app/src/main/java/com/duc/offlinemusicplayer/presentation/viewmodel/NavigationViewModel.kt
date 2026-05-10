package com.duc.offlinemusicplayer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.duc.offlinemusicplayer.presentation.utils.ext.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {
    private val _navDirections = MutableStateFlow<Event<NavDirections>?>(null)
    val navDirections = _navDirections.asStateFlow()

    private val _backPress = MutableStateFlow<Event<Boolean>?>(null)
    val backPress = _backPress.asStateFlow()

    fun navigate(directions: NavDirections) {
        _navDirections.value = Event(directions)
    }

    fun back() {
        _backPress.value = Event(true)
    }
}
