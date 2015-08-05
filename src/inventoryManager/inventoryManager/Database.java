package inventoryManager;

/**
 * Created by jarrah on 3/08/15.
 */
public interface Database {
    String getDatabase();

    void deleteEntry(String barcode);

    String getEntryName(String barcode);

    String[] getNamesOfEntries();

    boolean entryExists(String name, String barcode);

    void writeDatabaseCSV(String path);

    Person readEntry(String barcode); // todo: generalise this.

    void readEntries();


}
