package com.kids123.data.mapper

import com.google.gson.Gson
import com.kids123.data.local.database.entity.*
import com.kids123.domain.model.*
import com.kids123.engine.Kids123Generator

object DataMappers {
    private val gson = Gson()

    fun toEntity(game: Kids123Game): GameEntity {
        val gameState = GameStateJson(
            currentStepIndex = game.currentStepIndex,
            tracePoints = game.tracePoints,
            traceCompleted = game.traceCompleted,
            quizSelectedIndex = game.quizSelectedIndex,
            quizAnswered = game.quizAnswered,
            eliminatedQuizOptions = game.eliminatedQuizOptions.toList(),
            score = game.score,
            streak = game.streak,
            bestStreak = game.bestStreak,
            awaitingAdvance = game.awaitingAdvance,
            lastAnswerCorrect = game.lastAnswerCorrect
        )
        return GameEntity(
            id = game.id,
            seed = game.level.seed,
            levelNumber = game.level.levelNumber,
            difficulty = game.level.difficulty.name,
            status = game.status.name,
            tubeStateJson = gson.toJson(gameState),
            selectedTubeId = game.currentStepIndex,
            moves = game.moves,
            hintsUsed = game.hintsUsed,
            elapsedSeconds = game.elapsedSeconds,
            createdAt = game.createdAt,
            lastPlayedAt = game.lastPlayedAt,
            completedAt = game.completedAt,
            isTutorial = game.level.isTutorial,
            isEndless = game.level.isEndless,
            challengeType = game.level.challengeType?.name,
            challengeKey = game.level.challengeKey,
            levelJson = gson.toJson(toLevelJson(game.level)),
            coinsEarned = game.coinsEarned,
            xpEarned = game.xpEarned
        )
    }

    fun fromEntity(entity: GameEntity): Kids123Game {
        val levelJson = gson.fromJson(entity.levelJson, LevelJson::class.java)
        val gameState = runCatching {
            gson.fromJson(entity.tubeStateJson, GameStateJson::class.java)
        }.getOrNull()
        val level = fromLevelJson(entity, levelJson)

        return if (gameState != null) {
            Kids123Game(
                id = entity.id,
                level = level,
                status = GameStatus.valueOf(entity.status),
                currentStepIndex = gameState.currentStepIndex,
                tracePoints = gameState.tracePoints,
                traceCompleted = gameState.traceCompleted,
                quizSelectedIndex = gameState.quizSelectedIndex,
                quizAnswered = gameState.quizAnswered,
                eliminatedQuizOptions = gameState.eliminatedQuizOptions.toSet(),
                score = gameState.score,
                streak = gameState.streak,
                bestStreak = gameState.bestStreak,
                awaitingAdvance = gameState.awaitingAdvance,
                lastAnswerCorrect = gameState.lastAnswerCorrect,
                hintsUsed = entity.hintsUsed,
                moves = entity.moves,
                elapsedSeconds = entity.elapsedSeconds,
                createdAt = entity.createdAt,
                lastPlayedAt = entity.lastPlayedAt,
                completedAt = entity.completedAt,
                coinsEarned = entity.coinsEarned,
                xpEarned = entity.xpEarned
            )
        } else {
            Kids123Game(
                id = entity.id,
                level = level,
                status = GameStatus.valueOf(entity.status),
                currentStepIndex = entity.selectedTubeId.coerceAtLeast(0),
                hintsUsed = entity.hintsUsed,
                moves = entity.moves,
                elapsedSeconds = entity.elapsedSeconds,
                createdAt = entity.createdAt,
                lastPlayedAt = entity.lastPlayedAt,
                completedAt = entity.completedAt,
                coinsEarned = entity.coinsEarned,
                xpEarned = entity.xpEarned
            )
        }
    }

    private fun toLevelJson(level: Kids123Level) = LevelJson(
        numberValue = level.numberValue,
        entry = level.entry,
        steps = level.steps,
        quizType = level.quizType,
        quizOptions = level.quizOptions,
        quizCorrectIndex = level.quizCorrectIndex,
        quizPrompt = level.quizPrompt,
        compareValue = level.compareValue,
        isReview = level.isReview
    )

