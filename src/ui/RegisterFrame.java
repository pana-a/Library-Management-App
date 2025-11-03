package ui;

import models.User;
import services.AuthService;
import services.exceptions.EmailAlreadyExistsException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterFrame {
    private JFrame frame;
    private JPanel panel;
    private AuthService authService;

    public RegisterFrame(AuthService authService){
        this.authService = authService;
        frame = new JFrame("Înregistrare");

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //Register message
        panel.add(Box.createVerticalStrut(15));
        JLabel labelRegister = new JLabel("Crează-ți un cont!");
        labelRegister.setFont(new Font("Arial", Font.BOLD, 30));
        labelRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(labelRegister);

        //Name
        panel.add(Box.createVerticalStrut(15));
        JLabel labelName = new JLabel("Nume:");
        labelName.setFont(new Font("Arial", Font.PLAIN, 15));
        labelName.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(labelName);

        JTextField textFieldName = new JTextField();
        textFieldName.setMaximumSize(new Dimension(200, 25));
        textFieldName.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(textFieldName);

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
        JLabel labelPassword = new JLabel("Parolă: ");
        labelPassword.setFont(new Font("Arial", Font.PLAIN, 15));
        labelPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(labelPassword);

        JPasswordField textFieldPassword = new JPasswordField();
        textFieldPassword.setMaximumSize(new Dimension(200, 25));
        textFieldPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(textFieldPassword);

        //Role
        panel.add(Box.createVerticalStrut(15));
        JLabel labelRole = new JLabel("Selectează rolul: ");
        labelRole.setFont(new Font("Arial", Font.PLAIN, 15));
        labelRole.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(labelRole);

        JComboBox comboBoxRole = new JComboBox(new String[]{"Student", "Bibliotecar"});
        comboBoxRole.setMaximumSize(new Dimension(200, 25));
        panel.add(comboBoxRole);

        //Register button
        panel.add(Box.createVerticalStrut(15));
        JButton buttonRegister = new JButton("Crează contul!");
        buttonRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(buttonRegister);
        buttonRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = textFieldName.getText();
                String email = textFieldEmail.getText();
                String password = new String(textFieldPassword.getPassword());

                User.Role role;
                int roleIndex = comboBoxRole.getSelectedIndex();
                switch(roleIndex){
                    case 0:
                        role = User.Role.STUDENT;
                        break;
                    case 1:
                        role = User.Role.LIBRARIAN;
                        break;
                    default:
                        role = User.Role.STUDENT;
                }
                try{
                    authService.register(name, email, password, role);
                    JOptionPane.showMessageDialog(frame, "Contul a fost creat cu succes!", "Succes", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();

                }catch(IllegalArgumentException ex){
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Eroare înregistrare", JOptionPane.ERROR_MESSAGE);
                }
                catch (EmailAlreadyExistsException ex){
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Eroare înregistrare", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setContentPane(panel);
        frame.setBounds(1, 1, 400, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setVisible(true);
    }
}
