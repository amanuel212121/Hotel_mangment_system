import java.rmi.Naming;
import java.util.Scanner;

public class HotelClient {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            HotelInterface hotel = (HotelInterface) Naming.lookup("rmi://localhost/HotelService");
            while (true) {
                try {
                    System.out.println("\n1. Guest Portal\n2. Admin Dashboard\n3. Exit");
                    System.out.print("ምርጫዎ: ");
                    String choice = sc.nextLine();

                    if (choice.equals("1")) guestPortal(hotel);
                    else if (choice.equals("2")) adminPortal(hotel);
                    else if (choice.equals("3")) break;
                } catch (Exception e) {
                    System.out.println("⚠️ ስህተት፡ እባክዎ ትክክለኛ መረጃ ያስገቡ!");
                }
            }
        } catch (Exception e) { System.out.println("❌ ሰርቨሩ አልተገኘም!"); }
    }

    private static void adminPortal(HotelInterface hotel) throws Exception {
        System.out.print("ፓስወርድ: ");
        if (!sc.nextLine().equals("admin123")) { System.out.println("❌ የተሳሳተ ፓስወርድ!"); return; }
        
        while (true) {
            System.out.println("\n--- ADMIN ---\n1. View\n2. Add Room\n3. Approve Payment\n4. Logout");
            String ac = sc.nextLine();
            
            try {
                if (ac.equals("1")) System.out.println(hotel.viewRooms());
                else if (ac.equals("2")) {
                    System.out.print("የክፍል ቁጥር: "); int r = Integer.parseInt(sc.nextLine());
                    System.out.print("አይነት: "); String t = sc.nextLine();
                    System.out.print("ዋጋ: "); double p = Double.parseDouble(sc.nextLine());
                    System.out.println(hotel.addRoom(r, t, p));
                }
                else if (ac.equals("3")) {
                    System.out.print("የክፍል ቁጥር: "); int r = Integer.parseInt(sc.nextLine());
                    System.out.println(hotel.approvePayment(r));
                }
                else if (ac.equals("4")) break;
            } catch (NumberFormatException e) {
                System.out.println("⚠️ ስህተት፡ ቁጥር መግባት ያለበት ቦታ ላይ ፊደል ተጠቅመዋል!");
            }
        }
    }

    private static void guestPortal(HotelInterface hotel) throws Exception {
        // ... (እዚህም ላይ እንደ አስፈላጊነቱ Try-Catch መጨመር ይቻላል)
        System.out.println(hotel.viewRooms());
    }
}