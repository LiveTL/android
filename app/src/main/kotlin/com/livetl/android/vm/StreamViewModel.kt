package com.livetl.android.vm

import androidx.lifecycle.ViewModel
import com.livetl.android.data.stream.StreamService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StreamViewModel @Inject constructor(
    private val streamService: StreamService
) : ViewModel() {

    fun getVideoId(urlOrId: String): String {
        return streamService.getVideoId(urlOrId)
    }
}
