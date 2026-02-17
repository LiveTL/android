package com.livetl.android.ui.screen.home.composable

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livetl.android.data.feed.Stream
import com.livetl.android.data.stream.StreamRepository
import com.livetl.android.ui.common.textParser
import com.livetl.android.ui.navigation.Route
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = StreamInfoViewModel.Factory::class)
class StreamInfoViewModel @AssistedInject constructor(
    @Assisted val route: Route.StreamInfo,
    private val streamRepository: StreamRepository,
) : ViewModel() {

    val state = MutableStateFlow(State())

    init {
        viewModelScope.launch { loadStream() }
    }

    private suspend fun loadStream() {
        state.update { it.copy(isLoading = true) }
        val stream = streamRepository.getStream(route.urlOrId)
        state.update {
            it.copy(
                stream = stream,
                description = textParser(stream.description),
                isLoading = false,
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(route: Route.StreamInfo): StreamInfoViewModel
    }

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val stream: Stream? = null,
        val description: AnnotatedString? = null,
    )
}
