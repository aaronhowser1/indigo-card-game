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

    fun removeCard(index: Int): Card {
        val card = deck[index]
        deck.removeAt(index)
        return card
    }

    fun add(card: Card) {
        deck.add(card)
    }

    fun takeCard(index: Int, takeFrom: Deck) {
        if (index in 1..takeFrom.deck.size) {
            add(takeFrom.removeCard(index))
        }
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
var gameOver = false

val deck = Deck(empty = false)
val table = Player("Table", Deck(empty = true))
val player = Player("Player", Deck(empty = true))
val computer = Player("Computer", Deck(empty = true))

fun main() {

    println("Indigo Card Game")
    play()

}

fun play() {

    deck.shuffle()

    table.hand.takeCards(4, deck)

    playFirstCheck()

    println("Initial cards on the table: ${table.hand.deck.joinToString(" ")}")


    while (!gameOver) {
        playTurn()
    }

    println("Game Over")
}

fun playTurn() {
    println("\n${table.hand.deck.size} cards on the table, and the top card is ${topCard(table.hand)}")
    val currentPlayer = if (computersTurn) computer else player

    if (currentPlayer.hand.deck.isEmpty()) currentPlayer.hand.takeCards(6, deck)
    if (computersTurn) computersTurn() else playersTurn()

    computersTurn = !computersTurn
}

fun playersTurn() {

    print("Cards in hand:")
    for (i in 0 until player.hand.deck.size) {
        print(" ${i+1})${player.hand.deck[i]}")
    }
    print("\n")

    val deckSize = player.hand.deck.size

    val pickedCardIndex: Int

    while (true) {
        val cardNumber = inputFromPrompt("Choose a card to play: (1-${deckSize})")

        if (cardNumber.lowercase() == "exit") {
            gameOver = true
            return
        }

        if (cardNumber.toIntOrNull() in 1..deckSize) {
            pickedCardIndex = cardNumber.toInt() - 1
            break
        }
    }

    //TODO: figure out why this isn't taking index 0 cards
    table.hand.takeCard(pickedCardIndex, player.hand)

}

fun computersTurn() {

}

fun topCard(deck: Deck): Card {
    return deck.deck.last()
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