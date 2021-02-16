package com.livetl.android.di

import com.livetl.android.data.feed.FeedService
import com.livetl.android.data.stream.StreamService
import com.livetl.android.util.NetworkUtil
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { NetworkUtil(androidContext()) }
    single { Json { ignoreUnknownKeys = true } }

    single { StreamService(androidContext()) }
    single { FeedService(get(), get()) }
}