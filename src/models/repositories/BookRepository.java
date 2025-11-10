package models.repositories;

import models.Book;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class BookRepository {
    private final File file;
    private final List<Book> books = new ArrayList<>();
    private int nextId = 1;

    public BookRepository(String filePath) {
        this.file = new File(filePath);
        loadBooks();
    }

    // ----------------- API public (simplu) -----------------

    /** Adaugă o carte nouă (fără agregare automată). */
    public Book addBook(String title, String author, String collection, int year, int totalCopies) {
        if (totalCopies <= 0) totalCopies = 1;

        Book newBook = new Book(0, title, author, collection, year, totalCopies, totalCopies);

        // dacă există deja o carte identică (equals), doar actualizăm
        for (int i = 0; i < books.size(); i++) {
            Book existing = books.get(i);
            if (existing.equals(newBook)) {
                int newTotal = existing.getTotalCopies() + totalCopies;
                int newAvail = existing.getAvailableCopies() + totalCopies;
                Book updated = existing.withCopies(newTotal, newAvail);
                books.set(i, updated);
                return updated;
            }
        }

        // dacă nu există, adăugăm o carte nouă cu id unic
        Book added = new Book(nextId++, title, author, collection, year, totalCopies, totalCopies);
        books.add(added);
        return added;
    }


    public List<Book> listAllSorted() {
        ArrayList<Book> copy = new ArrayList<>(books);
        Collections.sort(copy);
        return copy;
    }

    /** Returnează toate cărțile (copie pentru siguranță). */
    public List<Book> listAll() {
        return new ArrayList<>(books);
    }

    /** Găsește după id (linear, suficient pt. proiect). */
    public Book findById(int id) {
        for (Book b : books) if (b.getBookId() == id) return b;
        return null;
    }

    /** Caută după autor (case-insensitive, trim). */
    public List<Book> findByAuthor(String author) {
        String needle = norm(author);
        List<Book> out = new ArrayList<>();
        for (Book b : books)
            if (norm(b.getAuthor()).equals(needle)) out.add(b);
        return out;
    }

    /** Caută după colecție (case-insensitive, trim). */
    public List<Book> findByCollection(String collection) {
        String needle = norm(collection);
        List<Book> out = new ArrayList<>();
        for (Book b : books)
            if (norm(b.getCollection()).equals(needle)) out.add(b);
        return out;
    }

    /** Căutare simplă în titlu (contains, case-insensitive). */
    public List<Book> searchTitleContains(String text) {
        String needle = norm(text);
        if (needle == null || needle.isEmpty()) return List.of();
        List<Book> out = new ArrayList<>();
        for (Book b : books)
            if (norm(b.getTitle()).contains(needle)) out.add(b);
        return out;
    }

    /** Actualizează o carte (înlocuiește obiectul din listă cu unul nou). */
    public boolean update(Book updated) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getBookId() == updated.getBookId()) {
                books.set(i, updated);
                return true;
            }
        }
        return false;
    }

    /** Șterge o carte după id. */
    public boolean delete(int id) {
        return books.removeIf(b -> b.getBookId() == id);
    }

    /** Salvăm la închidere (conform cerinței). */
    public void saveOnExit() {
        saveBooks();
    }

    // ----------------- Persistență -----------------

    private void loadBooks() {
        books.clear();
        nextId = 1;

        if (!file.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] p = line.split(";");
                if (p.length != 7) continue;

                int id           = Integer.parseInt(p[0]);
                String title     = p[1];
                String author    = p[2];
                String collection= p[3];
                int year         = Integer.parseInt(p[4]);
                int total        = Integer.parseInt(p[5]);
                int available    = Integer.parseInt(p[6]);

                books.add(new Book(id, title, author, collection, year, total, available));
                if (id >= nextId) nextId = id + 1;
            }
        } catch (IOException e) {
            System.out.println("Eroare la citirea cărților: " + e.getMessage());
        }
    }

    private void saveBooks() {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            for (Book b : books) {
                w.write(b.getBookId() + ";" +
                        b.getTitle() + ";" +
                        b.getAuthor() + ";" +
                        b.getCollection() + ";" +
                        b.getYear() + ";" +
                        b.getTotalCopies() + ";" +
                        b.getAvailableCopies());
                w.newLine();
            }
        } catch (IOException e) {
            System.out.println("Eroare la salvarea cărților: " + e.getMessage());
        }
    }

    // ----------------- Helpers -----------------

    private static String norm(String s) {
        return (s == null) ? "" : s.trim().toLowerCase();
    }
}
