package src;

import src.*;
import java.util.*;

public class TarotDeck {
    private final ArrayList<Card> deck = new ArrayList<Card>();

    // TODO: Make this give some form of anonymous or generic projectile from the enum
    public static Projectile getProjectile(Card card) {
        return null;
    }

    public TarotDeck() {
        deck.addAll(Arrays.asList(Card.values()));
        shuffle();
    }

    public Card[] getOptions() {
        Card[] ans = new Card[Math.min(3, deck.size())];

        for(int i = 0; i < ans.length; i++) {
            ans[i] = getTopCard();
        }

        return ans;
    }

    public void shuffle() {
        ArrayList<Card> temp = new ArrayList<Card>();

        while(!deck.isEmpty()) {
            temp.add(deck.remove((int)(Math.random() * deck.size())));
        }

        while(!temp.isEmpty()) {
            deck.add(temp.remove(0));
        }
    }

    public Card getTopCard() {
        if(deck.isEmpty()) {
            throw new EmptyStackException();
        }
        return deck.remove(deck.size()-1);
    }

    public Card seeTopCard() {
        if (deck.isEmpty()) {
            throw new EmptyStackException();
        }
        return deck.get(deck.size()-1);
    }

    public void putOnBottom(Card c) {
        if(deck.isEmpty()) {
            deck.add(c);
        } else {
            deck.add(0, c);
        }
    }

    public enum Card {
        THE_MAGICIAN,
        THE_LOVERS,
        THE_CHARIOT,
        STRENGTH,
        THE_HANGED_MAN,
        THE_MOON,
        THE_SUN
    }
}
