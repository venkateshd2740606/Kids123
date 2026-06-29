package com.kids123.engine

import com.kids123.domain.model.GameStatus
import com.kids123.domain.model.Kids123Game
import com.kids123.domain.model.Kids123Level
import com.kids123.domain.model.LearningStepMode
import com.kids123.domain.model.TracePoint

object Kids123Engine {

    const val TRACE_MIN_POINTS = 6
    const val TRACE_NEAR_OUTLINE_MIN = 4
    const val DIGIT_REGION_MIN_X = 0.22f
    const val DIGIT_REGION_MAX_X = 0.78f
    const val DIGIT_REGION_MIN_Y = 0.18f
    const val DIGIT_REGION_MAX_Y = 0.82f

    fun createInitialGame(level: Kids123Level): Kids123Game = Kids123Game(level = level)

    fun validateLevel(level: Kids123Level): Boolean {
        if (level.numberValue !in 1..20) return false
        if (level.quizOptions.size != Kids123Level.OPTIONS_PER_QUIZ) return false
        if (level.quizCorrectIndex !in 0 until Kids123Level.OPTIONS_PER_QUIZ) return false
        return level.quizOptions[level.quizCorrectIndex] == level.numberValue ||
            (level.compareValue != null && level.quizOptions[level.quizCorrectIndex] ==
                maxOf(level.numberValue, level.compareValue))
    }

    fun canNextStep(game: Kids123Game): Boolean {
        if (game.isCompleted || game.awaitingAdvance) return false
        return when (game.currentStep) {
            LearningStepMode.LEARN -> true
            LearningStepMode.TRACE -> game.traceCompleted
            LearningStepMode.QUIZ -> false
            null -> false
        }
    }

    fun nextStep(game: Kids123Game): Kids123Game {
        if (!canNextStep(game)) return game
        val now = System.currentTimeMillis()
        val nextIndex = game.currentStepIndex + 1
        if (nextIndex >= game.level.steps.size) {
            return game.copy(
                status = GameStatus.COMPLETED,
                completedAt = now,
                lastPlayedAt = now,
                moves = game.moves + 1
            )
        }
        return game.copy(
            currentStepIndex = nextIndex,
            tracePoints = emptyList(),
            traceCompleted = false,
            quizSelectedIndex = null,
            quizAnswered = false,
            lastAnswerCorrect = null,
            moves = game.moves + 1,
            lastPlayedAt = now
        )
    }

    fun addTracePoint(game: Kids123Game, x: Float, y: Float): Kids123Game {
        if (game.isCompleted || game.currentStep != LearningStepMode.TRACE || game.traceCompleted) return game
        val clamped = TracePoint(x.coerceIn(0f, 1f), y.coerceIn(0f, 1f))
        val updated = game.copy(tracePoints = game.tracePoints + clamped, lastPlayedAt = System.currentTimeMillis())
        return if (isTraceSufficient(updated)) updated.copy(traceCompleted = true) else updated
    }

    fun completeTrace(game: Kids123Game): Kids123Game {
        if (game.isCompleted || game.currentStep != LearningStepMode.TRACE) return game
        return game.copy(traceCompleted = true, moves = game.moves + 1, lastPlayedAt = System.currentTimeMillis())
    }

    fun canAnswerQuiz(game: Kids123Game): Boolean =
        !game.isCompleted && game.currentStep == LearningStepMode.QUIZ &&
            !game.quizAnswered && !game.awaitingAdvance

    fun selectQuizAnswer(game: Kids123Game, index: Int): Kids123Game {
        if (!canAnswerQuiz(game)) return game
        if (index !in 0 until Kids123Level.OPTIONS_PER_QUIZ) return game
        if (index in game.eliminatedQuizOptions) return game

        val correct = index == game.level.quizCorrectIndex
        val newScore = game.score + if (correct) 1 else 0
        val newStreak = if (correct) game.streak + 1 else 0
        val now = System.currentTimeMillis()

        return if (correct) {
            game.copy(
                quizSelectedIndex = index,
                quizAnswered = true,
                score = newScore,
                streak = newStreak,
                bestStreak = maxOf(game.bestStreak, newStreak),
                awaitingAdvance = true,
                lastAnswerCorrect = true,
                moves = game.moves + 1,
                status = GameStatus.COMPLETED,
                completedAt = now,
                lastPlayedAt = now
            )
        } else {
            game.copy(
                quizSelectedIndex = index,
                streak = newStreak,
                awaitingAdvance = true,
                lastAnswerCorrect = false,
                moves = game.moves + 1,
                lastPlayedAt = now
            )
        }
    }

    fun advanceAfterQuiz(game: Kids123Game): Kids123Game {
        if (!game.awaitingAdvance || game.lastAnswerCorrect != false) return game
        return game.copy(
            awaitingAdvance = false,
            quizSelectedIndex = null,
            lastAnswerCorrect = null,
            lastPlayedAt = System.currentTimeMillis()
        )
    }

    fun isWon(game: Kids123Game): Boolean = game.isCompleted

    fun optimalScore(game: Kids123Game): Int = 1

    fun canApplyHint(game: Kids123Game): Boolean {
        if (game.currentStep != LearningStepMode.QUIZ || !canAnswerQuiz(game)) return false
        return (0 until Kids123Level.OPTIONS_PER_QUIZ)
            .any { it != game.level.quizCorrectIndex && it !in game.eliminatedQuizOptions }
    }

    fun applyHint(game: Kids123Game): Kids123Game {
        if (!canApplyHint(game)) return game
        val wrong = (0 until Kids123Level.OPTIONS_PER_QUIZ)
            .first { it != game.level.quizCorrectIndex && it !in game.eliminatedQuizOptions }
        return game.copy(
            eliminatedQuizOptions = game.eliminatedQuizOptions + wrong,
            hintsUsed = game.hintsUsed + 1,
            lastPlayedAt = System.currentTimeMillis()
        )
    }

    fun isTraceSufficient(game: Kids123Game): Boolean {
        if (game.tracePoints.size < TRACE_MIN_POINTS) return false
        return game.tracePoints.count { isNearDigitOutline(it) } >= TRACE_NEAR_OUTLINE_MIN
    }

    fun isNearDigitOutline(point: TracePoint): Boolean =
        point.x in DIGIT_REGION_MIN_X..DIGIT_REGION_MAX_X &&
            point.y in DIGIT_REGION_MIN_Y..DIGIT_REGION_MAX_Y

    fun applyRemoteMove(game: Kids123Game, payload: String): Kids123Game {
        val parts = payload.split(":", limit = 2)
        return when (parts[0]) {
            "next" -> nextStep(game)
            "trace" -> {
                val coords = parts.getOrNull(1)?.split(",") ?: return game
                if (coords.size != 2) return game
                addTracePoint(game, coords[0].toFloatOrNull() ?: return game, coords[1].toFloatOrNull() ?: return game)
            }
            "traceDone" -> completeTrace(game)
            "quiz" -> selectQuizAnswer(game, parts.getOrNull(1)?.toIntOrNull() ?: return game)
            else -> game
        }
    }

    fun botQuizAnswer(game: Kids123Game, accuracy: Float = 0.7f): Int? {
        if (!canAnswerQuiz(game)) return null
        val available = (0 until Kids123Level.OPTIONS_PER_QUIZ).filter { it !in game.eliminatedQuizOptions }
        if (available.isEmpty()) return null
        return if (kotlin.random.Random.nextFloat() < accuracy) {
            game.level.quizCorrectIndex
        } else {
            available.filter { it != game.level.quizCorrectIndex }.randomOrNull()
                ?: game.level.quizCorrectIndex
        }
    }
}
