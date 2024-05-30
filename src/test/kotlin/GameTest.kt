import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class GameTest {

    @Test
    fun hierarchy() {
        val sfHand = Hand("AS 2S 3S 4S 5S")
        assertEquals(Hierarchy.StraightFlush, sfHand.hierarchy().first)

        val qHand = Hand("AD AS AC AH 2H")
        assertEquals(Hierarchy.Quads, qHand.hierarchy().first)

        val fhHand = Hand("AS AD AH JH JD")
        assertEquals(Hierarchy.FullHouse, fhHand.hierarchy().first)

        val fHand = Hand("AS 4S 2S 9S 3S")
        assertEquals(Hierarchy.Flush, fHand.hierarchy().first)

        val sHand = Hand("AS 2D 3D 4D 5D")
        assertEquals(Hierarchy.Straight, sHand.hierarchy().first)

        val tHand = Hand("7S 7D 7H 2S 3H")
        assertEquals(Hierarchy.Trips, tHand.hierarchy().first)

        val tpHand = Hand("AS 2D AH JH JD")
        assertEquals(Hierarchy.TwoPair, tpHand.hierarchy().first)

        val pHand = Hand("AS AD 2D 5H 8C 9S")
        assertEquals(Hierarchy.Pair, pHand.hierarchy().first)
    }

    @Test
    fun compareEquivalentOnePairHands() {
        val pHand = Hand("AS AD 2D 5H 8C 9S")
        val pHandEquiv = Hand("AS AD 3D 5H 8H 9C")
        assertEquals(0, pHand.compareTo(pHandEquiv))
    }

    @Test
    fun compareEquivalentThreePairHands() {
        //these hands should tie
        val tHand = Hand("AD AS 2D 2S 7D 7S 9H") //because this pair of twos is irrelevant
        val tHandEquiv = Hand("AD AS 4D 4S 7D 7S 9H") //and this pair of fours is also
        assertEquals(0, tHand.compareTo(tHandEquiv))
    }

    @Test
    fun compareDifferentThreePairHands() {
        //this hand should get ranked higher because one of the threes acts as a kicker
        val tHand = Hand("AD AS 3D 3S 7D 7S 2H")
        val tHandEquiv = Hand("AD AS 4D 4S 7D 7S 2H")
        assertEquals(-1, tHand.compareTo(tHandEquiv))
    }

    @Test
    fun compareDifferentTwoPairHands() {
        val tHand = Hand("AD AS 3D 5S 7D 7S 2H")
        val tHandEquiv = Hand("AD AS 4D 5S 6D 6S 2H")
        assertEquals(1, tHand.compareTo(tHandEquiv))
    }

    @Test
    fun compareEquivalentTripsHands() {
        val tHand = Hand("AD AS 2D 2S 7D 7S 9H")
        val tHandEquiv = Hand("AD AS 4D 4S 7D 7S 9H")
        assertEquals(0, tHand.compareTo(tHandEquiv))
    }

    @Test
    fun combinations() {
        val list = setOf(1, 2, 3)
        assertEquals(combinations(list, maxSize = 2), setOf(setOf(2, 3), setOf(1, 3), setOf(1, 2)))
        assertEquals(combinations(list, maxSize = 1), setOf(setOf(3), setOf(2), setOf(1)))
    }

    @Test
    fun combinationPriority() {
        val bigHand = Hand("2S KD AD 9D 2C 2H 2D")
        assertEquals(bigHand.hierarchy().first, Hierarchy.Quads)
        assertEquals(bigHand.hierarchy().second.first().value, Value.Two)
        assertEquals(bigHand.hierarchy().second.last().value, Value.Ace)
    }

    @Test
    fun odds() {
        val game = Game(setOf(Hand("AD AS"), Hand("KS QS")))
        print(game.odds(10000))
    }

    @Test
    fun openEndedOdds() {
        val game = Game(setOf(Hand("2D, 3D"), Hand("AD, AS")), findCards("3S, 4S, 5S"))
        print(game.odds(10000))
    }

    @Test
    fun numericalHierarchy() {
        randomCombinations(makeDeck(), 10000, 7)
            .map { Hand(it.toList()) }
            .sortedBy { it.numericalHierarchy() }
            .forEach {
                println(it.hierarchy())
                println(it.listHierarchy())
                println(it.numericalHierarchy())
            }
    }
}
