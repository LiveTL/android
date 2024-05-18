package com.livetl.android.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.bottomSheet
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.plusAssign
import androidx.navigation.toRoute
import com.livetl.android.ui.screen.about.AboutScreen
import com.livetl.android.ui.screen.about.LicensesScreen
import com.livetl.android.ui.screen.home.HomeScreen
import com.livetl.android.ui.screen.home.composable.StreamInfo
import com.livetl.android.ui.screen.home.settings.SettingsScreen
import com.livetl.android.ui.screen.player.PlayerScreen
import com.livetl.android.ui.screen.player.PlayerViewModel
import com.livetl.android.ui.screen.welcome.WelcomeScreen

fun NavHostController.navigateToPlayer(urlOrId: String) {
    navigate(Route.Player(urlOrId)) {
        launchSingleTop = true
        popUpTo(Route.Home) {}
    }
}

@Composable
fun mainNavHost(startRoute: Route): NavHostController {
    val navController = rememberNavController()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator

    fun navigateBack() {
        navController.popBackStack()
    }

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        NavHost(navController, startDestination = startRoute) {
            composable<Route.Home> {
                HomeScreen(
                    navigateToStreamInfo = { navController.navigate("${Route.StreamInfo}?urlOrId=$it") },
                    navigateToPlayer = { navController.navigateToPlayer(it) },
                    navigateToSettings = { navController.navigate(Route.Settings) },
                    navigateToAbout = { navController.navigate(Route.About) },
                )
            }

            // TODO: use typesafe data class when possible
            bottomSheet(
                "${Route.StreamInfo}?urlOrId={urlOrId}",
                arguments = listOf(navArgument("urlOrId") { defaultValue = "" }),
            ) { backStackEntry ->
                val urlOrId = backStackEntry.arguments?.getString("urlOrId")!!

                Surface {
                    StreamInfo(urlOrId)
                }
            }

            composable<Route.Welcome> {
                WelcomeScreen(
                    navigateToHome = { navController.navigate(Route.Home) },
                )
            }

            composable<Route.Player> { backStackEntry ->
                val playerViewModel = hiltViewModel<PlayerViewModel>()

                val urlOrId = backStackEntry.toRoute<Route.Player>().urlOrId
                val videoId = playerViewModel.getVideoId(urlOrId)

                PlayerScreen(videoId, playerViewModel)
            }

            composable<Route.Settings> {
                SettingsScreen(
                    onBackPressed = { navigateBack() },
                )
            }

            composable<Route.About> {
                AboutScreen(
                    onBackPressed = { navigateBack() },
                    navigateToLicenses = { navController.navigate(Route.Licenses) },
                    navigateToWelcome = { navController.navigate(Route.Welcome) },
                )
            }

            composable<Route.Licenses> {
                LicensesScreen(
                    onBackPressed = { navigateBack() },
                )
            }
        }
    }

    return navController
}
