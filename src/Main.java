import models.User;
import models.repositories.UserRepository;
import models.repositories.BookRepository;
import services.AuthService;
import services.BookService;
import ui.WelcomeFrame;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepo = new UserRepository("src/data/users.txt");
        BookRepository bookRepo = new BookRepository("src/data/books.txt");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                userRepo.saveOnExit();
                bookRepo.saveOnExit();
                System.out.println("Datele au fost salvate la închiderea aplicației.");
            }
        });

        AuthService authService = new AuthService(userRepo);
        BookService bookService = new BookService(bookRepo);
//
//        try {
//            User admin = new User(0, "Bibliotecar", "admin@lib.com", String.valueOf("admin".hashCode()), User.Role.LIBRARIAN);
//            System.out.println("=== Test adăugare cărți ===");
//            bookService.addBook(admin, "Clean Code", "Robert C. Martin", "Informatică", 2008, 3);
//            bookService.addBook(admin, "Effective Java", "Joshua Bloch", "Informatică", 2018, 2);
//
//            System.out.println("Cărțile existente:");
//            for (var b : bookService.listAll()) {
//                System.out.println(" - " + b);
//            }
//        } catch (Exception e) {
//            System.out.println("Eroare test: " + e.getMessage());
//        }
        new WelcomeFrame(authService);
    }
}
