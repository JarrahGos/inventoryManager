package inventoryManager;

import inventoryManager.formatters.ItemLog;
import inventoryManager.formatters.PasswordLog;
import inventoryManager.formatters.ReturnItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
/***
 * Copyright (C) 2015  Jarrah Gosbell
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


public class LoggingDatabase implements Database {
    /**
     * Stores the path of the database as a string, based on the OS being run.
     */

    public static void appendPasswordLog(String barcode, String adBarcode) {
        SQLInterface.addLog(barcode, adBarcode);
    }

    public static ArrayList<PasswordLog> getPasswordLog() {
        return SQLInterface.getPasswordLog();
    }

    public static ArrayList<PasswordLog> getPasswordLog(LocalDate from, LocalDate to) {
        return SQLInterface.getPasswordLog(from, to);
    }

    public static ArrayList<ItemLog> getItemLog(boolean outOnly, LocalDate from, LocalDate to) {
        return SQLInterface.getItemLog(outOnly, from, to);
    }

    public static ArrayList<ItemLog> getItemLog(boolean outOnly) {
        return SQLInterface.getItemLog(outOnly);
    }

    /**
     * Log a single item out of the database.
     *
     * @param ID     The ID of the item to log out.
     * @param persID The ID of the person to log the item out to.
     */
    public static void logItemOut(String ID, String persID) {
        SQLInterface.addLog(ID, persID, ItemDatabase.isControlled(ID));
    }

    /**
     * Log a linked list of items out of the database to a given user.
     *
     * @param IDs    A linked list of IDs to log out in the database.
     * @param persID The ID of the person to log the items to.
     */
    public static void logItemsOut(LinkedList<String> IDs, String persID) {
        for (String ID : IDs) {
            logItemOut(ID, persID);
        }
    }

    public ArrayList<inventoryManager.formatters.ReturnItem> getOutItems() {
        return SQLInterface.getOutItemsLog();
    }

    public ArrayList<String> getOutItemIDs() {
        return SQLInterface.getOutItemsLog(SQLInterface.COLITEMLOGITEMID);
    }

    public ArrayList<String> getOutItemPersIDs() {
        return SQLInterface.getOutItemsLog(SQLInterface.COLITEMLOGPERSID);
    }

    public void signItemsIn(ArrayList<ReturnItem> items, String adminName) {
        for (ReturnItem item : items) {
            SQLInterface.returnItem(item.getID(), item.getUserID(), item.getDate(), adminName);
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
