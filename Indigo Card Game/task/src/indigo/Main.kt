package indigo

val ranks = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
val suits = arrayOf("♦", "♥", "♠", "♣")
val deck = mutableListOf<String>()
val hands = mutableListOf<MutableList<String>>()

fun main() {
    reset()
    showMenu()
}

fun showMenu() {
    while (true) {
        println("Choose an action (reset, shuffle, get, exit):")
        when (readln()) {
            "reset" -> {
                reset()
                println("Card deck is reset.")
            }
            "shuffle" -> {
                shuffle()
                println("Card deck is shuffled.")
            }
            "get" -> {
                get()
            }
            "exit" -> {
                println("Bye")
                break
            }
            else -> println("Wrong action.")
        }
    }
}

fun reset() {
    deck.clear()
    for (suit in suits) for (rank in ranks) deck.add("$rank$suit")
}

fun shuffle() {
    deck.shuffle()
}

fun get() {
    println("Number of cards:")
    val amount = readln()

    if (amount.toIntOrNull() in 1..52) {

        if (amount.toInt() <= deck.size) {
            var hand = mutableListOf<String>()
            for (i in 0 until amount.toInt()) {
                hand.add(deck.first())
                deck.removeFirst()
            }
            println(hand.joinToString(" "))
        } else println("The remaining cards are insufficient to meet the request.")
    } else {
        println("Invalid number of cards.")
    }
}