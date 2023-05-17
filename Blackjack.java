package Blackjack; // Declare the package name for the code

import java.util.*; // Import the java.util package for various utility classes
import java.util.stream.*; // Import the Stream API for stream processing
import java.util.concurrent.ThreadLocalRandom; // Import the ThreadLocalRandom class for random number generation
import java.util.Collections; // Import the Collections class for collection-related operations
import java.util.Scanner; // Import the Scanner class for user input
import java.util.Arrays; // Import the Arrays class for array-related operations

public class Blackjack { // Declare the public class named "Blackjack"

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Deck deck = new Deck(true); // Create a deck for blackjack
        Player player = new Player(); // Create a player
        Dealer dealer = new Dealer(deck); // Create a dealer with the deck

        System.out.println("Welcome to Blackjack!");

        while (true) {
            System.out.println("Shuffling the deck...");
            deck.shuffle(); // Shuffle the deck

            player.clearHand(); // Clear player's hand
            dealer.clearHand(); // Clear dealer's hand

            System.out.println("Dealing the cards...");
            dealer.dealInitialCards(Arrays.asList(player, dealer)); // Deal initial cards to player and dealer

            System.out.println("Your hand: " + player.getCards()); // Display player's hand
            System.out.println("Dealer's hand: " + dealer.getVisibleHand()); // Display dealer's visible hand

            // Player's turn
            while (player.getScore() <= 21) {
                System.out.print("Hit or stand? ");
                String input = scanner.nextLine().toLowerCase();

                if (input.equals("hit")) {
                    Card drawnCard = deck.draw(); // Draw a card from the deck
                    player.addCard(drawnCard); // Add the card to player's hand
                    System.out.println("You drew: " + drawnCard);
                    System.out.println("Your hand: " + player.getCards());
                    System.out.println("Dealer's hand: " + dealer.getVisibleHand());

                    if (player.getScore() > 21) {
                        System.out.println("Bust! You lose.");
                        break;
                    }
                } else if (input.equals("stand")) {
                    break;
                }
            }

            if (player.getScore() <= 21) {
                // Dealer's turn
                System.out.println("Dealer's turn...");
                dealer.revealAllCards(); // Reveal all dealer's cards
                System.out.println("Dealer's hand: " + dealer.getCards());

                while (dealer.getScore() < 17) {
                    Card drawnCard = deck.draw(); // Draw a card from the deck
                    dealer.addCard(drawnCard); // Add the card to dealer's hand
                    System.out.println("Dealer drew: " + drawnCard);
                    System.out.println("Dealer's hand: " + dealer.getCards());
                }

                if (dealer.getScore() > 21) {
                    System.out.println("Dealer busts! You win.");
                } else if (dealer.getScore() >= player.getScore()) {
                    System.out.println("Dealer wins.");
                } else {
                    System.out.println("You win!");
                }
            }

            System.out.print("Play again? ");
            String input = scanner.nextLine().toLowerCase();

            if (!input.equals("yes")) {
                break;
            }
        }

        System.out.println("Thanks for playing!");
        scanner.close();
    }
}

class Deck {
    private List<Card> cards = new ArrayList<>();
    private int index = 0;

    public Deck(boolean isBlackjack) {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                if (isBlackjack && rank.getValue() >= 2 && rank.getValue() <= 10) {
                    cards.add(new Card(rank, suit)); // Add cards to the deck based on blackjack rules
                } else if (!isBlackjack) {
                    cards.add(new Card(rank, suit)); // Add cards to the deck
                }
            }
        }
        shuffle(); // Shuffle the deck
    }

    public void shuffle() {
        Collections.shuffle(cards); // Shuffle the cards in the deck
        index = 0; // Reset the index for drawing cards
    }

    public Card draw() {
        Card card = cards.get(index); // Get the card at the current index
        index++; // Increment the index for the next card
        return card; // Return the drawn card
    }
}

class Player {
    private List<Card> hand = new ArrayList<>();

    public void addCard(Card card) {
        hand.add(card); // Add a card to the player's hand
    }

    public List<Card> getCards() {
        return new ArrayList<>(hand.stream()
                .filter(card -> card.getSuit() != Suit.HIDDEN)  // Exclude hidden cards
                .collect(Collectors.toList())); // Return the visible cards in the player's hand
    }

    public void clearHand() {
        hand.clear(); // Clear the player's hand
    }

    public int getScore() {
        int score = 0;
        int aces = 0;

        for (Card card : hand) {
            if (card.getRank() == Rank.ACE) {
                aces++;
                score += 11; // Aces are initially worth 11 points
            } else if (card.getRank().getValue() > 10) {
                score += 10; // Face cards are worth 10 points
            } else {
                score += card.getRank().getValue(); // Other cards are worth their face value
            }
        }

        while (aces > 0 && score > 21) {
            score -= 10; // If there are aces and the score exceeds 21, convert an ace from 11 to 1 point
            aces--;
        }

        return score; // Return the player's score
    }
}

class Dealer extends Player {
    private Deck deck;
    private List<Card> cards;
    private List<Card> hiddenHand;

