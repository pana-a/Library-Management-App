package models;

public final class User {
    public enum Role {STUDENT, LIBRARIAN}

    private final int userId;
    private final String name;
    private final String email;
    private final String hashedPassword;
    private final Role role;

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
