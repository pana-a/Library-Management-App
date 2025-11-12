package services;

import models.Book;
import models.Loan;
import models.User;
import models.repositories.BookRepository;
import models.repositories.LoanRepository;
import services.exceptions.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Serviciu pentru gestionarea imprumuturilor de carti.
 * Permite imprumutul, returnarea si listarea imprumuturilor.
 */
public class LoanService {
    private final LoanRepository loanRepo;
    private final BookRepository bookRepo;
    private final int maxActiveLoansPerStudent = 5;
    private final int defaultDays = 14;

    public LoanService(LoanRepository loanRepo, BookRepository bookRepo) {
        this.loanRepo = loanRepo;
        this.bookRepo = bookRepo;
    }

    // --------- Borrow / Return ---------

    /**
     * Creeaza un nou imprumut.
     * @param actor utilizatorul care imprumuta
     * @param bookId id-ul cartii
     * @return imprumutul creat
     * @throws PermissionDeniedException daca utilizatorul nu este student
     */

    public Loan borrow(User actor, int bookId) throws PermissionDeniedException, NotFoundException,
            NoCopiesAvailableException, LoanLimitExceededException {

        ensureStudent(actor);

        // limită împrumuturi active
        List<Loan> active = loanRepo.listActiveByUser(actor.getUserId());
        if (active.size() >= maxActiveLoansPerStudent) {
            throw new LoanLimitExceededException("Ai atins limita de împrumuturi active (" + maxActiveLoansPerStudent + ").");
        }

        // verifică existența cărții și disponibilul
        Book b = requireBook(bookId);
        if (b.getAvailableCopies() <= 0) throw new NoCopiesAvailableException("Nu mai sunt exemplare disponibile.");

        // actualizează stocul (disponibile -1)
        Book updated = b.withCopies(b.getTotalCopies(), b.getAvailableCopies() - 1);
        bookRepo.update(updated);

        // creează împrumutul
        LocalDate today = LocalDate.now();
        LocalDate due = today.plusDays(defaultDays);
        return loanRepo.addLoan(actor.getUserId(), bookId, today, due);
    }



    /**
     * Marcheaza o carte ca returnata.
     * @param actor utilizatorul care returneaza
     * @param loanId id-ul imprumutului
     * @return imprumutul actualizat
     */
    public Loan returnBook(User actor, int loanId) throws NotFoundException, AlreadyReturnedException, PermissionDeniedException {
        Loan loan = requireLoan(loanId);

        // cine poate returna: bibliotecar sau chiar studentul care a împrumutat
        if (!(isLibrarian(actor) || (isStudent(actor) && actor.getUserId() == loan.getUserId()))) {
            throw new PermissionDeniedException("Nu ai dreptul să returnezi acest împrumut.");
        }

        if (!loan.isActive()) throw new AlreadyReturnedException("Împrumutul este deja returnat.");

        // incrementăm disponibilul la carte
        Book b = requireBook(loan.getBookId());
        Book updated = b.withCopies(b.getTotalCopies(), b.getAvailableCopies() + 1);
        bookRepo.update(updated);

        // marcăm împrumutul ca returnat
        Loan closed = loan.withReturnDate(LocalDate.now());
        loanRepo.update(closed);
        return closed;
    }

    // --------- Rapoarte simple ---------

    public List<Loan> listAll() { return loanRepo.listAll(); }
    public List<Loan> listActive() { return loanRepo.listActive(); }
    public List<Loan> listByUser(int userId) { return loanRepo.listByUser(userId); }
    public List<Loan> listByDate(LocalDate date) { return loanRepo.listByDate(date); }

    // --------- Helpers ---------

    private Book requireBook(int id) throws NotFoundException {
        Book b = bookRepo.findById(id);
        if (b == null) throw new NotFoundException("Cartea nu a fost găsită.");
        return b;
    }

    private Loan requireLoan(int id) throws NotFoundException {
        Loan l = loanRepo.findById(id);
        if (l == null) throw new NotFoundException("Împrumutul nu a fost găsit.");
        return l;
    }

    private void ensureStudent(User actor) throws PermissionDeniedException {
        if (actor == null || actor.getRole() != User.Role.STUDENT) {
            throw new PermissionDeniedException("Doar studenții pot împrumuta cărți.");
        }
    }

    private boolean isLibrarian(User u) { return u != null && u.getRole() == User.Role.LIBRARIAN; }
    private boolean isStudent(User u)   { return u != null && u.getRole() == User.Role.STUDENT; }
}
