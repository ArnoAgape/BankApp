package com.aura.ui.di

import com.aura.ui.data.network.repository.AuraRepository
import com.aura.ui.data.network.repository.AuraRepositoryInterface
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
    abstract fun bindAuraRepository(
        impl: AuraRepository
    ): AuraRepositoryInterface
}