package ui;

import models.Book;
import models.User;
import services.BookService;
import services.exceptions.PermissionDeniedException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

        //Label info about books
        JLabel title = new JLabel("Administrare cărți");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(12));
        panel.add(title);

        JLabel info = new JLabel("Vă rugăm să folosiți ID-urile cărților pentru a realiza acțiunile");
        info.setFont(new Font("Arial", Font.PLAIN, 15));
        info.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(12));
        panel.add(info);


        //Add book
        JButton btnAddBook = new JButton("Adaugă carte");
        btnAddBook.setAlignmentX(Component.CENTER_ALIGNMENT);
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
                printBooks(bookService.listAll(), "Toate cărțile");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "An / exemplare invalide.", "Eroare", JOptionPane.ERROR_MESSAGE);
            } catch (PermissionDeniedException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnAddBook);

        //Show books by ID
        JButton btnListBooksById = new JButton("Listează toate cărțile (după id)");
        btnListBooksById.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnListBooksById);
        btnListBooksById.addActionListener(e -> printBooks(bookService.listAll(), "Toate cărțile"));

        //Show books by name
        JButton btnListBooksByName = new JButton("Listează toate cărțile (alfabetic)");
        btnListBooksByName.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnListBooksByName);
        btnListBooksByName.addActionListener(e -> printBooks(bookService.listAllSorted(), "Toate cărțile"));

        //Increase number of books
        JButton btnIncCopies  = new JButton("Mărește numărul de exemplare");
        btnIncCopies.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnIncCopies);
        btnIncCopies.addActionListener(e -> {
            try {
                int id = askInt("Book ID:");
                int count = askInt("Cu câte exemplare mărim?");
                Book b = bookService.increaseCopies(currentUser, id, count);
                JOptionPane.showMessageDialog(frame, "Actualizat: disponibile:" + b.getAvailableCopies() + ", total:" + b.getTotalCopies());
                printBooks(bookService.listAll(), "Toate cărțile");
            } catch (Exception ex) {
                showError(ex);
            }
        });

        //Decrease number of books
        JButton btnDecCopies  = new JButton("Micșorează numărul de exemplare");
        btnDecCopies.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnDecCopies);
        btnDecCopies.addActionListener(e -> {
            try {
                int id = askInt("Book ID:");
                int count = askInt("Cu câte exemplare scădem?");
                Book b = bookService.decreaseCopies(currentUser, id, count);
                JOptionPane.showMessageDialog(frame, "Actualizat: disp=" + b.getAvailableCopies() + ", total=" + b.getTotalCopies());
                printBooks(bookService.listAll(), "Toate cărțile");
            } catch (Exception ex) {
                showError(ex);
            }
        });


        //Availablity - on hold for now
