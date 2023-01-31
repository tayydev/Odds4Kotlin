import org.junit.jupiter.api.Test

internal class GameTest {

    @Test
    fun odds() {
        val game = Game(listOf(Hand("AD AS"), Hand("KS QS")))
        print(game.odds(10000))
    }

    @Test
    fun numericalHierarchy() {
        randomCombinations(Deck(), 10000, 7)
            .map { Hand(it.toList()) }
            .sortedBy { it.numericalHierarchy() }
            .forEach {
                println(it.hierarchy())
                println(it.listHierarchy())
                println(it.numericalHierarchy())
            }
    }
}