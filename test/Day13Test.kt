import day13.Network
import org.junit.Test

import org.junit.Assert.*
import java.io.File

class Day13Test {

    @Test
    fun simulate() {
        val network = Network(File("src/day13/input.txt").readLines())
        network.simulate()
        val partOne = network.crashSites.first()
        val partTwo = network.carts.first()
        assertEquals(Pair(38, 57), partOne)
        assertEquals(Pair(4, 92), Pair(partTwo.x, partTwo.y))
    }
}