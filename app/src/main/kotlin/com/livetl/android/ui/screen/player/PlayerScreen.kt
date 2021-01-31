package com.livetl.android.ui.screen.player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.tooling.preview.Preview
import com.livetl.android.BuildConfig
import com.livetl.android.model.Stream
import com.livetl.android.ui.composable.VideoPlayer
import com.livetl.android.ui.screen.player.tab.ChatTab
import com.livetl.android.ui.screen.player.tab.DebugTab
import com.livetl.android.ui.screen.player.tab.InfoTab
import com.livetl.android.ui.theme.LiveTLTheme
import com.livetl.android.util.getYouTubeStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class Tabs(val nameRes: String) {
    Info("Info"),
    Chat("Chat"),
    Debug("Debug")
}

val visibleTabs = if (BuildConfig.DEBUG) {
    Tabs.values().toList()
} else {
    Tabs.values().dropLast(1)
}

@Composable
fun PlayerScreen(urlOrId: String?) {
    val context = AmbientContext.current
    val coroutineScope = rememberCoroutineScope()

    var stream by remember { mutableStateOf<Stream?>(null) }
    var selectedTab by remember { mutableStateOf(Tabs.Info) }

    // TODO: handle passed in urlOrId
    fun setSource(url: String) {
        coroutineScope.launch {
            val newStream = getYouTubeStream(context, url)
            withContext(Dispatchers.Main) {
                stream = newStream
            }
        }
    }

    Column {
        val mediaPlayback = VideoPlayer(
            videoSourceUrl = stream?.videoUrl,
            audioSourceUrl = stream?.audioUrl,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9F)
        )

        Row {
            IconButton(onClick = {
                mediaPlayback.rewind(10_000)
            }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Rewind")
            }

            IconButton(onClick = {
                mediaPlayback.playPause()
            }) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Play/pause")
            }

            IconButton(onClick = {
                mediaPlayback.forward(10_000)
            }) {
                Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "Fast forward")
            }
        }

        TabRow(selectedTabIndex = selectedTab.ordinal) {
            visibleTabs.forEachIndexed { index, tab ->
                Tab(
                    text = { Text(tab.nameRes) },
                    selected = index == selectedTab.ordinal,
                    onClick = { selectedTab = tab }
                )
            }
        }
        when (selectedTab) {
            Tabs.Info -> InfoTab(stream = stream)
            Tabs.Chat -> ChatTab()
            Tabs.Debug -> DebugTab(setSource = { setSource(it) })
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