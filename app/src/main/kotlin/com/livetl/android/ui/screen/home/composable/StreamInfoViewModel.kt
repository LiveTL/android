package com.livetl.android.ui.screen.home.composable

import androidx.lifecycle.ViewModel
import com.livetl.android.data.feed.Stream
import com.livetl.android.data.stream.StreamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StreamInfoViewModel @Inject constructor(private val streamRepository: StreamRepository) : ViewModel() {
    suspend fun getStream(urlOrId: String): Stream = streamRepository.getStream(urlOrId)
}
