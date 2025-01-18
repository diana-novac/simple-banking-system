package org.poo.models;

/**
 * Factory class for creating Card objects
 */
public final class CardFactory {
    private CardFactory() {

    }
    /**
     * Creates a Card object with the specified type
     *
     * @param oneTime Indicates whether the card should be a one-time-use card
     * @return A new Card instance
     */
    public static Card createCard(final boolean oneTime) {
        Card card = new Card();
        card.setOneTime(oneTime);
        return card;
    }
}
