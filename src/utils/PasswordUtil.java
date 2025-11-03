package utils;

public final class PasswordUtil {
    private PasswordUtil(){}

    public static String hashPassword(String password){
        if(password == null) return null;
        return String.valueOf(password.hashCode());
    }

    public static boolean isPasswordValid (String password, String hashedPassword){
        if(password == null || hashedPassword == null) return false;
        return hashPassword(password).equals(hashedPassword);
    }
}
