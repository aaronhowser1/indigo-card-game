package indigo

import kotlin.math.min

enum class Rank(val rankName: String, val points: Int) {
    A("A",1),
    TWO("2", 0),
    THREE("3",0),
    FOUR("4",0),
    FIVE("5",0),
    SIX("6",0),
    SEVEN("7",0),
    EIGHT("8",0),
    NINE("9",0),
    TEN("10",1),
    JOKER("J",1),
    QUEEN("Q",1),
    KING("K",1)
}

enum class Suit(val suitName: String) {
    DIAMOND("♦"),
    HEART("♥"),
    CLUB("♣"),
    SPADE("♠")
}

class Card(val rank: Rank, val suit: Suit) {
    override fun toString(): String {
        return "${rank.rankName}${suit.suitName}"
    }
}

class Deck(empty: Boolean) {
    val deck = mutableListOf<Card>()

    init {
        if (!empty) reset()
    }

    fun topCard(): Card? = deck.lastOrNull()

    fun reset() {
        deck.clear()
        for (suit in Suit.values()) for (rank in Rank.values()) deck.add(Card(rank, suit))
    }

    fun shuffle() {
        deck.shuffle()
    }

    fun removeTop(): Card {
        val topCard = deck.first()
        deck.removeFirst()
        return topCard
    }

    fun getCard(index: Int): Card = deck[index]

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

class Player(val name: String, val hand: Deck) {
    val wonCards = Deck(empty = true)

    val score: Int
        get() {
            var cardPointTotal = 0
            for (card in wonCards.deck) cardPointTotal+=card.rank.points
            return cardPointTotal
        }

}

var computersTurn = false
var gameOver = false

val remainingCards = Deck(empty = false)
val table = Deck(empty = true)
val player = Player("Player", Deck(empty = true))
val computer = Player("Computer", Deck(empty = true))

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
        if (remainingCards.deck.isEmpty()) {
            printTableCards()
            gameOver = true
        }
    }

    println("Game Over")
}

fun playTurn() {
    printTableCards()

//    println("""
//        Table deck:
//            Size: ${table.deck.size}
//            Cards: ${table.deck.joinToString(" ")}
//        Computer deck:
//            Size: ${computer.hand.deck.size}
//            Cards: ${computer.hand.deck.joinToString(" ")}
//        Player deck:
//            Size: ${player.hand.deck.size}
//            Cards: ${player.hand.deck.joinToString(" ")}
//    """.trimIndent())

    val currentPlayer = if (computersTurn) computer else player

    if (currentPlayer.hand.deck.isEmpty()) {
        val cardsLeft = remainingCards.deck.size
        currentPlayer.hand.takeCards(min(6, cardsLeft), remainingCards)
    }
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
//        val cardNumber = inputFromPrompt("Choose a card to play (1-${deckSize}): ")
        println("Choose a card to play: (1-${deckSize})")
        val cardNumber = "1"

        //TODO: When both players have no cards in hand, go to step 2 unless there are no more remaining cards in the card deck.
        //TODO: The remaining cards on the table go to the player who won the cards last. In the rare case where none of the players win any cards, then all cards go to the player who played first.

        if (cardNumber.lowercase() == "exit") {
            gameOver = true
            return
        }

        if (cardNumber.toIntOrNull() in 1..deckSize) {
            pickedCardIndex = cardNumber.toInt() - 1
            break
        }
    }

    val cardsMatch = checkCardMatch(player.hand.getCard(pickedCardIndex), table.deck.lastOrNull())

    table.takeCard(pickedCardIndex, player.hand)

    if (cardsMatch) clearTable(player)

}

fun computersTurn() {
    println("Computer plays ${computer.hand.getCard(0)}")
    val cardsMatch = checkCardMatch(computer.hand.getCard(0), table.deck.lastOrNull())

    table.takeCard(0, computer.hand)

    if (cardsMatch) clearTable(computer)
}

fun clearTable(winner: Player) {

    winner.wonCards.deck += table.deck
    table.deck.clear()

    println("""
        ${winner.name} wins cards
        Score: Player ${player.score} - Computer ${computer.score}
        Cards: Player ${player.wonCards.deck.size} - Computer ${computer.wonCards.deck.size}
    """.trimIndent())

}

fun playFirstCheck() {
    when (inputFromPrompt("Play first?").lowercase()) {
        "yes" -> computersTurn = false
        "no" -> computersTurn = true
        else -> playFirstCheck()
    }
}

fun checkCardMatch(card1: Card?, card2: Card?): Boolean {

    if (card1 == null || card2 == null) return false

    return (card1.rank == card2.rank || card1.suit == card2.suit)
}

fun inputFromPrompt(prompt: String): String {
    println(prompt)
    return readln()
}

fun printTableCards() {
    if (table.deck.isEmpty()) {
        println("\nNo cards on the table")
    } else {
        println("\n${table.deck.size} cards on the table, and the top card is ${table.topCard()}")
    }
}