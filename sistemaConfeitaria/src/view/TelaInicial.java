package view;

import javax.swing.SwingUtilities;

/**
 * Atalho para a tela principal (ViewHome). Mantido para compatibilidade.
 */
public class TelaInicial {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ViewHome tela = new ViewHome();
            tela.setVisible(true);
        });
    }
}