//        JButton btnSetAvail   = new JButton("Setează disponibile");
//        btnSetAvail.setAlignmentX(Component.CENTER_ALIGNMENT);
//        panel.add(Box.createVerticalStrut(6));
//        panel.add(btnSetAvail);
//        btnSetAvail.addActionListener(e -> {
//            try {
//                int id = askInt("Book ID:");
//                int avail = askInt("Setează disponibile la:");
//                Book b = bookService.setAvailableCopies(currentUser, id, avail);
//                JOptionPane.showMessageDialog(frame, "Actualizat: disp=" + b.getAvailableCopies());
//                printBooks(bookService.listAll(), "Toate cărțile");
//            } catch (Exception ex) {
//                showError(ex);
//            }
//        });

        //Delete
        JButton btnDeleteBook = new JButton("Șterge carte");
        btnDeleteBook.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnDeleteBook);
        btnDeleteBook.addActionListener(e -> {
            try {
                int id = askInt("Book ID de șters:");
                boolean ok = bookService.delete(currentUser, id);
                JOptionPane.showMessageDialog(frame, ok ? "Șters." : "Nu s-a găsit cartea.");
                printBooks(bookService.listAll(), "Toate cărțile");
            } catch (Exception ex) {
                showError(ex);
            }
        });

        JButton btnStats = new JButton("Statistici");
        btnStats.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnStats);

        btnStats.addActionListener(e -> {
            var s = bookService.computeSimpleStats();

            // sortăm genurile pentru o afișare consistentă
            java.util.List<String> genres = new java.util.ArrayList<>(s.titlesByGenre.keySet());
            java.util.Collections.sort(genres, String.CASE_INSENSITIVE_ORDER);

            // creăm un dialog simplu
            JDialog dialog = new JDialog(frame, "Statistici colecție", true);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setLayout(new BorderLayout(10,10));
            dialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

            // panel principal cu BoxLayout pe verticală
            JPanel panel_nou = new JPanel();
            panel_nou.setLayout(new BoxLayout(panel_nou, BoxLayout.Y_AXIS));

            JLabel title_nou = new JLabel("Statistici colecție");
            title_nou.setFont(new Font("Arial", Font.BOLD, 18));
            title_nou.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel_nou.add(title_nou);

            panel_nou.add(Box.createVerticalStrut(10));

            // secțiune: totaluri generale
            JLabel totals = new JLabel(String.format(
                    "<html>Titluri distincte: <b>%d</b><br>" +
                            "Exemplare totale: <b>%d</b><br>" +
                            "Disponibile: <b>%d</b><br>" +
                            "Împrumutate: <b>%d</b></html>",
                    s.totalTitles, s.totalCopies, s.availableCopies, s.borrowedCopies));
            totals.setFont(new Font("Arial", Font.PLAIN, 14));
            panel_nou.add(totals);

            panel_nou.add(Box.createVerticalStrut(10));

            // separator
            JSeparator sep = new JSeparator();
            sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            panel_nou.add(sep);

            panel_nou.add(Box.createVerticalStrut(10));

            // secțiune: pe genuri (colecții)
            JLabel byGenreTitle = new JLabel("Cărți pe genuri (colecții):");
            byGenreTitle.setFont(new Font("Arial", Font.BOLD, 15));
            panel_nou.add(byGenreTitle);
            panel_nou.add(Box.createVerticalStrut(5));

            // fiecare gen -> linie proprie
            for (String g : genres) {
                int titles = s.titlesByGenre.getOrDefault(g, 0);
                int copies = s.copiesByGenre.getOrDefault(g, 0);
                double pct = (s.totalCopies == 0) ? 0.0 : (copies * 100.0 / s.totalCopies);
                JLabel line = new JLabel(String.format("• %s — %d titluri, %d exemplare (%.1f%% din total)",
                        g, titles, copies, pct));
                line.setFont(new Font("Arial", Font.PLAIN, 13));
                panel_nou.add(line);
            }

            // scroll dacă lista e lungă
            JScrollPane scrollPane = new JScrollPane(panel_nou);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            dialog.add(scrollPane, BorderLayout.CENTER);

            // buton de închidere
            JButton closeButton = new JButton("Închide");
            closeButton.addActionListener(ev -> dialog.dispose());
            JPanel bottom = new JPanel();
            bottom.setLayout(new FlowLayout(FlowLayout.RIGHT));
            bottom.add(closeButton);
            dialog.add(bottom, BorderLayout.SOUTH);

            dialog.setSize(480, 500);
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
        });




        //JTextArea for viewing the books
        area = new JTextArea(10, 48);
        area.setEditable(false);
        area.setMargin(new Insets(5, 10, 5, 10));
        JScrollPane jScrollPane = new JScrollPane(area);
        jScrollPane.setBorder(new EmptyBorder(0,15,0,15));
        panel.add(jScrollPane);

        panel.add(Box.createVerticalStrut(10));

        //Logout
        JButton btnLogout = new JButton("Delogare");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(btnLogout);
        btnLogout.addActionListener(e -> frame.dispose());

        //final setup
        frame.setContentPane(panel);
        frame.setSize(560, 560);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        printBooks(bookService.listAll(), "Toate cărțile");
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
        area.setText(sb.toString());
    }
}
