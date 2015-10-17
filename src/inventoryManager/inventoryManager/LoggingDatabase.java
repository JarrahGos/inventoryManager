package inventoryManager;

import java.util.ArrayList;

/**
 * Created by jarrah on 4/09/15.
 */

public class LoggingDatabase {
    /**
     * Stores the path of the database as a string, based on the OS being run.
     */
    private static SQLInterface db = new SQLInterface();

    public static void appendPasswordLog(String barcode, String adBarcode) {
        db.addLog(barcode, adBarcode);
    }

    public ArrayList<String> getOutItems() {
        return db.getOutItemsLog();
    }
    public void signItemsIn(ArrayList<String> items, String persID) {
        for(String item : items) {
            db.returnItem(item, persID);
        }
    }
}
