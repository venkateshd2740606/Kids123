package com.kids123.engine

import com.kids123.domain.model.NumberEntry

object NumberBank {

    private val colors = listOf(
        0xFFEF5350, 0xFF42A5F5, 0xFF66BB6A, 0xFFFFA726, 0xFFAB47BC,
        0xFF26A69A, 0xFF5C6BC0, 0xFFEC407A, 0xFF8D6E63, 0xFF78909C,
        0xFFFF7043, 0xFF29B6F6, 0xFF9CCC65, 0xFFFFCA28, 0xFF7E57C2,
        0xFF26C6DA, 0xFF5D4037, 0xFF00897B, 0xFFD81B60, 0xFF3949AB
    )

    private val names = listOf(
        Names("One", "एक", "ఒకటి", "ஒன்று"),
        Names("Two", "दो", "రెండు", "இரண்டு"),
        Names("Three", "तीन", "మూడు", "மூன்று"),
        Names("Four", "चार", "నాలుగు", "நான்கு"),
        Names("Five", "पांच", "అయిదు", "ஐந்து"),
        Names("Six", "छह", "\u0C06\u0C30\u0C41", "\u0B86\u0BB1\u0BC1"),
        Names("Seven", "सात", "\u0C0E\u0C26\u0C41", "\u0B8F\u0BB4\u0BC1"),
        Names("Eight", "आठ", "\u0C0E\u0C28\u0C3F\u0C02\u0C26\u0C3F", "\u0B8E\u0B9F\u0BCD\u0B9F\u0BC1"),
        Names("Nine", "नौ", "\u0C24\u0C4B\u0C2E\u0C4D\u0C2E\u0C3F\u0C26\u0C3F", "\u0B92\u0BA9\u0BCD\u0BAA\u0BA4\u0BC1"),
        Names("Ten", "दस", "\u0C2A\u0C26\u0C3F", "\u0BAA\u0BA4\u0BCD\u0BA4\u0BC1"),
        Names("Eleven", "ग्यारह", "\u0C2A\u0C26\u0C15\u0C3F\u0C28\u0C41\u0C26\u0C41", "\u0BAA\u0BA4\u0BBF\u0BA9\u0BCA\u0BA9\u0BCD\u0BB1\u0BC1"),
        Names("Twelve", "बारह", "\u0C2A\u0C28\u0C4D\u0C28\u0C21\u0C41", "\u0BAA\u0BA9\u0BCD\u0BA9\u0BBF\u0BB0\u0BA3\u0BCD\u0B9F\u0BC1"),
        Names("Thirteen", "तेरह", "\u0C2A\u0C26\u0C2E\u0C41\u0C21\u0C41", "\u0BAA\u0BA4\u0BBF\u0BAE\u0BC2\u0BA9\u0BCD\u0BB1\u0BC1"),
        Names("Fourteen", "चौदह", "\u0C2A\u0C26\u0C28\u0C3E\u0C32\u0C41\u0C17\u0C41", "\u0BAA\u0BA4\u0BBF\u0BA9\u0BBE\u0BA9\u0BCD\u0B95\u0BC1"),
        Names("Fifteen", "पंद्रह", "\u0C2A\u0C26\u0C39\u0C3F\u0C28\u0C41", "\u0BAA\u0BA4\u0BBF\u0BA8\u0BC8\u0BA8\u0BCD\u0BA4\u0BC1"),
        Names("Sixteen", "सोलह", "\u0C2A\u0C26\u0C3F\u0C28\u0C3E\u0C30\u0C41", "\u0BAA\u0BA4\u0BBF\u0BA8\u0BBE\u0B9F\u0BC1"),
        Names("Seventeen", "सत्रह", "\u0C2A\u0C26\u0C3F\u0C28\u0C46\u0C26\u0C41", "\u0BAA\u0BA4\u0BBF\u0BA8\u0BC7\u0B9F\u0BC1"),
        Names("Eighteen", "अठारह", "\u0C2A\u0C26\u0C3F\u0C28\u0C46\u0C28\u0C3F\u0C02\u0C26\u0C3F", "\u0BAA\u0BA4\u0BBF\u0BA8\u0BC6\u0B9F\u0BCD\u0B9F\u0BC1"),
        Names("Nineteen", "उन्नीस", "\u0C2A\u0C26\u0C39\u0C46\u0C28\u0C4D\u0C2A\u0C26\u0C3F", "\u0BAA\u0BA4\u0BCD\u0BA4\u0BC6\u0BA9\u0BCD\u0BAA\u0BA4\u0BC1"),
        Names("Twenty", "बीस", "\u0C08\u0C30\u0C35\u0C48", "\u0B87\u0BB0\u0BC1\u0BAA\u0BCD\u0BAA\u0BA4\u0BC1")
    )

    val all: List<NumberEntry> = (1..20).map { n ->
        val name = names[n - 1]
        NumberEntry(
            number = n,
            englishName = name.english,
            hindiName = name.hindi,
            teluguName = name.telugu,
            tamilName = name.tamil,
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

    private data class Names(
        val english: String,
        val hindi: String,
        val telugu: String,
        val tamil: String
    )
}
