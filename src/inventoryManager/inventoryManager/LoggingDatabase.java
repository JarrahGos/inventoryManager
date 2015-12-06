package inventoryManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by jarrah on 4/09/15.
 */

public class LoggingDatabase implements Database {
    /**
     * Stores the path of the database as a string, based on the OS being run.
     */

    public static void appendPasswordLog(String barcode, String adBarcode) {
        SQLInterface.addLog(barcode, adBarcode);
    }

    public static ArrayList<String> getPasswordLog() {
        return SQLInterface.getLog(SQLInterface.TABPERSONLOG);
    }

    public static ArrayList<String> getPasswordLog(LocalDate date) {
        return SQLInterface.getLog(SQLInterface.TABPERSONLOG, date);
    }

    public static ArrayList<String> getItemLog() {
        return SQLInterface.getLog(SQLInterface.TABITEMLOG);
    }

    public ArrayList<String> getOutItems() {
        return SQLInterface.getOutItemsLog();
    }

    public void signItemsIn(ArrayList<String> items, String persID) {
        for(String item : items) {
            SQLInterface.returnItem(item, persID);
        }
    }

    @Override
    public ArrayList<String> getDatabase() {
        return null;
    }

    @Override
    @Deprecated
    public void deleteEntry(String barcode) {
        return;
    }

    public void deleteEntry(String type, String barcode) {
        SQLInterface.deleteEntry(type, barcode);
    }

    @Override
    public Optional<String> getEntryName(String barcode) {
        return Optional.empty();
    }

    @Override
    public ArrayList<String> getNamesOfEntries() {
        return SQLInterface.getName(SQLInterface.TABITEMLOG);
    }

    @Override
    @Deprecated
    public boolean entryExists(String barcode) {
        return false;
    }

    public boolean entryExists(String barcode, String type) {
        return SQLInterface.entryExists(type, barcode);
    }

    @Override
    public void writeDatabaseCSV(String path) {

    }
}
