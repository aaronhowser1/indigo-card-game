package indigo

val ranks = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
val suits = arrayOf("♦", "♥", "♠", "♣")

class Card(val rank: String, val suit: String) {
    override fun toString(): String {
        return "$rank$suit"
    }
}

class Deck(empty: Boolean) {
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

    fun takeCards(amount: Int, takeFrom: Deck) {
        if (amount in 1 .. 52 && amount <= takeFrom.deck.size) {
            for (i in 0 until amount) {
                add(takeFrom.removeTop())
            }
        }
    }

}


class Player(val name: String, val hand: Deck)

var computersTurn = false


fun main() {

    println("Indigo Card Game")
    play()

}

fun play() {

    val deck = Deck(empty = false)
    val table = Player("Table", Deck(empty = true))
    val player = Player("Player", Deck(empty = true))
    val computer = Player("Computer", Deck(empty = true))

    deck.shuffle()

    table.hand.takeCards(4, deck)

    playFirstCheck()

    println("Initial cards on the table: ${table.hand.deck.joinToString(" ")}")


}

fun playTurn() {


    computersTurn = !computersTurn
}


fun playFirstCheck() {
    when (inputFromPrompt("Play first?").lowercase()) {
        "yes" -> computersTurn = false
        "no" -> computersTurn = true
        else -> playFirstCheck()
    }
}



fun inputFromPrompt(prompt: String): String {
    println(prompt)
    return readln()
}