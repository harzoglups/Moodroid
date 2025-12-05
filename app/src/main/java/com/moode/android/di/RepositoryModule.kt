package com.moode.android.di

import com.moode.android.data.MoodeRepositoryImpl
import com.moode.android.data.SettingsRepositoryImpl
import com.moode.android.domain.repository.MoodeRepository
import com.moode.android.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing repository implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
    
    @Binds
    @Singleton
    abstract fun bindMoodeRepository(
        moodeRepositoryImpl: MoodeRepositoryImpl
    ): MoodeRepository
}
