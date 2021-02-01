package com.livetl.android.ui.screen.player

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.tooling.preview.Preview
import com.livetl.android.model.Stream
import com.livetl.android.ui.screen.player.tab.ChatTab
import com.livetl.android.ui.screen.player.tab.InfoTab
import com.livetl.android.ui.theme.LiveTLTheme
import com.livetl.android.util.getYouTubeStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class Tabs(val nameRes: String) {
    Info("Info"),
    Chat("Chat"),
}
val tabs = Tabs.values().toList()

@Composable
fun PlayerScreen(urlOrId: String) {
    val context = AmbientContext.current
    val coroutineScope = rememberCoroutineScope()

    var stream by remember { mutableStateOf<Stream?>(null) }
    var selectedTab by remember { mutableStateOf(Tabs.Info) }

    fun setSource(url: String) {
        coroutineScope.launch {
            val newStream = getYouTubeStream(context, url)
            withContext(Dispatchers.Main) {
                stream = newStream
            }
        }
    }

    DisposableEffect(urlOrId) {
        if (urlOrId.isNotEmpty()) {
            setSource(urlOrId)
        }
        onDispose {
            stream = null
        }
    }

    Column {
        Player(stream = stream)

        TabRow(selectedTabIndex = selectedTab.ordinal) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    text = { Text(tab.nameRes) },
                    selected = index == selectedTab.ordinal,
                    onClick = { selectedTab = tab }
                )
            }
        }
        when (selectedTab) {
            Tabs.Info -> InfoTab(stream = stream)
            Tabs.Chat -> ChatTab(stream = stream)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LiveTLTheme {
        PlayerScreen("")
    }
}