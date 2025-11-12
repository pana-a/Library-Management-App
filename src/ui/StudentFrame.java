package ui;

import models.Book;
import models.Loan;
import models.User;
import services.BookService;
import services.LoanService;
import services.exceptions.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class StudentFrame {
    private final JFrame frame;
    private final User currentUser;
    private final BookService bookService;
    private final LoanService loanService;

    private final JTextArea area;

    public StudentFrame(User currentUser, BookService bookService, LoanService loanService) {
        this.currentUser = currentUser;
        this.bookService = bookService;
        this.loanService = loanService;

        frame = new JFrame("Student – " + currentUser.getName());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //Welcome message
        JLabel title = new JLabel("Bun venit, " + currentUser.getName() + "!");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(12));
        panel.add(title);

        //Search book by name
        JButton btnSearchBooks = new JButton("Caută cărți după titlu");
        btnSearchBooks.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(12));
        panel.add(btnSearchBooks);
        btnSearchBooks.addActionListener(e -> {
            String q = JOptionPane.showInputDialog(frame, "Caută în titlu (fragment):");
            if (q == null) return;
            List<Book> books = bookService.searchTitleContains(q);
            printBooks(books, "Rezultate pentru: " + q);
        });

        //Search book by genre
        JButton btnSearchByGenre = new JButton("Caută cărți după gen");
        btnSearchByGenre.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnSearchByGenre);
        btnSearchByGenre.addActionListener(e -> {
            String g = JOptionPane.showInputDialog(frame, "Introdu genul (ex: Romantic, Fantezie, Clasic):");
            if (g == null) return; // anulare
            var books = bookService.findByCollection(g);
            printBooks(books, "Cărți din genul: " + g);
        });

        //Show all books by ID
        JButton btnListAllById = new JButton("Listează toate cărțile (după ID)");
        btnListAllById.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnListAllById);
        btnListAllById.addActionListener(e -> {
            List<Book> books = bookService.listAll();
            printBooks(books, "Toate cărțile");
        });

        //Show all books alphabetical
        JButton btnListAllByName = new JButton("Listează toate cărțile (alfabetic)");
        btnListAllByName.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnListAllByName);
        btnListAllByName.addActionListener(e -> {
            List<Book> books = bookService.listAllSorted();
            printBooks(books, "Toate cărțile");
        });

        //Show all loans
        JButton btnMyLoans     = new JButton("Împrumuturile mele");
        btnMyLoans.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnMyLoans);
        btnMyLoans.addActionListener(e -> {
            List<Loan> loans = loanService.listByUser(currentUser.getUserId());
            printLoans(loans, "Împrumuturile mele");
        });

        //Borrow
        JButton btnBorrow      = new JButton("Împrumută (după Book ID)");
        btnBorrow.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnBorrow);
        btnBorrow.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(frame, "Introdu Book ID pentru împrumut:");
            if (s == null) return;
            try {
                int bookId = Integer.parseInt(s.trim());
                Loan l = loanService.borrow(currentUser, bookId);
                JOptionPane.showMessageDialog(frame, "Împrumut creat (#" + l.getLoanId() + "), scadent: " + l.getDueDate());
                // afișează actualizat
                List<Loan> loans = loanService.listByUser(currentUser.getUserId());
                printLoans(loans, "Împrumuturile mele");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Book ID invalid.", "Eroare", JOptionPane.ERROR_MESSAGE);
            } catch (PermissionDeniedException | NotFoundException |
                     NoCopiesAvailableException | LoanLimitExceededException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Eroare împrumut", JOptionPane.ERROR_MESSAGE);
            }
        });

        //Return
        JButton btnReturn      = new JButton("Returnează (după Loan ID)");
        btnReturn.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnReturn);
        btnReturn.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(frame, "Introdu Loan ID pentru returnare:");
            if (s == null) return;
            try {
                int loanId = Integer.parseInt(s.trim());
                Loan l = loanService.returnBook(currentUser, loanId);
                JOptionPane.showMessageDialog(frame, "Returnat (#" + l.getLoanId() + ").");
                List<Loan> loans = loanService.listByUser(currentUser.getUserId());
                printLoans(loans, "Împrumuturile mele");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Loan ID invalid.", "Eroare", JOptionPane.ERROR_MESSAGE);
            } catch (NotFoundException | AlreadyReturnedException | PermissionDeniedException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Eroare returnare", JOptionPane.ERROR_MESSAGE);
            }
        });

        //JTextArea for viewing all the books
        panel.add(Box.createVerticalStrut(10));
        area = new JTextArea(16, 48);
        area.setEditable(false);
        area.setMargin(new Insets(5, 10, 5, 10));
        JScrollPane jScrollPane = new JScrollPane(area);
        jScrollPane.setBorder(new EmptyBorder(0,15,0,15));
        panel.add(jScrollPane);

        //Logout
        JButton btnLogout = new JButton("Delogare");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnLogout);
        btnLogout.addActionListener(e -> frame.dispose());

        //final setup
        frame.setContentPane(panel);
        frame.setSize(560, 520);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        printBooks(bookService.listAll(), "Toate cărțile");
    }

    private void printBooks(List<Book> books, String header) {
        StringBuilder sb = new StringBuilder();
        sb.append(header).append("\n");
        for (Book b : books) {
            sb.append(String.format("#%d | %s — %s | %s | %d | total:%d, disp:%d%n",
                    b.getBookId(), b.getTitle(), b.getAuthor(),
                    b.getCollection(), b.getYear(), b.getTotalCopies(), b.getAvailableCopies()));
        }
        area.setText(sb.toString());
    }

    //TODO - l.getBookId -> getBookNme
    private void printLoans(List<Loan> loans, String header) {
        StringBuilder sb = new StringBuilder();
        sb.append(header).append("\n");
        for (Loan l : loans) {
            sb.append(String.format("Loan #%d | Book %d | Borrow:%s | Due:%s | Returned:%s%n",
                    l.getLoanId(), l.getBookId(), l.getBorrowDate(), l.getDueDate(),
                    (l.getReturnDate() == null ? "-" : l.getReturnDate().toString())));
        }
        area.setText(sb.toString());
    }
}
