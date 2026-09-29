import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CafeteriaSystem extends JFrame {

    // --- DATA MODELS ---
    static class MenuItem {
        int id;
        String name;
        String category;
        double price;

        public MenuItem(int id, String name, String category, double price) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
        }
    }

    static class OrderItem {
        MenuItem item;
        int quantity;

        public OrderItem(MenuItem item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }

        public double getSubtotal() {
            return item.price * quantity;
        }
    }

    // Application state
    private final List<OrderItem> cart = new ArrayList<>();
    private final DefaultTableModel tableModel;
    private final JLabel lblSubtotal;
    private final JLabel lblDiscount;
    private final JLabel lblTotal;
    private final JCheckBox chkPwd;
    private final JTextField txtPayment;

    public CafeteriaSystem() {
        setTitle("Campus Cafeteria Ordering and Billing System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // TOP HEADER
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        JLabel headerLabel = new JLabel("CAMPUS CAFETERIA POS SYSTEM");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // LEFT PANEL: MENU SELECTION
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createTitledBorder(" Menu Items "));

        // Food Section
        JLabel lblFood = new JLabel("Food Items:");
        lblFood.setFont(new Font("Arial", Font.BOLD, 14));
        menuPanel.add(lblFood);
        menuPanel.add(createMenuItemButton("Food", 1, "1. Burger (₱50)"));
        menuPanel.add(createMenuItemButton("Food", 2, "2. Fried Chicken (₱80)"));
        menuPanel.add(createMenuItemButton("Food", 3, "3. Spaghetti (₱70)"));
        menuPanel.add(createMenuItemButton("Food", 4, "4. Fries (₱50)"));

        menuPanel.add(Box.createVerticalStrut(15));

        // Drinks Section
        JLabel lblDrinks = new JLabel("Drinks:");
        lblDrinks.setFont(new Font("Arial", Font.BOLD, 14));
        menuPanel.add(lblDrinks);
        menuPanel.add(createMenuItemButton("Drinks", 1, "1. Water (₱15)"));
        menuPanel.add(createMenuItemButton("Drinks", 2, "2. Coke (₱25)"));
        menuPanel.add(createMenuItemButton("Drinks", 3, "3. Sprite (₱25)"));

        add(menuPanel, BorderLayout.WEST);

        // CENTER PANEL: ORDER CART TABLE
        String[] columns = {"Item Name", "Category", "Unit Price", "Qty", "Subtotal"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(" Current Order Summary "));
        add(scrollPane, BorderLayout.CENTER);

        // RIGHT PANEL: BILLING & PAYMENT
        JPanel billingPanel = new JPanel();
        billingPanel.setLayout(new GridLayout(10, 1, 5, 5));
        billingPanel.setBorder(BorderFactory.createTitledBorder(" Billing & Checkout "));

        lblSubtotal = new JLabel("Subtotal: ₱0.00");
        lblDiscount = new JLabel("Discount: ₱0.00");
        lblTotal = new JLabel("Total Bill: ₱0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));

        chkPwd = new JCheckBox("Apply 20% PWD Discount");
        chkPwd.addActionListener(e -> updateTotals());

        JLabel lblPaymentPrompt = new JLabel("Enter Cash Payment (₱):");
        txtPayment = new JTextField();

        JButton btnCheckout = new JButton("Process Payment & Print Bill");
        btnCheckout.setBackground(new Color(39, 174, 96));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFont(new Font("Arial", Font.BOLD, 12));
        btnCheckout.addActionListener(e -> processCheckout());

        JButton btnReset = new JButton("Clear Order");
        btnReset.addActionListener(e -> resetOrder());

        billingPanel.add(lblSubtotal);
        billingPanel.add(chkPwd);
        billingPanel.add(lblDiscount);
        billingPanel.add(lblTotal);
        billingPanel.add(new JSeparator());
        billingPanel.add(lblPaymentPrompt);
        billingPanel.add(txtPayment);
        billingPanel.add(btnCheckout);
        billingPanel.add(btnReset);

        add(billingPanel, BorderLayout.EAST);
    }

    // Creates menu buttons wired to the requirement logic
    private JButton createMenuItemButton(String category, int choiceCode, String labelText) {
        JButton btn = new JButton(labelText);
        btn.setMaximumSize(new Dimension(220, 30));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addActionListener(e -> {
            // Fetch item using SWITCH STATEMENT
            MenuItem selectedItem = getMenuItemByCode(category, choiceCode);
            if (selectedItem != null) {
                String input = JOptionPane.showInputDialog(this, 
                        "Enter Quantity for " + selectedItem.name + ":", "Quantity", JOptionPane.QUESTION_MESSAGE);
                if (input != null && !input.trim().isEmpty()) {
                    try {
                        int qty = Integer.parseInt(input);
                        if (qty > 0) {
                            cart.add(new OrderItem(selectedItem, qty));
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
        });
        return btn;
    }

    // =========================================================================
    // REQUIRED ACADEMIC LOGIC: SWITCH, CONDITIONAL, AND LOOP STATEMENTS
    // =========================================================================

    // 1. SWITCH STATEMENT: Menu item selection logic
    public MenuItem getMenuItemByCode(String category, int choice) {
        if (category.equalsIgnoreCase("Food")) {
            switch (choice) {
                case 1: return new MenuItem(1, "Burger", "Food", 50.0);
                case 2: return new MenuItem(2, "Fried Chicken", "Food", 80.0);
                case 3: return new MenuItem(3, "Spaghetti", "Food", 70.0);
                case 4: return new MenuItem(4, "Fries", "Food", 50.0);
                default: return null;
            }
        } else if (category.equalsIgnoreCase("Drinks")) {
            switch (choice) {
                case 1: return new MenuItem(5, "Water", "Drinks", 15.0);
                case 2: return new MenuItem(6, "Coke", "Drinks", 25.0);
                case 3: return new MenuItem(7, "Sprite", "Drinks", 25.0);
                default: return null;
            }
        }
        return null;
    }

    // 2. LOOP STATEMENT: Iterate over cart items to refresh table and totals
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (OrderItem order : cart) { // Loop statement
            tableModel.addRow(new Object[]{
                    order.item.name,
                    order.item.category,
                    String.format("₱%.2f", order.item.price),
                    order.quantity,
                    String.format("₱%.2f", order.getSubtotal())
            });
        }
    }

    // 2. LOOP STATEMENT: Calculate subtotal
    private double calculateSubtotal() {
        double subtotal = 0.0;
        for (OrderItem order : cart) { // Loop statement
            subtotal += order.getSubtotal();
        }
        return subtotal;
    }

    // 3. CONDITIONAL STATEMENTS: Calculate discount & final total
    private double[] calculateFinals() {
        double subtotal = calculateSubtotal();
        double discount = 0.0;

        // Conditional statement: PWD discount vs Bulk bill discount
        if (chkPwd.isSelected()) {
            discount = subtotal * 0.20; // 20% PWD discount
        } else if (subtotal >= 500.0) {
            discount = subtotal * 0.10; // 10% discount for orders ₱500 and above
        }

        double finalTotal = subtotal - discount;
        return new double[]{subtotal, discount, finalTotal};
    }

    private void updateTotals() {
        double[] finals = calculateFinals();
        lblSubtotal.setText(String.format("Subtotal: ₱%.2f", finals[0]));
        lblDiscount.setText(String.format("Discount: -₱%.2f", finals[1]));
        lblTotal.setText(String.format("Total Bill: ₱%.2f", finals[2]));
    }

    // 3. CONDITIONAL STATEMENTS: Payment validation & Change calculation
    private void processCheckout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Order cart is empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double payment = Double.parseDouble(txtPayment.getText().trim());
            double[] finals = calculateFinals();
            double finalTotal = finals[2];

            // Conditional statement: Insufficient payment check
            if (payment < finalTotal) {
                JOptionPane.showMessageDialog(this, "Insufficient payment.", "Payment Error", JOptionPane.ERROR_MESSAGE);
            } else {
                double change = payment - finalTotal;

                // Order Summary Receipt Dialog
                StringBuilder receipt = new StringBuilder();
                receipt.append("--- CAMPUS CAFETERIA RECEIPT ---\n\n");
                for (OrderItem item : cart) { // Loop statement
                    receipt.append(String.format("%s x%d - ₱%.2f\n", item.item.name, item.quantity, item.getSubtotal()));
                }
                receipt.append("\n------------------------------\n");
                receipt.append(String.format("Subtotal: ₱%.2f\n", finals[0]));
                receipt.append(String.format("Discount: ₱%.2f\n", finals[1]));
                receipt.append(String.format("Total Amount Due: ₱%.2f\n", finalTotal));
                receipt.append(String.format("Cash Tendered: ₱%.2f\n", payment));
                receipt.append(String.format("Change: ₱%.2f\n", change));
                receipt.append("\nThank you for your order!");

                JOptionPane.showMessageDialog(this, receipt.toString(), "Order Summary & Receipt", JOptionPane.INFORMATION_MESSAGE);
                resetOrder();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric payment amount.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void resetOrder() {
        cart.clear();
        chkPwd.setSelected(false);
        txtPayment.setText("");
        refreshTable();
        updateTotals();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CafeteriaSystem().setVisible(true);
        });
    }
}