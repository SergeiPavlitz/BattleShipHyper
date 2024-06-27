package battleship

enum class ShootResult(val message: String) {
    HIT("You hit a ship!"),
    MISS("You missed!"),
    SANK("You sank a ship! Specify a new target:")
}