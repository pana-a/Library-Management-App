package ui;

import models.Book;
import models.User;
import services.BookService;
import services.exceptions.NotFoundException;
import services.exceptions.PermissionDeniedException;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LibrarianFrame {
    private final JFrame frame;
    private final User currentUser;
    private final BookService bookService;

    private final JTextArea area;

    public LibrarianFrame(User currentUser, BookService bookService) {
        this.currentUser = currentUser;
        this.bookService = bookService;

        frame = new JFrame("Bibliotecar – " + currentUser.getName());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Administrare cărți");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnAddBook    = new JButton("Adaugă carte");
        JButton btnListBooks  = new JButton("Listează toate cărțile");
        JButton btnIncCopies  = new JButton("Mărește exemplare");
        JButton btnDecCopies  = new JButton("Scade exemplare");
        JButton btnSetAvail   = new JButton("Setează disponibile");
        JButton btnDeleteBook = new JButton("Șterge carte");
        JButton btnLogout = new JButton("Delogare");

        btnAddBook.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnListBooks.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnIncCopies.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDecCopies.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSetAvail.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDeleteBook.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(12));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnAddBook);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnListBooks);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnIncCopies);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnDecCopies);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnSetAvail);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnDeleteBook);
        panel.add(Box.createVerticalStrut(10));

        area = new JTextArea(16, 48);
        area.setEditable(false);
        panel.add(new JScrollPane(area));

        panel.add(Box.createVerticalStrut(6));
        panel.add(btnLogout);

        // --- acțiuni ---
        btnListBooks.addActionListener(e -> printBooks(bookService.listAllSorted(), "Toate cărțile"));

        btnAddBook.addActionListener(e -> {
            try {
                String titleIn = JOptionPane.showInputDialog(frame, "Titlu:");
                if (titleIn == null) return;
                String authorIn = JOptionPane.showInputDialog(frame, "Autor:");
                if (authorIn == null) return;
                String collIn = JOptionPane.showInputDialog(frame, "Colecție:");
                if (collIn == null) return;
                String yearIn = JOptionPane.showInputDialog(frame, "An publicare:");
                if (yearIn == null) return;
                String copiesIn = JOptionPane.showInputDialog(frame, "Total exemplare:");
                if (copiesIn == null) return;

                int year = Integer.parseInt(yearIn.trim());
                int copies = Integer.parseInt(copiesIn.trim());

                Book b = bookService.addBook(currentUser, titleIn, authorIn, collIn, year, copies);
                JOptionPane.showMessageDialog(frame, "Adăugat/actualizat: #" + b.getBookId());
                printBooks(bookService.listAllSorted(), "Toate cărțile");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "An / exemplare invalide.", "Eroare", JOptionPane.ERROR_MESSAGE);
            } catch (PermissionDeniedException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnIncCopies.addActionListener(e -> {
            try {
                int id = askInt("Book ID:");
                int count = askInt("Cu câte exemplare mărim?");
                Book b = bookService.increaseCopies(currentUser, id, count);
                JOptionPane.showMessageDialog(frame, "Actualizat: disp=" + b.getAvailableCopies() + ", total=" + b.getTotalCopies());
                printBooks(bookService.listAllSorted(), "Toate cărțile");
            } catch (Exception ex) {
                showError(ex);
            }
        });

        btnDecCopies.addActionListener(e -> {
            try {
                int id = askInt("Book ID:");
                int count = askInt("Cu câte exemplare scădem?");
                Book b = bookService.decreaseCopies(currentUser, id, count);
                JOptionPane.showMessageDialog(frame, "Actualizat: disp=" + b.getAvailableCopies() + ", total=" + b.getTotalCopies());
                printBooks(bookService.listAllSorted(), "Toate cărțile");
            } catch (Exception ex) {
                showError(ex);
            }
        });

        btnSetAvail.addActionListener(e -> {
            try {
                int id = askInt("Book ID:");
                int avail = askInt("Setează disponibile la:");
                Book b = bookService.setAvailableCopies(currentUser, id, avail);
                JOptionPane.showMessageDialog(frame, "Actualizat: disp=" + b.getAvailableCopies());
                printBooks(bookService.listAllSorted(), "Toate cărțile");
            } catch (Exception ex) {
                showError(ex);
            }
        });

        btnDeleteBook.addActionListener(e -> {
            try {
                int id = askInt("Book ID de șters:");
                boolean ok = bookService.delete(currentUser, id);
                JOptionPane.showMessageDialog(frame, ok ? "Șters." : "Nu s-a găsit cartea.");
                printBooks(bookService.listAllSorted(), "Toate cărțile");
            } catch (Exception ex) {
                showError(ex);
            }
        });

        btnLogout.addActionListener(e -> frame.dispose());

        frame.setContentPane(panel);
        frame.setSize(560, 560);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        printBooks(bookService.listAllSorted(), "Toate cărțile");
    }

    private int askInt(String msg) {
        String s = JOptionPane.showInputDialog(frame, msg);
        if (s == null) throw new RuntimeException("Anulat.");
        return Integer.parseInt(s.trim());
    }

    private void showError(Exception ex) {
        String msg = ex.getMessage();
        if (msg == null || msg.isBlank()) msg = ex.getClass().getSimpleName();
        JOptionPane.showMessageDialog(frame, msg, "Eroare", JOptionPane.ERROR_MESSAGE);
    }

    private void printBooks(List<Book> books, String header) {
        StringBuilder sb = new StringBuilder();
        sb.append(header).append("\n");
        for (Book b : books) {
            sb.append(String.format("#%d | %s — %s | %s | %d | total:%d, disp:%d%n",
                    b.getBookId(), b.getTitle(), b.getAuthor(),
                    b.getCollection(), b.getYear(), b.getTotalCopies(), b.getAvailableCopies()));
        }
        // hint: poți copia ID-ul din listă ca să-l folosești în dialoguri
        sb.append("\nSfaturi: folosește Book ID din listă în celelalte acțiuni.");
        area.setText(sb.toString());
    }
}
