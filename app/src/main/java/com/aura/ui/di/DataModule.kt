package com.aura.ui.di

import com.aura.ui.data.network.LoginClient
import com.aura.ui.data.network.repository.LoginRepository
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
    fun provideLoginRepository(dataClient: LoginClient): LoginRepository {
        return LoginRepository(dataClient)
    }
}