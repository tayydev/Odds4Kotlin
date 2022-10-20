fun main() {
    println("hello world")

    val cards = arrayOf(
        Card(Value.Jack, Suit.Clubs),
        Card(Value.Eight, Suit.Spades),
        Card(Value.Ace, Suit.Hearts)
    )

    val royal = listOf(
        Card(Value.Ace, Suit.Diamonds),
        Card(Value.King, Suit.Diamonds),
        Card(Value.Jack, Suit.Diamonds),
        Card(Value.Queen, Suit.Diamonds),
        Card(Value.Ten, Suit.Diamonds),
    )

    val straight = listOf(
        Card(Value.Two, Suit.Diamonds),
        Card(Value.Four, Suit.Diamonds),
        Card(Value.Three, Suit.Diamonds),
        Card(Value.Ace, Suit.Diamonds),
        Card(Value.Five, Suit.Diamonds),
    )

    val arr = listOf(Hand(royal), Hand(straight))

    println(arr.max())

    val testing = Hand(straight)

    println("${testing.isFlush()}, ${testing.isStraight()}")

    println(cards.max().toString())
}

data class Hand(private var cards: List<Card>): Comparable<Hand> {
    init {
        //todo find best hand
    }

    fun hierarchy(): Pair<Hierarchy, List<Card>> {
        if(isFlush() && isStraight()) return Hierarchy.StraightFlush to
                cards.setHelper()
        if(isNPair(4)) return Hierarchy.Quads to
                cards.setHelper(4).plus(cards.setHelper(1))
        if(isNPair(3) && isNPair(2)) return Hierarchy.FullHouse to
                cards.setHelper(3).plus(cards.setHelper(2))
        if(isFlush()) return Hierarchy.Flush to
                cards.setHelper()
        if(isStraight()) return Hierarchy.Straight to
                cards.setHelper()
        if(isNPair(3)) return Hierarchy.Trips to
                cards.setHelper(3).plus(cards.setHelper(1))
        if(isTwoPair()) return Hierarchy.TwoPair to
                cards.setHelper(2).plus(cards.setHelper(1))
        if(isNPair(2)) return Hierarchy.Pair to
                cards.setHelper(2).plus(cards.setHelper(1))
        return Hierarchy.HighCard to
                cards.setHelper()
    }
    fun isFlush(): Boolean {
        return cards.all { it.suit == cards.first().suit } //assumes one card
    }

    fun isStraight(): Boolean {
        return cards.sorted().zipWithNext().all { (card1, card2) ->
            card1.value.ordinal + 1 == card2.value.ordinal ||
            card2.value == Value.Ace && cards.minOf { it }.value == Value.Two
        }
    }

    fun isNPair(num: Int): Boolean { //this is picky
        return cards.groupBy { it.value }.any { it.value.size == num }
    }

    fun isTwoPair(): Boolean {
        return cards.groupBy { it.value }.count { it.value.size == 2 } == 2
    }

    override fun compareTo(other: Hand): Int {
        val first = hierarchy().first.compareTo(other.hierarchy().first)
        val rest = hierarchy().second.zip(other.hierarchy().second).map { (self, other) -> self.compareTo(other) }
        return priorityCompare(listOf(first).plus(rest))
    }
    private fun priorityCompare(compared: List<Int>): Int {
        return compared.firstOrNull { it != 0 } ?: 0
    }
}

data class Card(val value: Value, val suit: Suit): Comparable<Card> {
    override fun compareTo(other: Card): Int {
        return value.compareTo(other.value)
    }
}

enum class Hierarchy {
    HighCard,
    Pair,
    TwoPair,
    Trips,
    Straight,
    Flush,
    FullHouse,
    Quads,
    StraightFlush
}

enum class Suit {
    Diamonds,
    Hearts,
    Clubs,
    Spades
}

enum class Value {
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,
    Ten,
    Jack,
    Queen,
    King,
    Ace
}

//finds sets with n values and sorts descending
private fun List<Card>.setHelper(num: Int = 1): List<Card> {
    val sets = this.groupBy { it.value }
        .filter {
            it.value.size == num
        }
    return sets.map { it.value.first() }.sortedDescending()
}