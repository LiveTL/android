package com.livetl.android.ui.screen.home.tab

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.livetl.android.data.feed.Stream
import com.livetl.android.data.feed.StreamStatus
import com.livetl.android.data.stream.StreamRepository
import com.livetl.android.util.AppPreferences
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class StreamsTabViewModel(
    private val streamRepository: StreamRepository,
    private val prefs: AppPreferences,
    private val status: StreamStatus,
) : ViewModel() {
    val state = MutableStateFlow(State())

    suspend fun loadStreams() {
        state.update { it.copy(isLoading = true) }

        val streams = streamRepository.getStreams(prefs.feedOrganization().get(), status).toImmutableList()
        state.update {
            it.copy(
                streams = streams,
                isLoading = false,
            )
        }
    }

    @Immutable
    data class State(val isLoading: Boolean = true, val streams: ImmutableList<Stream> = persistentListOf())
}
