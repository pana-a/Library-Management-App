package ui;

import models.User;
import services.AuthService;
import services.exceptions.InvalidCredentialsException;
import services.BookService;
import services.LoanService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomeFrame {
    private JFrame frame;
    private JPanel panel;
    private AuthService authService;
    private final BookService bookService;
    private final LoanService loanService;

    public WelcomeFrame(AuthService authService, BookService bookService, LoanService loanService){
        this.authService =  authService;
        this.bookService = bookService;
        this.loanService = loanService;

        frame = new JFrame("Biblioteca online");

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //Welcome message
        panel.add(Box.createVerticalStrut(15));
        JLabel labelWelcome = new JLabel("Bine ai venit!");
        labelWelcome.setFont(new Font("Arial", Font.BOLD, 30));
        labelWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(labelWelcome);

        //Login message
        panel.add(Box.createVerticalStrut(30));
        JLabel labelLogin = new JLabel("Conectează-te!");
        labelLogin.setFont(new Font("Arial", Font.PLAIN, 25));
        labelLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(labelLogin);

        //Email
        panel.add(Box.createVerticalStrut(15));
        JLabel labelEmail = new JLabel("Email:");
        labelEmail.setFont(new Font("Arial", Font.PLAIN, 15));
        labelEmail.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(labelEmail);

        JTextField textFieldEmail = new JTextField();
        textFieldEmail.setMaximumSize(new Dimension(200, 25));
        textFieldEmail.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(textFieldEmail);

        //Password
        panel.add(Box.createVerticalStrut(15));
        JLabel labelPassword = new JLabel("Parola: ");
        labelPassword.setFont(new Font("Arial", Font.PLAIN, 15));
        labelPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(labelPassword);

        JPasswordField textFieldPassword = new JPasswordField();
        textFieldPassword.setMaximumSize(new Dimension(200, 25));
        textFieldPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(textFieldPassword);

        //Login button
        panel.add(Box.createVerticalStrut(15));
        JButton buttonLogin = new JButton("Conectează-te!");
        buttonLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(buttonLogin);
        buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = textFieldEmail.getText();
                String password = textFieldPassword.getText();
                try{
                    User user = authService.login(email, password);
                    JOptionPane.showMessageDialog(null,
                            "Autentificat ca: " + user.getName() + " (" + user.getRole() + ")",
                            "Autentificare reusita",
                            JOptionPane.INFORMATION_MESSAGE);
                    textFieldEmail.setText("");
                    textFieldPassword.setText("");
                    if (user.getRole() == User.Role.STUDENT) {
                        new StudentFrame(user, bookService, loanService);
                    } else {
                        new LibrarianFrame(user, bookService);
                    }
                }catch(InvalidCredentialsException ex){
                    textFieldEmail.setText("");
                    textFieldPassword.setText("");
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Eroare login", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //Register
        panel.add(Box.createVerticalStrut(45));
        JLabel labelRegister = new JLabel("Nu ai un cont?");
        labelRegister.setFont(new Font("Arial", Font.BOLD, 15));
        labelRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(labelRegister);

        JButton buttonRegister = new JButton("Înregistrează-te!");
        buttonRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(buttonRegister);

        buttonRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterFrame registerFrame = new RegisterFrame(authService);
            }
        });

        //Final setup
        frame.setContentPane(panel);
        frame.setBounds(1, 1, 400, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


}
