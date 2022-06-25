package com.livetl.android.data.stream

import com.livetl.android.data.feed.FeedService
import com.livetl.android.data.feed.Stream
import com.livetl.android.data.feed.StreamStatus
import javax.inject.Inject

/**
 * An in-memory cache of stream info to avoid unnecessary network calls where possible.
 */
class StreamRepository @Inject constructor(
    private val videoIdParser: VideoIdParser,
    private val feedService: FeedService,
) {

    private val streams = mutableMapOf<String, Stream>()

    suspend fun getStreams(
        organization: String?,
        status: StreamStatus,
    ): List<Stream> {
        val feed = feedService.getFeed(organization, status)
        feed.forEach {
            streams[it.id] = it
        }
        return feed
    }

    suspend fun getStream(urlOrId: String): Stream {
        val id = videoIdParser.getVideoId(urlOrId)
        return streams.getOrPut(id) { feedService.getVideoInfo(id) }
    }
}
