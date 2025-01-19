package org.poo.data;

import lombok.Getter;
import org.poo.commerciants.Commerciant;
import org.poo.models.Account;
import org.poo.models.Card;
import org.poo.models.User;

import java.util.HashMap;

/**
 * A container for managing and accessing shared data within the application
 */
@Getter
public final class DataContainer {
    private final HashMap<String, User> emailMap = new HashMap<>();
    private final HashMap<String, Account> accountMap = new HashMap<>();
    private final HashMap<String, Account> accountCardMap = new HashMap<>();
    private final HashMap<String, User> userCardMap = new HashMap<>();
    private final HashMap<String, User> userAccountMap = new HashMap<>();
    private final HashMap<String, Card> cardMap = new HashMap<>();
    private final HashMap<String, Commerciant> commerciantMap = new HashMap<>();
    private final HashMap<String, Commerciant> commerciantAccountMap = new HashMap<>();
}
