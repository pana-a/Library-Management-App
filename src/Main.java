import models.repositories.UserRepository;
import models.repositories.BookRepository;
import models.repositories.LoanRepository;
import services.AuthService;
import services.BookService;
import services.LoanService;
import ui.WelcomeFrame;

/**
 * Punctul de intrare al aplicației Biblioteca.
 * Initializeaza repository-urile, serviciile, inregistreaza salvarea la inchidere
 * si lanseaza interfata grafica principala.
 */
public class Main {
    public static void main(String[] args) {
        UserRepository userRepo = new UserRepository("src/data/users.txt");
        BookRepository bookRepo = new BookRepository("src/data/books.txt");
        LoanRepository loanRepo = new LoanRepository("src/data/loans.txt");

        AuthService authService = new AuthService(userRepo);
        BookService bookService = new BookService(bookRepo, loanRepo);
        LoanService loanService = new LoanService(loanRepo, bookRepo);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                userRepo.saveOnExit();
                bookRepo.saveOnExit();
                loanRepo.saveOnExit();

                bookService.exportSimpleStats("src/data/raport.txt");
            }
        });
        new WelcomeFrame(authService, bookService, loanService);
    }
}
