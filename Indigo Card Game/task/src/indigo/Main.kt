package indigo

val ranks = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
val suits = arrayOf("♦", "♥", "♠", "♣")
val deck = mutableListOf<String>()

val tableHand = mutableListOf<String>()
val playerHand = mutableListOf<String>()
val computerHand = mutableListOf<String>()

fun main() {
    reset()

    println("Indigo Card Game")

    println(playFirst())

}

fun playFirst(): Boolean {
    return when (inputFromPrompt("Play first?").lowercase()) {
        "yes" -> true
        "no" -> false
        else -> playFirst()
    }
}

fun play() {
    shuffle()
    get(4, tableHand)
    println("Initial cards on the table: ${tableHand.joinToString(" ")}")

}


fun reset() {
    deck.clear()
    for (suit in suits) for (rank in ranks) deck.add("$rank$suit")
}

fun shuffle() {
    deck.shuffle()
}

fun get(amount: Int, hand: MutableList<String>) {

    if (amount in 1..52 && amount <= deck.size) {
        for (i in 0 until amount) {
            hand.add(deck.first())
            deck.removeFirst()
        }
    }

//    if (amount in 1..52) {
//        if (amount <= deck.size) {
//            val hand = mutableListOf<String>()
//            for (i in 0 until amount) {
//                hand.add(deck.first())
//                deck.removeFirst()
//            }
//            println(hand.joinToString(" "))
//        } else println("The remaining cards are insufficient to meet the request.")
//    } else {
//        println("Invalid number of cards.")
//    }
}

fun inputFromPrompt(prompt: String): String {
    println(prompt)
    return readln()
}