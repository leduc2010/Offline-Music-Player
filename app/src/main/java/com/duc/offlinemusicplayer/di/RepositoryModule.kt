package com.duc.offlinemusicplayer.di

import com.duc.offlinemusicplayer.data.repository.AppSettingRepositoryImpl
import com.duc.offlinemusicplayer.domain.repository.AppSettingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAppSettingRepository(
        appSettingRepositoryImpl: AppSettingRepositoryImpl
    ): AppSettingRepository
}
