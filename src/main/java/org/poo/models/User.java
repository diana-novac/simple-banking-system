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
 * accounts, and transactions
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
     * Constructs a User instance based on the provided input
     *
     * @param input UserInput containing user's details from the input configuration
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
     * Converts the user's details and associated accounts into a JSON representation
     *
     * @return ObjectNode containing the user's details and their accounts in JSON format
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

    public int getAge() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate birthDateParsed = LocalDate.parse(birthDate, formatter);

        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDateParsed, currentDate).getYears();
    }

    public void addSplitPayment(SplitPaymentRequest req) {
        activePaymentRequests.add(req);
    }

    public SplitPaymentRequest getNextRequestOfType(String type) {
        for (SplitPaymentRequest req : activePaymentRequests) {
            if (req.getType().equalsIgnoreCase(type)) {
                return req;
            }
        }
        return null;
    }

    public void removeSplitPayment() {
        activePaymentRequests.poll();
    }

    public boolean hasActivePayments() {
        return !activePaymentRequests.isEmpty();
    }
}
