package com.kids123.engine

import com.kids123.domain.model.Difficulty
import com.kids123.domain.model.GenerationProfile
import com.kids123.domain.model.Kids123Level
import com.kids123.domain.model.NumberEntry
import com.kids123.domain.model.NumberQuizType
import kotlin.random.Random

object Kids123Generator {

    private val reviewSpecs = listOf(
        ReviewSpec(5, NumberQuizType.COUNT),
        ReviewSpec(10, NumberQuizType.COUNT),
        ReviewSpec(15, NumberQuizType.COUNT),
        ReviewSpec(9, NumberQuizType.BIGGER, 6),
        ReviewSpec(12, NumberQuizType.BIGGER, 8),
        ReviewSpec(18, NumberQuizType.BIGGER, 14)
    )

    fun generate(
        seed: Long,
        levelNumber: Int,
        difficulty: Difficulty,
        generationProfile: GenerationProfile = GenerationProfile()
    ): Kids123Level {
        return if (levelNumber in 1..20) {
            val number = (levelNumber + generationProfile.numberOffsetModifier).coerceIn(1, 20)
            buildLevel(seed, levelNumber, difficulty, number, NumberQuizType.COUNT, isEndless = difficulty == Difficulty.ENDLESS)
        } else {
            val review = reviewSpecs[(levelNumber - 21).coerceIn(0, reviewSpecs.lastIndex)]
            buildLevel(
                seed, levelNumber, difficulty, review.primary, review.quizType,
                compareValue = review.compare, isReview = true,
                isEndless = difficulty == Difficulty.ENDLESS
            )
        }
    }

    fun generateForChallenge(seed: Long, levelNumber: Int, difficulty: Difficulty): Kids123Level =
        generate(seed, levelNumber, difficulty)

    fun seedFromLevelNumber(levelNumber: Int, difficulty: Difficulty): Long {
        val difficultyOffset = difficulty.ordinal * 100_000L
        return levelNumber.toLong() * 9973L + difficultyOffset + 42L
    }

    fun formatShareText(seed: Long, levelNumber: Int, difficulty: Difficulty): String =
        "Kids 123 Level\nSeed: $seed\nLevel: $levelNumber\nDifficulty: ${difficulty.name}"

    fun buildLevel(
        seed: Long,
        levelNumber: Int,
        difficulty: Difficulty,
        numberValue: Int,
        quizType: NumberQuizType,
        compareValue: Int? = null,
        isTutorial: Boolean = false,
        isEndless: Boolean = false,
        isReview: Boolean = false
    ): Kids123Level {
        val entry = NumberBank.byNumber(numberValue)
        val random = Random(seed)
        val quiz = when (quizType) {
            NumberQuizType.COUNT -> buildCountQuiz(numberValue, entry, random)
            NumberQuizType.BIGGER -> buildBiggerQuiz(numberValue, compareValue ?: (numberValue - 2).coerceAtLeast(1), entry, random)
        }
        val prompt = when (quizType) {
            NumberQuizType.COUNT -> "How many dots do you see?"
            NumberQuizType.BIGGER -> "Which number is bigger?"
        }
        return Kids123Level(
            seed = seed,
            levelNumber = levelNumber,
            difficulty = difficulty,
            numberValue = numberValue,
            entry = entry,
            quizType = quizType,
            quizOptions = quiz.options,
            quizCorrectIndex = quiz.correctIndex,
            quizPrompt = prompt,
            compareValue = compareValue,
            isTutorial = isTutorial,
            isEndless = isEndless,
            isReview = isReview
        )
    }

    private fun buildCountQuiz(number: Int, entry: NumberEntry, random: Random): QuizBuild {
        val options = (listOf(number) + entry.countDistractors).distinct().take(Kids123Level.OPTIONS_PER_QUIZ)
        val padded = if (options.size < Kids123Level.OPTIONS_PER_QUIZ) {
            options + (1..20).filter { it !in options }.shuffled(random).take(Kids123Level.OPTIONS_PER_QUIZ - options.size)
        } else options
        val shuffled = padded.shuffled(random)
        return QuizBuild(shuffled, shuffled.indexOf(number))
    }

    private fun buildBiggerQuiz(primary: Int, secondary: Int, entry: NumberEntry, random: Random): QuizBuild {
        val correct = maxOf(primary, secondary)
        val options = (listOf(correct, primary, secondary) + entry.biggerDistractors)
            .distinct().take(Kids123Level.OPTIONS_PER_QUIZ)
        val shuffled = options.shuffled(random)
        return QuizBuild(shuffled, shuffled.indexOf(correct))
    }

    data class ReviewSpec(val primary: Int, val quizType: NumberQuizType, val compare: Int? = null)
    data class QuizBuild(val options: List<Int>, val correctIndex: Int)
}
