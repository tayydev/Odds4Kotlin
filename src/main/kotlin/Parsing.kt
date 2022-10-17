fun Card(code: String) = Card(findValue(code), findSuit(code))
fun Hand(code: String) = Hand(findCards(code))

private val translateV = mapOf(
    "A" to Value.Ace,
    "K" to Value.King,
    "Q" to Value.Queen,
    "J" to Value.Jack,
    "10" to Value.Ten,
    "9" to Value.Nine,
    "8" to Value.Eight,
    "7" to Value.Seven,
    "6" to Value.Six,
    "5" to Value.Five,
    "4" to Value.Four,
    "3" to Value.Three,
    "2" to Value.Two
)
private fun findValue(str: String): Value {
    return translateV[translateV.keys.first { str.contains(it) }]!!
}
private val translateS = mapOf(
    "H" to Suit.Hearts,
    "D" to Suit.Diamonds,
    "S" to Suit.Spades,
    "C" to Suit.Clubs,
)
private fun findSuit(str: String): Suit {
    return translateS[translateS.keys.first { str.contains(it) }]!!
}

private fun findCards(code: String) = code.split(" ").map { Card(it) }