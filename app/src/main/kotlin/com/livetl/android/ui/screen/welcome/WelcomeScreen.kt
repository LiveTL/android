package com.livetl.android.ui.screen.welcome

import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.livetl.android.R
import com.livetl.android.util.setDefaultSettings

@Composable
fun WelcomeScreen(
    navigateToHome: () -> Unit,
    viewModel: WelcomeViewModel,
) {
    val context = LocalContext.current

    val webview = remember {
        WebView(context).apply {
            setDefaultSettings()
            loadUrl("file:///android_asset/welcome.html")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webview.destroy()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.welcome))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.dismissWelcomeScreen()
                            navigateToHome()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.cd_close)
                        )
                    }
                },
                contentPadding = rememberInsetsPaddingValues(
                    LocalWindowInsets.current.statusBars,
                    applyBottom = false,
                ),
            )
        },
    ) { contentPadding ->
        AndroidView(
            factory = { webview },
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        )
    }
}
