package indigo

import kotlin.math.min

const val deckDebug = false
const val computerDebug = false
const val autoPlay = false

enum class Rank(val rankName: String, val points: Int = 0) {
    A("A",1),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
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

data class Card(val rank: Rank, val suit: Suit) {
    override fun toString(): String {
        return "${rank.rankName}${suit.suitName}"
    }
}

class Deck(val empty: Boolean) {
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

    var additionalPoints = 0

    val score: Int
        get() {
            var cardPointTotal = 0
            for (card in wonCards.deck) cardPointTotal+=card.rank.points
            return cardPointTotal + additionalPoints
        }

}

var computersTurn = false
var gameOver = false
var quit = false

val remainingCards = Deck(empty = false)
val table = Deck(empty = true)
val player = Player("Player", Deck(empty = true))
val computer = Player("Computer", Deck(empty = true))

var firstPlayer: Player? = null
var mostRecentWinner: Player? = null

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
    }

    if (!quit) {
        //Game won, tally points
        val playerCardCount = player.wonCards.deck.size
        val computerCardCount = computer.wonCards.deck.size
        val mostCards =
            if (playerCardCount == computerCardCount) firstPlayer
            else if (playerCardCount > computerCardCount) player
            else computer

        mostCards?.additionalPoints = 3

        printTableCards()
        if (computersTurn) clearTable(computer, printWinner = false) else clearTable(player, printWinner = false)

        println("Game Over")
    } else {
        println("Game Over")
    }

}

fun playTurn() {

    if (remainingCards.deck.isEmpty() && player.hand.deck.isEmpty() && computer.hand.deck.isEmpty()) {
        gameOver = true
        return
    }

    printTableCards()

    if (deckDebug) println("""
        Table deck:
            Size: ${table.deck.size}
            Cards: ${table.deck.joinToString(" ")}
        Computer deck:
            Size: ${computer.hand.deck.size}
            Cards: ${computer.hand.deck.joinToString(" ")}
        Player deck:
            Size: ${player.hand.deck.size}
            Cards: ${player.hand.deck.joinToString(" ")}
    """.trimIndent())

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
        val cardNumber = if (autoPlay) {
            println("Choose a card to play: (1-${deckSize})")
            "1"
        } else {
            inputFromPrompt("Choose a card to play (1-${deckSize}): ")
        }

        if (cardNumber.lowercase() == "exit") {
            gameOver = true
            quit = true
            return
        }

        if (cardNumber.toIntOrNull() in 1..deckSize) {
            pickedCardIndex = cardNumber.toInt() - 1
            break
        }
    }

    val cardsMatch = checkCardMatch(player.hand.getCard(pickedCardIndex), table.deck.lastOrNull())

    table.takeCard(pickedCardIndex, player.hand)

    if (cardsMatch) {
        clearTable(player)
        mostRecentWinner = player
    }

}

