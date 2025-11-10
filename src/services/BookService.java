package services;

import models.Book;
import models.User;
import models.repositories.BookRepository;
import services.exceptions.NotFoundException;
import services.exceptions.PermissionDeniedException;

import java.time.Year;
import java.util.List;

public class BookService {
    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    // ------------------------- Create -------------------------

    public Book addBook(User actor, String title, String author, String collection, int year, int totalCopies)
            throws PermissionDeniedException {
        ensureLibrarian(actor);
        validateBookData(title, author, collection, year, totalCopies);
        return repo.addBook(trim(title), trim(author), trim(collection), year, totalCopies);
    }

    // ------------------------- Read / Query -------------------------

    public List<Book> listAll() { return repo.listAll(); }
    public List<Book> listAllSorted() { return repo.listAllSorted(); } // opțional, util în UI/rapoarte
    public Book findById(int id) { return repo.findById(id); }
    public List<Book> findByAuthor(String author) { return repo.findByAuthor(author); }
    public List<Book> findByCollection(String collection) { return repo.findByCollection(collection); }
    public List<Book> searchTitleContains(String text) { return repo.searchTitleContains(text); }

    // ------------------------- Update -------------------------

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

    // ------------------------- Delete -------------------------

    public boolean delete(User actor, int bookId) throws PermissionDeniedException {
        ensureLibrarian(actor);
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
}
