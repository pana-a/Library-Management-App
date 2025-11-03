package models.repositories;

import models.User;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserRepository {
    private final File file;
    private final Map<String, User> users = new LinkedHashMap<>();
    private int nextId = 1;

    public UserRepository(String filePath){
        this.file = new File(filePath);
        loadUsers();
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public User findByEmail(String email) {
        return users.get(normalizeEmail(email));
    }

    //add a new user
    public void addUser(String name, String email, String hashedPassword, User.Role role) {
        String key = normalizeEmail(email);
        if (users.containsKey(key)) return;
        User user = new User(nextId++, name, key, hashedPassword, role);
        users.put(key, user);
        saveUsers();
    }

    //optional
//    public Map<String, User> getAllUsers() {
//        return new LinkedHashMap<>(users);
//    }

    //load all users from the file
    private void loadUsers() {
        users.clear();
        nextId = 1;
        if (!file.exists()) return; // nimic de încărcat prima dată
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length != 5) continue; // format invalid
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String email = normalizeEmail(parts[2]);
                String hashedPassword = parts[3];
                User.Role role = User.Role.valueOf(parts[4]);

                users.put(email, new User(id, name, email, hashedPassword, role));
                if (id >= nextId)
                    nextId = id + 1;
            }
        } catch (IOException e) {
            System.out.println("Eroare la citirea utilizatorilor: " + e.getMessage());
        }
    }

    //save all users - used especially when shutting down the app
    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (User u : users.values()) {
                writer.write(u.getUserId() + ";" +
                        u.getName() + ";" +
                        u.getEmail() + ";" +
                        u.getHashedPassword() + ";" +
                        u.getRole());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Eroare la salvarea utilizatorilor: " + e.getMessage());
        }
    }
}
