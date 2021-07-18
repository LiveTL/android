package com.livetl.android.ui.navigation

sealed class Route(val id: String) {
    object Home : Route("home")
    object Welcome : Route("welcome")
    object Player : Route("player")
    object About : Route("about")
    object Licenses : Route("licenses")
}
