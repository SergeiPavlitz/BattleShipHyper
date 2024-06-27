package battleship.field

import battleship.GameController.Utils.FOG

class Cell(var name: String = FOG) {
    var nearSheep = false
    var isRevealed = false

    fun revealed(): String {
        return if (isRevealed) {
            name
        }else{
            FOG
        }
    }

    override fun toString(): String {
        return "Cell(name='$name', nearSheep=$nearSheep)"
    }


}