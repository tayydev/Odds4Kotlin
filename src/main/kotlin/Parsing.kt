private val translateV = mapOf(
    "A" to Value.Ace,
    "K" to Value.King,
    "Q" to Value.Queen,
    "J" to Value.Jack,
    "T" to Value.Ten,
    "9" to Value.Nine,
    "8" to Value.Eight,
    "7" to Value.Seven,
    "6" to Value.Six,
    "5" to Value.Five,
    "4" to Value.Four,
    "3" to Value.Three,
    "2" to Value.Two
)
private val translateS = mapOf(
    "H" to Suit.Hearts,
    "D" to Suit.Diamonds,
    "S" to Suit.Spades,
    "C" to Suit.Clubs,
)
private fun findValue(str: String): Value {
    return translateV[translateV.keys.first { str.contains(it) }]!!
}
private fun findSuit(str: String): Suit {
    return translateS[translateS.keys.first { str.contains(it) }]!!
}

fun Card(code: String) = Card(findValue(code), findSuit(code))

fun Hand(code: String) = Hand(findCards(code))

fun Game(code: String): Game {
    val board = findCards(
        code.split("(").first()
    )
    val burns = findCards(
        code.split("(")[1].split(")")[0] //lol
    )
    val hands = code.uppercase().split(")")[1].split("V").map { Hand(it) }.toSet()

    return Game(
        hands=hands,
        board=board,
        burns=burns
    )
}

fun findCards(code: String): Set<Card> {
    val sanitized = code.uppercase().trim()
    //space based mapping
    return if(sanitized.contains(" ")) sanitized.split(" ").map { Card(it) }.toSet()
    else if(sanitized.length % 2 == 0) {
        sanitized.chunked(2).map { Card(it) }.toSet()
    }
    else throw Exception("Invalid card code")
}