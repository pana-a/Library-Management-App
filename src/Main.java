import models.User;
import models.repositories.UserRepository;
import services.AuthService;
import ui.WelcomeFrame;
import utils.PasswordUtil;

public class Main {
    public static void main(String[] args) {
        UserRepository repo = new UserRepository("D:\\ASE\\MASTER\\An I, sem I\\PPOO\\Proiect_PPOO\\src\\data\\users.txt");
        //repo.addUser("Ana Pop", "ana@ex.com", PasswordUtil.hashPassword("parola123"), User.Role.STUDENT);
        //repo.addUser("Larisa Ion", "larisa@ex.com", PasswordUtil.hashPassword("parola123"), User.Role.STUDENT);
        //repo.addUser("Alexandra Stanescu", "alexandra@ex.com", PasswordUtil.hashPassword("parola123"), User.Role.STUDENT);
        //repo.addUser("Maria Ionescu", "maria@ex.com", PasswordUtil.hashPassword("parola123"), User.Role.STUDENT);

        //User found = repo.findByEmail("ana@ex.com");
        //System.out.println(found);
        AuthService auth = new AuthService(repo);
        WelcomeFrame welcomeFrame = new WelcomeFrame(auth);

    }
}