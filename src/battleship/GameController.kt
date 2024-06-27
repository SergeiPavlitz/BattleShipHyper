package battleship

import battleship.PrintType.*
import battleship.ValidationResponse.*
import battleship.field.Cell
import battleship.field.MAP_SIZE
import battleship.player.Player
import battleship.sheep.Sheep
import java.lang.IllegalArgumentException
import kotlin.math.abs
import kotlin.math.max


class GameController {

    private val playerOne = Player("Player 1")
    private val playerTwo = Player("Player 2")
    private var currentPlayer = playerOne
    private var oppositePlayer = playerTwo

    fun play() {

        placeFleet(playerOne)
        placeFleet(playerTwo)


        while (playerOne.shoots.isNotEmpty() && playerTwo.shoots.isNotEmpty()) {
            takeShot()
            switchPlayer()
        }
        println("You sank the last ship. You won. Congratulations!")


    }

    private fun takeShot() {
        printField(oppositePlayer.field, PARTIALLY_REVEALED)
        printField(currentPlayer.field, FULL_REVEALED)
        println("${currentPlayer.name}, it's your turn:")
        var input = readln()
        var message = checkShotCoords(input)
        while (message != OK) {
            println(message.message)
            input = requestCoords()
            message = checkShotCoords(input)
        }
        val shootResult = placeShot(input)
        println(shootResult.message)

        println("Press Enter and pass the move to another player")
        println("...")

    }

//    private fun takeShot() {
//        println("The game starts!")
//        printField(PARTIALLY_REVEALED)
//        println("Take a shot!")
//        while (shoots.isNotEmpty()){
//            var input = readln()
//            var message = checkShotCoords(input)
//            while (message != OK) {
//                println(message.message)
//                input = requestCoords()
//                message = checkShotCoords(input)
//            }
//            val shootResult = placeShot(input)
//            printField(PARTIALLY_REVEALED)
//            println(shootResult.message)
//
//        }
//        println("You sank the last ship. You won. Congratulations!")
//
//    }

    private fun placeShot(input: String): ShootResult {
        val shotCoords = convertCoordsInIndices(input)
        val cell = oppositePlayer.field[shotCoords.first][shotCoords.second]
        cell.isRevealed = true

        var shootResult: ShootResult = ShootResult.MISS
        when (cell.name) {
            MISS -> {
                shootResult = ShootResult.MISS
            }

            SHEEP_PLACE -> {
                cell.name = HIT
                val sheep = oppositePlayer.shoots.remove(cell)
                sheep?.let {
                    it.handleShoot()
                    shootResult = if (it.isAlive()) {
                        ShootResult.HIT
                    } else {
                        if (oppositePlayer.sankSheep.contains(it)) {
                            ShootResult.HIT
                        } else {
                            oppositePlayer.sankSheep.add(it)
                            ShootResult.SANK
                        }
                    }
                }
            }

            HIT -> {
                shootResult = ShootResult.HIT
            }

            FOG -> {
                cell.name = MISS
                shootResult = ShootResult.MISS
            }
        }
        return shootResult
    }


    private fun placeFleet(player: Player) {
        println("${player.name}, place your ships on the game field")
        printField(player.field, FULL_REVEALED)
        for (sheep in player.fleet) {
            askCoords(sheep.name)
            var input = requestCoords()
            var message = checkCoords(player.field, sheep.cells, input)
            while (message != OK) {
                println(message.message)
                input = requestCoords()
                message = checkCoords(player.field, sheep.cells, input)
            }
            placeSheep(input, sheep, player)
            printField(player.field, FULL_REVEALED)
        }
        println("Press Enter and pass the move to another player")
        println("...")
    }

