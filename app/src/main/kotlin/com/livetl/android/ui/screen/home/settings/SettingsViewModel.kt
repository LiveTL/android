package com.livetl.android.ui.screen.home.settings

import androidx.lifecycle.ViewModel
import com.livetl.android.util.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(val prefs: AppPreferences) : ViewModel()
