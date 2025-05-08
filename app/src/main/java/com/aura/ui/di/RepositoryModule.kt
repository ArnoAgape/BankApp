package com.aura.ui.di

import com.aura.ui.data.network.repository.AuraRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuraRepository(
        impl: AuraRepository
    ): com.aura.ui.data.network.repository.AuraRepositoryInterface
}
