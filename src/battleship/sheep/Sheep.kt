package battleship.sheep

class Sheep(val name: String, val cells: Int, private val counter: ShootCounter) : ShootCounter by counter {
    override var remainedShoots: Int = cells
}


interface ShootCounter {
    var remainedShoots: Int
    fun handleShoot() {
        remainedShoots--
    }

    fun isAlive(): Boolean {
        return remainedShoots > 0
    }

}