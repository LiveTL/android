package com.livetl.android.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun okhttpClient() = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun json() = Json { ignoreUnknownKeys = true }
}
