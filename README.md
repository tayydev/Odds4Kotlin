# Odds4Kt
A toy Kotlin project that demonstrates a simple Texas Hold 'Em Monte Carlo calculator implemented in pure Kotlin

## Example
```kotlin
fun main() {
    val game = Game("AD 2D 3S (6H) ACTC v TDTH")
    println(game.odds(testSize = 100))
}

//{[Ace of Clubs, Ten of Clubs]=Result(wins=89, ties=0, total=100), [Ten of Diamonds, Ten of Hearts]=Result(wins=11, ties=0, total=100)}
```

## Limitations
While this project provides accurate simulations, it is not well optimized. It serves more so as a playground for data manipulation in Kotlin than a high performance solution to poker hand ranking.
- I imagine that my next steps to make this more performant might involve representing hands as 64-bit integers (where each bit represents one of 52 cards) and using a bunch of bitwise operations, but that is a story for another day

## Installation
1. Clone git repo
2. Build gradle
3. Run gradle tests