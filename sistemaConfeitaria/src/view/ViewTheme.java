package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Tema visual do sistema de confeitaria: cores e fontes leves e acolhedoras.
 * Uso em todas as views para manter consistência.
 */
public final class ViewTheme {

    // Cores – paleta confeitaria (creme, caramelo, marrom suave)
    public static final Color BACKGROUND = new Color(0xFFF9F5);
    public static final Color CARD_BG = new Color(0xFFFDFB);
    public static final Color ACCENT = new Color(0xB8860B);   // dark goldenrod
    public static final Color ACCENT_HOVER = new Color(0xCD9B1D);
    public static final Color TEXT = new Color(0x4A3728);
    public static final Color TEXT_MUTED = new Color(0x6B5344);
    public static final Color BORDER = new Color(0xE8D5C4);
    public static final Color INPUT_BG = Color.WHITE;

    // Fontes
    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_LABEL = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 13);

    private ViewTheme() {}

    /** Painel base com fundo e borda do tema. */
    public static JPanel createPanel(int top, int left, int bottom, int right) {
        JPanel p = new JPanel();
        p.setBackground(BACKGROUND);
        p.setBorder(new EmptyBorder(top, left, bottom, right));
        return p;
    }

    /** Rótulo de título (tema). */
    public static JLabel createTitleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TITLE);
        l.setForeground(TEXT);
        return l;
    }

    /** Rótulo de subtítulo ou descrição. */
    public static JLabel createSubtitleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SUBTITLE);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    /** Rótulo de campo (nome, e-mail, etc.). */
    public static JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT);
        return l;
    }

    /** Botão primário (Cadastrar, Entrar). */
    public static JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BUTTON);
        b.setBackground(ACCENT);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_HOVER, 1),
                new EmptyBorder(10, 24, 10, 24)));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Botão secundário (Voltar, Cancelar). */
    public static JButton createSecondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BUTTON);
        b.setBackground(CARD_BG);
        b.setForeground(TEXT);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 20, 10, 20)));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Campo de texto com estilo do tema. */
    public static JTextField createTextField(int columns) {
        JTextField t = new JTextField(columns);
        t.setFont(FONT_LABEL);
        t.setBackground(INPUT_BG);
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)));
        return t;
    }

    /** Campo de senha com estilo do tema. */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField p = new JPasswordField(columns);
        p.setFont(FONT_LABEL);
        p.setBackground(INPUT_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)));
        return p;
    }

    /** Seção com título (ex.: "Dados pessoais", "Endereço"). */
    public static JPanel createSection(String title) {
        JPanel section = new JPanel(new BorderLayout(0, 6));
        section.setBackground(BACKGROUND);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(12, 0, 12, 0)));
        JLabel label = createFieldLabel(title);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        label.setForeground(ACCENT);
        section.add(label, BorderLayout.NORTH);
        return section;
    }
}
