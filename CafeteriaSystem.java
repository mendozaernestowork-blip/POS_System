import javax.swing.*;
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
    private JPanel cartContainer; 
    private JLabel lblSubtotal, lblDiscount, lblTotal;

    // --- MULTI-SCREEN VARIABLES ---
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private String orderType = "";
    private int orderCounter = 1; // Tracks the order number for the day

    // --- UI COLOR PALETTE ---
    private final Color PRIMARY_COLOR = new Color(230, 126, 34); 
    private final Color BG_COLOR = new Color(249, 250, 251);     
    private final Color TEXT_DARK = new Color(44, 62, 80);       
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);  
    private final Color DANGER_COLOR = new Color(231, 76, 60);   

    public CafeteriaSystem() {
        setTitle("Campus Cafeteria Kiosk");
        setSize(1100, 800); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
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
    // KIOSK FLOW PANELS
    // =========================================================================

    private JPanel createWelcomeScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);

        JLabel title = new JLabel("<html><center>CAMPUS<br>CAFETERIA</center></html>", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 72));
        title.setForeground(Color.WHITE);

        JButton btnStart = new JButton("TAP HERE TO ORDER");
        btnStart.setFont(new Font("SansSerif", Font.BOLD, 36));
        styleFlatSolidButton(btnStart, Color.WHITE, PRIMARY_COLOR);
        btnStart.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
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

        JButton btnDineIn = new JButton("DINE-IN");
        btnDineIn.setFont(new Font("SansSerif", Font.BOLD, 48));
        styleFlatSolidButton(btnDineIn, SUCCESS_COLOR, Color.WHITE);
        btnDineIn.addActionListener(e -> {
            orderType = "Dine-In";
            cardLayout.show(mainContainer, "MENU");
        });

        JButton btnTakeOut = new JButton("TAKE-OUT");
        btnTakeOut.setFont(new Font("SansSerif", Font.BOLD, 48));
        styleFlatSolidButton(btnTakeOut, PRIMARY_COLOR, Color.WHITE);
        btnTakeOut.addActionListener(e -> {
            orderType = "Take-Out";
            cardLayout.show(mainContainer, "MENU");
        });

        panel.add(btnDineIn);
        panel.add(btnTakeOut);

        return panel;
    }

    // =========================================================================
    // MENU & CHECKOUT SCREEN
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
        styleFlatSolidButton(btnCancel, Color.WHITE, DANGER_COLOR);
        btnCancel.addActionListener(e -> {
            resetOrder();
            orderType = "";
            cardLayout.show(mainContainer, "WELCOME");
        });

        headerPanel.add(headerLabel, BorderLayout.CENTER);
        headerPanel.add(btnCancel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // --- LEFT PANEL: MENU SELECTION ---
        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBackground(BG_COLOR);

        addCategoryHeader(menuContainer, "Combo Rice Meals");
        JPanel gridRice = createMenuGrid();
        gridRice.add(createComboButton(1, "1 Meat + Rice", 70.0));
        gridRice.add(createComboButton(2, "2 Meats + Rice", 120.0));
        gridRice.add(createComboButton(3, "Vegetable + Rice", 55.0));
        gridRice.add(createComboButton(4, "Meat + Veg + Rice", 90.0));
        menuContainer.add(gridRice);

        addCategoryHeader(menuContainer, "Fast Food");
        JPanel gridFastFood = createMenuGrid();
        gridFastFood.add(createMenuItemButton("Fast Food", 1, "Burger", 50.0));
        gridFastFood.add(createMenuItemButton("Fast Food", 2, "Fried Chicken", 80.0));
        gridFastFood.add(createMenuItemButton("Fast Food", 3, "Spaghetti", 70.0));
        gridFastFood.add(createMenuItemButton("Fast Food", 4, "Fries", 50.0));
        menuContainer.add(gridFastFood);

        addCategoryHeader(menuContainer, "Snacks");
        JPanel gridSnacks = createMenuGrid();
        gridSnacks.add(createMenuItemButton("Snacks", 1, "Skyflakes", 10.0));
        gridSnacks.add(createMenuItemButton("Snacks", 2, "Hansel Mocha", 10.0));
        gridSnacks.add(createMenuItemButton("Snacks", 3, "Piattos", 18.0));
        gridSnacks.add(createMenuItemButton("Snacks", 4, "Pancit Canton", 25.0));
        menuContainer.add(gridSnacks);

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
        menuScroll.setPreferredSize(new Dimension(550, 0)); 
        panel.add(menuScroll, BorderLayout.CENTER);

        // --- RIGHT PANEL: CART & CHECKOUT ---
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setBackground(BG_COLOR);
        rightPanel.setPreferredSize(new Dimension(450, 0));

        cartContainer = new JPanel();
        cartContainer.setLayout(new BoxLayout(cartContainer, BoxLayout.Y_AXIS));
        cartContainer.setBackground(Color.WHITE);
        
        JPanel cartWrapper = new JPanel(new BorderLayout());
        cartWrapper.setBackground(Color.WHITE);
        cartWrapper.add(cartContainer, BorderLayout.NORTH); 
        
        JScrollPane cartScroll = new JScrollPane(cartWrapper);
        cartScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 228)), " Current Order "
        ));
        cartScroll.getViewport().setBackground(Color.WHITE);
        cartScroll.getVerticalScrollBar().setUnitIncrement(16);
        rightPanel.add(cartScroll, BorderLayout.CENTER);

        // Cleaner Billing Panel (No Cash Entry)
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

        lblTotal = new JLabel("Total Due: ₱0.00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblTotal.setForeground(PRIMARY_COLOR);

        JButton btnCheckout = new JButton("CHECKOUT");
        btnCheckout.setFont(new Font("SansSerif", Font.BOLD, 26));
        styleFlatSolidButton(btnCheckout, SUCCESS_COLOR, Color.WHITE);
        btnCheckout.addActionListener(e -> processCheckout());

        JButton btnClear = new JButton("Clear Order");
        btnClear.setFont(new Font("SansSerif", Font.BOLD, 16));
        styleFlatSolidButton(btnClear, Color.WHITE, TEXT_DARK);
        btnClear.setBorderPainted(true);
        btnClear.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        btnClear.addActionListener(e -> resetOrder());

        billingPanel.add(lblSubtotal);
        billingPanel.add(lblDiscount);
        billingPanel.add(lblTotal);
        billingPanel.add(new JSeparator());
        billingPanel.add(btnCheckout);
        billingPanel.add(btnClear);

        rightPanel.add(billingPanel, BorderLayout.SOUTH);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    // --- UX HELPER METHODS ---

    private void styleFlatSolidButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false); 
        btn.setContentAreaFilled(false); 
        btn.setOpaque(true); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel createMenuGrid() {
        JPanel grid = new JPanel(new GridLayout(0, 2, 15, 15)); 
        grid.setBackground(BG_COLOR);
        grid.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        return grid;
    }

    private void addCategoryHeader(JPanel panel, String title) {
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        label.setForeground(TEXT_DARK);
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        label.setAlignmentX(Component.CENTER_ALIGNMENT); 
        panel.add(label);
    }

    private JButton styleTileButton(String name, double price) {
        String htmlText = "<html><center><span style='font-size:16px; color:#2c3e50;'>" + name + 
                          "</span><br><br><span style='font-size:18px; color:#e67e22;'><b>₱" + price + "</b></span></center></html>";
        JButton btn = new JButton(htmlText);
        btn.setPreferredSize(new Dimension(0, 100)); 
        
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); 
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }

    private void styleCartQtyButton(JButton btn, Color bg) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 20));
        btn.setPreferredSize(new Dimension(45, 35));
        styleFlatSolidButton(btn, bg, Color.WHITE);
    }

    // =========================================================================
    // TOUCH-NATIVE POPUP DIALOGS 
    // =========================================================================

    private String promptTouchSelection(String title, String[] options) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(700, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(BG_COLOR);

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        dialog.add(lblTitle, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 15, 15));
        grid.setBackground(BG_COLOR);
        grid.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        final String[] result = {null};

        for (String opt : options) {
            JButton btn = new JButton(opt);
            btn.setFont(new Font("SansSerif", Font.BOLD, 22));
            styleFlatSolidButton(btn, PRIMARY_COLOR, Color.WHITE);
            btn.addActionListener(e -> {
                result[0] = opt;
                dialog.dispose();
            });
            grid.add(btn);
        }
        
        JButton btnCancel = new JButton("CANCEL");
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 22));
        styleFlatSolidButton(btnCancel, DANGER_COLOR, Color.WHITE);
        btnCancel.addActionListener(e -> dialog.dispose());
        
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottom.setBackground(BG_COLOR);
        bottom.add(btnCancel, BorderLayout.CENTER);
        
        dialog.add(grid, BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
        return result[0];
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
            if (selectedItem != null) addToCart(selectedItem);
        });
        return btn;
    }

    private void handleComboClick(int choiceCode) {
        String[] meats = {"Pork Adobo", "Beef Tapa", "Chicken Inasal", "Lechon Kawali", "Sisig"};
        String[] veggies = {"Pinakbet", "Ginisang Monggo", "Chopsuey", "Laing"};
        
        String finalName = "";
        double price = 0.0;

        switch (choiceCode) {
            case 1:
                String m1 = promptTouchSelection("Select your Meat:", meats);
                if (m1 == null) return;
                finalName = m1 + " + Rice"; price = 70.0;
                break;
            case 2:
                String mm1 = promptTouchSelection("Select FIRST Meat:", meats);
                if (mm1 == null) return;
                String mm2 = promptTouchSelection("Select SECOND Meat:", meats);
                if (mm2 == null) return;
                finalName = mm1 + " & " + mm2 + " + Rice"; price = 120.0;
                break;
            case 3:
                String v1 = promptTouchSelection("Select your Vegetable:", veggies);
                if (v1 == null) return;
                finalName = v1 + " + Rice"; price = 55.0;
                break;
            case 4:
                String mv1 = promptTouchSelection("Select your Meat:", meats);
                if (mv1 == null) return;
                String vv1 = promptTouchSelection("Select your Vegetable:", veggies);
                if (vv1 == null) return;
                finalName = mv1 + " & " + vv1 + " + Rice"; price = 90.0;
                break;
        }

        addToCart(new MenuItem(100 + choiceCode, finalName, "Rice Combo", price));
    }

    private void addToCart(MenuItem newItem) {
        for (OrderItem order : cart) {
            if (order.item.name.equals(newItem.name)) {
                order.quantity++; 
                refreshCartUI();
                updateTotals();
                return;
            }
        }
        cart.add(new OrderItem(newItem, 1));
        refreshCartUI();
        updateTotals();
    }

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

    private void refreshCartUI() {
        cartContainer.removeAll();
        
        for (OrderItem order : cart) {
            JPanel row = new JPanel(new BorderLayout(5, 5));
            row.setBackground(Color.WHITE);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
            row.setPreferredSize(new Dimension(0, 65)); 
            
            row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)), 
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            
            String infoHtml = "<html><b>" + order.item.name + "</b><br><span style='color:#e67e22; font-size:12px;'>₱" + String.format("%.2f", order.getSubtotal()) + "</span></html>";
            JLabel lblInfo = new JLabel(infoHtml);
            lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 16));
            
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actionPanel.setBackground(Color.WHITE);
            
            JButton btnMinus = new JButton("-");
            styleCartQtyButton(btnMinus, DANGER_COLOR);
            btnMinus.addActionListener(e -> {
                order.quantity--;
                if(order.quantity <= 0) cart.remove(order); 
                refreshCartUI();
                updateTotals();
            });
            
            JLabel lblQty = new JLabel(String.valueOf(order.quantity), SwingConstants.CENTER);
            lblQty.setFont(new Font("SansSerif", Font.BOLD, 18));
            lblQty.setPreferredSize(new Dimension(30, 30));
            
            JButton btnPlus = new JButton("+");
            styleCartQtyButton(btnPlus, SUCCESS_COLOR);
            btnPlus.addActionListener(e -> {
                order.quantity++;
                refreshCartUI();
                updateTotals();
            });
            
            actionPanel.add(btnMinus);
            actionPanel.add(lblQty);
            actionPanel.add(btnPlus);
            
            row.add(lblInfo, BorderLayout.CENTER);
            row.add(actionPanel, BorderLayout.EAST);
            
            cartContainer.add(row);
        }
        
        cartContainer.revalidate();
        cartContainer.repaint();
    }

    private double calculateSubtotal() {
        double subtotal = 0.0;
        for (OrderItem order : cart) subtotal += order.getSubtotal(); 
        return subtotal;
    }

    private double[] calculateFinals() {
        double subtotal = calculateSubtotal();
        double discount = 0.0;
        
        if (subtotal >= 500.0) discount = subtotal * 0.10;

        double finalTotal = subtotal - discount;
        return new double[]{subtotal, discount, finalTotal};
    }

    private void updateTotals() {
        double[] finals = calculateFinals();
        lblSubtotal.setText(String.format("Subtotal: ₱%.2f", finals[0]));
        lblDiscount.setText(String.format("Discount: -₱%.2f", finals[1]));
        lblTotal.setText(String.format("Total Due: ₱%.2f", finals[2]));
    }

    private void processCheckout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Order cart is empty!", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double[] finals = calculateFinals();
        double finalTotal = finals[2];
        
        // Formats the order number to always be 3 digits (e.g., 001, 015)
        String currentOrderNum = String.format("%03d", orderCounter++);

        StringBuilder receipt = new StringBuilder();
        receipt.append("========= ORDER TICKET =========\n");
        receipt.append("Order No: #").append(currentOrderNum).append("\n");
        receipt.append("Type: ").append(orderType).append("\n\n");
        
        for (OrderItem item : cart) {
            receipt.append(String.format("%s (x%d) - ₱%.2f\n", item.item.name, item.quantity, item.getSubtotal()));
        }
        
        receipt.append("\n--------------------------------\n");
        receipt.append(String.format("Subtotal: ₱%.2f\n", finals[0]));
        receipt.append(String.format("Discount: ₱%.2f\n", finals[1]));
        receipt.append(String.format("TOTAL DUE: ₱%.2f\n", finalTotal));
        receipt.append("================================\n\n");
        receipt.append("   Please present this ticket\n");
        receipt.append("   at the counter to pay.\n");

        JOptionPane.showMessageDialog(this, receipt.toString(), "Ticket Printed", JOptionPane.INFORMATION_MESSAGE);
        
        resetOrder();
        orderType = "";
        cardLayout.show(mainContainer, "WELCOME");
    }

    private void resetOrder() {
        cart.clear();
        refreshCartUI();
        updateTotals();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CafeteriaSystem().setVisible(true));
    }
}