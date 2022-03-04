package com.livetl.android.ui.screen.home.settings

import androidx.lifecycle.ViewModel
import com.livetl.android.util.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val prefs: PreferencesHelper,
) : ViewModel()
