package com.livetl.android.navigation

import android.content.IntentFilter
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import androidx.navigation.compose.rememberNavController
import com.livetl.android.MainActivity
import com.livetl.android.ui.screen.home.HomeScreen
import com.livetl.android.ui.screen.player.PlayerScreen
import com.livetl.android.util.BroadcastReceiver

@Composable
fun MainNavHost() {
    val navController = rememberNavController()

    fun navigateToPlayer(urlOrId: String) {
        navController.navigate("${Route.Player.id}?urlOrId=$urlOrId") {
            launchSingleTop = true
            popUpTo(Route.Home.id) {}
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        NavHost(navController, startDestination = Route.Home.id) {
            composable(Route.Home.id) {
                HomeScreen(navigateToPlayer = { navigateToPlayer(it) })
            }

            composable(
                "${Route.Player.id}?urlOrId={urlOrId}",
                arguments = listOf(navArgument("urlOrId") { defaultValue = "" })
            ) { backStackEntry ->
                val urlOrId = backStackEntry.arguments?.getString("urlOrId")!!
                PlayerScreen(urlOrId)
            }
        }
    }

    BroadcastReceiver(
        intentFilter = IntentFilter().apply { addAction(MainActivity.DEEP_LINK_INTENT) },
        receiver = {
            val data = it.getStringExtra(MainActivity.DEEP_LINK_INTENT_EXTRA)!!
            navigateToPlayer(data)
        }
    )
}