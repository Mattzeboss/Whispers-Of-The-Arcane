package src;

import java.awt.image.BufferedImage;
import java.util.*;

public class TarotDeck {
    private final ArrayList<Card> deck = new ArrayList<>();

    public TarotDeck() {
        deck.addAll(Arrays.asList(Card.values()));
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    public int size() {
        return deck.size();
    }

    public Card getTopCard() {
        if (deck.isEmpty()) {
            throw new EmptyStackException();
        }
        return deck.remove(deck.size() - 1);
    }

    public void putOnBottom(Card c) {
        if (deck.isEmpty()) {
            deck.add(c);
        } else {
            deck.add(0, c);
        }
    }

    public enum Card {
        THE_MAGICIAN(
                "./src/graphics/cards/themagician.png",
                "+ Fireball replaces projectile"
        ),
        THE_LOVERS(
                "./src/graphics/cards/thelovers.png",
                "+ Fire two projectiles"
        ),
        THE_CHARIOT(
                "./src/graphics/cards/thechariot.png",
                "+ Movement Speed"
        ),
        STRENGTH(
                "./src/graphics/cards/strength.png",
                "+ Projectile damage\n" +
                        "+ Attack Speed"
        ),
        THE_HANGED_MAN(
                "./src/graphics/cards/thehangedman.png",
                "+ Revive"
        ),
        THE_MOON(
                "./src/graphics/cards/themoon.png",
                "+ Slows Enemies near the Moon"
        ),
        THE_SUN(
                "./src/graphics/cards/thesun.png",
                "+ Damage enemies near the Sun"
        );

        private final BufferedImage sprite;
        private final String description;

        Card(String path, String description) {
            this.sprite = Sprites.loadImage(path);
            this.description = description;
        }

        public BufferedImage getSprite() {
            return sprite;
        }

        public String getDescription() {
            return description;
        }
    }
}
