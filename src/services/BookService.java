package services;

import models.Book;
import models.Statistic;
import models.User;
import models.repositories.BookRepository;
import models.repositories.LoanRepository;
import services.exceptions.NotFoundException;
import services.exceptions.PermissionDeniedException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Serviciu pentru gestionarea cartilor din biblioteca.
 * Contine operatii de adaugare, modificare, stergere si listare.
 */
public class BookService {
    private final BookRepository repo;
    private final LoanRepository loanRepo;

    public BookService(BookRepository repo, LoanRepository loanRepo) {
        this.repo = repo;
        this.loanRepo = loanRepo;
    }

    /**
     * Adauga o carte noua in colectie.
     * @param actor utilizatorul care face operatia
     * @param title titlul cartii
     * @param author autorul
     * @param collection genul sau colectia
     * @param year anul publicarii
     * @param totalCopies numarul total de exemplare
     * @return obiectul Book adaugat
     * @throws PermissionDeniedException daca actorul nu este bibliotecar
     */
    public Book addBook(User actor, String title, String author, String collection, int year, int totalCopies)
            throws PermissionDeniedException {
        ensureLibrarian(actor);
        validateBookData(title, author, collection, year, totalCopies);
        return repo.addBook(trim(title), trim(author), trim(collection), year, totalCopies);
    }

    // Read

    /**
     * Returneaza toate cartile in ordinea introducerii (dupa id).
     */
    public List<Book> listAll() { return repo.listAll(); }
    /**
     * Returneaza toate cartile sortate alfabetic (ordinea naturala {@link Book#compareTo(Book)}).
     */
    public List<Book> listAllSorted() { return repo.listAllSorted(); } // opțional, util în UI/rapoarte
    public Book findById(int id) { return repo.findById(id); }
    public List<Book> findByAuthor(String author) { return repo.findByAuthor(author); }
    public List<Book> findByCollection(String collection) { return repo.findByCollection(collection); }
    public List<Book> searchTitleContains(String text) { return repo.searchTitleContains(text); }

    //Update
    public Book increaseCopies(User actor, int bookId, int count)
            throws PermissionDeniedException, NotFoundException {
        ensureLibrarian(actor);
        if (count <= 0) throw new IllegalArgumentException("Count trebuie să fie > 0.");
        Book b = requireBook(bookId);
        Book updated = b.withCopies(b.getTotalCopies() + count, b.getAvailableCopies() + count);
        repo.update(updated);
        return updated;
    }

    public Book decreaseCopies(User actor, int bookId, int count)
            throws PermissionDeniedException, NotFoundException {
        ensureLibrarian(actor);
        if (count <= 0) throw new IllegalArgumentException("Count trebuie să fie > 0.");
        Book b = requireBook(bookId);
        int newTotal = Math.max(b.getAvailableCopies(), b.getTotalCopies() - count);
        int newAvail = Math.min(b.getAvailableCopies(), newTotal);
        Book updated = b.withCopies(newTotal, newAvail);
        repo.update(updated);
        return updated;
    }

    public Book setAvailableCopies(User actor, int bookId, int available)
            throws PermissionDeniedException, NotFoundException {
        ensureLibrarian(actor);
        if (available < 0) throw new IllegalArgumentException("Disponibile nu poate fi negativ.");
        Book b = requireBook(bookId);
        int clamped = Math.min(available, b.getTotalCopies());
        Book updated = b.withCopies(b.getTotalCopies(), clamped);
        repo.update(updated);
        return updated;
    }

    /**
     * Sterge o carte dupa id.
     * Blocheaza operatia dacă exista imprumuturi active sau exemplare imprumutate.
     * @param actor utilizator bibliotecar
     * @param bookId id carte
     * @return {@code true} dacă s-a sters, altfel {@code false}
     * @throws services.exceptions.PermissionDeniedException daca actorul nu e bibliotecar
     * @throws IllegalStateException dacă exista imprumuturi active/exemplare imprumutate
     */
    public boolean delete(User actor, int bookId) throws PermissionDeniedException {
        ensureLibrarian(actor);


        if (loanRepo != null && loanRepo.hasActiveLoansForBook(bookId)) {
            throw new IllegalStateException("Cartea are împrumuturi active și nu poate fi ștearsă.");
        }

        Book b = repo.findById(bookId);
        if (b != null && b.getAvailableCopies() < b.getTotalCopies()) {
            throw new IllegalStateException("Există exemplare împrumutate pentru această carte. Nu se poate șterge.");
        }

        return repo.delete(bookId);
    }


