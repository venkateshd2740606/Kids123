package com.kids123.domain.repository

import com.kids123.domain.model.Achievement
import com.kids123.domain.model.ChallengeRecord
import com.kids123.domain.model.ChallengeType
import com.kids123.domain.model.Kids123Game
import com.kids123.domain.model.Kids123Level
import com.kids123.domain.model.Difficulty
import com.kids123.domain.model.EconomyState
import com.kids123.domain.model.PuzzleProfile
import com.kids123.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    suspend fun createNewGame(difficulty: Difficulty, levelNumber: Int): Kids123Game
    suspend fun createGameFromSeed(seed: Long, levelNumber: Int, difficulty: Difficulty): Kids123Game
    suspend fun createTutorialGame(tutorialIndex: Int): Kids123Game?
    suspend fun createEndlessGame(wave: Int): Kids123Game
    suspend fun saveGame(game: Kids123Game): Long
    suspend fun getGame(gameId: Long): Kids123Game?
    suspend fun getInProgressGame(): Kids123Game?
    fun observeInProgressGame(): Flow<Kids123Game?>
    suspend fun completeGame(game: Kids123Game): Kids123Game
    suspend fun abandonGame(gameId: Long)
    suspend fun getLevel(seed: Long, levelNumber: Int, difficulty: Difficulty): Kids123Level
}

interface ChallengeRepository {
    suspend fun getChallenge(type: ChallengeType, key: String): ChallengeRecord?
    suspend fun createChallenge(type: ChallengeType, key: String, difficulty: Difficulty): ChallengeRecord
    suspend fun resolveActiveChallenge(type: ChallengeType): ChallengeRecord
    fun observeActiveChallenge(type: ChallengeType): Flow<ChallengeRecord?>
    suspend fun completeChallenge(record: ChallengeRecord, timeSeconds: Long, moves: Int): ChallengeRecord
    fun observeChallengeHistory(type: ChallengeType): Flow<List<ChallengeRecord>>
    suspend fun getCurrentStreak(type: ChallengeType): Int
    suspend fun getChallengeGame(record: ChallengeRecord): Kids123Game
}

interface ProgressionRepository {
    fun observeStats(): Flow<UserStats>
    suspend fun getStats(): UserStats
    suspend fun updateStatsAfterGame(game: Kids123Game)
    suspend fun grantChallengeRewards(rewardCoins: Int, rewardXp: Int)
    fun observePuzzleProfile(): Flow<PuzzleProfile>
    suspend fun getPuzzleProfile(): PuzzleProfile
    fun observeAchievements(): Flow<List<Achievement>>
    suspend fun checkAndUnlockAchievements(
        game: Kids123Game,
        sameDevicePlayed: Boolean = false
    ): List<Achievement>
    fun observeEconomy(): Flow<EconomyState>
    suspend fun getEconomy(): EconomyState
    suspend fun spendCoins(amount: Int): Boolean
    suspend fun earnCoins(amount: Int)
    suspend fun unlockTheme(themeId: String): Boolean
}

interface PreferencesRepository {
    fun getUserPreferences(): Flow<com.kids123.domain.model.UserPreferences>
    suspend fun updatePreferences(transform: (com.kids123.domain.model.UserPreferences) -> com.kids123.domain.model.UserPreferences)
    suspend fun getCampaignLevel(difficulty: Difficulty): Int
    suspend fun advanceCampaignLevel(difficulty: Difficulty): Int
}