    private fun placeSheep(input: String, sheep: Sheep, player: Player) {
        val coords = input.split(" ").map { convertCoordsInIndices(it) }
        val start = coords[0]
        val end = coords[1]
        val pairs = getIntervalOfPairs(start, end)
        for (p in pairs) {
            val cell = player.field[p.first][p.second]
            cell.name = SHEEP_PLACE
            player.shoots[cell] = sheep

            for (i in p.first - 1..p.first + 1) {
                for (j in p.second - 1..p.second + 1) {
                    player.field[i][j].nearSheep = true
                }
            }

        }
    }

    private fun askCoords(message: String) {
        println("Enter the coordinates of the $message:")
        println()
    }

    private fun requestCoords(): String {
        return readln()
    }


    private fun printField(field: MutableList<MutableList<Cell>>, printType: PrintType) {
        val partiallyRevealed: (Cell) -> String = { c -> c.revealed() }
        val fullRevealed: (Cell) -> String = { c -> c.name }

        val lambda = when (printType) {
            FOGGED -> partiallyRevealed
            FULL_REVEALED -> fullRevealed
            PARTIALLY_REVEALED -> partiallyRevealed
        }


        for (i in 0 until field.size - 1) {
            val l = field[i]
            val subLine = l.subList(0, MAP_SIZE - 1).map(lambda)
            println(subLine.joinToString(" "))
        }
    }

    private fun switchPlayer() {
        if (currentPlayer == playerOne) {
            currentPlayer = playerTwo
            oppositePlayer = playerOne
        } else {
            currentPlayer = playerOne
            oppositePlayer = playerTwo
        }
    }


    companion object Utils {

        const val MISS = "M"
        const val HIT = "X"
        const val SHEEP_PLACE = "O"
        const val FOG = "~"


        val LETTERS = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")

        fun checkShotCoords(input: String): ValidationResponse {
            val letter = input[0]
            val number = input.substring(1).toInt()
            if (!LETTERS.contains(letter.toString()) || number !in 1..10) {
                return WRONG_SHOT_COORDS
            }
            return OK
        }

        fun checkCoords(map: MutableList<MutableList<Cell>>, sheepLength: Int, input: String): ValidationResponse {
//            if (input.isBlank()) {
//                return EMPTY_INPUT
//            }
            val coords = input.split(" ").map { convertCoordsInIndices(it) }
            val start = coords[0]
            val end = coords[1]

            val len = max(abs(end.first - start.first) + 1, abs(end.second - start.second) + 1)

            if (len != sheepLength) {
                return WRONG_LENGTH
            }

            if (end.first != start.first && end.second != start.second) {
                return WRONG_LOCATION
            }

            val pairs = getIntervalOfPairs(start, end)
            for (p in pairs) {
                val cell = map[p.first][p.second]
                if (cell.nearSheep) {
                    return TOO_CLOSE
                }
            }

            return OK
        }

        fun convertCoordsInIndices(coordinate: String): Pair<Int, Int> {
            val letter = convertLetterInNumber(coordinate[0])
            val num = coordinate.substring(1).toInt()
            return Pair(letter, num)
        }

        private fun convertLetterInNumber(char: Char): Int {
            return when (char) {
                'A' -> 1
                'B' -> 2
                'C' -> 3
                'D' -> 4
                'E' -> 5
                'F' -> 6
                'G' -> 7
                'H' -> 8
                'I' -> 9
                'J' -> 10
                else -> throw IllegalArgumentException("Wrong coordinate letter")
            }
        }

        fun getIntervalOfPairs(start: Pair<Int, Int>, end: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            val list = mutableListOf<Pair<Int, Int>>()
            var vertStart = start.first
            var vertEnd = end.first
            if (vertStart > vertEnd) {
                vertStart = end.first
                vertEnd = start.first
            }
            var horStart = start.second
            var horEnd = end.second
            if (horStart > horEnd) {
                horStart = end.second
                horEnd = start.second
            }
            for (i in vertStart..vertEnd) {
                for (j in horStart..horEnd) {
                    list.add(Pair(i, j))

                }
            }
            return list
        }
    }
}