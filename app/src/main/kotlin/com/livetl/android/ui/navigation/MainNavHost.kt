package com.livetl.android.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
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
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.livetl.android.ui.screen.about.AboutScreen
import com.livetl.android.ui.screen.about.LicensesScreen
import com.livetl.android.ui.screen.home.HomeScreen
import com.livetl.android.ui.screen.home.HomeViewModel
import com.livetl.android.ui.screen.player.PlayerScreen
import com.livetl.android.ui.screen.player.PlayerViewModel
import com.livetl.android.ui.screen.settings.SettingsScreen
import com.livetl.android.ui.screen.settings.SettingsViewModel
import com.livetl.android.ui.screen.welcome.WelcomeScreen
import com.livetl.android.ui.screen.welcome.WelcomeViewModel

fun NavHostController.navigateToPlayer(urlOrId: String) {
    navigate("${Route.Player.id}?urlOrId=$urlOrId") {
        launchSingleTop = true
        popUpTo(Route.Home.id) {}
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun MainNavHost(
    startRoute: Route,
    setKeepScreenOn: (Boolean) -> Unit,
    setFullscreen: (Boolean?) -> Unit,
): NavHostController {
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
            .background(MaterialTheme.colors.background),
    ) {
        NavHost(navController, startDestination = startRoute.id) {
            composable(Route.Home.id) {
                val homeViewModel = hiltViewModel<HomeViewModel>()

                HomeScreen(
                    navigateToPlayer = { navController.navigateToPlayer(it) },
                    navigateToSettings = { navController.navigate(Route.Settings.id) },
                    navigateToAbout = { navController.navigate(Route.About.id) },
                    viewModel = homeViewModel,
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
                arguments = listOf(navArgument("urlOrId") { defaultValue = "" })
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
                    onBackPressed = { navigateBack() }
                )
            }
        }
    }

    return navController
}
