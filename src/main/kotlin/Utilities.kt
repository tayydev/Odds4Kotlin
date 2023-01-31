fun relativePrimeIn(range: Iterable<Long>, primeTo: Long) = range.first { gcd(it, primeTo) == 1L } //todo this might wanna be more random

fun gcd(a: Long, b: Long): Long = if(a == 0L) b else gcd(b % a, a)

fun longCombinations(deck: Set<Card>, runoutSize: Int): Set<Long> {
    var progress = setOf(0L)
    for(i in 0 until runoutSize) {
        val range = i until 52
        progress = multiply(progress, range.toSet())
    }
    return progress
}

fun multiply(shifted: Set<Long>, raw: Set<Int>): Set<Long> {
    return shifted.flatMap { shiftedVal ->
        raw.map { rawVal ->
            shiftedVal or (1L shl rawVal)
        }
    }.toSet()
}