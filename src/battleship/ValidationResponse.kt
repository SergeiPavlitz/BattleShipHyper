package battleship

enum class ValidationResponse(val message: String) {
    TOO_CLOSE("Error! You placed it too close to another one. Try again:"),
    WRONG_LOCATION("Error! Wrong ship location! Try again:"),
    WRONG_LENGTH("Error! Wrong length of the Submarine! Try again:"),
    WRONG_SHOT_COORDS("Error! You entered the wrong coordinates! Try again:"),
    EMPTY_INPUT("Empty input"),
    OK("OK")
}