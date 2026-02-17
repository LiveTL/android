package com.livetl.android.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Route : NavKey {
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