fun computersTurn() {
    println(computer.hand.deck.joinToString(" "))
    if (computer.hand.deck.size == 1) {

        // 1) If there is only one card in hand, put it on the table
        computerPlayCard(0)
        if (computerDebug) println("1) Computer playing its only card")
    } else {

        val candidateCards = getCandidateCards()
        if (computerDebug) println("Computer candidate cards: ${candidateCards.joinToString(" ")}")
        if (candidateCards.size == 1) {

            // 2) If there is only one candidate card, put it on the table
            val candidateCard = candidateCards.first()
            val candidateCardIndex = computer.hand.deck.indexOf(candidateCard)
            if (computerDebug) println("2) Computer playing its only candidate card")
            computerPlayCard(candidateCardIndex)
            return
        }

        // 3) If there are no cards on the table:
        // 4) If there are cards on the table but no candidate cards, use the same tactics as in step 3:
        if (table.deck.size == 0 || candidateCards.size == 0) {

            if (computerDebug) println("3) 4) ${table.deck.size} cards on table, and ${candidateCards.size} candidate cards")

            val doubles = getDoubleCards(computer.hand)
            val cardsSameSuit = doubles.first()
            val cardsSameRank = doubles.last()

            // 3a) If there are cards in hand with the same suit, throw one of them at random
            if (cardsSameSuit.isNotEmpty()) {
                val randomCard = cardsSameSuit.random()
                val randomCardIndex = computer.hand.deck.indexOf(randomCard)

                if (computerDebug) println("3a) Choosing card $randomCard from cardsSameSuit: ${cardsSameSuit.joinToString(" ")}")

                computerPlayCard(randomCardIndex)
                return
            }

            // 3b) If there are no cards in hand with the same suit, but there are cards with the same rank
            // (this situation occurs only when there are 4 or fewer cards in hand),
            // then throw one of them at random
            if (cardsSameRank.isNotEmpty()) {
                val randomCard = cardsSameRank.random()
                val randomCardIndex = computer.hand.deck.indexOf(randomCard)

                if (computerDebug) println("3b) Choosing card $randomCard from cardsSameRank: ${cardsSameRank.joinToString(" ")}")

                computerPlayCard(randomCardIndex)
                return
            }

            // 3c) If there are no cards in hand with the same suit or rank, throw any card at random
            val randomCard = computer.hand.deck.random()
            val randomCardIndex = computer.hand.deck.indexOf(randomCard)

            if (computerDebug) println("3c) No cards with same suit or rank, so choosing card at random")

            computerPlayCard(randomCardIndex)
            return
        }

        // 5) If there are two or more candidate cards:
        if (candidateCards.size >= 2) {

            if (computerDebug) println("5) There are ${candidateCards.size} candidate cards")

            val candidateDeck = Deck(empty = true)
            for (card in candidateCards) candidateDeck.add(card)

            val tableTopCard = table.topCard()

            val sameSuit = mutableListOf<Card>()
            val sameRank = mutableListOf<Card>()
            for (card in candidateCards) {
                if (card.suit == tableTopCard?.suit) sameSuit.add(card)
                if (card.rank == tableTopCard?.rank) sameRank.add(card)
            }


            // 5a) If there are 2 or more candidate cards with the same suit as the top card on the table, throw one of them at random
            if (sameSuit.size >= 2) {
                val randomCard = sameSuit.random()
                val randomCardIndex = computer.hand.deck.indexOf(randomCard)

                if (computerDebug) println("5a) There are ${sameSuit.size} cards with the same suit as the top card ($tableTopCard): ${sameSuit.joinToString(" ")}")

                computerPlayCard(randomCardIndex)
                return
            }

            // 5b) If the above isn't applicable, but there are 2 or more candidate cards with the same rank as the top card on the table, throw one of them at random
            if (sameRank.size >= 2) {
                val randomCard = sameRank.random()
                val randomCardIndex = computer.hand.deck.indexOf(randomCard)

                if (computerDebug) println("5b) There are ${sameRank.size} cards with the same rank as the top card ($tableTopCard): ${sameRank.joinToString(" ")}")

                computerPlayCard(randomCardIndex)
                return
            }

            // 5c) If nothing of the above is applicable, then throw any of the candidate cards at random.
            if (computerDebug) println("5c")
            computerPlayCard(computer.hand.deck.indexOf(computer.hand.deck.random()))
            return
        }

    }
}

fun computerPlayCard(index: Int) {
    println("Computer plays ${computer.hand.getCard(index)}")
    val cardsMatch = checkCardMatch(computer.hand.getCard(index), table.deck.lastOrNull())

    table.takeCard(index, computer.hand)

    if (cardsMatch) {
        clearTable(computer)
        mostRecentWinner = computer
    }
}

fun getCandidateCards(): MutableList<Card> {
    val candidateCards = mutableListOf<Card>()
    for (card in computer.hand.deck) {
        val topTableCard = table.topCard()
        if (checkCardMatch(topTableCard, card)) candidateCards.add(card)
    }
    return candidateCards
}

fun getDoubleCards(deck: Deck): Array<MutableList<Card>>{

    //Returns an array of 2 mutable lists
    //First list is made of all the cards in the input deck that have the same suit
    //Second list is made of all the cards in the input deck that have the same rank

    val cardsSameSuit = mutableListOf<Card>()
    val cardsSameRank = mutableListOf<Card>()
    val doubles = arrayOf(cardsSameSuit, cardsSameRank)

    for (card1 in deck.deck) {
        val copyHand = Deck(empty = true)
        for (card in deck.deck) if (card != card1) copyHand.add(card)

        val suit1 = card1.suit
        val rank1 = card1.rank

        for (card2 in copyHand.deck) {
            val suit2 = card2.suit
            val rank2 = card2.rank

            if (suit1 == suit2) {
                if (!cardsSameSuit.contains(card1)) cardsSameSuit.add(card1)
                if (!cardsSameSuit.contains(card2)) cardsSameSuit.add(card2)
            }
            if (rank1 == rank2) {
                if (!cardsSameRank.contains(card1)) cardsSameRank.add(card1)
                if (!cardsSameRank.contains(card2)) cardsSameRank.add(card2)
            }
        }
    }

    return doubles

}



fun clearTable(winner: Player, printWinner: Boolean = true) {

    winner.wonCards.deck += table.deck
    table.deck.clear()

    if (printWinner) println("${winner.name} wins cards")

    println("""
        Score: Player ${player.score} - Computer ${computer.score}
        Cards: Player ${player.wonCards.deck.size} - Computer ${computer.wonCards.deck.size}
    """.trimIndent())

}

fun playFirstCheck() {
    if (autoPlay) {computersTurn = false} else {
        when (inputFromPrompt("Play first?").lowercase()) {
            "yes" -> {
                computersTurn = false
                firstPlayer = player
            }
            "no" -> {
                computersTurn = true
                firstPlayer = computer
            }
            else -> playFirstCheck()
        }
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
