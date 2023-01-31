import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
internal class HandTest {
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
    fun compareToEqual() {
        val pHand = Hand("AS AD 2D 5H 8C 9S")
        val pHandEquiv = Hand("AS AD 2D 5H 8H 9C")
        assertEquals(0, pHand.compareTo(pHandEquiv))
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
}