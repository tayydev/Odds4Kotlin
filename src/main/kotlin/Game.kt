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

    fun hierarchy(): Hierarchy {
        if(isFlush() && isStraight()) return Hierarchy.StraightFlush
        if(isNPair(4)) return Hierarchy.Quads
        if(isNPair(3) && isNPair(2)) return Hierarchy.FullHouse
        if(isFlush()) return Hierarchy.Flush
        if(isStraight()) return Hierarchy.Straight
        if(isNPair(3)) return Hierarchy.Trips
        if(isTwoPair()) return Hierarchy.TwoPair
        if(isNPair(2)) return Hierarchy.Pair
        return Hierarchy.HighCard
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
        if(hierarchy() != other.hierarchy()) {
            return hierarchy().compareTo(other.hierarchy())
        }
        when(hierarchy()) {
            Hierarchy.StraightFlush -> return valueCompare(this.cards, other.cards)
            Hierarchy.Quads -> {
                val grouped = this.cards.groupBy { it.value }
                val otherGrouped = other.cards.groupBy { it.value }

                return priorityCompare(
                    grouped.firstNotNullOf { it.value.size == 4 }.compareTo(otherGrouped.firstNotNullOf { it.value.size == 4 }),
                    grouped.firstNotNullOf { it.value.size == 1 }.compareTo(otherGrouped.firstNotNullOf { it.value.size == 1 })
                )
            }
            else -> {}
        }
        return -1
    }

    private fun valueCompare(self: List<Card>, other: List<Card>): Int {
        return priorityCompare(
            self.sortedDescending().zip(other.sortedDescending()).map { (self, other) ->
                self.compareTo(other)
            }
        )
    }

    private fun priorityCompare(vararg compared: Int): Int = priorityCompare(compared.toList())
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