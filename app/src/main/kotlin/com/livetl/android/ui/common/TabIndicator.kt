package com.livetl.android.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun TabIndicator(currentTabPosition: TabPosition) {
    SecondaryIndicator(
        Modifier
            .tabIndicatorOffset(currentTabPosition)
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)),
    )
}
