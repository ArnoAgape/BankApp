package com.aura.ui.di

import com.aura.ui.data.network.AuraClient
import com.aura.ui.data.network.repository.AuraRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideLoginRepository(dataClient: AuraClient): AuraRepository {
        return AuraRepository(dataClient)
    }
}