package com.livetl.android.data.feed

import com.livetl.android.data.holodex.HoloDexService
import javax.inject.Inject

class FeedService @Inject constructor(
    private val holoDexService: HoloDexService,
) {

    suspend fun getFeed(organization: String?): Feed {
        val response = holoDexService.getFeed(organization)

        return Feed(
            live = response.items
                .filter { it.status == "live" }
                .sortedByDescending { it.start_actual },
            upcoming = response.items
                .filter { it.status == "upcoming" }
                .sortedBy { it.start_scheduled },
            ended = response.items
                .filter { it.status == "past" }
                .sortedByDescending { it.end_actual },
        )
    }
}
