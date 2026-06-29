package com.kids123.di

import com.kids123.data.repository.ChallengeRepositoryImpl
import com.kids123.data.repository.GameRepositoryImpl
import com.kids123.data.repository.PreferencesRepositoryImpl
import com.kids123.data.repository.ProgressionRepositoryImpl
import com.kids123.domain.repository.ChallengeRepository
import com.kids123.domain.repository.GameRepository
import com.kids123.domain.repository.PreferencesRepository
import com.kids123.domain.repository.ProgressionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindGameRepository(impl: GameRepositoryImpl): GameRepository
    @Binds @Singleton abstract fun bindChallengeRepository(impl: ChallengeRepositoryImpl): ChallengeRepository
    @Binds @Singleton abstract fun bindProgressionRepository(impl: ProgressionRepositoryImpl): ProgressionRepository
    @Binds @Singleton abstract fun bindPreferencesRepository(impl: PreferencesRepositoryImpl): PreferencesRepository
}
