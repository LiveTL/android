package com.livetl.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.livetl.android.ui.screen.about.AboutScreen
import com.livetl.android.ui.screen.about.LicensesScreen
import com.livetl.android.ui.screen.about.welcome.WelcomeScreen
import com.livetl.android.ui.screen.home.HomeScreen
import com.livetl.android.ui.screen.home.composable.StreamInfo
import com.livetl.android.ui.screen.home.settings.SettingsScreen
import com.livetl.android.ui.screen.player.PlayerScreen
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MainNavDisplay(startRoute: Route, pendingPlayerUrl: StateFlow<String?>) {
    val backStack = rememberNavBackStack(startRoute)
    val pendingUrl by pendingPlayerUrl.collectAsStateWithLifecycle()

    LaunchedEffect(pendingUrl) {
        pendingUrl?.let { urlOrId ->
            backStack.removeAll { it is Route.Player }
            backStack.add(Route.Player(urlOrId))
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategy = remember { BottomSheetSceneStrategy() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Route.Home> {
                HomeScreen(
                    navigateToStreamInfo = { backStack.add(Route.StreamInfo(it)) },
                    navigateToPlayer = { urlOrId ->
                        backStack.removeAll { it is Route.Player }
                        backStack.add(Route.Player(urlOrId))
                    },
                    navigateToSettings = { backStack.add(Route.Settings) },
                    navigateToAbout = { backStack.add(Route.About) },
                )
            }
            entry<Route.StreamInfo>(
                metadata = BottomSheetSceneStrategy.bottomSheet(),
            ) { key ->
                StreamInfo(key)
            }
            entry<Route.Welcome> {
                WelcomeScreen(
                    navigateToHome = { backStack.add(Route.Home) },
                )
            }
            entry<Route.Player> { key ->
                PlayerScreen(key.urlOrId)
            }
            entry<Route.Settings> {
                SettingsScreen(
                    onBackPressed = { backStack.removeLastOrNull() },
                )
            }
            entry<Route.About> {
                AboutScreen(
                    onBackPressed = { backStack.removeLastOrNull() },
                    navigateToLicenses = { backStack.add(Route.Licenses) },
                    navigateToWelcome = { backStack.add(Route.Welcome) },
                )
            }
            entry<Route.Licenses> {
                LicensesScreen(
                    onBackPressed = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}
