package view;

import javax.swing.*;
import java.awt.*;

/**
 * Tela interna do sistema (área logada). Placeholder: mensagem de boas-vindas.
 */
public class ViewDashboard extends JFrame {

    public ViewDashboard() {
        configureFrame();
        setContentPane(buildMainPanel());
    }

    private void configureFrame() {
        setTitle("Sistema de Confeitaria - Área interna");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 360);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
    }

    private JPanel buildMainPanel() {
        JPanel panel = ViewTheme.createPanel(48, 48, 48, 48);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(buildWelcomeSection());
        panel.add(Box.createVerticalStrut(24));
        panel.add(buildMessageSection());

        return panel;
    }

    private Component buildWelcomeSection() {
        JLabel title = ViewTheme.createTitleLabel("Área interna");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        return title;
    }

    private Component buildMessageSection() {
        JLabel msg = ViewTheme.createSubtitleLabel("Você entrou no sistema. Em breve: pedidos, produtos e mais.");
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);
        return msg;
    }
}
