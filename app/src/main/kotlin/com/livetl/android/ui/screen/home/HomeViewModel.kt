package com.livetl.android.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.livetl.android.data.feed.StreamStatus
import com.livetl.android.data.stream.StreamRepository
import com.livetl.android.ui.screen.home.tab.StreamsTabViewModel
import com.livetl.android.util.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val streamRepository: StreamRepository,
        val prefs: AppPreferences,
    ) : ViewModel() {
        val tabs =
            StreamStatus.values().map {
                it to
                    StreamsTabViewModel(
                        streamRepository = streamRepository,
                        prefs = prefs,
                        status = it,
                    )
            }

        var showOpenVideoDialog by mutableStateOf(false)
        var openVideoUrl by mutableStateOf("")

        fun showOpenVideoDialog() {
            showOpenVideoDialog = true
        }

        fun hideOpenVideoDialog() {
            showOpenVideoDialog = false
        }
    }
