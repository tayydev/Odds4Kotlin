class Game(val hands:Set<Hand>, val board: Set<Card> = emptySet(), val burns:Set<Card> = emptySet()) {
    val deck = Deck()

    init {
        deck.removeAll(hands.flatMap { it.cards })
        deck.removeAll(board)
        deck.removeAll(burns)
    }

    fun odds(testSize: Int = 10000): Map<Hand, Result> { // wins - ties - outcomes
        val runouts = randomCombinations(deck, testSize, 5 - board.size)

        val data = mutableMapOf<Hand, Result>()
        hands.map { data[it] = Result(total = runouts.size) } // create results with totals

        for(runout in runouts) {
            val resultingBoard = board.plus(runout)
            val handsToScore = hands.associateWith { startingHand ->
                val sevenCards = startingHand.cards.plus(resultingBoard)
                val hand = Hand(sevenCards)
                return@associateWith hand.numericalHierarchy()
            }
            val highestScore = handsToScore.values.max()
            val handsWithHighestScore = handsToScore.filter { it.value == highestScore }
            if(handsWithHighestScore.size == 1) {
                val winningHand = handsWithHighestScore.keys.first()
                data[winningHand]!!.wins++
            }
            else {
                handsWithHighestScore.keys.map { tiedHand ->
                    data[tiedHand]!!.ties++
                }
            }
        }
        return data
    }
}

data class Result(var wins: Int = 0, var ties: Int = 0, var total: Int = 0)

class Deck: HashSet<Card>(makeDeck())
private fun makeDeck() = Value.values()
    .flatMap { face -> Suit.values().map { face to it } }
    .map { (face, suit) -> Card(face, suit) }

fun <T> combinations(cards: Set<T>, maxSize: Int = 5): Set<Set<T>> {
    if(cards.size == maxSize) return setOf(cards)
    return cards
        .map { cards.minus(it) }
        .flatMap { combinations(it, maxSize) }
        .toSet()
}

fun <T> randomCombinations(cards: Set<T>, returnSize: Int, individualSize: Int): List<Set<T>> {
    return arrayOfNulls<Set<T>>(returnSize)
        .map {
            cards
                .shuffled()
                .take(individualSize)
                .toSet()
        }
}

fun Hand(cards: List<Card>): Hand = Hand(cards.toSet())
class Hand(cards: Set<Card>): Comparable<Hand> {
    val cards =
        if (cards.size >= 5) {
            combinations(cards).maxBy { Hand(it) }
        }
        else if (cards.size > 2) {
            throw Exception("Unable to parse Hand with 5 > size > 2")
        } else {
            cards
        }

    fun numericalHierarchy(): Int {
        val list = listHierarchy()
        return  list[0] * 13 * 13 * 13 * 13 * 13 + //hierarchy
                list[1] * 13 * 13 * 13 * 13 + //kicker
                list[2] * 13 * 13 * 13 + //kicker
                list[3] * 13 * 13 + //kicker
                list[4] * 13 + //kicker
                list[5] //kicker
    }
    fun listHierarchy(): List<Int> {
        val base = hierarchy()
        return listOf(base.first.ordinal,
                (base.second.getOrNull(0)?.value?.ordinal ?: 0),
                (base.second.getOrNull(1)?.value?.ordinal ?: 0),
                (base.second.getOrNull(2)?.value?.ordinal ?: 0),
                (base.second.getOrNull(3)?.value?.ordinal ?: 0),
                (base.second.getOrNull(4)?.value?.ordinal ?: 0))
    }
    fun hierarchy(): Pair<Hierarchy, List<Card>> { //TODO: i think capping size is irrelevant when this method only gets called on five card hands
        if(isFlush() && isStraight()) {
            val kickers = if(isWheel()) cards.setHelper().drop(1) else cards.setHelper() //drop the ace for wheels
            return Hierarchy.StraightFlush to kickers
        }
        if(isNPair(4)) {
            val quads = cards.setHelper(4).capSize(1)
            val kicker = cards.setHelper(1).capSize(1)
            return Hierarchy.Quads to quads + kicker
        }
        if(isNPair(3) && isNPair(2)) {
            val trips = cards.setHelper(3).capSize(3)
            val pairs = cards.setHelper(2).capSize(2)
            return Hierarchy.FullHouse to trips + pairs
        }
        if(isFlush()) return Hierarchy.Flush to cards.setHelper()
        if(isStraight()) {
            val kickers = if(isWheel()) cards.setHelper().drop(1) else cards.setHelper() //drop the ace for wheels
            return Hierarchy.Straight to kickers
        }
        if(isNPair(3)){
            val trips = cards.setHelper(3).capSize(1)
            val kickers = cards.setHelper(1).capSize(2)
            return Hierarchy.Trips to trips + kickers
        }
        if(isTwoPair()) {
            val pairs = cards.setHelper(2).capSize(4)
            val kicker = cards.setHelper(1).capSize(1)
            return Hierarchy.TwoPair to pairs + kicker
        }
        if(isNPair(2)) {
            val pair = cards.setHelper(2).capSize(2) //this capListSize is irrelevant but whatever
            val kickers = cards.setHelper(1).capSize(3)
            return Hierarchy.Pair to pair + kickers
        }
        return Hierarchy.HighCard to cards.setHelper()
    }
    private fun isFlush(): Boolean {
        return cards.all { it.suit == cards.first().suit } //assumes one card
    }

    private fun isStraight(): Boolean {
        val lowAce = cards.map { if(it.value == Value.Ace) -1 else it.value.ordinal }
        val highAce = cards.map { it.value.ordinal }

        fun testSequence(list: List<Int>) = list.sorted().zipWithNext().all { (val1, val2) -> val1 + 1 == val2 }

        return testSequence(lowAce) || testSequence(highAce)
    }

    private fun isWheel(): Boolean {
        if(!isStraight()) return false
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

        if (cards.containsAll(other.cards) && other.cards.containsAll(cards)) return true

        return false
    }

    override fun hashCode(): Int {
        return cards.hashCode()
    }

    override fun toString(): String {
        return cards.toString()
    }
}

data class Card(val value: Value, val suit: Suit): Comparable<Card> {
    override fun compareTo(other: Card): Int {
        return value.compareTo(other.value)
    }

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

//finds sets with n values and sorts descending
private fun Set<Card>.setHelper(num: Int = 1): List<Card> {
    val sets = this.groupBy { it.value }
        .filter {
            it.value.size == num
        }
    return sets.flatMap { it.value }.sortedDescending()
}

fun <T> List<T>.capSize(cap: Int): List<T> {
    val cappedSize = minOf(cap, this.size)
    return this.subList(0, cappedSize)
}
