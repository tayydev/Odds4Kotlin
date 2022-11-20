import org.junit.jupiter.api.Test

internal class GameTest {

    @Test
    fun odds() {
        val game = Game(listOf(Hand("AD AS"), Hand("KS QS")))
        print(game.odds(10000))
    }
}