    // ------------------------- Helpers -------------------------

    private Book requireBook(int id) throws NotFoundException {
        Book b = repo.findById(id);
        if (b == null) throw new NotFoundException("Cartea nu a fost găsită.");
        return b;
    }

    private void ensureLibrarian(User actor) throws PermissionDeniedException {
        if (actor == null || actor.getRole() != User.Role.LIBRARIAN) {
            throw new PermissionDeniedException("Doar bibliotecarii pot modifica colecția de cărți.");
        }
    }

    private void validateBookData(String title, String author, String collection, int year, int totalCopies) {
        if (isBlank(title)) throw new IllegalArgumentException("Titlul este obligatoriu.");
        if (isBlank(author)) throw new IllegalArgumentException("Autorul este obligatoriu.");
        if (isBlank(collection)) throw new IllegalArgumentException("Colecția este obligatorie.");
        int currentYear = Year.now().getValue();
        if (year < 1450 || year > currentYear + 1) {
            throw new IllegalArgumentException("Anul publicării pare invalid.");
        }
        if (totalCopies <= 0) throw new IllegalArgumentException("Numărul total de exemplare trebuie să fie > 0.");
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static String trim(String s) { return s == null ? null : s.trim(); }

    /**
     * Calculează statistici simple pe colectie (titluri, exemplare, pe genuri).
     * @return sumarul statisticilor
     */
    public Statistic computeSimpleStats() {
        List<Book> all = repo.listAll();

        int totalTitles = all.size();
        int totalCopies = 0;
        int availableCopies = 0;

        // păstrăm ordinea de întâlnire a genurilor (colecțiilor)
        Map<String, Integer> titlesByGenre = new LinkedHashMap<>();
        Map<String, Integer> copiesByGenre = new LinkedHashMap<>();

        for (Book b : all) {
            totalCopies += b.getTotalCopies();
            availableCopies += b.getAvailableCopies();

            String genre = b.getCollection(); // în proiectul tău "collection" = gen
            titlesByGenre.put(genre, titlesByGenre.getOrDefault(genre, 0) + 1);
            copiesByGenre.put(genre, copiesByGenre.getOrDefault(genre, 0) + b.getTotalCopies());
        }

        int borrowedCopies = totalCopies - availableCopies;

        return new Statistic(totalTitles, totalCopies, availableCopies, borrowedCopies,
                titlesByGenre, copiesByGenre);
    }

    /**
     * Exporta sumarul statisticilor într-un fișier text.
     * @param filePath calea fisierului de iesire
     */
    public void exportSimpleStats(String filePath) {
        Statistic s = computeSimpleStats();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(filePath))) {
            w.write("STATISTICI COLECȚIE\n");
            w.write("-------------------\n");
            w.write(String.format("Titluri distincte : %d%n", s.totalTitles));
            w.write(String.format("Exemplare totale  : %d%n", s.totalCopies));
            w.write(String.format("Disponibile       : %d%n", s.availableCopies));
            w.write(String.format("Împrumutate       : %d%n", s.borrowedCopies));
            w.newLine();
            w.write("Pe genuri (colecții):\n");
            w.write(String.format("  %-20s | %8s | %8s%n", "Gen", "Titluri", "Exemplare"));
            w.write("  ---------------------+----------+----------\n");

            // ordonăm alfabetic genurile pentru o ieșire stabilă
            java.util.List<String> genres = new java.util.ArrayList<>(s.titlesByGenre.keySet());
            java.util.Collections.sort(genres, String.CASE_INSENSITIVE_ORDER);

            for (String g : genres) {
                int titles = s.titlesByGenre.getOrDefault(g, 0);
                int copies = s.copiesByGenre.getOrDefault(g, 0);
                w.write(String.format("  %-20s | %8d | %8d%n", g, titles, copies));
            }
        } catch (IOException e) {
            System.out.println("Eroare la exportul raportului: " + e.getMessage());
        }
    }
}
