package view;

import controller.ControllerProduct;
import model.entities.Flavor;
import model.entities.FlavorLevel;
import model.entities.FlavorLevelPreset;
import model.entities.FlavorPreset;
import model.entities.Product;
import model.entities.Size;
import model.entities.SizePreset;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela de produtos com CRUD e total de itens comprados.
 */
public class ViewProducts extends JFrame {

    private final ControllerProduct controller;

    private JTextField fieldId;
    private JTextField fieldName;
    private JTextField fieldBasePrice;
    private JTextArea fieldDescription;
    private JComboBox<Flavor> comboFlavor;
    private JComboBox<Size> comboSize;
    private JLabel labelTotalPurchased;
    private JLabel labelFinalPrice;

    private JTable tableProducts;
    private DefaultTableModel tableModel;
    private JButton btnSave;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnSeed;
    private boolean usingPresetData;
    private List<Product> products = new ArrayList<>();
    private List<Flavor> flavors = new ArrayList<>();
    private List<Size> sizes = new ArrayList<>();

    public ViewProducts(ControllerProduct controller) {
        this.controller = controller;
        configureFrame();
        setContentPane(buildMainPanel());
        loadCombos();
        refreshTable();
        refreshTotalPurchased();
        updateFinalPrice();
    }

    private void configureFrame() {
        setTitle("Produtos - Sistema de Confeitaria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 560);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
    }

    private JPanel buildMainPanel() {
        JPanel panel = ViewTheme.createPanel(24, 24, 24, 24);
        panel.setLayout(new BorderLayout(16, 16));

        panel.add(buildHeader(), BorderLayout.NORTH);
        panel.add(buildContent(), BorderLayout.CENTER);

        return panel;
    }

    private Component buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ViewTheme.BACKGROUND);

        JLabel title = ViewTheme.createTitleLabel("Produtos");
        header.add(title, BorderLayout.WEST);

        labelTotalPurchased = ViewTheme.createSubtitleLabel("Total de produtos comprados: 0");
        header.add(labelTotalPurchased, BorderLayout.EAST);

