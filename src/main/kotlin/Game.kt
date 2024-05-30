/**
 * Very explicit abstraction around a "Game" of poker. Specify all [Hand]s involved, current board, and burned cards.
 */
@Suppress("MemberVisibilityCanBePrivate")
class Game(val hands: Set<Hand>, val board: Set<Card> = emptySet(), val burns: Set<Card> = emptySet()) {
    /**
     * Return a mapping from each [Hand] to its [Result] in a Monte Carlo simulation.
     *
     * @param testSize the number of simulations to run
     */
    fun odds(testSize: Int = 10000): Map<Hand, Result> { // wins - ties - outcomes
        val deckMinusBurns = makeDeck()
            .minus(hands.flatMap { it.cards }.toSet())
            .minus(board)
            .minus(burns)

        val runs = randomCombinations(deckMinusBurns, testSize, 5 - board.size)

        val data = mutableMapOf<Hand, Result>()
        hands.map { data[it] = Result(total = runs.size) } // create results with totals

        for (run in runs) {
            val resultingBoard = board.plus(run)
            val handsToScore = hands.associateWith { startingHand ->
                val sevenCards = startingHand.cards.plus(resultingBoard)
                val hand = Hand(sevenCards)
                return@associateWith hand.numericalHierarchy()
            }
            val highestScore = handsToScore.values.max()
            val handsWithHighestScore = handsToScore.filter { it.value == highestScore }
            if (handsWithHighestScore.size == 1) {
                val winningHand = handsWithHighestScore.keys.first()
                data[winningHand]!!.wins++
            } else {
                handsWithHighestScore.keys.map { tiedHand ->
                    data[tiedHand]!!.ties++
                }
            }
        }
        return data
    }

    override fun toString(): String {
        return "$board ($burns) ${hands.joinToString { "v" }}"
    }
}

/**
 * Data class that represents a result for one hand in a Monte Carlo simulation
 */
data class Result(var wins: Int = 0, var ties: Int = 0, var total: Int = 0)

/**
 * Find combinations of a set that are <= maxSize
 *
 * Useful to reduce 7 cards down to every possible 5 card combination
 */
fun <T> combinations(cards: Set<T>, maxSize: Int = 5): Set<Set<T>> {
    if (cards.size <= maxSize) return setOf(cards)
    return cards
        .map { cards.minus(it) }
        .flatMap { combinations(it, maxSize) }
        .toSet() //this is doing a lot of heavy lifting; this method is not well optimized
}

/**
 * Take random combinations from a set of cards
 *
 * @param returnSize number of combinations to take
 * @param individualSize how large is any one combination supposed to be
 */
fun <T> randomCombinations(cards: Set<T>, returnSize: Int, individualSize: Int): List<Set<T>> {
    return arrayOfNulls<Set<T>>(returnSize)
        .map {
            cards
                .shuffled()
                .take(individualSize)
                .toSet()
        }
}

/**
 * Instantiate a hand from a list of cards
 */
fun Hand(cards: List<Card>): Hand = Hand(cards.toSet())

/**
 * Wrappers around a set of cards that allow for comparability
 */
class Hand(cards: Set<Card>) : Comparable<Hand> {
    val cards =
        if (cards.size >= 5) {
            combinations(cards).maxBy { Hand(it) }
        } else if (cards.size > 2) {
            throw Exception("Unable to parse Hand with 5 > size > 2")
        } else {
            cards
        }

    /**
     * Stack the integers from [listHierarchy] into one int value
     */
    fun numericalHierarchy(): Int {
        val list = listHierarchy()
        return list[0] * 13 * 13 * 13 * 13 * 13 + //hierarchy
                list[1] * 13 * 13 * 13 * 13 + //kicker
                list[2] * 13 * 13 * 13 + //kicker
                list[3] * 13 * 13 + //kicker
                list[4] * 13 + //kicker
                list[5] //kicker
    }

    /**
     * Remap the human-readable results from [hierarchy] into a list of integers
     */
    fun listHierarchy(): List<Int> {
        val base = hierarchy()
        return listOf(
            base.first.ordinal,
            (base.second.getOrNull(0)?.value?.ordinal ?: 0),
            (base.second.getOrNull(1)?.value?.ordinal ?: 0),
            (base.second.getOrNull(2)?.value?.ordinal ?: 0),
            (base.second.getOrNull(3)?.value?.ordinal ?: 0),
            (base.second.getOrNull(4)?.value?.ordinal ?: 0)
        )
    }

