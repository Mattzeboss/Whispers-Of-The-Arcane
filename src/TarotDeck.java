package src;

import java.awt.image.BufferedImage;
import java.util.*;

public class TarotDeck {
    private final ArrayList<Card> deck = new ArrayList<Card>();

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
        //TODO: remove the commented out code
//        ArrayList<Card> temp = new ArrayList<Card>();
//
//        while(!deck.isEmpty()) {
//            temp.add(deck.remove((int)(Math.random() * deck.size())));
//        }
//
//        while(!temp.isEmpty()) {
//            deck.add(temp.remove(0));
//        }

        Collections.shuffle(deck);
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
        THE_MAGICIAN("./src/graphics/cards/placeholder_card.png"),
        THE_LOVERS("./src/graphics/cards/placeholder_card.png"),
        THE_CHARIOT("./src/graphics/cards/placeholder_card.png"),
        STRENGTH("./src/graphics/cards/placeholder_card.png"),
        THE_HANGED_MAN("./src/graphics/cards/placeholder_card.png"),
        THE_MOON("./src/graphics/cards/placeholder_card.png"),
        THE_SUN("./src/graphics/cards/placeholder_card.png");

        private final BufferedImage sprite;

        Card(String path){
            this.sprite = Sprites.loadImage(path);
        }

        public BufferedImage getSprite() {
            return sprite;
        }
    }
}
