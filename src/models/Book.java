package models;

import java.util.Objects;

public final class Book implements Comparable<Book>{
    private final int bookId;            // auto-increment
    private final String title;          // ex: "Clean Code"
    private final String author;         // ex: "Robert C. Martin"
    private final String collection;     // ex: "Informatică"
    private final int year;              // ex: 2008
    private final int totalCopies;       // total exemplare
    private final int availableCopies;   // exemplare disponibile

    public Book(int bookId, String title, String author, String collection, int year,
                int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.collection = collection;
        this.year = year;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCollection() { return collection; }
    public int getYear() { return year; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }

    // Helpers pentru „modificări” imutabile (creează noi instanțe cu valori schimbate)
    public Book withCopies(int total, int available) {
        return new Book(bookId, title, author, collection, year, total, available);
    }

    private static String norm(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        // carte identică: title + author + collection + year (normalizate)
        return year == book.year
                && Objects.equals(norm(title), norm(book.title))
                && Objects.equals(norm(author), norm(book.author))
                && Objects.equals(norm(collection), norm(book.collection));
    }

    @Override
    public int hashCode() {
        return Objects.hash(norm(title), norm(author), norm(collection), year);
    }

    // sortare aliniată cu equals: title -> author -> collection -> year
    @Override
    public int compareTo(Book o) {
        int c1 = norm(this.title).compareTo(norm(o.title));
        if (c1 != 0) return c1;
        int c2 = norm(this.author).compareTo(norm(o.author));
        if (c2 != 0) return c2;
        int c3 = norm(this.collection).compareTo(norm(o.collection));
        if (c3 != 0) return c3;
        return Integer.compare(this.year, o.year);
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", collection='" + collection + '\'' +
                ", year=" + year +
                ", totalCopies=" + totalCopies +
                ", availableCopies=" + availableCopies +
                '}';
    }
}
