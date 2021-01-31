package com.livetl.android.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.livetl.android.ui.screen.home.HomeScreen
import com.livetl.android.ui.screen.player.PlayerScreen

val YOUTUBE_URIS = listOf(
    "https://youtu.be/{urlOrId}",
    "https://youtube.com/watch?v={urlOrId}",
    "https://m.youtube.com/watch?v={urlOrId}",
    "https://www.youtube.com/watch?v={urlOrId}",
)

@Composable
fun MainNavHost() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        NavHost(navController, startDestination = Route.Home.id) {
            composable(Route.Home.id) {
                HomeScreen(
                    navigateToPlayer = { navController.navigate(Route.Player.id) }
                )
            }

            composable(
                "${Route.Player.id}?urlOrId={urlOrId}",
                arguments = listOf(navArgument("urlOrId") { defaultValue = ""; type = NavType.StringType }),
//                deepLinks = YOUTUBE_URIS.map { navDeepLink { uriPattern = it } }
            ) { backStackEntry ->
                val urlOrId = backStackEntry.arguments?.getString("urlOrId")
                PlayerScreen(urlOrId)
            }
        }
    }
}