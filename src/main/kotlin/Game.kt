import kotlin.system.measureTimeMillis

fun main() {
//    val a = 1L
//    val b = 3
//
//    val combined = a or (1L shl b)
//    println(combined)
//
//    val exists = combined and (1L shl b)
//    val exists2 = combined and (1L shl 2)
//    val exists3 = combined and (1L shl 0)
//
//    val list = LinkedCardNode(Card("AD"), LinkedCardNode(Card("AS")))
//    val hash = list.hash
//    println(hash)
//
//    val compare = list.contains(Card("AD"))
//    val otherCompare = list.contains(Card("AC"))
//    println("stop")
    val deck = Deck()
    for(i in 0 until 16) deck.remove(deck.first())
    var sizeA = 0
    var sizeB = 0
    var sizeC = 0
    val reg = measureTimeMillis {
        val combos = fastCombinations(deck, desiredSize = 5)
        sizeA = combos.size
    }
    val linked = measureTimeMillis {
        val combos = linkedCombinations(deck, desiredSize = 5)
        sizeB = combos.size
    }
    val fastLinked = measureTimeMillis {
        val combos = fastLinkedCombinations(deck, desiredSize = 5)
        sizeC = combos.size
    }
    println("$reg (size $sizeA), $linked (size $sizeB), $fastLinked (size $sizeC)")
    print("Done")
}

class Game(val hands:List<Hand>, val board: List<Card> = emptyList(), val burns:List<Card> = emptyList()) {
    val deck = Deck()

    init {
        deck.removeAll(hands.flatMap { it.cards })
        deck.removeAll(board)
        deck.removeAll(burns)
    }

    fun odds(returnSize: Int = 10000): Map<Hand, Result> { // wins - ties - outcomes
        val map = mutableMapOf<Hand, Result>()
        val options = randomCombinations(deck, returnSize, 5 - board.size)
        for(opt in options) {
            hands.map { map.computeIfAbsent(it) { Result() }.total++ }
            val tempBoard = board.plus(opt)
            val handMaps = hands.associateWith { Hand(it.cards.plus(tempBoard)) }
            val bestHand = handMaps.maxBy { it.value }.key
            map[bestHand]!!.wins++
        }
        return map
    }
}

data class Result(var wins: Int = 0, var ties: Int = 0, var total: Int = 0)

class Deck: HashSet<Card>(makeDeck())
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

fun <T> randomCombinations(cards: Set<T>, returnSize: Int, individualSize: Int): List<Set<T>> {
    return arrayOfNulls<Set<T>>(returnSize)
        .map { cards.shuffled().take(individualSize).toSet() }
}

//fun <T> fastCombinations(inputs: List<T>, desiredSize: Int): List<List<T>> {
//    val returnable = mutableListOf<Pair<T, T>>()
//    for(item in inputs) {
//        for(second in inputs.minus(item)) {
//            returnable.add(item to second)
//        }
//    }
//}
fun <T> fastCombinations(inputs: Set<T>, desiredSize: Int): Set<Set<T>> {
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
fun <T> fastCombinationsHelper2(inputs: Set<T>, progress: Set<Set<T>>, desiredSize: Int): Set<Set<T>> {
    var cSet = progress
    var nSet: MutableSet<Set<T>> = mutableSetOf()
    var count = 0
    while(true) {
        for(set in cSet) {
            for(item in inputs.minus(set)) {
                nSet.add(set.plus(item))

                count++
                println("in $count of ${cSet.size * (inputs.size-set.size)}")
            }
        }
        if(nSet.first().size == desiredSize) return nSet
        else {
            cSet = nSet
            nSet = mutableSetOf()
        }
    }
//
//    if(nextIter.first().size == desiredSize) return nextIter
//    return fastCombinationsHelper(inputs, nextIter, desiredSize)
}

class LinkedCardNode(val content: Card, val next: LinkedCardNode? = null, val hash: Long = (next?.hash ?: 0) or (1L shl content.ordinal())) {
    fun size(): Int = next?.size()?.plus(1) ?: 1
    fun contains(other: Card): Boolean = hash and (1L shl other.ordinal()) > 0
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LinkedCardNode

        if (hash != other.hash) return false

        return true
    }

    override fun hashCode(): Int {
        return hash.hashCode()
    }
}

