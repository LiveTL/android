package com.livetl.android.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.livetl.android.ui.screen.about.AboutScreen
import com.livetl.android.ui.screen.about.LicensesScreen
import com.livetl.android.ui.screen.home.HomeScreen
import com.livetl.android.ui.screen.home.HomeViewModel
import com.livetl.android.ui.screen.home.composable.StreamInfo
import com.livetl.android.ui.screen.home.composable.StreamInfoViewModel
import com.livetl.android.ui.screen.home.settings.SettingsScreen
import com.livetl.android.ui.screen.home.settings.SettingsViewModel
import com.livetl.android.ui.screen.player.PlayerScreen
import com.livetl.android.ui.screen.player.PlayerViewModel
import com.livetl.android.ui.screen.welcome.WelcomeScreen
import com.livetl.android.ui.screen.welcome.WelcomeViewModel

fun NavHostController.navigateToPlayer(urlOrId: String) {
    navigate("${Route.Player.id}?urlOrId=$urlOrId") {
        launchSingleTop = true
        popUpTo(Route.Home.id) {}
    }
}

@Composable
fun mainNavHost(
    startRoute: Route,
    setKeepScreenOn: (Boolean) -> Unit,
    setFullscreen: (Boolean) -> Unit,
): NavHostController {
    val navController = rememberNavController()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator

    fun navigateBack() {
        navController.popBackStack()
    }

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        NavHost(navController, startDestination = startRoute.id) {
            composable(Route.Home.id) {
                val homeViewModel = hiltViewModel<HomeViewModel>()

                HomeScreen(
                    navigateToStreamInfo = { navController.navigate("${Route.StreamInfo.id}?urlOrId=$it") },
                    navigateToPlayer = { navController.navigateToPlayer(it) },
                    navigateToSettings = { navController.navigate(Route.Settings.id) },
                    navigateToAbout = { navController.navigate(Route.About.id) },
                    viewModel = homeViewModel,
                )
            }

            bottomSheet(
                "${Route.StreamInfo.id}?urlOrId={urlOrId}",
                arguments = listOf(navArgument("urlOrId") { defaultValue = "" }),
            ) { backStackEntry ->
                val streamInfoViewModel = hiltViewModel<StreamInfoViewModel>()

                val urlOrId = backStackEntry.arguments?.getString("urlOrId")!!

                StreamInfo(
                    urlOrId = urlOrId,
                    viewModel = streamInfoViewModel,
                )
            }

            composable(Route.Welcome.id) {
                val welcomeViewModel = hiltViewModel<WelcomeViewModel>()

                WelcomeScreen(
                    navigateToHome = { navController.navigate(Route.Home.id) },
                    viewModel = welcomeViewModel,
                )
            }

            composable(
                "${Route.Player.id}?urlOrId={urlOrId}",
                arguments = listOf(navArgument("urlOrId") { defaultValue = "" }),
            ) { backStackEntry ->
                val playerViewModel = hiltViewModel<PlayerViewModel>()

                val urlOrId = backStackEntry.arguments?.getString("urlOrId")!!
                val videoId = playerViewModel.getVideoId(urlOrId)

                PlayerScreen(videoId, setKeepScreenOn, setFullscreen, playerViewModel)
            }

            composable(Route.Settings.id) {
                val settingsViewModel = hiltViewModel<SettingsViewModel>()

                SettingsScreen(
                    onBackPressed = { navigateBack() },
                    viewModel = settingsViewModel,
                )
            }

            composable(Route.About.id) {
                AboutScreen(
                    onBackPressed = { navigateBack() },
                    navigateToLicenses = { navController.navigate(Route.Licenses.id) },
                    navigateToWelcome = { navController.navigate(Route.Welcome.id) },
                )
            }

            composable(Route.Licenses.id) {
                LicensesScreen(
                    onBackPressed = { navigateBack() },
                )
            }
        }
    }

    return navController
}
