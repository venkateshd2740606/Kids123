package com.kids123.engine

import com.kids123.domain.model.Difficulty
import com.kids123.domain.model.GameStatus
import com.kids123.domain.model.GenerationProfile
import com.kids123.domain.model.PuzzleArchetype
import com.kids123.domain.model.PuzzleProfile
import com.kids123.domain.model.PuzzleProfileMetrics
import com.kids123.domain.model.Kids123Game
import com.kids123.domain.model.SkillCategory
import kotlin.math.max
import kotlin.math.roundToInt

object PuzzleProfileEngine {

    private const val SECONDS_PER_STEP_FAST = 10
    private const val SECONDS_PER_STEP_SLOW = 35

    fun updateMetrics(current: PuzzleProfileMetrics, game: Kids123Game): PuzzleProfileMetrics {
        if (game.status != GameStatus.COMPLETED || game.level.isTutorial) return current

        val stepCount = max(game.level.stepCount, 1)
        val optimalScore = Kids123Engine.optimalScore(game)
        val secondsPerStep = game.elapsedSeconds.toFloat() / stepCount
        val isFast = secondsPerStep <= SECONDS_PER_STEP_FAST
        val isSlow = secondsPerStep >= SECONDS_PER_STEP_SLOW
        val isPerfect = game.hintsUsed == 0 && game.score >= optimalScore
        val isComplex = game.level.numberValue >= 15
        val isInefficient = game.score < optimalScore
        val isHintHeavy = game.hintsUsed >= 1

        return current.copy(
            gamesAnalyzed = current.gamesAnalyzed + 1,
            totalSolveTimeSeconds = current.totalSolveTimeSeconds + game.elapsedSeconds,
            totalMoves = current.totalMoves + game.moves,
            totalOptimalMoves = current.totalOptimalMoves + stepCount,
            totalHintsUsed = current.totalHintsUsed + game.hintsUsed,
            fastCompletions = current.fastCompletions + if (isFast) 1 else 0,
            slowCompletions = current.slowCompletions + if (isSlow) 1 else 0,
            perfectCompletions = current.perfectCompletions + if (isPerfect) 1 else 0,
            complexChainWins = current.complexChainWins + if (isComplex) 1 else 0,
            inefficientWins = current.inefficientWins + if (isInefficient) 1 else 0,
            hintHeavyWins = current.hintHeavyWins + if (isHintHeavy) 1 else 0
        )
    }

    fun buildProfile(metrics: PuzzleProfileMetrics): PuzzleProfile {
        if (metrics.gamesAnalyzed == 0) {
            return PuzzleProfile(
                metrics = metrics,
                archetype = PuzzleArchetype.EXPLORER,
                strength = SkillCategory.PATTERN_RECOGNITION,
                weakness = SkillCategory.TIME_PRESSURE,
                adaptiveColorModifier = 0
            )
        }
        val scores = categoryScores(metrics)
        return PuzzleProfile(
            metrics = metrics,
            archetype = resolveArchetype(metrics),
            strength = scores.maxBy { it.value }.key,
            weakness = scores.minBy { it.value }.key,
            adaptiveColorModifier = resolveAdaptiveModifier(metrics, scores)
        )
    }

    fun adaptiveGenerationProfile(profile: PuzzleProfile): GenerationProfile {
        return GenerationProfile(numberOffsetModifier = profile.adaptiveColorModifier.coerceIn(-1, 2))
    }

    fun percentileTopValue(profile: PuzzleProfile, category: SkillCategory): Int {
        val score = categoryScores(profile.metrics)[category] ?: 50
        return (100 - score.coerceIn(5, 98))
    }

    fun percentileLabel(profile: PuzzleProfile, category: SkillCategory): String {
        val score = categoryScores(profile.metrics)[category] ?: 50
        return "Top ${100 - score.coerceIn(5, 98)}%"
    }

    private fun resolveArchetype(metrics: PuzzleProfileMetrics): PuzzleArchetype {
        val games = metrics.gamesAnalyzed.toFloat()
        val hintRate = metrics.totalHintsUsed / games
        val fastRate = metrics.fastCompletions / games
        val perfectRate = metrics.perfectCompletions / games
        val stepEfficiency = if (metrics.totalMoves > 0) {
            metrics.totalOptimalMoves.toFloat() / metrics.totalMoves
        } else 1f
        return when {
            perfectRate >= 0.35f && stepEfficiency >= 0.85f -> PuzzleArchetype.ARCHITECT
            fastRate >= 0.4f && hintRate <= 0.8f -> PuzzleArchetype.SPRINTER
            hintRate >= 1.0f -> PuzzleArchetype.ANALYST
            metrics.complexChainWins / games >= 0.35f -> PuzzleArchetype.STRATEGIST
            else -> PuzzleArchetype.EXPLORER
        }
    }

    private fun categoryScores(metrics: PuzzleProfileMetrics): Map<SkillCategory, Int> {
        if (metrics.gamesAnalyzed == 0) return SkillCategory.entries.associateWith { 50 }
        val games = metrics.gamesAnalyzed.toFloat()
        val stepEfficiency = if (metrics.totalMoves > 0) {
            metrics.totalOptimalMoves.toFloat() / metrics.totalMoves
        } else 0.5f
        return mapOf(
            SkillCategory.PATTERN_RECOGNITION to score(stepEfficiency * 100f + metrics.perfectCompletions / games * 20f),
            SkillCategory.PLANNING to score(stepEfficiency * 90f + metrics.perfectCompletions / games * 30f),
            SkillCategory.SPEED to score(metrics.fastCompletions / games * 100f + 40f),
            SkillCategory.ACCURACY to score((1f - metrics.totalHintsUsed / games / 3f) * 70f + metrics.perfectCompletions / games * 40f),
            SkillCategory.COMPLEX_CHAINS to score(metrics.complexChainWins / games * 100f),
            SkillCategory.TIME_PRESSURE to score(100f - metrics.slowCompletions / games * 50f + 30f)
        )
    }

    private fun resolveAdaptiveModifier(metrics: PuzzleProfileMetrics, scores: Map<SkillCategory, Int>): Int = when {
        (scores.values.minOrNull() ?: 50) < 35 && metrics.slowCompletions > metrics.fastCompletions -> -1
        (scores.values.maxOrNull() ?: 50) > 75 && metrics.perfectCompletions >= 3 -> 1
        (scores.values.maxOrNull() ?: 50) > 85 && metrics.gamesAnalyzed >= 10 -> 2
        else -> 0
    }

    private fun score(raw: Float): Int = raw.roundToInt().coerceIn(0, 99)
}