fun linkedCombinations(inputs: Iterable<Card>, desiredSize: Int): Set<LinkedCardNode> =
    linkedCombinationsHelper(inputs.toSet(), inputs.map { LinkedCardNode(it) }.toSet(), desiredSize)

fun linkedCombinationsHelper(inputs: Set<Card>, progress: Set<LinkedCardNode>, desiredSize: Int): Set<LinkedCardNode> {
    val nextIter = mutableSetOf<LinkedCardNode>()

    var count = 0

    for(ll in progress) {
        for(item in inputs) {
            if(ll.contains(item)) continue
            nextIter.add(LinkedCardNode(item, ll)) //add to bottom of tree

            count++
            println("in $count of ${progress.size * (inputs.size)}" )
        }
    }
    if(nextIter.first().size() == desiredSize) return nextIter
    return linkedCombinationsHelper(inputs, nextIter, desiredSize)
}

fun fastLinkedCombinations(inputs: Iterable<Card>, desiredSize: Int): Set<LinkedCardSet> =
    fastLinkedCombinationsHelper(inputs.toSet(), inputs.map { LinkedCardSet(it) }.toSet(), desiredSize)

fun fastLinkedCombinationsHelper(inputs: Set<Card>, progress: Set<LinkedCardSet>, desiredSize: Int): Set<LinkedCardSet> {
    val nextIter = mutableSetOf<LinkedCardSet>()

    var count = 0

    for(ll in progress) {
        for(item in inputs) {
            if(ll.contains(item)) continue
            nextIter.add(ll.copyAndAddElement(item)) //add to bottom of tree

            count++
            println("in $count of ${progress.size * (inputs.size)}" )
        }
    }
    if(nextIter.first().size() == desiredSize) return nextIter
    return fastLinkedCombinationsHelper(inputs, nextIter, desiredSize)
}

fun LinkedCardSet(card: Card) = LinkedCardSet(1L shl card.ordinal())
@JvmInline
value class LinkedCardSet(private val contents: Long) {
    fun copyAndAddElement(card: Card) = LinkedCardSet(contents or (1L shl card.ordinal()))
    fun contains(card: Card) = (contents and (1L shl card.ordinal())) > 0
    fun contents() = Deck().filter { contains(it) }
    fun size() = contents.countOneBits()
}

class Hand(cards: List<Card>): Comparable<Hand> {
    val cards =
        if (cards.size >= 5) {
            combinations(cards).maxBy { Hand(it) }
        }
        else if (cards.size > 2) {
            throw Exception("Unable to parse Hand with 5 > size > 2")
        } else {
            cards
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
    private fun isFlush(): Boolean {
        return cards.all { it.suit == cards.first().suit } //assumes one card
    }

    private fun isStraight(): Boolean {
        return cards.sorted().zipWithNext().all { (card1, card2) ->
            card1.value.ordinal + 1 == card2.value.ordinal ||
            card2.value == Value.Ace && cards.minOf { it }.value == Value.Two
        }
    }

    private fun isNPair(num: Int): Boolean { //this is picky
        return cards.groupBy { it.value }.any { it.value.size == num }
    }

    private fun isTwoPair(): Boolean {
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

    override fun toString(): String {
        return cards.toString()
    }
}

data class Card(val value: Value, val suit: Suit): Comparable<Card> {
    override fun compareTo(other: Card): Int {
        return value.compareTo(other.value)
    }

    fun ordinal() = value.ordinal + 13 * suit.ordinal
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