import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HotelInterface extends Remote {
    String viewRooms() throws RemoteException;
    String getPaymentInstructions(int roomNo) throws RemoteException;
    String sendTransactionId(int roomNo, String txId) throws RemoteException;
    String bookRoom(int roomNo, String guestName) throws RemoteException;
    boolean checkPaymentStatus(int roomNo) throws RemoteException;
    String approvePayment(int roomNo) throws RemoteException;
    String addRoom(int roomNo, String type, double price) throws RemoteException;
    String deleteRoom(int roomNo) throws RemoteException;
}