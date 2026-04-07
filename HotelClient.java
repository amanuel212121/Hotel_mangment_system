import java.rmi.Naming;
import java.util.Scanner;

public class HotelClient {
    private static Scanner sc = new Scanner(System.in);

    private static int getInt() {
        while (true) {
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (Exception e) { System.out.print("Enter a number: "); }
        }
    }

    public static void main(String[] args) {
        try {
            HotelInterface hotel = (HotelInterface) Naming.lookup("rmi://localhost/HotelService");
            
            while (true) {
                System.out.println("\n--- HOTEL MANAGEMENT SYSTEM ---");
                System.out.println("1. Guest Portal");
                System.out.println("2. Admin Dashboard"); // አሁን ይመጣል!
                System.out.println("3. Exit");
                System.out.print("Choice: ");
                int role = getInt();

                if (role == 3) break;

                if (role == 1) { // GUEST PORTAL
                    while (true) {
                        System.out.println("\n[GUEST MENU]\n1. View Rooms\n2. Pay (TX-ID)\n3. Finalize Booking\n4. Logout");
                        int c = getInt();
                        if (c == 1) System.out.println(hotel.viewRooms());
                        else if (c == 2) {
                            System.out.print("Room No: "); int r = getInt();
                            System.out.println(hotel.getPaymentInstructions(r));
                            System.out.print("Enter TX-ID: ");
                            System.out.println(hotel.sendTransactionId(r, sc.nextLine()));
                            System.out.println("⏳ Waiting for Admin Approval...");
                            while (!hotel.checkPaymentStatus(r)) { Thread.sleep(3000); }
                            System.out.println("✅ APPROVED! You can now finalize.");
                        } else if (c == 3) {
                            System.out.print("Room No: "); int r = getInt();
                            System.out.print("Your Name: ");
                            System.out.println(hotel.bookRoom(r, sc.nextLine()));
                        } else break;
                    }
                } 
                else if (role == 2) { // ADMIN DASHBOARD
                    System.out.print("Enter Admin Password: ");
                    if (sc.nextLine().equals("admin123")) {
                        while (true) {
                            System.out.println("\n[ADMIN MENU]\n1. Add Room\n2. Delete Room\n3. View All\n4. Logout");
                            int ac = getInt();
                            if (ac == 1) {
                                System.out.print("Room No: "); int r = getInt();
                                System.out.print("Type: "); String t = sc.nextLine();
                                System.out.print("Price: "); double p = Double.parseDouble(sc.nextLine());
                                System.out.println(hotel.addRoom(r, t, p));
                            } else if (ac == 2) {
                                System.out.print("Room No to Delete: ");
                                System.out.println(hotel.deleteRoom(getInt()));
                            } else if (ac == 3) {
                                System.out.println(hotel.viewRooms());
                            } else break;
                        }
                    } else { System.out.println("❌ Wrong Password!"); }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}