import kotlin.math.pow
import kotlin.random.Random

class ConvergingDeck(val deck: Deck, val runoutSize: Int) {
    private val cap = 52.0.pow(runoutSize).toLong()
    private val a = relativePrimeIn((cap / 2) until (cap),  cap)
    private val b = Random.nextLong(0, cap)
    private var x = 0L //count for math
    private var outs = 0L //count of actually valid outputs provided

    fun long() = longCombinations(deck, runoutSize)

    fun nextID(): Long {
        val id = (a * x + b) % cap
        x += 1

        return id
    }

    fun nextRunout(): Set<Card> {
        if(outs == cap) throw Exception("Reached end of $cap unique runouts!")
        var id = nextID()

        println("Id $id") //QC5C id 1901 - 1901 - 1901

        var bitSet = 0L
        for(i in 0 until runoutSize) {
            val possible = 1L shl (id % 52).toInt()
            if(possible and bitSet > 0) {
                return nextRunout() // we have duplicate bits
            }
            if(possible < bitSet) {
                return nextRunout() // we have bits in decreasing order
            }
            bitSet = bitSet or possible
            id /= 52
        }
//        if(bitSet.countOneBits() != runoutSize) { //if we have a number with overlapping bits
//            return nextRunout()
//        }
        val cardSet = Deck().filter { (bitSet and (1L shl it.ordinal)) > 0 }.toSet()

//        if(cardSet.size != 2) {
//            println("problem")
//            nextRunout()
//        }

        return if(!deck.containsAll(cardSet)) { //invalid out
           nextRunout()
        } else { //valid out
            outs += 1
            cardSet
        }
    }
}