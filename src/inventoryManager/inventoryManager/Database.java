package inventoryManager;

import java.util.ArrayList;

/**
 * Created by jarrah on 3/08/15.
 */
interface Database {
    ArrayList<String> getDatabase();

    void deleteEntry(String barcode);

    String getEntryName(String barcode);

    ArrayList<String> getNamesOfEntries();

    boolean entryExists(String barcode);

    void writeDatabaseCSV(String path);




}
