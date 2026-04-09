import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

public class HotelServer extends UnicastRemoteObject implements HotelInterface {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String USER = "root"; 
    private static final String PASS = ""; 

    protected HotelServer() throws RemoteException { super(); }

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
        conn.setAutoCommit(true); 
        return conn;
    }

    // --- አውቶማቲክ የክፍል ሁኔታ መቀየር (Advanced Status) ---
    public String approvePayment(int roomNo) throws RemoteException {
        try (Connection conn = getConnection()) {
            // ክፍያ ሲጸድቅ ሁኔታው ወደ 'Occupied' እንዲቀየር
            PreparedStatement ps = conn.prepareStatement("UPDATE rooms SET status = 'Occupied' WHERE room_no = ?");
            ps.setInt(1, roomNo);
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Room " + roomNo + " is now Occupied.");
                return "✅ ክፍያ ጸድቋል! ክፍሉ አሁን ተይዟል (Occupied)።";
            }
            return "❌ ክፍሉ አልተገኘም።";
        } catch (Exception e) { return "❌ Error: " + e.getMessage(); }
    }

    public String addRoom(int roomNo, String type, double price) throws RemoteException {
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO rooms (room_no, type, price, status) VALUES (?, ?, ?, 'Available')");
            ps.setInt(1, roomNo); ps.setString(2, type); ps.setDouble(3, price);
            ps.executeUpdate();
            return "✅ Room " + roomNo + " added successfully.";
        } catch (Exception e) { return "❌ Error: " + e.getMessage(); }
    }

    public String viewRooms() throws RemoteException {
        StringBuilder sb = new StringBuilder("\n--- የክፍሎች ዝርዝር ---\n");
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM rooms");
            while (rs.next()) {
                sb.append("ቁጥር: ").append(rs.getInt("room_no")).append(" | ").append(rs.getString("type"))
                  .append(" | ").append(rs.getDouble("price")).append(" ETB | ሁኔታ: ").append(rs.getString("status")).append("\n");
            }
        } catch (Exception e) { return "❌ DB Error."; }
        return sb.toString();
    }

    // ሌሎች ሜተዶች (deleteRoom, sendTransactionId, placeOrder...) እንዳሉ ይቀጥላሉ
    public String deleteRoom(int r) throws RemoteException { return "Deleted"; }
    public String sendTransactionId(int r, String t) throws RemoteException { return "TX Sent"; }
    public String placeOrder(int r, String i, double p) throws RemoteException { return "Order Sent"; }
    public String viewAllOrders() throws RemoteException { return "Orders List"; }

    public static void main(String[] args) {
        try {
            try { LocateRegistry.createRegistry(1099); } catch (Exception e) {}
            Naming.rebind("HotelService", new HotelServer());
            System.out.println("✅ ሰርቨሩ በላቀ ሁኔታ ስራ ጀምሯል...");
        } catch (Exception e) { e.printStackTrace(); }
    }
}