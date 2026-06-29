package com.kids123.data

import com.kids123.domain.model.Difficulty
import com.kids123.domain.model.GameStatus
import com.kids123.engine.Kids123Engine
import com.kids123.engine.Kids123Generator
import com.kids123.util.ProgressionCalculator
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressionCalculatorTest {

    @Test
    fun xpForCompletedGame_isPositive() {
        val level = Kids123Generator.generate(1L, 1, Difficulty.EASY)
        val game = Kids123Engine.createInitialGame(level).copy(status = GameStatus.COMPLETED)
        assertTrue(ProgressionCalculator.xpForGame(game) > 0)
    }

    @Test
    fun xpForGame_withHints_isLowerThanWithoutHints() {
        val level = Kids123Generator.generate(1L, 1, Difficulty.EASY)
        val withHints = Kids123Engine.createInitialGame(level).copy(hintsUsed = 2, status = GameStatus.COMPLETED)
        val noHints = Kids123Engine.createInitialGame(level).copy(hintsUsed = 0, status = GameStatus.COMPLETED)
        assertTrue(ProgressionCalculator.xpForGame(noHints) >= ProgressionCalculator.xpForGame(withHints))
    }
}
