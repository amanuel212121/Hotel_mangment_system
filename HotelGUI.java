import javax.swing.*;
import java.awt.*;
import java.rmi.Naming;

public class HotelGUI {
    private HotelInterface hotel;
    private JFrame frame;
    private JTextArea displayArea;

    public HotelGUI() {
        try {
            hotel = (HotelInterface) Naming.lookup("rmi://localhost/HotelService");
            prepareGUI();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "❌ Connection Error: Start HotelServer first!");
            System.exit(0);
        }
    }

    private void prepareGUI() {
        frame = new JFrame("Hotel Management System - Full Dashboard");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // መረጃ ማሳያ ቦታ
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        displayArea.setBackground(new Color(245, 245, 245));
        frame.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // በተኖቹን የምናስቀምጥበት ቦታ
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 4, 10, 10)); // 2 ረድፍ እና 4 ኮለምን
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. View Rooms
        JButton btnView = new JButton("View Rooms");
        btnView.addActionListener(e -> {
            try { displayArea.setText(hotel.viewRooms()); } catch (Exception ex) { err(); }
        });

        // 2. Add Room
        JButton btnAdd = new JButton("Add Room");
        btnAdd.addActionListener(e -> {
            try {
                int r = Integer.parseInt(JOptionPane.showInputDialog("Room Number:"));
                String t = JOptionPane.showInputDialog("Type (VIP/Standard/Suite):");
                double p = Double.parseDouble(JOptionPane.showInputDialog("Price:"));
                displayArea.setText(hotel.addRoom(r, t, p));
            } catch (Exception ex) { validErr(); }
        });

        // 3. Delete Room
        JButton btnDelete = new JButton("Delete Room");
        btnDelete.addActionListener(e -> {
            try {
                int r = Integer.parseInt(JOptionPane.showInputDialog("Enter Room No to DELETE:"));
                displayArea.setText(hotel.deleteRoom(r));
            } catch (Exception ex) { validErr(); }
        });

        // 4. Approve Payment
        JButton btnApprove = new JButton("Approve Payment");
        btnApprove.addActionListener(e -> {
            try {
                int r = Integer.parseInt(JOptionPane.showInputDialog("Enter Room No to APPROVE:"));
                displayArea.setText(hotel.approvePayment(r));
            } catch (Exception ex) { validErr(); }
        });

        // 5. Place Order (Food)
        JButton btnOrder = new JButton("Place Order");
        btnOrder.addActionListener(e -> {
            try {
                int r = Integer.parseInt(JOptionPane.showInputDialog("Room Number:"));
                String item = JOptionPane.showInputDialog("Item Name:");
                double p = Double.parseDouble(JOptionPane.showInputDialog("Total Price:"));
                displayArea.setText(hotel.placeOrder(r, item, p));
            } catch (Exception ex) { validErr(); }
        });

        // 6. View All Orders
        JButton btnViewOrders = new JButton("View All Orders");
        btnViewOrders.addActionListener(e -> {
            try { displayArea.setText(hotel.viewAllOrders()); } catch (Exception ex) { err(); }
        });

        // 7. Send TX-ID
        JButton btnTxId = new JButton("Send TX-ID");
        btnTxId.addActionListener(e -> {
            try {
                int r = Integer.parseInt(JOptionPane.showInputDialog("Room Number:"));
                String tx = JOptionPane.showInputDialog("Enter Transaction ID:");
                displayArea.setText(hotel.sendTransactionId(r, tx));
            } catch (Exception ex) { validErr(); }
        });

        // 8. Clear Screen
        JButton btnClear = new JButton("Clear Screen");
        btnClear.addActionListener(e -> displayArea.setText(""));

        // በተኖቹን ወደ ፓነሉ መጨመር
        mainPanel.add(btnView);
        mainPanel.add(btnAdd);
        mainPanel.add(btnDelete);
        mainPanel.add(btnApprove);
        mainPanel.add(btnOrder);
        mainPanel.add(btnViewOrders);
        mainPanel.add(btnTxId);
        mainPanel.add(btnClear);

        frame.add(mainPanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null); // መሃል ላይ እንዲከፈት
        frame.setVisible(true);
    }

    private void err() { JOptionPane.showMessageDialog(frame, "❌ Connection Error!"); }
    private void validErr() { JOptionPane.showMessageDialog(frame, "⚠️ Please enter valid numeric data!"); }

    public static void main(String[] args) {
        new HotelGUI();
    }
}