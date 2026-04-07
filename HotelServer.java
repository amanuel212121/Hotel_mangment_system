import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class HotelServer extends UnicastRemoteObject implements HotelInterface {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String USER = "root"; 
    private static final String PASS = ""; 

    protected HotelServer() throws RemoteException { super(); }

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
        return conn;
    }

    public String approvePayment(int roomNo) throws RemoteException {
        try (Connection conn = getConnection()) {
            // ዳታቤዙ ላይ is_approved ን 1 (True) ለማድረግ
            String sql = "UPDATE rooms SET is_approved = 1 WHERE room_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, roomNo);
            int rows = ps.executeUpdate();
            
            if (rows > 0) {
                System.out.println("✅ Room " + roomNo + " has been APPROVED in Database.");
                return "SUCCESS: Room " + roomNo + " is now APPROVED.";
            } else {
                return "FAIL: Room " + roomNo + " not found.";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public boolean checkPaymentStatus(int roomNo) throws RemoteException {
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT is_approved FROM rooms WHERE room_no = ?");
            ps.setInt(1, roomNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getBoolean("is_approved");
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public String viewRooms() throws RemoteException {
        StringBuilder sb = new StringBuilder("\n--- ROOM STATUS ---\n");
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM rooms");
            while (rs.next()) {
                sb.append("Room ").append(rs.getInt("room_no"))
                  .append(" | ").append(rs.getString("status"))
                  .append(" | Paid: ").append(rs.getBoolean("is_approved") ? "YES" : "NO").append("\n");
            }
        } catch (Exception e) { return "Error: " + e.getMessage(); }
        return sb.toString();
    }

    public String sendTransactionId(int roomNo, String txId) throws RemoteException {
        System.out.println("\n⚠️ [NEW PAYMENT] Room: " + roomNo + " | TX-ID: " + txId);
        System.out.print("Type Room No to Approve: ");
        return "SUCCESS: Admin notified.";
    }

    public String bookRoom(int r, String n) throws RemoteException {
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE rooms SET status='Booked', guest_name=?, is_approved=0 WHERE room_no=? AND is_approved=1");
            ps.setString(1, n); ps.setInt(2, r);
            if (ps.executeUpdate() > 0) return "SUCCESS: Booking complete!";
            return "FAIL: Payment not approved yet.";
        } catch (Exception e) { return "Error: " + e.getMessage(); }
    }

    public String addRoom(int r, String t, double p) throws RemoteException {
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO rooms (room_no, type, price, status, is_approved) VALUES (?,?,?, 'Available', 0)");
            ps.setInt(1, r); ps.setString(2, t); ps.setDouble(3, p);
            ps.executeUpdate(); return "SUCCESS: Room added.";
        } catch (Exception e) { return "Error: " + e.getMessage(); }
    }

    public String deleteRoom(int r) throws RemoteException {
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM rooms WHERE room_no = ?");
            ps.setInt(1, r); ps.executeUpdate(); return "SUCCESS: Deleted.";
        } catch (Exception e) { return "Error: " + e.getMessage(); }
    }

    public String getPaymentInstructions(int r) throws RemoteException { return "CBE Acc: 1000123456789"; }

    public static void main(String[] args) {
        try {
            HotelServer s = new HotelServer();
            try { LocateRegistry.createRegistry(1099); } catch (Exception e) {}
            Naming.rebind("HotelService", s);
            System.out.println("Server is running...");
            Scanner sc = new Scanner(System.in);
            while (true) {
                if (sc.hasNextInt()) {
                    System.out.println(s.approvePayment(sc.nextInt()));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}