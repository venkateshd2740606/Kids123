package com.kids123.engine

import com.kids123.domain.model.Difficulty
import com.kids123.domain.model.NumberQuizType

object TutorialLevels {

    val all = (1..5).map { number ->
        Kids123Generator.buildLevel(
            seed = number.toLong(),
            levelNumber = number,
            difficulty = Difficulty.BEGINNER,
            numberValue = number,
            quizType = NumberQuizType.COUNT,
            isTutorial = true
        )
    }

    fun getTutorialLevel(index: Int) = all.getOrNull(index)
}
