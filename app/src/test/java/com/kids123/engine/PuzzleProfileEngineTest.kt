package com.kids123.engine

import com.kids123.domain.model.Difficulty
import com.kids123.domain.model.GameStatus
import com.kids123.domain.model.Kids123Game
import com.kids123.domain.model.PuzzleProfileMetrics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleProfileEngineTest {

    @Test
    fun buildProfile_afterCompletedGame_updatesMetrics() {
        val level = Kids123Generator.generate(42L, 1, Difficulty.EASY)
        val completed = Kids123Engine.createInitialGame(level).copy(
            status = GameStatus.COMPLETED,
            moves = 10,
            hintsUsed = 0,
            elapsedSeconds = 30
        )
        val metrics = PuzzleProfileEngine.updateMetrics(PuzzleProfileMetrics(), completed)
        assertEquals(1, metrics.gamesAnalyzed)
        val profile = PuzzleProfileEngine.buildProfile(metrics)
        assertTrue(profile.archetype.name.isNotEmpty())
    }

    @Test
    fun adaptiveGenerationProfile_returnsModifierInRange() {
        val profile = PuzzleProfileEngine.buildProfile(
            PuzzleProfileMetrics(gamesAnalyzed = 5, perfectCompletions = 2)
        )
        val generation = PuzzleProfileEngine.adaptiveGenerationProfile(profile)
        assertTrue(generation.numberOffsetModifier in -1..2)
    }
}
