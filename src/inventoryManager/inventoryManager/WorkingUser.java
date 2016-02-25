package inventoryManager;

/*
*    Copyright (C) 2015  Jarrah Gosbell
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;


/**
 * @author jarrah
 */
public final class WorkingUser {

    /**
     * The currently logged in user
     */
    private static String userID = null;
    private static String userName = null;
    /**
     * The database which stores all products used by the system.
     */
    private static ItemDatabase itemDatabase = new ItemDatabase();
    /**
     * The database which stores all people who can use the system
     */
    private static PersonDatabase personDatabase = new PersonDatabase();
    /**
     * the checkout used to store what a person is purchasing at a given time
     */
    private static CheckOut checkOuts = new CheckOut();
    /**
     * The database used for logging changes and transactions
     */
    private static LoggingDatabase loggingDatabase = new LoggingDatabase();

    /**
     * Create the working user instance with both databases and a checkout.
     */
    private WorkingUser() {
        itemDatabase = new ItemDatabase();
        personDatabase = new PersonDatabase();
        checkOuts = new CheckOut();
        loggingDatabase = new LoggingDatabase();
        userID = null;
        userName = null;
    }


    /**
     * Get a hashed password ready for storage or comparison.
     *
     * @param password The password to hash
     * @param NaCl     The salt to use in the hashing process
     * @return A string array with the password in the 0 position and the salt in the 1 position
     */
    private static String[] getSecurePassword(String password, String NaCl)
    {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        System.out.println("-----------------------------------------");
        System.out.println(NaCl);
        byte[] salt = NaCl.getBytes();


        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf;
        byte[] hash = null;
        try {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            hash = skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        String[] ret = new String[0];
        try {
            ret = new String[]{(new String(hash, "UTF-8")), (new String(salt, "UTF-8"))};
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Create a random salt for use in generating hashes
     *
     * @return A strting of UTF-8 encoded random bytes
     * @throws NoSuchAlgorithmException
     */
    private static String getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        String ret = null;
        try {
            ret = new String(salt, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.print(e);
        }
        return ret;
    }

    /**
     * Take a cleartext password and hash it ready for either checking or storage
     * @param password The clear text password
     * @return The hash of the given password.
     */
//    public final String getSecurePassword(String passwordToHash) {
//        String generatedPassword = null;
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-1");
//            byte[] bytes = md.digest(passwordToHash.getBytes());
//            StringBuilder sb = new StringBuilder();
//            for (byte aByte : bytes) {
//                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
//            }
//            generatedPassword = sb.toString();
//        } catch (NoSuchAlgorithmException e) {
//            inventoryManager.Log.print(e);
//        }
//        return generatedPassword;
//    }


    /**
     * Take the given PMKeyS and find the user which correlates with it. Then authenticate the user with the given password.
     *
     * @param ID   The ID that you wish to search for as a string
     * @param pass The password of the ID.
     * @return 0 if the user found, 1 if the user dose not exist, 2 if the user cannot buy.
     */
    public static int getbarcode(String ID, String pass) {
        //if ((input != null && !input.equals("")) && (!input.matches("[0-9]+"))) {
        //    input = input.substring(1);
        //}
        if (ID == null || ID.equals("") || !personDatabase.entryExists(ID)) { // checks for valid numbers in the PMKeyS
            userName = null;
            userID = null;
            return 1;
        } else {
            if (passwordsEqual(ID, pass)) {
                userName = personDatabase.getEntryName(ID).get();
                userID = ID;
            } else {
                return 1;
            }
        }
        return 0;
    }

    public static Optional<String> getPersonID(String name) {
        return personDatabase.getEntryID(name);
    }

    /**
     * Get a list of the names of all users in the database
     *
     * @return A string array of the usernames
     */
    public static ArrayList<String> getUserNames() {
        return personDatabase.getNamesOfEntries();
    }

    public static ArrayList<Person> getUserDetails() {
        return personDatabase.getEntryDetails();
    }
    /**
     * Get a list of the names of all products in the database
     *
     * @return A string array of the product names
     */
    public static ArrayList<String> getProductNames(String type) {
        return itemDatabase.getNamesOfEntries(type);
    }

    /**
     * Test whether a cleartext password is equal to the stored admin password
     *
     * @param PW A cleartext password to test
     * @return The boolean test for whether the passwords are equal.
     */
    public static boolean passwordsEqual(String barcode, String PW) {
        String[] old = personDatabase.getPassword(barcode);
        if (old[0] == null || old[1] == null) return false;
        System.out.println("Hash Old: " + old[0] +
                "\nSalt Old: " + old[1]);
        String[] testing;
        testing = getSecurePassword(PW, old[1]); //get secure password from new password and old salt
        System.out.println("Hash New: " + testing[0]);
        System.out.println("Salt New:" + testing[1]);
        return (testing[0].equals(old[0]));
    }

    /**
     * Takes the given (prehashed) password and set it as the admin password
     *
     * @param PW The prehashed password to be set
     */
    public static int setPassword(String barcode, String PW, String adBarcode, String adPass) {
        if (isUserAdmin(adBarcode) && passwordsEqual(adBarcode, adPass)) {
            if (personDatabase.entryExists(barcode)) {
                String[] pass;
                try {
                    pass = getSecurePassword(PW, getSalt());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return -1;
                }
                personDatabase.setPassword(barcode, pass[0], pass[1]);
                LoggingDatabase.appendPasswordLog(barcode, adBarcode);
                return 0;
            }
            return 1;
        }
        return 2;
    }

    public static boolean isUserAdmin(String barcode) {
        return personDatabase.getRole(barcode) > PersonDatabase.USER;
    }

    /**
     * Create a scroll pane of one of the databases
     *
     * @param type A string of either SQLInterface.TABITEM or SQLInterface.TABPERSON used to determine which database to print.
     * @return A scroll pane showing the database chosen in the parameter or the person database by default.
     * @throws IOException
     */
    public static ScrollPane printDatabase(String type) throws IOException {
        TextArea textArea;
        switch (type) {
            case (SQLInterface.TABITEM):
                textArea = new TextArea(itemDatabase.getDatabase().toString());
                break;
            case (SQLInterface.TABPERSON):
                textArea = new TextArea(personDatabase.getDatabase().toString());
                break;
            default:
                textArea = new TextArea(personDatabase.getDatabase().toString());
                break;
        }
        textArea.setEditable(false); // stop the user being able to edit this and thinking it will save.
        ScrollPane scrollPane = new ScrollPane(textArea);
        textArea.setWrapText(true);
        scrollPane.setHvalue(600);
        scrollPane.setVvalue(800);
        return scrollPane;

    }

    /**
     * Log the user out from this class
     */
    public static void logOut() {
        userName = null;
        userID = null;
        checkOuts = new CheckOut();
    }

    /**
     * Have the connected user buy the products in the checkout, adding the total cost to the users bill,
     * taking the number bought from the products in the database and clearing both the user and the checkout
     */
    public static void checkOutItems() {
        LinkedList<String> purchased = checkOuts.productBought(); // clear the quantities and checkout
        LoggingDatabase.logItemsOut(purchased, userID);
        checkOuts = new CheckOut(); // ensure checkout clear
        userName = null;
        userID = null;
    }

    /**
     * Get the names of all products in the checkout
     *
     * @return A string array of the names of all products in the checkout
     */
    public static LinkedList<String> getCheckOutNames() {
        return checkOuts.getCheckOutNames();
    }

    /**
     * Takes the error value given by getPMKeyS and uses it to give the username or an error message
     *
     * @param userError 0 if the user was correctly found, 1 if the user was not found and 2 if the user has been locked out.
     * @return The appropriate error message or the users name
     */
    public static String userName(int userError) {
        switch (userError) {
            case 0:
                if (userName != null) return userName;
            case 1:
                return "User not found";
        }
        return (userID == null && userName != null) ? "Error" : userName;
    }

    public static String getUserID() {
        return userID;
    }

    public static ArrayList<inventoryManager.formatters.ReturnItem> getOutItems() {
        return loggingDatabase.getOutItems();
    }

    public static ArrayList<String> getOutItemIDs() {
        return loggingDatabase.getOutItemIDs();
    }

    public static ArrayList<String> getOutItemPersIDs() {
        return loggingDatabase.getOutItemPersIDs();
    }

    /**
     * Takes the barcode for a product and adds it to the checkout
     *
     * @param input The barcode for the product as a string
     * @return True if the product was added, false if it failed
     */
    public static boolean addToCart(String input) {
        String tempBarCode = "-1";
        if (input != null && !input.equals("")) {
            tempBarCode = input; // disallows the user from entering nothing or clicking cancel.
        } else if ((input == null) || ("".equals(input))) {
            return false;
        }
        String adding = itemDatabase.getEntryName(tempBarCode).orElse("ERROR");
        if (adding != null && !adding.equals("ERROR")) {
            System.out.println(tempBarCode + "\n" + adding);
            checkOuts.addProduct(tempBarCode, adding); //otherwise, add the product as normal.
            return true;
        }
        return false;
    }

    /**
     * Get the permission role of the user which is currently logged in.
     *
     * @return The role of the user. 0 for user, 2 for admin, 3 for root (full access)
     */
    public static int getRole() {
        if (userID == null || userName == null) return 0;
        return personDatabase.getRole(userID);
    }

    /**
     * Add a person to the database, given their name and PMKeyS
     *
     * @param name The name of the person you wish to add
     * @param ID   The PMKeyS of the person you wish to add
     */
    public static void addPersonToDatabase(String name, String ID, String password) {
        String[] passwd = new String[0];
        try {
            passwd = getSecurePassword(password, getSalt());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        personDatabase.setEntry(ID, name, PersonDatabase.USER, passwd[0], passwd[1]);
    }

    /**
     * Add barcode product to the database given their name, barcode and price
     *
     * @param name    The name of the product you wish to add
     * @param barcode The barcode for the product you wish to add.
     */
    public static void addItemToDatabase(String barcode, String name) {
        itemDatabase.addEntry(barcode, name);
    } //TODO: make this work for general and controlled items

    public static void addItemToDatabase(String name, String barcode, String description, long quantity, String location, String setName) {
        itemDatabase.addEntry(barcode, name, setName, description, quantity, location);
    }

    public static void addItemToDatabase(String name, String barcode, String type, String tagno, String set, String state) {
        itemDatabase.addEntry(barcode, name, set, state, tagno, type);
    }
    /**
     * Alter a product in the database
     *
     * @param name       The new name of the product
     * @param oldName    The old name of the product
     * @param barcode    The new barcode of the product
     * @param oldBarcode The old barcode of the product
     */
    public static void changeDatabaseProduct(String name, String oldName, String barcode, String oldBarcode) {
        itemDatabase.changeItem(oldName, barcode, oldBarcode);
    }

    /**
     * Alter a product in the database
     * @param name          The new name of the person
     * @param ID            The new ID of the person
     * @param oldID         The old ID of the person
     */
    public static void changeDatabasePerson(String name, String ID, String oldID) {
        personDatabase.changeDatabasePerson(name, ID, oldID);
    }

    /**
     * Write out the CSV version of the database for the admin.
     *
     * @param type SQLInterface.TABPERSON for the person database or "Produt" for the product database
     */
    public static void adminWriteOutDatabase(String type) {
        switch (type) {
            case (SQLInterface.TABPERSON):
                personDatabase.writeDatabaseCSV("adminPersonDatabase.csv");
                break;
            case (SQLInterface.TABITEM):
                itemDatabase.writeDatabaseCSV(type, "adminItemDatabase.csv");
                break;
            case (SQLInterface.TABGENERAL):
                itemDatabase.writeDatabaseCSV(type, "adminGeneralDatabase.csv");
                break;
            case (SQLInterface.TABCONTROLLED):
                itemDatabase.writeDatabaseCSV(type, "adminControlledDatabase.csv");
                break;
            default:
                personDatabase.writeDatabaseCSV("adminItemDatabase.csv");
        }
    }

    /**
     * Write the given type of database out to a CSV file at the given location.
     *
     * @param type The type of database to write. Person or Item (general or controlled)
     * @param path The location which the file will be stored at
     */
    public static void adminWriteOutDatabase(String type, String path) {
        switch (type) {
            case (SQLInterface.TABPERSON):
                personDatabase.writeDatabaseCSV(path);
                break;
            default:
                itemDatabase.writeDatabaseCSV(type, path);
        }
    }

    /**
     * Delete the specified person
     *
     * @param ID The PMKeyS or name of the person as a string
     * @throws IOException
     * @throws InterruptedException
     */
    public static void removePerson(String ID, String rootID, String rootPasswd) throws IOException, InterruptedException {
        if (passwordsEqual(rootID, rootPasswd)) {
            personDatabase.deleteEntry(ID);
        }
    }

    /**
     * Remove the specified product
     *
     * @param ID The barcode or name of the product to be removed
     * @throws IOException
     * @throws InterruptedException
     */
    public static void removeItem(String ID) throws IOException, InterruptedException {
        itemDatabase.deleteEntry(ID);
    }

    /**
     * Delete an item, requires root user/password
     *
     * @param ID         The ID of the item to delete
     * @param rootID     The ID of the root user attempting to delete the item.
     * @param rootPassWd The password of the root user attempting to delete the item.
     */
    public static void removeItem(String ID, String rootID, String rootPassWd) {
        if (passwordsEqual(rootID, rootPassWd)) {
            itemDatabase.deleteEntry(ID);
        }
    }

    /**
     * Delete a product from the checkout given it's barcode in the checkout array
     *
     * @param barcode The index of the item to delete in the checkout array
     */
    public static void deleteProduct(int barcode) {
        checkOuts.delItem(barcode);
    }

    /**
     * Get the barcode of a product given it's name
     *
     * @param name The name of the product to get the barcode of
     * @return The barcode of the product with the name specified.
     */
    public static String getProductBarCode(String name) {
        return itemDatabase.getBarcode(name).orElse("Item Not Found");
    }

    /**
     * Get the name of a product given it's barcode
     *
     * @param barcode The barcode of the product as a string
     * @return The name of the product with the given barcode
     */
    public static String getProductName(String barcode) {
        return itemDatabase.getEntryName(barcode).orElse("ERROR");
    }

    /**
     * Get the number of a product left in stock
     *
     * @param ID The name of the product you wish to check stock count.
     * @return The number of the specified product in stock
     */
    public static int getProductNumber(String ID) {
        return itemDatabase.getNumber(ID);
    }

    /**
     * set the number of a product in stock
     *
     * @param ID               The name of the product you wish to set the stock count for
     * @param numberOfProducts The new stock count.
     */
    public static void setNumberOfProducts(String ID, int numberOfProducts) {
        itemDatabase.setNumber(ID, numberOfProducts);
    }

    /**
     * Determine whether there is a user logged in
     *
     * @return The boolean value of whether the user is logged in.
     */
    public static boolean userLoggedIn() {
        return (userID != null && userName != null);
    }

    /**
     * Determine whether a person personExists in the database.
     *
     * @param ID The ID of the person to check for.
     * @return Boolean of does the member exist in the database.
     */
    public static boolean personExists(String ID) {
        return personDatabase.entryExists(ID);
    }

    public static LinkedList<String> getInItems() {
        return checkOuts.getCheckOutNames();
    }

    public static String getItemName(String barcode) {
        return itemDatabase.getEntryName(barcode).orElse("ERROR");
    }

    public static void signItemsIn(ArrayList<String> items) {
        loggingDatabase.signItemsIn(items, userID);
    }

    public static boolean itemExists(String barcode) {
        return itemDatabase.entryExists(barcode);
    }

    public static void updateRole(String ID, int role) {
        personDatabase.updateRole(ID, role);
    }

    public static ArrayList<PasswordLog> getPasswordLog() {
        return LoggingDatabase.getPasswordLog();
    }

    public static ArrayList<PasswordLog> getPasswordLog(LocalDate from, LocalDate to) {
        return LoggingDatabase.getPasswordLog(from, to);
    }

    public static ArrayList<ItemLog> getItemLog(boolean outOnly, LocalDate from, LocalDate to) {
        return LoggingDatabase.getItemLog(outOnly, from, to);
    }

    public static ArrayList<ItemLog> getItemLog(boolean outOnly) {
        return LoggingDatabase.getItemLog(outOnly);
    }

    public static ArrayList<String> getSets() {
        return itemDatabase.getSets();
    }

    public static ArrayList<String> getTypes() {
        return itemDatabase.getTypes();
    }

    public static void addSet(String name, String barcode) {
        itemDatabase.addSet(barcode, name);
    }
}
