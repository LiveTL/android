package com.livetl.android.ui.screen.welcome

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.livetl.android.R

@Composable
fun WelcomeScreen(navigateToHome: () -> Unit, viewModel: WelcomeViewModel = hiltViewModel()) {
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
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.cd_close),
                        )
                    }
                },
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .consumeWindowInsets(contentPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentPadding = contentPadding,
        ) {
            item {
                Text(
                    text = stringResource(R.string.welcome_thank_you),
                    style = MaterialTheme.typography.headlineMedium,
                )
            }

            item {
                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = stringResource(R.string.welcome_get_started),
                )
            }

            item {
                HorizontalDivider(Modifier.padding(8.dp))
            }

            item {
                Text(
                    text = stringResource(R.string.welcome_faq),
                    style = MaterialTheme.typography.headlineMedium,
                )
            }

            items(
                items = FAQ,
                key = { it.first },
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = stringResource(it.first),
                    fontWeight = FontWeight.W700,
                )
                Text(
                    text = stringResource(it.second),
                )
            }
        }
    }
}

private val FAQ =
    listOf(
        R.string.welcome_faq_q_how_it_works to R.string.welcome_faq_a_how_it_works,
        R.string.welcome_faq_q_trust to R.string.welcome_faq_a_trust,
        R.string.welcome_faq_q_no_translations to R.string.welcome_faq_a_no_translations,
        R.string.welcome_faq_q_custom_filter to R.string.welcome_faq_a_custom_filter,
    )
