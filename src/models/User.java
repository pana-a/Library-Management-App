package models;

/**
 * Reprezinta un utilizator al aplicatiei (student sau bibliotecar).
 * Instantele sunt immutable.
 */
public final class User {
    /** Rolul unui utilizator. */
    public enum Role {STUDENT, LIBRARIAN}

    private final int userId;
    private final String name;
    private final String email;
    private final String hashedPassword;
    private final Role role;

    /**
     * Creeaza un utilizator.
     * @param userId id intern
     * @param name nume afisat
     * @param email email (normalizat lowercase)
     * @param hashedPassword parola hash-uita
     * @param role rolul
     */
    public User(int userId, String name, String email, String hashedPassword, Role role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
