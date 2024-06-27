package battleship.player

import battleship.GameController
import battleship.PrintType
import battleship.ValidationResponse
import battleship.field.Cell
import battleship.field.FieldFactory
import battleship.sheep.Sheep
import battleship.sheep.SheepFactory

class Player(val name: String) {
    val field = FieldFactory.createField()

    val shoots = mutableMapOf<Cell, Sheep>()

    val fleet = SheepFactory.generateFleet()

    val sankSheep = mutableListOf<Sheep>()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Player

        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }


}