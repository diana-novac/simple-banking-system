package org.poo.models.roles;

/**
 * Factory class for creating role instances
 */
public final class RoleFactory {

    /**
     * Private constructor to prevent instantiation of the factory class
     */
    private RoleFactory() { }

    /**
     * Creates a role instance based on the provided role type
     * @param role A string representing the role type (e.g., "owner", "manager", "employee")
     * @return An instance of the corresponding role type:
     *         - Owner if the input is "owner"
     *         - Manager if the input is "manager"
     *         - Employee if the input is "employee"
     *         - null if the role type does not match any of these types
     */
    public static Role createRole(final String role) {
        return switch (role.toLowerCase()) {
            case "owner" -> new Owner();
            case "manager" -> new Manager();
            case "employee" -> new Employee();
            default -> null;
        };
    }
}
