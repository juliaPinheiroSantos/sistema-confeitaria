package view;

import controller.ControllerLogin;
import model.entities.User;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Tela de login. Em sucesso: dispose() e abre ViewDashboard; em Voltar, fecha e retorna à ViewHome.
 */
public class ViewLogin extends JFrame {

    private final ControllerLogin controller;
    private final Runnable onBack;
    private final Consumer<ViewLogin> onLoginSuccess;

    private JTextField fieldEmail;
    private JPasswordField fieldPassword;

    public ViewLogin(ControllerLogin controller, Runnable onBack, Consumer<ViewLogin> onLoginSuccess) {
        this.controller = controller;
        this.onBack = onBack;
        this.onLoginSuccess = onLoginSuccess;
        configureFrame();
        setContentPane(buildMainPanel());
    }

    private void configureFrame() {
        setTitle("Entrar - Sistema de Confeitaria");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                onBack.run();
            }
        });
    }

    private JPanel buildMainPanel() {
        JPanel panel = ViewTheme.createPanel(32, 44, 32, 44);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(buildTitleSection());
        panel.add(Box.createVerticalStrut(24));
        panel.add(buildFieldsSection());
        panel.add(Box.createVerticalStrut(24));
        panel.add(buildButtonsSection());

        return panel;
    }

    private Component buildTitleSection() {
        JLabel title = ViewTheme.createTitleLabel("Entrar");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        return title;
    }

    private Component buildFieldsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(ViewTheme.BACKGROUND);

        section.add(ViewTheme.createFieldLabel("E-mail"));
        section.add(Box.createVerticalStrut(4));
        fieldEmail = ViewTheme.createTextField(22);
        section.add(fieldEmail);
        section.add(Box.createVerticalStrut(12));
        section.add(ViewTheme.createFieldLabel("Senha"));
        section.add(Box.createVerticalStrut(4));
        fieldPassword = ViewTheme.createPasswordField(22);
        section.add(fieldPassword);

        return section;
    }

    private Component buildButtonsSection() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttons.setBackground(ViewTheme.BACKGROUND);

        JButton btnBack = ViewTheme.createSecondaryButton("Voltar");
        btnBack.addActionListener(e -> onBackClick());
        JButton btnLogin = ViewTheme.createPrimaryButton("Entrar");
        btnLogin.addActionListener(e -> onLoginClick());

        buttons.add(btnBack);
        buttons.add(btnLogin);
        return buttons;
    }

    private void onLoginClick() {
        String email = fieldEmail.getText();
        char[] password = fieldPassword.getPassword();
        User user = controller.login(email, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "E-mail ou senha incorretos.", "Login", JOptionPane.WARNING_MESSAGE);
            return;
        }
        onLoginSuccess.accept(this);
    }

    private void onBackClick() {
        dispose();
        onBack.run();
    }
}
