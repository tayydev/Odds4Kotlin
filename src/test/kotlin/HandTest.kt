import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
internal class HandTest {
    @Test
    fun hierarchy() {
        val sfHand = Hand("AS 2S 3S 4S 5S")
        assertEquals(sfHand.hierarchy().first, Hierarchy.StraightFlush)

        val qHand = Hand("AD AS AC AH 2H")
        assertEquals(qHand.hierarchy().first, Hierarchy.Quads)

        val fhHand = Hand("AS AD AH JH JD")
        assertEquals(fhHand.hierarchy().first, Hierarchy.FullHouse)

        val fHand = Hand("AS 4S 2S 9S 3S")
        assertEquals(fHand.hierarchy().first, Hierarchy.Flush)

        val sHand = Hand("AS 2D 3D 4D 5D")
        assertEquals(sHand.hierarchy().first, Hierarchy.Straight)

        val tHand = Hand("7S 7D 7H 2S 3H")
        assertEquals(tHand.hierarchy().first, Hierarchy.Trips)

        val tpHand = Hand("AS 2D AH JH JD")
        assertEquals(tpHand.hierarchy().first, Hierarchy.TwoPair)

        val pHand = Hand("AS AD 2D 5H 8C 9S")
        assertEquals(pHand.hierarchy().first, Hierarchy.Pair)
    }

    @Test
    fun compareToEqual() {
        val pHand = Hand("AS AD 2D 5H 8C 9S")
        val pHandEquiv = Hand("AS AD 2D 5H 8H 9C")
        assertEquals(0, pHand.compareTo(pHandEquiv))
    }

    @Test
    fun combinations() {
        val list = listOf(1, 2, 3)
        assertEquals(combinations(list, maxSize = 2), listOf(listOf(2, 3), listOf(1, 3), listOf(1, 2)))
        assertEquals(combinations(list, maxSize = 1), listOf(listOf(3), listOf(2), listOf(1)))
    }
    
    @Test 
    fun fastCombinations() {
        val list = listOf(1, 2, 3)
        assertEquals(fastCombinations(list, 2), setOf(setOf(2, 3), setOf(1, 3), setOf(1, 2)))
        assertEquals(fastCombinations(list, 1), setOf(setOf(3), setOf(2), setOf(1)))
    }

    @Test
    fun combinationPriority() {
        val bigHand = Hand("2S KD AD 9D 2C 2H 2D")
        assertEquals(bigHand.hierarchy().first, Hierarchy.Quads)
        assertEquals(bigHand.hierarchy().second.first().value, Value.Two)
        assertEquals(bigHand.hierarchy().second.last().value, Value.Ace)
    }
}