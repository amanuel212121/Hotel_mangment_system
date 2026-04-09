import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HotelInterface extends Remote {
    // Rooms
    String viewRooms() throws RemoteException;
    String addRoom(int roomNo, String type, double price) throws RemoteException;
    String deleteRoom(int roomNo) throws RemoteException;
    String approvePayment(int roomNo) throws RemoteException;
    
    // Guest Actions
    String sendTransactionId(int roomNo, String txId) throws RemoteException;
    String placeOrder(int roomNo, String items, double price) throws RemoteException;
    
    // Admin View
    String viewAllOrders() throws RemoteException;
}