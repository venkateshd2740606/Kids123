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
        Names("One", "एक", "ఒకటి", "ஒன்று", "ಒಂದು", "ഒന്ന്"),
        Names("Two", "दो", "రెండు", "இரண்டு", "ಎರಡು", "രണ്ട്"),
        Names("Three", "तीन", "మూడు", "மூன்று", "ಮೂರು", "മൂന്ന്"),
        Names("Four", "चार", "నాలుగు", "நான்கு", "ನಾಲ್ಕು", "നാല്"),
        Names("Five", "पांच", "అయిదు", "ஐந்து", "ಐದು", "അഞ്ച്"),
        Names("Six", "छह", "ఆరు", "ஆறு", "ಆರು", "ആറ്"),
        Names("Seven", "सात", "ఏడు", "ஏழு", "ಏಳು", "ഏഴ്"),
        Names("Eight", "आठ", "ఎనిమిది", "எட்டு", "ಎಂಟು", "എട്ട്"),
        Names("Nine", "नौ", "తొమ్మిది", "ஒன்பது", "ಒಂಬತ್ತು", "ഒമ്പത്"),
        Names("Ten", "दस", "పది", "பத்து", "ಹತ್ತು", "പത്ത്"),
        Names("Eleven", "ग्यारह", "పదకొండు", "பதினொன்று", "ಹನ್ನೊಂದು", "പതിനൊന്ന്"),
        Names("Twelve", "बारह", "పన్నెండు", "பன்னிரண்டு", "ಹನ್ನೆರಡು", "പന്ത്രണ്ട്"),
        Names("Thirteen", "तेरह", "పదమూడు", "பதிமூன்று", "ಹದಿಮೂರು", "പതിമൂന്ന്"),
        Names("Fourteen", "चौदह", "పద్నాలుగు", "பதினான்கு", "ಹದಿನಾಲ್ಕು", "പതിനാല്"),
        Names("Fifteen", "पंद्रह", "పదిహేను", "பதினைந்து", "ಹದಿನೈದು", "പതിനഞ്ച്"),
        Names("Sixteen", "सोलह", "పదహారు", "பதினாறு", "ಹದಿನಾರು", "പതിനാറ്"),
        Names("Seventeen", "सत्रह", "పదిహేడు", "பதினேழு", "ಹದಿನೇಳು", "പതിനേഴ്"),
        Names("Eighteen", "अठारह", "పద్దెనిమిది", "பதினெட்டு", "ಹದಿನೆಂಟು", "പതിനെട്ട്"),
        Names("Nineteen", "उन्नीस", "పందొమ్మిది", "பத்தொன்பது", "ಹತ್ತೊಂಬತ್ತು", "പത്തൊമ്പത്"),
        Names("Twenty", "बीस", "ఇరవై", "இருபது", "ಇಪ್ಪತ್ತು", "ഇരുപത്")
    )

    val all: List<NumberEntry> = (1..20).map { n ->
        val name = names[n - 1]
        NumberEntry(
            number = n,
            englishName = name.english,
            hindiName = name.hindi,
            teluguName = name.telugu,
            tamilName = name.tamil,
            kannadaName = name.kannada,
            malayalamName = name.malayalam,
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
        val tamil: String,
        val kannada: String,
        val malayalam: String
    )
}
