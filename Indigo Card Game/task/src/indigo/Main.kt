package indigo

val ranks = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
val suits = arrayOf("♦", "♥", "♠", "♣")

class Card(val rank: String, val suit: String) {
    override fun toString(): String {
        return "$rank$suit"
    }
}

class Deck(empty: Boolean = false) {
    val deck = mutableListOf<Card>()
    init {
        if (!empty) reset()
    }

    fun reset() {
        deck.clear()
        for (suit in suits) for (rank in ranks) deck.add(Card(rank, suit))
    }

    fun shuffle() {
        deck.shuffle()
    }

    fun removeTop(): Card {
        val topCard = deck.first()
        deck.removeFirst()
        return topCard
    }

    fun add(card: Card) {
        deck.add(card)
    }

    fun takeCards(amount: Int, target: Deck) {
        if (amount in 1 .. 52 && amount <= deck.size) {
            for (i in 0 until amount) {
                add(target.removeTop())
            }
        }
    }

}

class Table {
    init {

    }
}

open class Player

class Computer : Player()
class Human : Player()


fun main() {

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

}

fun inputFromPrompt(prompt: String): String {
    println(prompt)
    return readln()
}