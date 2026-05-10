package com.duc.offlinemusicplayer.di

import android.content.Context
import com.duc.offlinemusicplayer.data.source.local.pref.PreferenceHelper
import com.duc.offlinemusicplayer.data.source.remote.cloud.FirebaseMgr
import com.duc.offlinemusicplayer.presentation.ui.language.LanguageImpl
import com.duc.offlinemusicplayer.presentation.ui.onboarding.OnboardingImpl
import com.duc.offlinemusicplayer.splash.SplashImpl
import com.duc.offlinemusicplayer.presentation.utils.AppSession
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.leansoft.ads.ui.language.LeansoftLanguageInterface
import com.leansoft.ads.ui.onboarding.LeansoftOnboardingInterface
import com.leansoft.ads.ui.splash.LeansoftSplashInterface
import com.leansoft.ads.ui.uninstall.LeansoftUninstallInterface
import com.duc.offlinemusicplayer.presentation.ui.uninstall.UninstallImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideAppSession(): AppSession = AppSession()

    @Provides
    @Singleton
    fun provideFirebaseMgr(): FirebaseMgr = FirebaseMgr()

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    @Provides
    @Singleton
    fun providePreferenceHelper(@ApplicationContext context: Context): PreferenceHelper =
        PreferenceHelper(context)


    @Provides
    @Singleton
    fun provideSplashInterface(): LeansoftSplashInterface = SplashImpl()


    @Provides
    @Singleton
    fun provideLanguageInterface(firebaseMgr: FirebaseMgr): LeansoftLanguageInterface = LanguageImpl(firebaseMgr)

    @Provides
    @Singleton
    fun provideOnboardingInterface(firebaseMgr: FirebaseMgr): LeansoftOnboardingInterface = OnboardingImpl(firebaseMgr = firebaseMgr)

    @Provides
    @Singleton
    fun provideUninstallInterface(): LeansoftUninstallInterface = UninstallImpl()
}