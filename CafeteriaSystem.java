import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CafeteriaSystem extends JFrame {

    // --- ORIGINAL DATA MODELS ---
    static class MenuItem {
        int id; String name; String category; double price;
        public MenuItem(int id, String name, String category, double price) {
            this.id = id; this.name = name; this.category = category; this.price = price;
        }
    }

    static class OrderItem {
        MenuItem item; int quantity;
        public OrderItem(MenuItem item, int quantity) {
            this.item = item; this.quantity = quantity;
        }
        public double getSubtotal() { return item.price * quantity; }
    }

    // --- APPLICATION STATE ---
    private final List<OrderItem> cart = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JLabel lblSubtotal, lblDiscount, lblTotal;
    private JTextField txtPayment;

    // --- MULTI-SCREEN VARIABLES ---
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private String orderType = "";

    // --- UI COLOR PALETTE (Modern & Warm) ---
    private final Color PRIMARY_COLOR = new Color(230, 126, 34); // Warm Carrot Orange
    private final Color BG_COLOR = new Color(249, 250, 251);     // Off-white background
    private final Color TEXT_DARK = new Color(44, 62, 80);       // Dark Slate
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);  // Green
    private final Color DANGER_COLOR = new Color(231, 76, 60);   // Red

    public CafeteriaSystem() {
        setTitle("Campus Cafeteria Kiosk");
        setSize(1100, 800); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Try to set system look and feel for smoother base rendering
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(BG_COLOR);

        mainContainer.add(createWelcomeScreen(), "WELCOME");
        mainContainer.add(createOrderTypeScreen(), "ORDER_TYPE");
        mainContainer.add(createMenuScreen(), "MENU"); 

        add(mainContainer);
        cardLayout.show(mainContainer, "WELCOME");
    }

    // =========================================================================
    // KIOSK FLOW PANELS (UX Upgrades)
    // =========================================================================

    private JPanel createWelcomeScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);

        JLabel title = new JLabel("<html><center>CAMPUS<br>CAFETERIA</center></html>", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 72));
        title.setForeground(Color.WHITE);

        JButton btnStart = new JButton("TAP HERE TO ORDER");
        btnStart.setFont(new Font("SansSerif", Font.BOLD, 36));
        btnStart.setBackground(Color.WHITE);
        btnStart.setForeground(PRIMARY_COLOR);
        btnStart.setFocusPainted(false);
        btnStart.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        btnStart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStart.addActionListener(e -> cardLayout.show(mainContainer, "ORDER_TYPE"));

        panel.add(title, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 0));
        bottomPanel.add(btnStart);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOrderTypeScreen() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(200, 150, 200, 150));
        panel.setBackground(BG_COLOR);

        JButton btnDineIn = createHugeButton("DINE-IN", SUCCESS_COLOR);
        btnDineIn.addActionListener(e -> {
            orderType = "Dine-In";
            cardLayout.show(mainContainer, "MENU");
        });

        JButton btnTakeOut = createHugeButton("TAKE-OUT", PRIMARY_COLOR);
        btnTakeOut.addActionListener(e -> {
            orderType = "Take-Out";
            cardLayout.show(mainContainer, "MENU");
        });

        panel.add(btnDineIn);
        panel.add(btnTakeOut);

        return panel;
    }

    private JButton createHugeButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 48));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // =========================================================================
    // MENU & CHECKOUT SCREEN (Clean Flat Design)
    // =========================================================================

    private JPanel createMenuScreen() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- TOP HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel headerLabel = new JLabel(" Choose Your Items", SwingConstants.LEFT);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        headerLabel.setForeground(Color.WHITE);
        
        JButton btnCancel = new JButton("Start Over");
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setForeground(DANGER_COLOR);
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> {
            resetOrder();
            orderType = "";
            cardLayout.show(mainContainer, "WELCOME");
        });

        headerPanel.add(headerLabel, BorderLayout.CENTER);
        headerPanel.add(btnCancel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // --- LEFT PANEL: MENU SELECTION (Grid Tiles) ---
        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBackground(BG_COLOR);

        // --- RICE MEALS ---
        addCategoryHeader(menuContainer, "Combo Rice Meals");
        JPanel gridRice = createMenuGrid();
        gridRice.add(createComboButton(1, "1 Meat + Rice", 70.0));
        gridRice.add(createComboButton(2, "2 Meats + Rice", 120.0));
        gridRice.add(createComboButton(3, "Vegetable + Rice", 55.0));
        gridRice.add(createComboButton(4, "Meat + Veg + Rice", 90.0));
        menuContainer.add(gridRice);

        // --- FAST FOOD ---
        addCategoryHeader(menuContainer, "Fast Food");
        JPanel gridFastFood = createMenuGrid();
        gridFastFood.add(createMenuItemButton("Fast Food", 1, "Burger", 50.0));
        gridFastFood.add(createMenuItemButton("Fast Food", 2, "Fried Chicken", 80.0));
        gridFastFood.add(createMenuItemButton("Fast Food", 3, "Spaghetti", 70.0));
        gridFastFood.add(createMenuItemButton("Fast Food", 4, "Fries", 50.0));
        menuContainer.add(gridFastFood);

        // --- SNACKS ---
        addCategoryHeader(menuContainer, "Snacks");
        JPanel gridSnacks = createMenuGrid();
        gridSnacks.add(createMenuItemButton("Snacks", 1, "Skyflakes", 10.0));
        gridSnacks.add(createMenuItemButton("Snacks", 2, "Hansel Mocha", 10.0));
        gridSnacks.add(createMenuItemButton("Snacks", 3, "Piattos", 18.0));
        gridSnacks.add(createMenuItemButton("Snacks", 4, "Pancit Canton", 25.0));
        menuContainer.add(gridSnacks);

        // --- DRINKS ---
        addCategoryHeader(menuContainer, "Drinks");
        JPanel gridDrinks = createMenuGrid();
        gridDrinks.add(createMenuItemButton("Drinks", 1, "Mineral Water", 15.0));
        gridDrinks.add(createMenuItemButton("Drinks", 2, "Coca-Cola", 25.0));
        gridDrinks.add(createMenuItemButton("Drinks", 3, "Sprite", 25.0));
        gridDrinks.add(createMenuItemButton("Drinks", 4, "C2 Apple", 20.0));
        menuContainer.add(gridDrinks);

        JScrollPane menuScroll = new JScrollPane(menuContainer);
        menuScroll.setBorder(null);
        menuScroll.getVerticalScrollBar().setUnitIncrement(20); 
        menuScroll.setPreferredSize(new Dimension(550, 0)); // Wider area for grids
        panel.add(menuScroll, BorderLayout.CENTER);

        // --- RIGHT PANEL: CART & CHECKOUT ---
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setBackground(BG_COLOR);
        rightPanel.setPreferredSize(new Dimension(450, 0));

        // 1. Cleaner Table
        String[] columns = {"Item", "Qty", "Subtotal"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable orderTable = new JTable(tableModel);
        orderTable.setRowHeight(35); // Touch-friendly rows
        orderTable.setFont(new Font("SansSerif", Font.PLAIN, 16));
        orderTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        orderTable.setShowGrid(false); // Removes Excel look
        
        JScrollPane tableScroll = new JScrollPane(orderTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 228)));
        tableScroll.getViewport().setBackground(Color.WHITE);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        // 2. Billing Area
        JPanel billingPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        billingPanel.setBackground(Color.WHITE);
        billingPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 228)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        lblSubtotal = new JLabel("Subtotal: ₱0.00", SwingConstants.RIGHT);
        lblDiscount = new JLabel("Discount: ₱0.00", SwingConstants.RIGHT);
        lblSubtotal.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblDiscount.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblDiscount.setForeground(DANGER_COLOR);

        lblTotal = new JLabel("Total: ₱0.00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTotal.setForeground(PRIMARY_COLOR);

        JPanel paymentInputPanel = new JPanel(new BorderLayout(10, 0));
        paymentInputPanel.setBackground(Color.WHITE);
        JLabel lblPay = new JLabel("Cash (₱):");
        lblPay.setFont(new Font("SansSerif", Font.BOLD, 18));
        txtPayment = new JTextField();
        txtPayment.setFont(new Font("SansSerif", Font.BOLD, 20));
        paymentInputPanel.add(lblPay, BorderLayout.WEST);
        paymentInputPanel.add(txtPayment, BorderLayout.CENTER);

        JButton btnCheckout = new JButton("PAY NOW");
        btnCheckout.setFont(new Font("SansSerif", Font.BOLD, 22));
        btnCheckout.setBackground(SUCCESS_COLOR);
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFocusPainted(false);
        btnCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckout.addActionListener(e -> processCheckout());

        billingPanel.add(lblSubtotal);
        billingPanel.add(lblDiscount);
        billingPanel.add(lblTotal);
        billingPanel.add(new JSeparator());
        billingPanel.add(paymentInputPanel);
        billingPanel.add(btnCheckout);

        rightPanel.add(billingPanel, BorderLayout.SOUTH);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    // --- UX HELPER METHODS ---

    private JPanel createMenuGrid() {
        JPanel grid = new JPanel(new GridLayout(0, 2, 15, 15)); // 2 columns, nice spacing
        grid.setBackground(BG_COLOR);
        grid.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        return grid;
    }

    private void addCategoryHeader(JPanel panel, String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        label.setForeground(TEXT_DARK);
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
        panel.add(label);
    }

    // =========================================================================
    // SYSTEM LOGIC & BUTTON GENERATORS
    // =========================================================================

    private JButton createComboButton(int choiceCode, String name, double price) {
        JButton btn = styleTileButton(name, price);
        btn.addActionListener(e -> handleComboClick(choiceCode));
        return btn;
    }

    private JButton createMenuItemButton(String category, int choiceCode, String name, double price) {
        JButton btn = styleTileButton(name, price);
        btn.addActionListener(e -> {
            MenuItem selectedItem = getMenuItemByCode(category, choiceCode);
            if (selectedItem != null) askForQuantity(selectedItem);
        });
        return btn;
    }

    // Creates the modern square "Tile" look for buttons
    private JButton styleTileButton(String name, double price) {
        String htmlText = "<html><center><span style='font-size:16px; color:#2c3e50;'>" + name + 
                          "</span><br><br><span style='font-size:18px; color:#e67e22;'><b>₱" + price + "</b></span></center></html>";
        JButton btn = new JButton(htmlText);
        btn.setPreferredSize(new Dimension(0, 100)); // Tall tiles
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 228), 2));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void handleComboClick(int choiceCode) {
        String[] meats = {"Pork Adobo", "Beef Tapa", "Chicken Inasal", "Lechon Kawali", "Sisig"};
        String[] veggies = {"Pinakbet", "Ginisang Monggo", "Chopsuey", "Laing"};
        
        String finalName = "";
        double price = 0.0;

        // RUBRIC: Switch Statement for combos
        switch (choiceCode) {
            case 1:
                String m1 = promptSelection("Select your Meat:", meats);
                if (m1 == null) return;
                finalName = m1 + " + Rice"; price = 70.0;
                break;
            case 2:
                String mm1 = promptSelection("Select FIRST Meat:", meats);
                if (mm1 == null) return;
                String mm2 = promptSelection("Select SECOND Meat:", meats);
                if (mm2 == null) return;
                finalName = mm1 + " & " + mm2 + " + Rice"; price = 120.0;
                break;
            case 3:
                String v1 = promptSelection("Select your Vegetable:", veggies);
                if (v1 == null) return;
                finalName = v1 + " + Rice"; price = 55.0;
                break;
            case 4:
                String mv1 = promptSelection("Select your Meat:", meats);
                if (mv1 == null) return;
                String vv1 = promptSelection("Select your Vegetable:", veggies);
                if (vv1 == null) return;
                finalName = mv1 + " & " + vv1 + " + Rice"; price = 90.0;
                break;
        }

        askForQuantity(new MenuItem(100 + choiceCode, finalName, "Rice Combo", price));
    }

    private String promptSelection(String title, String[] options) {
        return (String) JOptionPane.showInputDialog(this, title, "Choose Menu", 
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }

    private void askForQuantity(MenuItem item) {
        String input = JOptionPane.showInputDialog(this, "Enter Quantity for " + item.name + ":", "Quantity", JOptionPane.QUESTION_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                int qty = Integer.parseInt(input);
                if (qty > 0) {
                    cart.add(new OrderItem(item, qty));
                    refreshTable();
                    updateTotals();
                } else {
                    JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        }
    }

    // RUBRIC REQUIREMENT: Switch Statement for standard items
    public MenuItem getMenuItemByCode(String category, int choice) {
        if (category.equalsIgnoreCase("Fast Food")) {
            switch (choice) {
                case 1: return new MenuItem(201, "Burger", "Fast Food", 50.0);
                case 2: return new MenuItem(202, "Fried Chicken", "Fast Food", 80.0);
                case 3: return new MenuItem(203, "Spaghetti", "Fast Food", 70.0);
                case 4: return new MenuItem(204, "Fries", "Fast Food", 50.0);
                default: return null;
            }
        } else if (category.equalsIgnoreCase("Snacks")) {
            switch (choice) {
                case 1: return new MenuItem(301, "Skyflakes", "Snacks", 10.0);
                case 2: return new MenuItem(302, "Hansel Mocha", "Snacks", 10.0);
                case 3: return new MenuItem(303, "Piattos", "Snacks", 18.0);
                case 4: return new MenuItem(304, "Pancit Canton", "Snacks", 25.0);
                default: return null;
            }
        } else if (category.equalsIgnoreCase("Drinks")) {
            switch (choice) {
                case 1: return new MenuItem(401, "Mineral Water", "Drinks", 15.0);
                case 2: return new MenuItem(402, "Coca-Cola", "Drinks", 25.0);
                case 3: return new MenuItem(403, "Sprite", "Drinks", 25.0);
                case 4: return new MenuItem(404, "C2 Apple", "Drinks", 20.0);
                default: return null;
            }
        }
        return null;
    }

    // RUBRIC REQUIREMENT: Loops
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (OrderItem order : cart) {
            tableModel.addRow(new Object[]{
                    order.item.name,
                    "x" + order.quantity,
                    String.format("₱%.2f", order.getSubtotal())
            });
        }
    }

    private double calculateSubtotal() {
        double subtotal = 0.0;
        for (OrderItem order : cart) subtotal += order.getSubtotal(); // LOOP
        return subtotal;
    }

    private double[] calculateFinals() {
        double subtotal = calculateSubtotal();
        double discount = 0.0;
        
        // RUBRIC REQUIREMENT: Conditional logic for discounts
        if (subtotal >= 500.0) discount = subtotal * 0.10;

        double finalTotal = subtotal - discount;
        return new double[]{subtotal, discount, finalTotal};
    }

    private void updateTotals() {
        double[] finals = calculateFinals();
        lblSubtotal.setText(String.format("Subtotal: ₱%.2f", finals[0]));
        lblDiscount.setText(String.format("Discount: -₱%.2f", finals[1]));
        lblTotal.setText(String.format("Total: ₱%.2f", finals[2]));
    }

    private void processCheckout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Order cart is empty!", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            double payment = Double.parseDouble(txtPayment.getText().trim());
            double[] finals = calculateFinals();
            double finalTotal = finals[2];

            // RUBRIC REQUIREMENT: Conditional logic for payments
            if (payment < finalTotal) {
                JOptionPane.showMessageDialog(this, "Insufficient payment.", "Payment Error", JOptionPane.ERROR_MESSAGE);
            } else {
                double change = payment - finalTotal;

                StringBuilder receipt = new StringBuilder();
                receipt.append("--- CAMPUS CAFETERIA RECEIPT ---\n");
                receipt.append("Order Type: ").append(orderType).append("\n\n");
                for (OrderItem item : cart) {
                    receipt.append(String.format("%s (x%d) - ₱%.2f\n", item.item.name, item.quantity, item.getSubtotal()));
                }
                receipt.append("\n------------------------------\n");
                receipt.append(String.format("Subtotal: ₱%.2f\n", finals[0]));
                receipt.append(String.format("Discount: ₱%.2f\n", finals[1]));
                receipt.append(String.format("Total Due: ₱%.2f\n", finalTotal));
                receipt.append(String.format("Cash Tendered: ₱%.2f\n", payment));
                receipt.append(String.format("Change: ₱%.2f\n", change));
                receipt.append("\nThank you for your order!");

                JOptionPane.showMessageDialog(this, receipt.toString(), "Payment Successful", JOptionPane.INFORMATION_MESSAGE);
                
                resetOrder();
                orderType = "";
                cardLayout.show(mainContainer, "WELCOME");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void resetOrder() {
        cart.clear();
        txtPayment.setText("");
        refreshTable();
        updateTotals();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CafeteriaSystem().setVisible(true));
    }
}