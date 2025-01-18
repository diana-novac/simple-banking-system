package org.poo.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.poo.utils.Utils;

/**
 * Represents a card associated with an account, including its details such as
 * card number, status, and whether it is a one-time-use card
 */
@Data
public final class Card {
    private String cardNumber;
    private String status;
    private boolean oneTime = false;

    /**
     * Constructs a Card instance with a generated card number and default status
     */
    public Card() {
        cardNumber = Utils.generateCardNumber();
        status = "active";
    }

    /**
     * Converts the card details into a JSON representation
     *
     * @return ObjectNode containing the card's details in JSON format
     */
    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode cardNode = mapper.createObjectNode();

        cardNode.put("cardNumber", cardNumber);
        cardNode.put("status", status);
        return cardNode;
    }
}
