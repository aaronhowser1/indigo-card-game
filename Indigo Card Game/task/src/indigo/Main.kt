package indigo

val ranks = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
val suits = arrayOf("♦", "♥", "♠", "♣")

class Card(val rank: String, val suit: String) {
    override fun toString(): String {
        return "$rank$suit"
    }
}

class Deck(val name: String, empty: Boolean) {
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
        if (index in 0 until takeFrom.deck.size) {
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

var computersTurn = false
var gameOver = false

val remainingCards = Deck("deck", empty = false)
val table = Deck("table", empty = true)
val player = Deck("player", empty = true)
val computer = Deck("computer", empty = true)

fun main() {

    println("Indigo Card Game")
    play()

}

fun play() {

    remainingCards.shuffle()

    table.takeCards(4, remainingCards)

    playFirstCheck()

    println("Initial cards on the table: ${table.deck.joinToString(" ")}")


    while (!gameOver) {
        playTurn()
        if (table.deck.size == 52) {
            println("\n${table.deck.size} cards on the table, and the top card is ${topCard(table)}")
            gameOver = true
        }
    }

    println("Game Over")
}

fun playTurn() {
    println("\n${table.deck.size} cards on the table, and the top card is ${topCard(table)}")
    val currentPlayer = if (computersTurn) computer else player

    if (currentPlayer.deck.isEmpty()) currentPlayer.takeCards(6, remainingCards)
    if (computersTurn) computersTurn() else playersTurn()

    computersTurn = !computersTurn
}

fun playersTurn() {

    print("Cards in hand:")
    for (i in 0 until player.deck.size) {
        print(" ${i+1})${player.deck[i]}")
    }
    print("\n")

    val deckSize = player.deck.size

    val pickedCardIndex: Int

    while (true) {
        val cardNumber = inputFromPrompt("Choose a card to play (1-${deckSize}): ")
//        println("Choose a card to play: (1-${deckSize})")
//        val cardNumber = "1"

        if (cardNumber.lowercase() == "exit") {
            gameOver = true
            return
        }

        if (cardNumber.toIntOrNull() in 1..deckSize) {
            pickedCardIndex = cardNumber.toInt() - 1
            break
        }
    }

    table.takeCard(pickedCardIndex, player)

}

fun computersTurn() {
    println("Computer plays ${computer.deck[0]}")
    table.takeCard(0, computer)
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