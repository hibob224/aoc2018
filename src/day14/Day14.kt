package day14

fun main(args: Array<String>) {
    println("Part one: ${Day14.solvePartOne()}")
    println("Parse two: ${Day14.solvePartTwo()}")
}

object Day14 {

    private var input = mutableListOf(3, 7)
    private var elfOne = 0
    private var elfTwo = 1
    private var practiceRecipes = 320851

    fun solvePartOne(): String {
        do {
            performRecipes()
        } while (input.size < practiceRecipes + 10)

        return input.subList(practiceRecipes, practiceRecipes + 10).joinToString("")
    }

    fun solvePartTwo(): Int {
        do {
            performRecipes()
        } while (practiceRecipes.toString() !in input.takeLast(10).joinToString(""))

        return input.size - 7
    }

    private fun performRecipes() {
        val newRecipe = (input[elfOne] + input[elfTwo]).toString()
        input.addAll(newRecipe.map { it.toString().toInt() })
        elfOne += 1 + input[elfOne]
        elfTwo += 1 + input[elfTwo]

        while (elfOne >= input.size) {
            elfOne -= input.size
        }
        while (elfTwo >= input.size) {
            elfTwo -= input.size
        }
    }
}