    /**
     * Assign hands the smallest possible human-readable bits of information to determine their comparative ranking
     *
     * @return a Pair where the first item is an [Hierarchy] enum representing the type of hand and the second component
     * is a list of [Card]s that serve as kickers
     */
    fun hierarchy(): Pair<Hierarchy, List<Card>> {
        if (isFlush() && isStraight()) {
            val kickers = if (isWheel()) cards.setHelper().drop(1) else cards.setHelper() //drop the ace for wheels
            val kicker = kickers.capSize(1) //only one kicker for straights
            return Hierarchy.StraightFlush to kicker
        }
        if (isNPair(4)) {
            val quads = cards.setHelper(4).capSize(1) //these are all the same so we just use one
            val kicker = cards.setHelper(1).capSize(1)
            return Hierarchy.Quads to quads + kicker
        }
        if (isNPair(3) && isNPair(2)) { //full house
            val trips = cards.setHelper(3).capSize(1) //these are all the same so we just use one
            val pairs = cards.setHelper(2).capSize(1) //these will also all be the same
            return Hierarchy.FullHouse to trips + pairs
        }
        if (isFlush()) {
            return Hierarchy.Flush to cards.setHelper() //in a flush we can just use kickers w/out thinking
        }
        if (isStraight()) {
            val kickers = if (isWheel()) cards.setHelper().drop(1) else cards.setHelper() //drop the ace for wheels
            val kicker = kickers.capSize(1) //only one kicker needed for straights
            return Hierarchy.Straight to kicker
        }
        if (isNPair(3)) { //this is really trips
            val trips = cards.setHelper(3).capSize(1)
            val kickers = cards.setHelper(1).capSize(2)
            return Hierarchy.Trips to trips + kickers
        }
        if (isTwoPair()) {
            val pairs = cards.setHelper(2).capSize(4) //we need all four to adequately compare
            /*
            We can trust that cards.setHelper(2) will never have size > 4 (and thus our kicker won't be part of a pair)
            because at this stage we've limited ourselves to five cards, so even if we had a three-pair we would have
            already filtered it down to the best possible two pair + one kicker
            */
            val kicker = cards.setHelper(1).capSize(1)
            return Hierarchy.TwoPair to pairs + kicker
        }
        if (isNPair(2)) { //this is really single pair
            val pair = cards.setHelper(2).capSize(1)
            val kickers = cards.setHelper(1).capSize(4)
            return Hierarchy.Pair to pair + kickers
        }
        return Hierarchy.HighCard to cards.setHelper()
    }

    private fun isFlush(): Boolean {
        return cards.all { it.suit == cards.first().suit } //assumes at least one card
    }

    private fun isStraight(): Boolean {
        val lowAce = cards.map { if (it.value == Value.Ace) -1 else it.value.ordinal }
        val highAce = cards.map { it.value.ordinal }

        fun testSequence(list: List<Int>) = list.sorted().zipWithNext().all { (val1, val2) -> val1 + 1 == val2 }

        return testSequence(lowAce) || testSequence(highAce)
    }

    private fun isWheel(): Boolean {
        if (!isStraight()) return false
        val values = cards.map { it.value }
        return values.contains(Value.Ace) && values.contains(Value.Two)
    }

    private fun isNPair(num: Int): Boolean { //this is picky
        return cards.groupBy { it.value }.any { it.value.size == num }
    }

    private fun isTwoPair(): Boolean {
        return cards.groupBy { it.value }.count { it.value.size == 2 } == 2
    }

    override fun compareTo(other: Hand): Int {
        return this.numericalHierarchy().compareTo(other.numericalHierarchy())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Hand

        return cards.containsAll(other.cards) && other.cards.containsAll(cards)
    }

    override fun hashCode(): Int {
        return cards.hashCode()
    }

    override fun toString(): String {
        return cards.toString()
    }
}

/**
 * Make a deck of every possible card
 */
fun makeDeck(): Set<Card> = Value.values()
    .flatMap { face -> Suit.values().map { face to it } }
    .map { (face, suit) -> Card(face, suit) }
    .toSet()

data class Card(val value: Value, val suit: Suit) : Comparable<Card> {
    override fun compareTo(other: Card): Int {
        return value.compareTo(other.value)
    }

    @Suppress("unused")
    val ordinal = value.ordinal + 13 * suit.ordinal
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

/**
 * Finds sets with n values and sorts descending.
 *
 * i.e. If you call setHelper with num=2 on a two pair hand, it will return all the paired cars in descending order (so
 * two of the higher pair followed by two of the lower pair)
 */
private fun Set<Card>.setHelper(num: Int = 1): List<Card> {
    val sets = this.groupBy { it.value }
        .filter {
            it.value.size == num
        }
    return sets.flatMap { it.value }.sortedDescending()
}

/**
 * List with a maximum size
 */
fun <T> List<T>.capSize(cap: Int): List<T> {
    val cappedSize = minOf(cap, this.size)
    return this.subList(0, cappedSize)
}
