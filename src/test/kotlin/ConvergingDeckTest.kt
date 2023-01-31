import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ConvergingDeckTest {

    @Test
    fun testLongCombinations() {
        val deck = Deck()
        val size = 5
        val converge = ConvergingDeck(deck, size)
        assertEquals(52 * 51 * 50 * 49 * 48, converge.long().size)
    }

    @Test
    fun testNextID() {
        val deck = Deck()
        val size = 3
        val converge = ConvergingDeck(deck, size)
        val out = mutableSetOf<Long>()
        for(i in 0..52 * 52 * 52) {
            out.add(converge.nextID())
        }
        assertEquals(52 * 52 * 52, out.size) //assert no duplicates
    }

    @Test
    fun testConvergingDeckSizeOne() {
        val deck = Deck()
        val size = 1
        val converge = ConvergingDeck(deck, size)
        val out = mutableSetOf<Set<Card>>()
        for (i in 0 until 52) {
            val run = converge.nextRunout()
            out.add(run)
            println(run)
        }
        val flat = out.flatten()
        assertEquals(52, flat.size)
    }

    @Test
    fun testConvergingDeckSizeTwo() {
        val deck = Deck()
        val size = 2
        val converge = ConvergingDeck(deck, size)
        val out = mutableSetOf<Set<Card>>()
        for(i in 0 until 52 * 52) {
            val run = converge.nextRunout()
            out.add(run)
            println(run)
        }
        assertEquals(52 * 52, out.size)
        assert(out.all { it.size == size })
    }
}