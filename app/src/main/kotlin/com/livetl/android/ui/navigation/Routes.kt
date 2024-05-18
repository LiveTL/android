package com.livetl.android.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data class StreamInfo(val urlOrId: String) : Route

    @Serializable
    data object Welcome : Route

    @Serializable
    data class Player(val urlOrId: String) : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object Licenses : Route

    @Serializable
    data object About : Route
}
