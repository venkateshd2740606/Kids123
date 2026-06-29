package com.kids123.engine

import com.kids123.domain.model.Difficulty
import com.kids123.domain.model.GameStatus
import com.kids123.domain.model.NumberQuizType
import org.junit.Assert.*
import org.junit.Test

class Kids123EngineTest {

    @Test
    fun tutorialLevel_isValid() {
        val level = TutorialLevels.getTutorialLevel(0)!!
        assertTrue(Kids123Engine.validateLevel(level))
    }

    @Test
    fun nextStep_advancesFromLearnToTrace() {
        val level = TutorialLevels.getTutorialLevel(0)!!
        var game = Kids123Engine.createInitialGame(level)
        game = Kids123Engine.nextStep(game)
        assertEquals(1, game.currentStepIndex)
    }

    @Test
    fun selectQuizAnswer_correctCompletesLevel() {
        val level = TutorialLevels.getTutorialLevel(0)!!
        var game = Kids123Engine.createInitialGame(level)
        game = Kids123Engine.nextStep(game)
        game = Kids123Engine.completeTrace(game)
        game = Kids123Engine.nextStep(game)
        game = Kids123Engine.selectQuizAnswer(game, level.quizCorrectIndex)
        assertTrue(game.isCompleted)
    }

    @Test
    fun generatedLevel_isValid() {
        val level = Kids123Generator.generate(12345L, 7, Difficulty.EASY)
        assertTrue(Kids123Engine.validateLevel(level))
        assertEquals(7, level.numberValue)
    }

    @Test
    fun reviewLevel_usesBiggerQuiz() {
        val level = Kids123Generator.generate(99L, 24, Difficulty.MEDIUM)
        assertTrue(level.isReview)
        assertEquals(NumberQuizType.BIGGER, level.quizType)
    }

    @Test
    fun addTracePoint_autoCompletesWhenEnoughPoints() {
        val level = TutorialLevels.getTutorialLevel(0)!!
        var game = Kids123Engine.createInitialGame(level)
        game = Kids123Engine.nextStep(game)
        repeat(Kids123Engine.TRACE_MIN_POINTS) {
            game = Kids123Engine.addTracePoint(game, 0.5f, 0.5f)
        }
        assertTrue(game.traceCompleted)
    }
}
