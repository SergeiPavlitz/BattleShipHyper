package battleship.field

import battleship.GameController.Utils.LETTERS


const val MAP_SIZE = 12

object FieldFactory {

    fun createField(): MutableList<MutableList<Cell>> {
        val field = MutableList(MAP_SIZE) { MutableList(MAP_SIZE) { Cell() } }

        field[0][0].name = " "
        field[0][0].isRevealed = true

        for (i in LETTERS.indices) {
            field[0][i+1].name = (i+1).toString()
            field[0][i+1].isRevealed = true
            field[i+1][0].name = LETTERS[i]
            field[i+1][0].isRevealed = true
        }

        return field
    }
}