    public Dealer(Deck deck) {
        this.deck = deck; // Set the deck for the dealer
        cards = new ArrayList<>();
        hiddenHand = new ArrayList<>();
    }

    public void dealInitialCards(List<Player> players) {
        for (int i = 0; i < 2; i++) {
            for (Player player : players) {
                player.addCard(deck.draw()); // Deal two cards to each player
            }
        }
        Card hiddenCard = deck.draw(); // Draw a card for the dealer's hidden hand
        hiddenCard.setHidden(true); // Set the card as hidden
        hiddenHand.add(hiddenCard); // Add the card to the hidden hand
        Card card = deck.draw(); // Draw a card for the dealer's visible hand
        card.setHidden(false); // Set the card as visible
        cards.add(card); // Add the card to the visible hand
    }

    public void revealAllCards() {
        cards.addAll(hiddenHand); // Move all cards from the hidden hand to the visible hand
        hiddenHand.clear(); // Clear the hidden hand
    }

    public List<Card> getVisibleHand() {
        List<Card> visibleCards = new ArrayList<>(cards);
    
        // If there is a hidden card in the hiddenHand list
        if (hiddenHand.size() > 0) {
            // If the hidden card is not hidden, replace the corresponding card in the visibleCards list
            // Otherwise, remove the hidden card from the visibleCards list
            if (!hiddenHand.get(0).isHidden()) {
                visibleCards.set(0, hiddenHand.get(0));
            } else {
                visibleCards.remove(0);
            }
        }
    
        return visibleCards;    // Display visableCards
    }
    
    public List<Card> getCards() {
        List<Card> visibleCards = new ArrayList<>(cards);
    
        // If there is a hidden card in the hiddenHand list
        if (hiddenHand.size() > 0) {
            // Replace the corresponding card in the visibleCards list with the hidden card
            visibleCards.set(0, hiddenHand.get(0));
        }
    
        return visibleCards;
    }
    
    public void revealAllCards() {
        // Move all cards from the hiddenHand list to the cards list
        cards.addAll(hiddenHand);
        // Clear the hiddenHand list
        hiddenHand.clear();
    }
    
    public void setHidden(boolean hidden) {
        // If the hiddenHand list is not empty, set the hidden flag of the first card to the provided value
        if (!hiddenHand.isEmpty()) {
            hiddenHand.get(0).setHidden(hidden);
        }
    }
    
    public boolean isHidden() {
        // Check if the hiddenHand list is not empty and if the first card is hidden
        return !hiddenHand.isEmpty() && hiddenHand.get(0).isHidden();
    }
    
enum Suit {
    CLUBS,      // Represents the suit of clubs.
    DIAMONDS,   // Represents the suit of diamonds.
    HEARTS,     // Represents the suit of hearts.
    SPADES,     // Represents the suit of spades.
    HIDDEN      // Represents a hidden card, used in certain game scenarios.
}

enum Rank {
    ACE(1),     // Represents the rank of an Ace with a value of 1.
    TWO(2),     // Represents the rank of a Two with a value of 2.
    THREE(3),   // Represents the rank of a Three with a value of 3.
    FOUR(4),    // Represents the rank of a Four with a value of 4.
    FIVE(5),    // Represents the rank of a Five with a value of 5.
    SIX(6),     // Represents the rank of a Six with a value of 6.
    SEVEN(7),   // Represents the rank of a Seven with a value of 7.
    EIGHT(8),   // Represents the rank of an Eight with a value of 8.
    NINE(9),    // Represents the rank of a Nine with a value of 9.
    TEN(10),    // Represents the rank of a Ten with a value of 10.
    JACK(10),   // Represents the rank of a Jack with a value of 10.
    QUEEN(10),  // Represents the rank of a Queen with a value of 10.
    KING(10),   // Represents the rank of a King with a value of 10.
    HIDDEN(0);  // Represents a hidden card with a value of 0, used in certain game scenarios.

    private int value;    // This private variable stores the value associated with the rank.

    Rank(int value) {    //This is the constructor of the Rank enum. It takes an integer value as a parameter and assigns it to the value variable.
        this.value = value;
    }

    public int getValue() {    //This public method returns the value associated with the rank of the card. It allows external code to access the value of a rank.
        return value;
    }
}

class Card {
    private Rank rank;        // Represents the rank of the card.
    private Suit suit;        // Represents the suit of the card.
    private boolean hidden;   // Indicates whether the card is hidden or not.

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
        this.hidden = false;   // Default to not hidden
    }

    public Rank getRank() {    // This function returns the rank of the card.
        return rank;
    }

    public Suit getSuit() {    // This function returns the suit of the card.
        return suit;
    }

    public int getValue() {    // This function returns the value associated with the rank of the card. It calls the getValue() method of the Rank enum.
        return rank.getValue();
    }

    public boolean isHidden() {    // This function returns a boolean value indicating whether the card is hidden or not.
        return hidden;
    }

    public void setHidden(boolean hidden) {    // This function sets the hidden state of the card based on the boolean value passed as an argument.
        this.hidden = hidden;
    }

    public String toString() {    // This function returns a string representation of the card, displaying its rank and suit.
        return rank + " of " + suit;
    }
}
