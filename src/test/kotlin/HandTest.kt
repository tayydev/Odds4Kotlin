import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.*

internal class HandTest {

    @Test
    fun hierarchy() {
        val sfHand = Hand("AS 2S 3S 4S 5S")
        assertEquals(sfHand.hierarchy(), Hierarchy.StraightFlush)

        val qHand = Hand("AD AS AC AH 2H")
        assertEquals(qHand.hierarchy(), Hierarchy.Quads)

        val fhHand = Hand("AS AD AH JH JD")
        assertEquals(fhHand.hierarchy(), Hierarchy.FullHouse)

        val fHand = Hand("AS 4S 2S 9S 3S")
        assertEquals(fHand.hierarchy(), Hierarchy.Flush)

        val sHand = Hand("AS 2D 3D 4D 5D")
        assertEquals(sHand.hierarchy(), Hierarchy.Straight)

        val tHand = Hand("7S 7D 7H 2S 3H")
        assertEquals(tHand.hierarchy(), Hierarchy.Trips)

        val tpHand = Hand("AS 2D AH JH JD")
        assertEquals(tpHand.hierarchy(), Hierarchy.TwoPair)

        val pHand = Hand("AS AD 2D 5H 8C 9S")
        assertEquals(pHand.hierarchy(), Hierarchy.Pair)
    }
}