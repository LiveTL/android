package com.livetl.android.ui.screen.home.composable

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import com.livetl.android.data.feed.Stream
import com.livetl.android.data.stream.StreamRepository
import com.livetl.android.ui.common.textParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class StreamInfoViewModel @Inject constructor(private val streamRepository: StreamRepository) : ViewModel() {
    val state = MutableStateFlow(State())

    suspend fun loadStream(urlOrId: String) {
        state.update { it.copy(isLoading = true) }

        val stream = streamRepository.getStream(urlOrId)
        state.update {
            it.copy(
                stream = stream,
                description = textParser(stream.description),
                isLoading = false,
            )
        }
    }

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val stream: Stream? = null,
        val description: AnnotatedString? = null,
    )
}
