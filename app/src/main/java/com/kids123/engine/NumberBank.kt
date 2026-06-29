package com.kids123.engine

import com.kids123.domain.model.NumberEntry

object NumberBank {

    private val colors = listOf(
        0xFFEF5350, 0xFF42A5F5, 0xFF66BB6A, 0xFFFFA726, 0xFFAB47BC,
        0xFF26A69A, 0xFF5C6BC0, 0xFFEC407A, 0xFF8D6E63, 0xFF78909C,
        0xFFFF7043, 0xFF29B6F6, 0xFF9CCC65, 0xFFFFCA28, 0xFF7E57C2,
        0xFF26C6DA, 0xFF5D4037, 0xFF00897B, 0xFFD81B60, 0xFF3949AB
    )

    val all: List<NumberEntry> = (1..20).map { n ->
        NumberEntry(
            number = n,
            dotColorArgb = colors[(n - 1) % colors.size],
            countDistractors = listOf(
                (n - 2).coerceAtLeast(1),
                (n + 1).coerceAtMost(20),
                (n + 2).coerceAtMost(20)
            ).filter { it != n }.distinct().take(3),
            biggerDistractors = listOf(
                (n - 1).coerceAtLeast(1),
                (n - 3).coerceAtLeast(1),
                (n + 1).coerceAtMost(20)
            ).filter { it != n }.distinct().take(3)
        )
    }

    fun byNumber(number: Int): NumberEntry = all[(number - 1).coerceIn(0, all.lastIndex)]
}
