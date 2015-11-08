package inventoryManager;

import java.util.ArrayList;
import java.util.Optional;

interface Database {
    ArrayList<String> getDatabase();

    void deleteEntry(String barcode);

    Optional<String> getEntryName(String barcode);

    ArrayList<String> getNamesOfEntries();

    boolean entryExists(String barcode);

    void writeDatabaseCSV(String path);

}
