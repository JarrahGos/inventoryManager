package inventoryManager;

import java.util.ArrayList;

interface Database {
    ArrayList<String> getDatabase();

    void deleteEntry(String barcode);

    String getEntryName(String barcode);

    ArrayList<String> getNamesOfEntries();

    boolean entryExists(String barcode);

    void writeDatabaseCSV(String path);

}