        return header;
    }

    private Component buildContent() {
        JPanel content = new JPanel(new GridLayout(1, 2, 16, 0));
        content.setBackground(ViewTheme.BACKGROUND);

        content.add(wrapCard(buildFormPanel()));
        content.add(wrapCard(buildTablePanel()));

        return content;
    }

    private Component buildFormPanel() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(ViewTheme.CARD_BG);

        form.add(ViewTheme.createFieldLabel("Nome"));
        fieldName = ViewTheme.createTextField(24);
        form.add(fieldName);
        form.add(Box.createVerticalStrut(10));

        form.add(ViewTheme.createFieldLabel("Preço base"));
        fieldBasePrice = ViewTheme.createTextField(10);
        fieldBasePrice.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateFinalPrice();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateFinalPrice();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateFinalPrice();
            }
        });
        form.add(fieldBasePrice);
        form.add(Box.createVerticalStrut(10));

        form.add(ViewTheme.createFieldLabel("Sabor"));
        comboFlavor = new JComboBox<>();
        comboFlavor.setRenderer(new NamedCellRenderer());
        styleComboBox(comboFlavor);
        comboFlavor.addActionListener(e -> updateFinalPrice());
        form.add(comboFlavor);
        form.add(Box.createVerticalStrut(10));

        form.add(ViewTheme.createFieldLabel("Tamanho"));
        comboSize = new JComboBox<>();
        comboSize.setRenderer(new NamedCellRenderer());
        styleComboBox(comboSize);
        comboSize.addActionListener(e -> updateFinalPrice());
        form.add(comboSize);
        form.add(Box.createVerticalStrut(10));

        form.add(ViewTheme.createFieldLabel("Preço final (base + tamanho + nível)"));
        labelFinalPrice = ViewTheme.createSubtitleLabel("R$ 0,00");
        form.add(labelFinalPrice);
        form.add(Box.createVerticalStrut(10));

        form.add(ViewTheme.createFieldLabel("Descrição"));
        fieldDescription = new JTextArea(4, 24);
        fieldDescription.setFont(ViewTheme.FONT_LABEL);
        fieldDescription.setLineWrap(true);
        fieldDescription.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(fieldDescription);
        descScroll.setBorder(BorderFactory.createLineBorder(ViewTheme.BORDER));
        form.add(descScroll);
        form.add(Box.createVerticalStrut(14));

        form.add(buildButtonsPanel());

        return form;
    }

    private Component buildButtonsPanel() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setBackground(ViewTheme.CARD_BG);

        JButton btnNew = ViewTheme.createSecondaryButton("Novo");
        btnNew.addActionListener(e -> clearForm());

        btnSave = ViewTheme.createPrimaryButton("Salvar");
        btnSave.addActionListener(e -> onCreateProduct());

        btnUpdate = ViewTheme.createSecondaryButton("Atualizar");
        btnUpdate.addActionListener(e -> onUpdateProduct());

        btnDelete = ViewTheme.createSecondaryButton("Excluir");
        btnDelete.addActionListener(e -> onDeleteProduct());

        JButton btnReload = ViewTheme.createSecondaryButton("Recarregar");
        btnReload.addActionListener(e -> refreshTable());

        btnSeed = ViewTheme.createSecondaryButton("Seed padrão");
        btnSeed.addActionListener(e -> onSeedDefaults());

        buttons.add(btnNew);
        buttons.add(btnSave);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnReload);
        buttons.add(btnSeed);

        return buttons;
    }

    private Component buildTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setBackground(ViewTheme.CARD_BG);

        tableModel = new DefaultTableModel(
                new String[]{"Nome", "Sabor", "Tamanho", "Preço base", "Descrição"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableProducts = new JTable(tableModel);
        tableProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableProducts.getSelectionModel().addListSelectionListener(e -> onTableSelection());
        styleTable(tableProducts);

        JScrollPane scroll = new JScrollPane(tableProducts);
        scroll.setBorder(BorderFactory.createLineBorder(ViewTheme.BORDER));
        tablePanel.add(scroll, BorderLayout.CENTER);

        return tablePanel;
    }

    private void loadCombos() {
        controller.seedDefaults();
        flavors = controller.listFlavors();
        sizes = controller.listSizes();

        comboFlavor.removeAllItems();
        for (Flavor flavor : flavors) {
            comboFlavor.addItem(flavor);
        }

        comboSize.removeAllItems();
        for (Size size : sizes) {
            comboSize.addItem(size);
        }
        usingPresetData = false;
        if (flavors.isEmpty() || sizes.isEmpty()) {
            loadPresetCombos();
            usingPresetData = true;
            toggleCrudEnabled(false);
            JOptionPane.showMessageDialog(this,
                    "Sabores e tamanhos não encontrados no banco. " +
                            "Mostrando dados padrão (somente visual). Use o botão \"Seed padrão\".",
                    "Produtos",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            toggleCrudEnabled(true);
        }
        updateFinalPrice();
    }

    private void refreshTable() {
        products = controller.listProducts();
        tableModel.setRowCount(0);
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                    p.getName(),
                    p.getFlavor() != null ? p.getFlavor().getName() : "",
                    p.getSize() != null ? p.getSize().getName() : "",
                    p.getBasePrice(),
                    p.getDescription()
            });
        }
        refreshTotalPurchased();
    }

    private void refreshTotalPurchased() {
        long total = controller.getTotalProductsPurchased();
        labelTotalPurchased.setText("Total de produtos comprados: " + total);
    }

    private void onTableSelection() {
        int row = tableProducts.getSelectedRow();
        if (row < 0 || row >= products.size()) return;
        Product p = products.get(row);
        fieldName.setText(p.getName() != null ? p.getName() : "");
        fieldBasePrice.setText(p.getBasePrice() != null ? String.valueOf(p.getBasePrice()) : "");
        fieldDescription.setText(p.getDescription() != null ? p.getDescription() : "");
        selectComboById(comboFlavor, p.getFlavor() != null ? p.getFlavor().getId() : null);
        selectComboById(comboSize, p.getSize() != null ? p.getSize().getId() : null);
    }

    private void onCreateProduct() {
        Product product = buildProductFromForm(false);
        if (product == null) return;
        boolean ok = controller.createProduct(product);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Não foi possível salvar o produto.", "Produtos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        clearForm();
        refreshTable();
    }

    private void onUpdateProduct() {
        Product product = buildProductFromForm(true);
        if (product == null) return;
        boolean ok = controller.updateProduct(product);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Não foi possível atualizar o produto.", "Produtos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        clearForm();
        refreshTable();
    }

    private void onDeleteProduct() {
        int row = tableProducts.getSelectedRow();
        if (row < 0 || row >= products.size()) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.", "Produtos",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja excluir o produto?", "Produtos",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Product product = products.get(row);
        boolean ok = controller.deleteProduct(product);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Não foi possível excluir o produto.", "Produtos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        clearForm();
        refreshTable();
    }

    private Product buildProductFromForm(boolean requireId) {
        if (usingPresetData) {
            JOptionPane.showMessageDialog(this,
                    "Você está usando dados padrão sem salvar no banco. " +
                            "Clique em \"Seed padrão\" para criar no banco e liberar o CRUD.",
                    "Produtos",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        String name = fieldName.getText();
        String basePriceText = fieldBasePrice.getText();
        Flavor flavor = (Flavor) comboFlavor.getSelectedItem();
        Size size = (Size) comboSize.getSelectedItem();
        String description = fieldDescription.getText();

        if (name == null || name.isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do produto.", "Produtos",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        if (flavor == null || size == null) {
            JOptionPane.showMessageDialog(this, "Selecione sabor e tamanho.", "Produtos",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        Double basePrice;
        try {
            basePrice = Double.parseDouble(basePriceText.replace(",", "."));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Preço base inválido.", "Produtos",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        Product product = new Product(name, flavor, size, basePrice, description);
        if (requireId) {
            int row = tableProducts.getSelectedRow();
            if (row < 0 || row >= products.size()) {
                JOptionPane.showMessageDialog(this, "Selecione um produto para atualizar.", "Produtos",
                        JOptionPane.INFORMATION_MESSAGE);
                return null;
            }
            product.setId(products.get(row).getId());
        }
        return product;
    }

    private void clearForm() {
        fieldName.setText("");
        fieldBasePrice.setText("");
        fieldDescription.setText("");
        if (comboFlavor.getItemCount() > 0) comboFlavor.setSelectedIndex(0);
        if (comboSize.getItemCount() > 0) comboSize.setSelectedIndex(0);
        tableProducts.clearSelection();
        updateFinalPrice();
    }

    private void onSeedDefaults() {
        boolean ok = controller.seedDefaults();
        loadCombos();
        refreshTable();
        refreshTotalPurchased();
        updateFinalPrice();
        JOptionPane.showMessageDialog(this,
                ok ? "Seed padrão executado com sucesso." : "Não foi possível executar o seed padrão.",
                "Produtos",
                ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    private void loadPresetCombos() {
        comboFlavor.removeAllItems();
        for (FlavorPreset preset : FlavorPreset.values()) {
            FlavorLevel level = new FlavorLevel(null, preset.getLevelName(), 0.0);
            Flavor flavor = new Flavor(preset.getName(), level, preset.getDescription());
            comboFlavor.addItem(flavor);
        }
        comboSize.removeAllItems();
        for (SizePreset preset : SizePreset.values()) {
            Size size = new Size(null, preset.getName(), preset.getYield(),
                    preset.getWeight(), preset.getPrice());
            comboSize.addItem(size);
        }
        updateFinalPrice();
    }

    private JPanel wrapCard(Component content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ViewTheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private void styleComboBox(JComboBox<?> combo) {
        combo.setBackground(ViewTheme.INPUT_BG);
        combo.setForeground(ViewTheme.TEXT);
        combo.setFont(ViewTheme.FONT_LABEL);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.setGridColor(ViewTheme.BORDER);
        table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(ViewTheme.CARD_BG);
        table.getTableHeader().setForeground(ViewTheme.TEXT);
        table.getTableHeader().setFont(ViewTheme.FONT_SUBTITLE);
        table.setSelectionBackground(ViewTheme.ACCENT_HOVER);
        table.setSelectionForeground(Color.WHITE);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? ViewTheme.CARD_BG : ViewTheme.BACKGROUND);
                    c.setForeground(ViewTheme.TEXT);
                }
                return c;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    private void toggleCrudEnabled(boolean enabled) {
        if (btnSave != null) btnSave.setEnabled(enabled);
        if (btnUpdate != null) btnUpdate.setEnabled(enabled);
        if (btnDelete != null) btnDelete.setEnabled(enabled);
    }

    private void updateFinalPrice() {
        double base = parsePrice(fieldBasePrice.getText());
        Flavor flavor = (Flavor) comboFlavor.getSelectedItem();
        Size size = (Size) comboSize.getSelectedItem();

        double levelPrice = 0.0;
        if (flavor != null && flavor.getLevel() != null && flavor.getLevel().getPrice() != null) {
            levelPrice = flavor.getLevel().getPrice();
        }
        double sizePrice = 0.0;
        if (size != null && size.getPrice() != null) {
            sizePrice = size.getPrice();
        }

        double total = base + levelPrice + sizePrice;
        labelFinalPrice.setText(String.format("R$ %.2f", total));
    }

    private static double parsePrice(String text) {
        if (text == null || text.isBlank()) return 0.0;
        try {
            return Double.parseDouble(text.replace(",", "."));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static void selectComboById(JComboBox<?> combo, Integer id) {
        if (id == null) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item instanceof Flavor) {
                if (id.equals(((Flavor) item).getId())) {
                    combo.setSelectedIndex(i);
                    return;
                }
            } else if (item instanceof Size) {
                if (id.equals(((Size) item).getId())) {
                    combo.setSelectedIndex(i);
                    return;
                }
            }
        }
    }

    private static class NamedCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Flavor) {
                setText(((Flavor) value).getName());
            } else if (value instanceof Size) {
                setText(((Size) value).getName());
            }
            return this;
        }
    }
}
