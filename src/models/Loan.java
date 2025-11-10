package models;

import java.time.LocalDate;

public final class Loan {
    private final int loanId;
    private final int userId;
    private final int bookId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private final LocalDate returnDate; // poate fi null (ne-returnată încă)

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

    public boolean isActive() { return returnDate == null; }

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
