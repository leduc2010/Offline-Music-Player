package com.duc.offlinemusicplayer.di

import com.duc.offlinemusicplayer.ads.AdConfigImpl
import com.leansoft.ads.AdConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdsModule {

    @Provides
    @Singleton
    fun provideAdConfig(): AdConfig = AdConfigImpl()
}
