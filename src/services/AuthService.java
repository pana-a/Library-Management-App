package services;

import models.User;
import models.repositories.UserRepository;
import services.exceptions.EmailAlreadyExistsException;
import services.exceptions.InvalidCredentialsException;
import utils.PasswordUtil;

/**
 * Serviciu pentru autentificare si inregistrare utilizatori.
 * Ofera functionalitati de login si register.
 */
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    //Helper
    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private void validateRegister(String name, String email, String password, User.Role role) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Numele este obligatoriu.");
        if (email == null || email.trim().isEmpty()) throw new IllegalArgumentException("Emailul este obligatoriu.");
        if (password == null || password.isEmpty()) throw new IllegalArgumentException("Parola este obligatorie.");
        if (role == null) throw new IllegalArgumentException("Rolul este obligatoriu.");
    }

    /**
     * Inregistreaza un nou utilizator.
     * @param name numele complet
     * @param email adresa de email
     * @param password parola
     * @param role rolul utilizatorului (STUDENT sau LIBRARIAN)
     * @return utilizatorul creat
     * @throws EmailAlreadyExistsException daca adresa exista deja
     */
    public User register (String name, String email, String password, User.Role role)
            throws EmailAlreadyExistsException {

        validateRegister(name, email, password, role);
        String normalizedEmail = normalizeEmail(email);

        User newUser = userRepository.findByEmail(normalizedEmail);
        if(newUser != null)
            throw new EmailAlreadyExistsException("Acest email a fost deja inregistrat!");

        String hashedPassword = PasswordUtil.hashPassword(password);
        userRepository.addUser(name, normalizedEmail, hashedPassword, role);
        return userRepository.findByEmail(normalizedEmail);
    }

    /**
     * Autentifica un utilizator existent.
     * @param email email-ul introdus
     * @param password parola in clar
     * @return utilizatorul autentificat
     * @throws InvalidCredentialsException daca datele nu sunt corecte
     */
    public User login(String email, String password) throws InvalidCredentialsException{
        String normalizedEmail = normalizeEmail(email);

        User user = userRepository.findByEmail(normalizedEmail);
        if(user == null || !PasswordUtil.isPasswordValid(password, user.getHashedPassword()))
            throw new InvalidCredentialsException("Email sau parola incorecta!");
        return user;
    }

}


