package com.livetl.android.data.stream

import javax.inject.Inject

class VideoIdParser @Inject constructor() {
    fun getVideoId(pageUrl: String): String = when {
        LIVETL_URI_REGEX.matches(pageUrl) -> {
            val videoIdWithQuery = LIVETL_URI_REGEX.find(pageUrl)!!.groupValues[1]
            // We don't do anything with the query parameters right now
            val (videoId, _) = videoIdWithQuery.split('?', limit = 2)
            videoId
        }

        else -> {
            getVideoIdFromYouTubeUrl(pageUrl)
                ?: throw IllegalArgumentException("Invalid YouTube link: $pageUrl")
        }
    }

    private fun getVideoIdFromYouTubeUrl(url: String): String? {
        // Just return the input if it doesn't seem like a URL
        if (!url.startsWith("http")) {
            return url
        }

        var matcher = PAGE_LINK_PATTERN.matcher(url)
        if (matcher.find()) {
            return matcher.group(3)
        }

        matcher = SHORT_LINK_PATTERN.matcher(url)
        if (matcher.find()) {
            return matcher.group(3)
        }
        if (url.matches(GRAPH_REGEX)) {
            return url
        }

        return null
    }
}

private val PAGE_LINK_PATTERN by lazy {
    "(http|https)://(www\\.|m.|)youtube\\.com/watch\\?v=(.+?)( |\\z|&)".toPattern()
}

private val SHORT_LINK_PATTERN by lazy {
    "(http|https)://(www\\.|)youtu.be/(.+?)( |\\z|&)".toPattern()
}

private val GRAPH_REGEX by lazy {
    "\\p{Graph}+?".toRegex()
}

private val LIVETL_URI_REGEX by lazy {
    "livetl://translate/(.+)".toRegex()
}
