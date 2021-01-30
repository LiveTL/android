package com.livetl.android.navigation

sealed class Route(val id: String) {
    object Home : Route("home")
    object Player : Route("player")
}