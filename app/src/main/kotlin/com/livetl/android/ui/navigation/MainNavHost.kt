package com.livetl.android.ui.navigation

import android.content.IntentFilter
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.livetl.android.ui.BroadcastReceiver
import com.livetl.android.ui.MainActivity
import com.livetl.android.ui.screen.about.AboutScreen
import com.livetl.android.ui.screen.about.LicensesScreen
import com.livetl.android.ui.screen.home.HomeScreen
import com.livetl.android.ui.screen.home.HomeViewModel
import com.livetl.android.ui.screen.player.PlayerScreen
import com.livetl.android.ui.screen.player.PlayerViewModel

@Composable
fun MainNavHost(
    setKeepScreenOn: (Boolean) -> Unit,
    setFullscreen: (Boolean) -> Unit,
) {
    val navController = rememberNavController()

    fun navigateToPlayer(urlOrId: String) {
        navController.navigate("${Route.Player.id}?urlOrId=$urlOrId") {
            launchSingleTop = true
            popUpTo(Route.Home.id) {}
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        NavHost(navController, startDestination = Route.Home.id) {
            composable(Route.Home.id) {
                val homeViewModel = hiltViewModel<HomeViewModel>()

                HomeScreen(
                    navigateToPlayer = { navigateToPlayer(it) },
                    navigateToAbout = { navController.navigate(Route.About.id) },
                    homeViewModel = homeViewModel
                )
            }

            composable(
                "${Route.Player.id}?urlOrId={urlOrId}",
                arguments = listOf(navArgument("urlOrId") { defaultValue = "" })
            ) { backStackEntry ->
                val playerViewModel = hiltViewModel<PlayerViewModel>()

                val urlOrId = backStackEntry.arguments?.getString("urlOrId")!!
                val videoId = playerViewModel.getVideoId(urlOrId)

                PlayerScreen(videoId, setKeepScreenOn, setFullscreen, playerViewModel)
            }

            composable(Route.About.id) {
                AboutScreen(
                    onBackPressed = { navigateBack() },
                    navigateToLicenses = { navController.navigate(Route.Licenses.id) },
                )
            }

            composable(Route.Licenses.id) {
                LicensesScreen(
                    onBackPressed = { navigateBack() }
                )
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