    private fun fromLevelJson(entity: GameEntity, levelJson: LevelJson?): Kids123Level {
        if (levelJson == null) {
            return Kids123Generator.generate(entity.seed, entity.levelNumber, Difficulty.valueOf(entity.difficulty))
        }
        return Kids123Level(
            id = entity.id,
            seed = entity.seed,
            levelNumber = entity.levelNumber,
            difficulty = Difficulty.valueOf(entity.difficulty),
            numberValue = levelJson.numberValue,
            entry = levelJson.entry,
            steps = levelJson.steps,
            quizType = levelJson.quizType,
            quizOptions = levelJson.quizOptions,
            quizCorrectIndex = levelJson.quizCorrectIndex,
            quizPrompt = levelJson.quizPrompt,
            compareValue = levelJson.compareValue,
            isTutorial = entity.isTutorial,
            isEndless = entity.isEndless,
            isReview = levelJson.isReview,
            challengeType = entity.challengeType?.let { ChallengeType.valueOf(it) },
            challengeKey = entity.challengeKey
        )
    }

    fun toStatsEntity(stats: UserStats) = StatsEntity(
        gamesPlayed = stats.gamesPlayed, gamesWon = stats.gamesWon, gamesAbandoned = stats.gamesAbandoned,
        totalPlayTimeSeconds = stats.totalPlayTimeSeconds,
        fastestTimeBeginner = stats.fastestTimeBeginner, fastestTimeEasy = stats.fastestTimeEasy,
        fastestTimeMedium = stats.fastestTimeMedium, fastestTimeHard = stats.fastestTimeHard,
        fastestTimeExpert = stats.fastestTimeExpert, fastestTimeMaster = stats.fastestTimeMaster,
        currentStreak = stats.currentStreak, longestStreak = stats.longestStreak,
        lastPlayedDate = stats.lastPlayedDate, xpPoints = stats.xpPoints, level = stats.level,
        hintsUsedTotal = stats.hintsUsedTotal, perfectGames = stats.perfectGames,
        poursTotal = stats.poursTotal, endlessHighScore = stats.endlessHighScore
    )

    fun fromStatsEntity(entity: StatsEntity?) = entity?.let {
        UserStats(
            gamesPlayed = it.gamesPlayed, gamesWon = it.gamesWon, gamesAbandoned = it.gamesAbandoned,
            totalPlayTimeSeconds = it.totalPlayTimeSeconds,
            fastestTimeBeginner = it.fastestTimeBeginner, fastestTimeEasy = it.fastestTimeEasy,
            fastestTimeMedium = it.fastestTimeMedium, fastestTimeHard = it.fastestTimeHard,
            fastestTimeExpert = it.fastestTimeExpert, fastestTimeMaster = it.fastestTimeMaster,
            currentStreak = it.currentStreak, longestStreak = it.longestStreak,
            lastPlayedDate = it.lastPlayedDate, xpPoints = it.xpPoints, level = it.level,
            hintsUsedTotal = it.hintsUsedTotal, perfectGames = it.perfectGames,
            poursTotal = it.poursTotal, endlessHighScore = it.endlessHighScore
        )
    } ?: UserStats()

    fun toChallengeEntity(record: ChallengeRecord) = ChallengeEntity(
        key = record.key, type = record.type.name, seed = record.seed,
        difficulty = record.difficulty.name, isCompleted = record.isCompleted,
        completionTime = record.completionTime, moves = record.moves,
        rewardCoins = record.rewardCoins, rewardXp = record.rewardXp, streakDay = record.streakDay
    )

    fun fromChallengeEntity(entity: ChallengeEntity) = ChallengeRecord(
        key = entity.key, type = ChallengeType.valueOf(entity.type), seed = entity.seed,
        difficulty = Difficulty.valueOf(entity.difficulty), isCompleted = entity.isCompleted,
        completionTime = entity.completionTime, moves = entity.moves,
        rewardCoins = entity.rewardCoins, rewardXp = entity.rewardXp, streakDay = entity.streakDay
    )

