package org.poo.models.roles;

public class RoleFactory {
    public static Role createRole(String role) {
        return switch (role.toLowerCase()) {
            case "owner" -> new Owner();
            case "manager" -> new Manager();
            case "employee" -> new Employee();
            default -> null;
        };
    }
}
