import models.repositories.UserRepository;
import models.repositories.BookRepository;
import models.repositories.LoanRepository;
import services.AuthService;
import services.BookService;
import services.LoanService;
import ui.WelcomeFrame;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepo = new UserRepository("src/data/users.txt");
        BookRepository bookRepo = new BookRepository("src/data/books.txt");
        LoanRepository loanRepo = new LoanRepository("src/data/loans.txt");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                userRepo.saveOnExit();
                bookRepo.saveOnExit();
                loanRepo.saveOnExit();
            }
        });

        AuthService authService = new AuthService(userRepo);
        BookService bookService = new BookService(bookRepo);
        LoanService loanService = new LoanService(loanRepo, bookRepo);

        new WelcomeFrame(authService, bookService, loanService);
    }
}
