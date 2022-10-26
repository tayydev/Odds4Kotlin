fun main() {
    val deck = Deck()
    for(i in 0 until 4) deck.removeFirst()
    val combos = fastCombinations(deck, desiredSize = 5)
    print("Done")
}

class Game(val hands:List<Hand>, val board: List<Card> = emptyList(), val burns:List<Card> = emptyList()) {
    val deck = Deck()

    init {
        deck.removeAll(hands.flatMap { it.cards })
        deck.removeAll(board)
        deck.removeAll(burns)
    }

    fun odds(): Map<Hand, Result> { // wins - ties - outcomes
        val map = mutableMapOf<Hand, Result>()
        val options = combinations(deck, maxSize = 5 - board.size)
        for(opt in options) {
            hands.map { map.computeIfAbsent(it) { Result() }.total++ }
            val tempBoard = board.plus(opt)
            val handMaps = hands.associateWith { Hand(it.cards.plus(tempBoard)) }
            val bestHand = handMaps.values.max()
            handMaps
                .filterValues { it == bestHand }
                .keys.forEach { hand: Hand -> map[hand]!!.wins++ }
        }
        return map
    }
}

data class Result(var wins: Int = 0, var ties: Int = 0, var total: Int = 0)

class Deck: ArrayList<Card>(makeDeck())
private fun makeDeck() = Value.values()
    .flatMap { face -> Suit.values().map { face to it } }
    .map { (face, suit) -> Card(face, suit) }

fun <T> combinations(cards: List<T>, maxSize: Int = 5): List<List<T>> {
    if(cards.size == maxSize) return listOf(cards)
    return cards
        .map { cards.minus(it) }
        .flatMap { combinations(it, maxSize) }
        .distinct()
}

//fun <T> fastCombinations(inputs: List<T>, desiredSize: Int): List<List<T>> {
//    val returnable = mutableListOf<Pair<T, T>>()
//    for(item in inputs) {
//        for(second in inputs.minus(item)) {
//            returnable.add(item to second)
//        }
//    }
//}
fun <T> fastCombinations(inputs: List<T>, desiredSize: Int): Set<Set<T>> {
    val start = mutableSetOf<Set<T>>()
    for(item in inputs) {
        start.add(setOf(item))
    }
    return fastCombinationsHelper(inputs.toSet(), start, desiredSize)
}

//todo this doesn't work with desiredSize > 2
fun <T> fastCombinationsHelper(inputs: Set<T>, progress: Set<Set<T>>, desiredSize: Int): Set<Set<T>> {
    val nextIter = mutableSetOf<Set<T>>()
    var count = 0
    for(set in progress) {
        for(item in inputs.minus(set)) {
            nextIter.add(set.plus(item))

            count++
            println("in $count of ${progress.size * (inputs.size-set.size)}")
        }
    }
    if(nextIter.first().size == desiredSize) return nextIter
    return fastCombinationsHelper(inputs, nextIter, desiredSize)
}


fun deckCombinations(input: Deck) {

}

class Hand(cards: List<Card>): Comparable<Hand> {
    val cards = if (cards.size == 5) combinations(cards).maxBy { Hand(it) } else cards

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Hand

        if (cards.containsAll(other.cards) && other.cards.containsAll(cards)) return true

        return false
    }

    override fun hashCode(): Int {
        return cards.hashCode()
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