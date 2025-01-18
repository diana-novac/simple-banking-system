package org.poo.data;

import org.poo.fileio.UserInput;
import org.poo.models.User;

import java.util.ArrayList;

/**
 * Provides functionality to convert input data into user objects and store
 * them in the data container
 */
public final class UserInitializer {

    /**
     * Loads users from the provided input data and populates the data container
     *
     * @param userInputs    An array of UserInput objects containing user details
     * @param dataContainer The data container for storing user mappings
     * @return An ArrayList of User objects created from the input data
     */
    public ArrayList<User> loadUsers(final UserInput[] userInputs,
                                     final DataContainer dataContainer) {
        ArrayList<User> users = new ArrayList<>();
        for (UserInput userInput : userInputs) {
            User user = new User(userInput);
            users.add(user);

            // Map the user's email to the User object in the data container
            dataContainer.getEmailMap().put(user.getEmail(), user);
        }
        return users;
    }
}
