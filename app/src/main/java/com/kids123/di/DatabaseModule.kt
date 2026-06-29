package com.kids123.di

import android.content.Context
import androidx.room.Room
import com.kids123.data.local.database.Kids123Database
import com.kids123.data.local.database.dao.AchievementDao
import com.kids123.data.local.database.dao.ChallengeDao
import com.kids123.data.local.database.dao.EconomyDao
import com.kids123.data.local.database.dao.GameDao
import com.kids123.data.local.database.dao.ProfileDao
import com.kids123.data.local.database.dao.StatsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Kids123Database =
        Room.databaseBuilder(context, Kids123Database::class.java, "kids123.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideGameDao(db: Kids123Database): GameDao = db.gameDao()
    @Provides fun provideStatsDao(db: Kids123Database): StatsDao = db.statsDao()
    @Provides fun provideAchievementDao(db: Kids123Database): AchievementDao = db.achievementDao()
    @Provides fun provideChallengeDao(db: Kids123Database): ChallengeDao = db.challengeDao()
    @Provides fun provideEconomyDao(db: Kids123Database): EconomyDao = db.economyDao()
    @Provides fun provideProfileDao(db: Kids123Database): ProfileDao = db.profileDao()
}