    fun toEconomyEntity(state: EconomyState) = EconomyEntity(
        coins = state.coins, totalCoinsEarned = state.totalCoinsEarned,
        totalCoinsSpent = state.totalCoinsSpent, unlockedThemes = gson.toJson(state.unlockedThemeIds.toList())
    )

    fun fromEconomyEntity(entity: EconomyEntity?) = entity?.let {
        EconomyState(
            coins = it.coins, totalCoinsEarned = it.totalCoinsEarned,
            totalCoinsSpent = it.totalCoinsSpent,
            unlockedThemeIds = gson.fromJson(it.unlockedThemes, Array<String>::class.java)?.toSet() ?: emptySet()
        )
    } ?: EconomyState()

    fun mergeAchievement(def: Achievement, entity: AchievementEntity?) = def.copy(
        isUnlocked = entity?.isUnlocked ?: false,
        unlockedAt = entity?.unlockedAt,
        progress = entity?.progress ?: 0
    )

    fun toProfileEntity(profile: PuzzleProfile) = ProfileEntity(
        gamesAnalyzed = profile.metrics.gamesAnalyzed,
        totalSolveTimeSeconds = profile.metrics.totalSolveTimeSeconds,
        totalMoves = profile.metrics.totalMoves,
        totalOptimalMoves = profile.metrics.totalOptimalMoves,
        totalHintsUsed = profile.metrics.totalHintsUsed,
        fastCompletions = profile.metrics.fastCompletions,
        slowCompletions = profile.metrics.slowCompletions,
        perfectCompletions = profile.metrics.perfectCompletions,
        complexChainWins = profile.metrics.complexChainWins,
        inefficientWins = profile.metrics.inefficientWins,
        hintHeavyWins = profile.metrics.hintHeavyWins,
        archetype = profile.archetype.name,
        strength = profile.strength.name,
        weakness = profile.weakness.name,
        adaptiveColorModifier = profile.adaptiveColorModifier
    )

    fun fromProfileEntity(entity: ProfileEntity?) = entity?.let {
        PuzzleProfile(
            metrics = PuzzleProfileMetrics(
                gamesAnalyzed = it.gamesAnalyzed, totalSolveTimeSeconds = it.totalSolveTimeSeconds,
                totalMoves = it.totalMoves, totalOptimalMoves = it.totalOptimalMoves,
                totalHintsUsed = it.totalHintsUsed, fastCompletions = it.fastCompletions,
                slowCompletions = it.slowCompletions, perfectCompletions = it.perfectCompletions,
                complexChainWins = it.complexChainWins, inefficientWins = it.inefficientWins,
                hintHeavyWins = it.hintHeavyWins
            ),
            archetype = runCatching { PuzzleArchetype.valueOf(it.archetype) }.getOrDefault(PuzzleArchetype.EXPLORER),
            strength = runCatching { SkillCategory.valueOf(it.strength) }.getOrDefault(SkillCategory.PATTERN_RECOGNITION),
            weakness = runCatching { SkillCategory.valueOf(it.weakness) }.getOrDefault(SkillCategory.TIME_PRESSURE),
            adaptiveColorModifier = it.adaptiveColorModifier
        )
    } ?: PuzzleProfile()

    data class LevelJson(
        val numberValue: Int,
        val entry: NumberEntry,
        val steps: List<LearningStepMode>,
        val quizType: NumberQuizType,
        val quizOptions: List<Int>,
        val quizCorrectIndex: Int,
        val quizPrompt: String,
        val compareValue: Int? = null,
        val isReview: Boolean = false
    )

    data class GameStateJson(
        val currentStepIndex: Int = 0,
        val tracePoints: List<TracePoint> = emptyList(),
        val traceCompleted: Boolean = false,
        val quizSelectedIndex: Int? = null,
        val quizAnswered: Boolean = false,
        val eliminatedQuizOptions: List<Int> = emptyList(),
        val score: Int = 0,
        val streak: Int = 0,
        val bestStreak: Int = 0,
        val awaitingAdvance: Boolean = false,
        val lastAnswerCorrect: Boolean? = null
    )
}
