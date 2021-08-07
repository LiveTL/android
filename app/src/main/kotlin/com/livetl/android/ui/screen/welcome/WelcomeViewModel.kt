package com.livetl.android.ui.screen.welcome

import androidx.lifecycle.ViewModel
import com.livetl.android.util.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    val prefs: PreferencesHelper,
) : ViewModel() {

    fun dismissWelcomeScreen() {
        prefs.showWelcomeScreen().set(false)
    }
}
