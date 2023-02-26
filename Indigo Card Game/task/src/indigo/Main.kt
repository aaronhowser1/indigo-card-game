package indigo

val ranks = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
val suits = arrayOf("♦", "♥", "♠", "♣")

fun main() {

    println(ranks.joinToString(" "))
    println(suits.joinToString(" "))

    val deck = mutableListOf<String>()
    for (suit in suits) for (rank in ranks) deck.add("$rank$suit")

    println(deck.joinToString(" "))

}