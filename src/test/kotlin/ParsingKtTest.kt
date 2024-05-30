import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class ParsingKtTest {

    @Test
    fun game() {
        val game1 = Game("ADAH2DD3 () AS CA v 2s3s v 4D 6D")
        //4D 6D has a one outer to win
        assertTrue(game1.odds(testSize = 1000)[Hand("ASAC")]!!.wins < 1000)

        val game2 = Game("ADAH2DD3 (D5) AS CA v 2s3s v 4D 6D")
        //This hand always wins because 4D 6D outs are burned
        assertTrue(game2.odds(testSize = 1000)[Hand("ASAC")]!!.wins == 1000)

    }
}