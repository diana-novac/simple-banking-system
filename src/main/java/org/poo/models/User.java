package org.poo.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.poo.fileio.UserInput;
import org.poo.plans.AccountPlan;
import org.poo.plans.StandardPlan;
import org.poo.plans.StudentPlan;
import org.poo.split.SplitPaymentRequest;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a user in the system, including their personal information,
 * accounts, and transaction management.
 */
@Data
public final class User {
    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private String occupation;
    private AccountPlan accountPlan;
    private ArrayList<Account> accounts;
    private Queue<SplitPaymentRequest> activePaymentRequests;

    // Maps account identifiers to accounts
    private HashMap<String, Account> accountMap;
    // Maps card numbers to their associated accounts
    private HashMap<String, Account> accountCardMap;
    // Handles user transactions
    private TransactionHandler transactionHandler;

    /**
     * Constructs a User instance based on the provided input.
     *
     * @param input UserInput containing the user's details from the input configuration.
     */
    public User(final UserInput input) {
        firstName = input.getFirstName();
        lastName = input.getLastName();
        email = input.getEmail();
        birthDate = input.getBirthDate();
        occupation = input.getOccupation();
        accounts = new ArrayList<>();
        accountMap = new HashMap<>();
        accountCardMap = new HashMap<>();
        activePaymentRequests = new LinkedList<>();

        // Initialize the transaction handler for this user
        transactionHandler = new TransactionHandler();
        if (occupation.equals("student")) {
            accountPlan = new StudentPlan();
        } else {
            accountPlan = new StandardPlan();
        }
    }

    /**
     * Converts the user's details and associated accounts into a JSON representation.
     *
     * @return ObjectNode containing the user's details and their accounts in JSON format.
     */
    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode userNode = mapper.createObjectNode();
        ArrayNode accountsArray = mapper.createArrayNode();

        userNode.put("firstName", firstName);
        userNode.put("lastName", lastName);
        userNode.put("email", email);

        for (Account account : accounts) {
            accountsArray.add(account.toJson());
        }
        userNode.set("accounts", accountsArray);
        return userNode;
    }

    /**
     * Calculates and returns the age of the user based on their birthdate
     *
     * @return The user's age in years
     */
    public int getAge() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate birthDateParsed = LocalDate.parse(birthDate, formatter);

        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDateParsed, currentDate).getYears();
    }

    /**
     * Adds a split payment request to the user's active payment requests
     *
     * @param req The SplitPaymentRequest to be added
     */
    public void addSplitPayment(final SplitPaymentRequest req) {
        activePaymentRequests.add(req);
    }

    /**
     * Retrieves the next split payment request of the specified type
     *
     * @param type The type of split payment to search for
     * @return The next SplitPaymentRequest of the specified type, or null if none found.
     */
    public SplitPaymentRequest getNextRequestOfType(final String type) {
        Queue<SplitPaymentRequest> tempQueue = new LinkedList<>();

        SplitPaymentRequest foundRequest = null;
        while (!activePaymentRequests.isEmpty()) {
            SplitPaymentRequest req = activePaymentRequests.poll();
            if (foundRequest == null && req.getType().equalsIgnoreCase(type)) {
                foundRequest = req;
            }
            tempQueue.add(req);
        }

        activePaymentRequests = tempQueue;
        return foundRequest;
    }
}
