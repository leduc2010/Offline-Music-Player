package com.duc.offlinemusicplayer.presentation.viewmodel

import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.duc.offlinemusicplayer.presentation.utils.ext.Event
import com.leansoft.ads.AdManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {

    private val _actionDestination = MutableLiveData<Event<ActionNavigate>>()
    val actionDestination: LiveData<Event<ActionNavigate>> = _actionDestination

    private val _naviDirection = MutableLiveData<Event<NavDirections>>()
    val naviDirection: LiveData<Event<NavDirections>> = _naviDirection

    private val _actionBack = MutableLiveData<Event<Unit>>()
    val actionBack: LiveData<Event<Unit>> = _actionBack

    private val _actionBackToFrag = MutableLiveData<Event<Int>>()
    val actionBackToFrag: LiveData<Event<Int>> = _actionBackToFrag

    private val _showNativeFullEvent = MutableLiveData<Event<String>>()
    val showNativeFullEvent: LiveData<Event<String>> = _showNativeFullEvent

    private val _showPaywall = MutableLiveData<Event<Unit>>()
    val showPaywall: LiveData<Event<Unit>> = _showPaywall

    fun showNativeFull(placement: String) {
        _showNativeFullEvent.postValue(Event(placement))
    }

    fun navigate(destination: Int, bundle: Bundle? = null, navOptions: NavOptions? = null) {
        _actionDestination.postValue(Event(ActionNavigate(destination, bundle, navOptions)))
    }

    fun navigate(action: ActionNavigate) {
        _actionDestination.postValue(Event(action))
    }

    fun navigate(naviDirections: NavDirections, adPlacement: String? = null) {
        if (adPlacement == null) {
            _naviDirection.postValue(Event(naviDirections))
            return
        }
        try {
            AdManager.instance.showInterAd(adPlacement) {
                navigate(naviDirections)
            }
        } catch (e: WindowManager.BadTokenException) {
            e.printStackTrace()
            navigate(naviDirections)
        }
    }

    fun back(destinationId: Int? = null, adPlacement: String? = null) {
        fun navigateBack() {
            if (destinationId == null) {
                _actionBack.postValue(Event(Unit))
            } else {
                _actionBackToFrag.postValue(Event(destinationId))
            }
        }

        if (adPlacement == null) {
            navigateBack()
            return
        }

        try {
            AdManager.instance.showInterAd(adPlacement) {
                navigateBack()
            }
        } catch (e: WindowManager.BadTokenException) {
            e.printStackTrace()
            navigateBack()
        }
    }

    fun navigatePaywall() {
        _showPaywall.postValue(Event(Unit))
    }
}

data class ActionNavigate(
    val destination: Int,
    val args: Bundle? = null,
    var navOptions: NavOptions? = null,
)
