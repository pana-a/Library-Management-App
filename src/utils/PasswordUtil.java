package utils;

/**
 * Clasa utilitara pentru generarea si verificarea hash-urilor de parole.
 * Atentie: foloseste String.hashCode(), deci nu este sigura pentru productie.
 */
public final class PasswordUtil {
    /** Constructor privat pentru a preveni instantierea. */
    private PasswordUtil(){}

    /**
     * Genereaza hash-ul pentru o parola.
     * @param password parola in clar
     * @return hash-ul rezultat sau null daca parola este null
     */
    public static String hashPassword(String password){
        if(password == null) return null;
        return String.valueOf(password.hashCode());
    }

    /**
     * Verifica daca o parola se potriveste cu hash-ul salvat.
     * @param password parola introdusa
     * @param hashedPassword parola salvata (hash)
     * @return true daca parolele coincid, altfel false
     */
    public static boolean isPasswordValid (String password, String hashedPassword){
        if(password == null || hashedPassword == null) return false;
        return hashPassword(password).equals(hashedPassword);
    }
}
