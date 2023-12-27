package com.livetl.android.ui.navigation

sealed class Route(val id: String) {
    data object Home : Route("home")

    data object StreamInfo : Route("stream")

    data object Welcome : Route("welcome")

    data object Player : Route("player")

    data object Settings : Route("settings")

    data object About : Route("about")

    data object Licenses : Route("licenses")
}
