package indigo

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

    fun checkCard(index: Int): Card = deck[index]

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

class Player(val hand: Deck) {
    val score = Deck(empty = true)
}

var computersTurn = false
var gameOver = false

val remainingCards = Deck(empty = false)
val table = Deck(empty = true)
val player = Player(Deck(empty = true))
val computer = Player(Deck(empty = true))

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

    if (currentPlayer.hand.deck.isEmpty()) currentPlayer.hand.takeCards(6, remainingCards)
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

    table.takeCard(pickedCardIndex, player.hand)

}

fun computersTurn() {
    println("Computer plays ${computer.hand.deck[0]}")
    table.takeCard(0, computer.hand)
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