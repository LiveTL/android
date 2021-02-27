package com.livetl.android.di

import com.livetl.android.data.chat.ChatFilterService
import com.livetl.android.data.chat.ChatService
import com.livetl.android.data.feed.FeedService
import com.livetl.android.data.stream.StreamService
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { HttpClient(Android) }
    single { Json { ignoreUnknownKeys = true } }

    single { ChatService(androidContext(), get(), get()) }
    single { ChatFilterService(get()) }
    single { FeedService(get(), get()) }
    single { StreamService(androidContext()) }
}