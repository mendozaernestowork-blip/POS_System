import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CafeteriaSystem extends JFrame {

    // --- ORIGINAL DATA MODELS ---
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

    // --- APPLICATION STATE (Original Variables) ---
    private final List<OrderItem> cart = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JLabel lblSubtotal;
    private JLabel lblDiscount;
    private JLabel lblTotal;
    private JTextField txtPayment;

    // --- NEW MULTI-SCREEN VARIABLES ---
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private String orderType = "";

    public CafeteriaSystem() {
        setTitle("Campus Cafeteria Ordering and Billing System");
        setSize(1024, 768); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Setup CardLayout to swap screens
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Add the three phases of the kiosk
        mainContainer.add(createWelcomeScreen(), "WELCOME");
        mainContainer.add(createOrderTypeScreen(), "ORDER_TYPE");
        mainContainer.add(createMenuScreen(), "MENU"); 

        add(mainContainer);
        
        // Boot directly to the welcome screen
        cardLayout.show(mainContainer, "WELCOME");
    }

    // =========================================================================
    // KIOSK FLOW PANELS
    // =========================================================================

    private JPanel createWelcomeScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));

        JLabel title = new JLabel("CAMPUS CAFETERIA POS SYSTEM", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 48));
        title.setForeground(Color.WHITE);

        JButton btnStart = new JButton("TAP TO START");
        btnStart.setFont(new Font("Arial", Font.BOLD, 32));
        btnStart.setBackground(Color.WHITE);
        btnStart.setForeground(new Color(41, 128, 185));
        btnStart.setFocusPainted(false);
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
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(150, 100, 150, 100));
        panel.setBackground(Color.WHITE);

        JButton btnDineIn = new JButton("DINE-IN");
        btnDineIn.setFont(new Font("Arial", Font.BOLD, 40));
        btnDineIn.setBackground(new Color(39, 174, 96));
        btnDineIn.setForeground(Color.WHITE);
        btnDineIn.addActionListener(e -> {
            orderType = "Dine-In";
            cardLayout.show(mainContainer, "MENU");
        });

        JButton btnTakeOut = new JButton("TAKE-OUT");
        btnTakeOut.setFont(new Font("Arial", Font.BOLD, 40));
        btnTakeOut.setBackground(new Color(39, 174, 96));
        btnTakeOut.setForeground(Color.WHITE);
        btnTakeOut.addActionListener(e -> {
            orderType = "Take-Out";
            cardLayout.show(mainContainer, "MENU");
        });

        panel.add(btnDineIn);
        panel.add(btnTakeOut);

        return panel;
    }

    // =========================================================================
    // YOUR ORIGINAL UI ENCAPSULATED IN A PANEL
    // =========================================================================

    private JPanel createMenuScreen() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // TOP HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        JLabel headerLabel = new JLabel(" SELECT YOUR ORDER", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setForeground(Color.WHITE);
        
        JButton btnCancel = new JButton("Cancel Order");
        btnCancel.setBackground(new Color(231, 76, 60));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> {
            resetOrder();
            orderType = "";
            cardLayout.show(mainContainer, "WELCOME");
        });

        headerPanel.add(headerLabel, BorderLayout.CENTER);
        headerPanel.add(btnCancel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // LEFT PANEL: MENU SELECTION
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createTitledBorder(" Menu Items "));

        JLabel lblFood = new JLabel("Food Items:");
        lblFood.setFont(new Font("Arial", Font.BOLD, 14));
        menuPanel.add(lblFood);
        menuPanel.add(createMenuItemButton("Food", 1, "1. Burger (₱50)"));
        menuPanel.add(createMenuItemButton("Food", 2, "2. Fried Chicken (₱80)"));
        menuPanel.add(createMenuItemButton("Food", 3, "3. Spaghetti (₱70)"));
        menuPanel.add(createMenuItemButton("Food", 4, "4. Fries (₱50)"));

        menuPanel.add(Box.createVerticalStrut(15));

        JLabel lblDrinks = new JLabel("Drinks:");
        lblDrinks.setFont(new Font("Arial", Font.BOLD, 14));
        menuPanel.add(lblDrinks);
        menuPanel.add(createMenuItemButton("Drinks", 1, "1. Water (₱15)"));
        menuPanel.add(createMenuItemButton("Drinks", 2, "2. Coke (₱25)"));
        menuPanel.add(createMenuItemButton("Drinks", 3, "3. Sprite (₱25)"));

        panel.add(menuPanel, BorderLayout.WEST);

        // CENTER PANEL: ORDER CART TABLE
        String[] columns = {"Item Name", "Category", "Unit Price", "Qty", "Subtotal"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(" Current Order Summary "));
        panel.add(scrollPane, BorderLayout.CENTER);

        // RIGHT PANEL: BILLING & PAYMENT
        JPanel billingPanel = new JPanel();
        billingPanel.setLayout(new GridLayout(9, 1, 5, 5)); // Adjusted for removed checkbox
        billingPanel.setBorder(BorderFactory.createTitledBorder(" Billing & Checkout "));

        lblSubtotal = new JLabel("Subtotal: ₱0.00");
        lblDiscount = new JLabel("Discount: ₱0.00");
        lblTotal = new JLabel("Total Bill: ₱0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));

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
        billingPanel.add(lblDiscount);
        billingPanel.add(lblTotal);
        billingPanel.add(new JSeparator());
        billingPanel.add(lblPaymentPrompt);
        billingPanel.add(txtPayment);
        billingPanel.add(btnCheckout);
        billingPanel.add(btnReset);

        panel.add(billingPanel, BorderLayout.EAST);

        return panel;
    }

    // =========================================================================
    // YOUR ORIGINAL LOGIC METHODS
    // =========================================================================

    private JButton createMenuItemButton(String category, int choiceCode, String labelText) {
        JButton btn = new JButton(labelText);
        btn.setMaximumSize(new Dimension(220, 30));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addActionListener(e -> {
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

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (OrderItem order : cart) {
            tableModel.addRow(new Object[]{
                    order.item.name,
                    order.item.category,
                    String.format("₱%.2f", order.item.price),
                    order.quantity,
                    String.format("₱%.2f", order.getSubtotal())
            });
        }
    }

    private double calculateSubtotal() {
        double subtotal = 0.0;
        for (OrderItem order : cart) {
            subtotal += order.getSubtotal();
        }
        return subtotal;
    }

    private double[] calculateFinals() {
        double subtotal = calculateSubtotal();
        double discount = 0.0;

        // Simplified Conditional Statement: Only applies bulk discount
        if (subtotal >= 500.0) {
            discount = subtotal * 0.10;
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

    private void processCheckout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Order cart is empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double payment = Double.parseDouble(txtPayment.getText().trim());
            double[] finals = calculateFinals();
            double finalTotal = finals[2];

            if (payment < finalTotal) {
                JOptionPane.showMessageDialog(this, "Insufficient payment.", "Payment Error", JOptionPane.ERROR_MESSAGE);
            } else {
                double change = payment - finalTotal;

                StringBuilder receipt = new StringBuilder();
                receipt.append("--- CAMPUS CAFETERIA RECEIPT ---\n");
                receipt.append("Type: ").append(orderType).append("\n\n");
                for (OrderItem item : cart) {
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
                
                // End transaction and return to welcome screen
                resetOrder();
                orderType = "";
                cardLayout.show(mainContainer, "WELCOME");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric payment amount.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void resetOrder() {
        cart.clear();
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