package com.livetl.android.di

import com.livetl.android.service.StreamScheduleService
import com.livetl.android.service.YouTubeVideoExtractor
import com.livetl.android.util.NetworkUtil
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { NetworkUtil(androidContext()) }
    single { YouTubeVideoExtractor(androidContext()) }
    single { StreamScheduleService(get()) }
}