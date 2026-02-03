package view;

import controller.ControllerHome;

import javax.swing.*;
import java.awt.*;

/**
 * Tela inicial do sistema de confeitaria: Cadastrar e Entrar.
 * Controller injetado via {@link #setController(ControllerHome)} após a construção.
 */
public class ViewHome extends JFrame {

    private ControllerHome controller;

    public ViewHome() {
        configureFrame();
        setContentPane(buildMainPanel());
    }

    private void configureFrame() {
        setTitle("Sistema de Confeitaria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 420);
        setLocation(80, 80);
        setResizable(false);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
        setState(Frame.NORMAL);
    }

    private JPanel buildMainPanel() {
        JPanel panel = ViewTheme.createPanel(48, 56, 48, 56);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(buildTitleSection());
        panel.add(Box.createVerticalStrut(36));
        panel.add(buildButtonsSection());

        return panel;
    }

    private Component buildTitleSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(ViewTheme.BACKGROUND);
        section.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = ViewTheme.createTitleLabel("Sistema de Confeitaria");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.add(title);

        section.add(Box.createVerticalStrut(8));

        JLabel subtitle = ViewTheme.createSubtitleLabel("Cadastre-se ou entre para continuar");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.add(subtitle);

        return section;
    }

    private Component buildButtonsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(ViewTheme.BACKGROUND);
        section.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnRegister = ViewTheme.createPrimaryButton("Cadastrar");
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(220, 44));
        btnRegister.addActionListener(e -> controller.openRegister());

        JButton btnLogin = ViewTheme.createSecondaryButton("Entrar");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(220, 44));
        btnLogin.addActionListener(e -> controller.openLogin());

        section.add(btnRegister);
        section.add(Box.createVerticalStrut(14));
        section.add(btnLogin);

        return section;
    }

    /**
     * Injeta o controller (chamado pelo bootstrap após criar ControllerHome com esta view).
     */
    public void setController(ControllerHome controller) {
        this.controller = controller;
    }
}
