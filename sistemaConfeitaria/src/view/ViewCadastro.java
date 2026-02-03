package view;

import controller.ControllerCadastro;
import model.entities.Area;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Tela de cadastro de usuário. Verifica e-mail duplicado antes de fechar; botão Voltar além do X.
 */
public class ViewCadastro extends JFrame {

    private final ControllerCadastro controller;
    private final Runnable onClose;

    private JTextField fieldFirstName;
    private JTextField fieldLastName;
    private JTextField fieldEmail;
    private JPasswordField fieldPassword;
    private JComboBox<Area> comboArea;
    private JTextField fieldStreet;
    private JTextField fieldNumber;
    private JTextField fieldCep;
    private JTextField fieldComplement;
    private JTextField fieldReference;

    public ViewCadastro(ControllerCadastro controller, Runnable onClose) {
        this.controller = controller;
        this.onClose = onClose;
        configureFrame();
        setContentPane(buildMainPanel());
    }

    private void configureFrame() {
        setTitle("Cadastrar - Sistema de Confeitaria");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(440, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                onClose.run();
            }
        });
    }

    private JScrollPane buildMainPanel() {
        JPanel panel = ViewTheme.createPanel(24, 28, 24, 28);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(buildTitleSection());
        panel.add(Box.createVerticalStrut(16));
        panel.add(buildPersonalSection());
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildAddressSection());
        panel.add(Box.createVerticalStrut(20));
        panel.add(buildButtonsSection());

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(ViewTheme.BACKGROUND);
        return scroll;
    }

    private Component buildTitleSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(ViewTheme.BACKGROUND);

        section.add(ViewTheme.createTitleLabel("Novo cadastro"));
        section.add(Box.createVerticalStrut(4));
        section.add(ViewTheme.createSubtitleLabel("Preencha os dados para se cadastrar"));
        return section;
    }

    private Component buildPersonalSection() {
        JPanel section = ViewTheme.createSection("Dados pessoais");
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(ViewTheme.BACKGROUND);

        content.add(addFieldRow("Nome *", fieldFirstName = ViewTheme.createTextField(20)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("Sobrenome", fieldLastName = ViewTheme.createTextField(20)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("E-mail *", fieldEmail = ViewTheme.createTextField(20)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("Senha *", fieldPassword = ViewTheme.createPasswordField(20)));

        section.add(content, BorderLayout.CENTER);
        return section;
    }

    private Component buildAddressSection() {
        JPanel section = ViewTheme.createSection("Endereço");
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(ViewTheme.BACKGROUND);

        content.add(ViewTheme.createFieldLabel("Área (região) *"));
        content.add(Box.createVerticalStrut(4));
        content.add(buildAreaCombo());
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("Rua *", fieldStreet = ViewTheme.createTextField(20)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("Número", fieldNumber = ViewTheme.createTextField(8)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("CEP", fieldCep = ViewTheme.createTextField(12)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("Complemento", fieldComplement = ViewTheme.createTextField(20)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("Referência", fieldReference = ViewTheme.createTextField(20)));

        section.add(content, BorderLayout.CENTER);
        return section;
    }

    private JComboBox<Area> buildAreaCombo() {
        List<Area> areas;
        try {
            areas = controller.listAreas();
        } catch (Exception e) {
            areas = List.of();
        }
        comboArea = new JComboBox<>(areas.toArray(new Area[0]));
        comboArea.setFont(ViewTheme.FONT_LABEL);
        comboArea.setBackground(ViewTheme.INPUT_BG);
        comboArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        comboArea.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Area a) {
                    setText(a.getName());
                }
                return this;
            }
        });
        return comboArea;
    }

    private JPanel addFieldRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setBackground(ViewTheme.BACKGROUND);
        row.add(ViewTheme.createFieldLabel(labelText), BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private Component buildButtonsSection() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttons.setBackground(ViewTheme.BACKGROUND);

        JButton btnBack = ViewTheme.createSecondaryButton("Voltar");
        btnBack.addActionListener(e -> onBackClick());
        JButton btnRegister = ViewTheme.createPrimaryButton("Cadastrar");
        btnRegister.addActionListener(e -> onRegisterClick());

        buttons.add(btnBack);
        buttons.add(btnRegister);
        return buttons;
    }

    private void onRegisterClick() {
        Area area = comboArea.getItemCount() > 0 ? (Area) comboArea.getSelectedItem() : null;
        Integer idArea = area != null ? area.getId() : null;
        String numStr = fieldNumber.getText();
        Integer number = numStr == null || numStr.isBlank() ? null : parseIntOrNull(numStr.trim());

        String error = controller.register(
                fieldFirstName.getText(),
                fieldLastName.getText(),
                fieldEmail.getText(),
                fieldPassword.getPassword(),
                idArea,
                fieldStreet.getText(),
                number,
                fieldCep.getText(),
                fieldComplement.getText(),
                fieldReference.getText()
        );
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Cadastro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso!", "Cadastro", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private static Integer parseIntOrNull(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void onBackClick() {
        dispose();
        onClose.run();
    }
}
