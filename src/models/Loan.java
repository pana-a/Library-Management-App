package models;

import java.time.LocalDate;

/**
 * Reprezinta un imprumut al unei carti de catre un utilizator.
 * Returnarea setează {@code returnDate} printr-o noua instanta.
 */
public final class Loan {
    private final int loanId;
    private final int userId;
    private final int bookId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private final LocalDate returnDate; // poate fi null (ne-returnată încă)

    /**
     * Creează un imprumut.
     * @param loanId id imprumut
     * @param userId id utilizator
     * @param bookId id carte
     * @param borrowDate data imprumutului
     * @param dueDate scadenta
     * @param returnDate data returnarii (poate fi {@code null})
     */
    public Loan(int loanId, int userId, int bookId,
                LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate) {
        this.loanId = loanId;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public int getLoanId() { return loanId; }
    public int getUserId() { return userId; }
    public int getBookId() { return bookId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }

    /** @return {@code true} daca nu are setata data returnarii. */
    public boolean isActive() { return returnDate == null; }

    /**
     * Creeaza o copie marcata ca returnata.
     * @param date data returnarii
     * @return un nou {@code Loan} cu {@code returnDate}=date
     */
    public Loan withReturnDate(LocalDate date) {
        return new Loan(loanId, userId, bookId, borrowDate, dueDate, date);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "loanId=" + loanId +
                ", userId=" + userId +
                ", bookId=" + bookId +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                '}';
    }
}
