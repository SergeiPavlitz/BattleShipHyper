package battleship.sheep

object SheepFactory {

    fun generateFleet(): List<Sheep> {
        return mutableListOf(
            Sheep("Aircraft Carrier (5 cells)", 5, createCounter(5)),
            Sheep("Battleship (4 cells)", 4, createCounter(4)),
            Sheep("Submarine (3 cells)", 3, createCounter(3)),
            Sheep("Cruiser (3 cells)", 3, createCounter(3)),
            Sheep("Destroyer (2 cells)", 2, createCounter(2)),
        )
    }

    private fun createCounter(amountOfShoots: Int): ShootCounter {
        return object : ShootCounter {
            override var remainedShoots: Int = amountOfShoots

        }
    }
}