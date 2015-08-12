package inventoryManager;


import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.LinkedList;

/*
*    TOC19 is a simple program to run TOC payments within a small group.
*    Copyright (C) 2014  Jarrah Gosbell
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

/**
 *
 * @author jarrah
 */
class WorkingUser {

    /** The database which stores all products used by the system. */
    private final ItemDatabase itemDatabase;
	private final GeneralDatabase gd;
	private final ControlledDatabase cd;
    /** The database which stores all people who can use the system */
    private final PersonDatabase personDatabase;
    /** the checkout used to store what a person is purchasing at a given time */
    private CheckOut checkOuts;
    /** The currently logged in user */
    private static String userID;
    private static String userName;

    /**
     * Create the working user instance with both databases and a checkout.
     */
    public WorkingUser() {
        itemDatabase = new ItemDatabase();
		gd = new GeneralDatabase();
		cd = new ControlledDatabase();
        personDatabase = new PersonDatabase();
        checkOuts = new CheckOut();
        userID = null;
        userName = null;
    }

    /**
     * Take the given PMKeyS and find the user which correlates with it.
     * @param input The PMKeyS that you wish to search for as a string
     * @return 0 if the user found, 1 if the user dose not exist, 2 if the user cannot buy.
     */
    public final int getPMKeyS(String input, String barcode) {
        //if ((input != null && !input.equals("")) && (!input.matches("[0-9]+"))) {
        //    input = input.substring(1);
        //}
        if (input == null || input.equals("") || !personDatabase.entryExists(input)) { // checks for valid numbers in the PMKeyS
            user = null;
            return 1;
        } else {
			if(passwordsEqual(input, barcode)) {
	            userName = personDatabase.getName(input);
				userID = personDatabase.getID(input);
			}
			else {
				return 1;
			}
        }
        return 0;
    }

    /**
     * Get a list of the names of all users in the database
     * @return A string array of the usernames
     */
    public final ArrayList<String> getUserNames() {
        return personDatabase.getNamesOfEntries();
    }
    
    public final inventoryManager.Person getUser(String name){
        return personDatabase.readEntry(name);
    }

    /**
     * Get a list of the names of all products in the database
     * @return A string array of the product names
     */
    public final ArrayList<String> getProductNames() {
        return itemDatabase.getItemNames();
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

    private static String[] getSecurePassword(String password) //throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt().getBytes();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = null;
        try {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        String[] ret = {hash.toString(), salt.toString()};
        return ret;
    }
    private static String[] getSecurePassword(String password, String NaCl) //throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = NaCl.getBytes();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = null;
        byte[] hash = null;
        try {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        String[] ret = {hash.toString(), salt.toString()};
        return ret;
    }

    private static String getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt.toString();
    }

    /**
     * Test whether a cleartext password is equal to the stored admin password
     * @param PW A cleartext password to test
     * @return The boolean test for whether the passwords are equal.
     */
    public final boolean passwordsEqual(String barcode, String PW) {
        String[] old = personDatabase.getPassword(barcode);
        if(old == null) return false;
        String[] testing = new String[0];
        testing = getSecurePassword(PW, old[1]); //get secure password from new password and old salt
        return (testing[0].equals(old[0]));
    }

    /**
     * Takes the given (prehashed) password and set it as the admin password
     * @param PW The prehashed password to be set
     */
    public final void setPassword(String barcode, String[] PW) {
        personDatabase.setPassword(barcode, PW[0], PW[1]);
    }

    final boolean isLong(String s) {
        if (s == null) return false;
        try {
            Long.parseLong(s); // try to parse the string, catching a failure.
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Create a scroll pane of one of the databases
     * @param type A string of either "Product" or "Person" used to determine which database to print.
     * @return A scroll pane showing the database chosen in the parameter or the person database by default.
     * @throws IOException
     */
    public ScrollPane printDatabase(String type) throws IOException {
        TextArea textArea;
        switch (type) {
            case ("Product"):
                textArea = new TextArea(itemDatabase.getDatabase().toString());
                break;
            case ("Person"):
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
    public final void logOut() { //TODO: this will probably have ta change.
        userName = null;
        userID = null;
        checkOuts = new CheckOut();
    }

    /**
     * Have the connected user buy the products in the checkout, adding the total cost to the users bill,
     * taking the number bought from the products in the database and clearing both the user and the checkout
     */
    public final void checkOutItems() {
        LinkedList purchased = checkOuts.productBought(); // clear the quantities and checkout
        itemDatabase.logItemsOut(purchased, userID); //TODO: Make this actually write out the items.
        checkOuts = new CheckOut(); // ensure checkout clear
        userName = null;
        userID = null;
    }

    /**
     * Get the names of all products in the checkout
     * @return A string array of the names of all products in the checkout
     */
    public final LinkedList<String> getCheckOutNames() {
        return checkOuts.getCheckOutNames();
    }


    /**
     * Takes the error value given by getPMKeyS and uses it to give the username or an error message
     * @param userError 0 if the user was correctly found, 1 if the user was not found and 2 if the user has been locked out.
     * @return The appropriate error message or the users name
     */
    public final String userName(int userError) {
        switch (userError) {
            case 0:
                if(userName != null) return userName;
            case 1:
                return "User not found";
        }
        return (userID == null && userName != null) ? "Error" : userName;
    }

    /**
     * Takes the barcode for a product and adds it to the checkout
     * @param input The barcode for the product as a string
     * @return True if the product was added, false if it failed
     */
    public final boolean addToCart(String input) {
        String tempBarCode = "-1";
        if (input != null && !input.equals("") && isLong(input)) {
            tempBarCode = input; // disallows the user from entering nothing or clicking cancel.
        } else if ((input == null) || ("".equals(input))) {
            return false;
        }
        String adding = itemDatabase.getItemName(tempBarCode);
        if (adding != null) {
            checkOuts.addProduct(tempBarCode, adding); //otherwise, add the product as normal.
            return true;
        }
        else if(userID == tempBarCode) {
            adding = "Checking yourself out are you? You can't do that.";
            checkOuts.addProduct("-1", adding);
            return true;
        }
        return false;
    }


    /**
     * Add a person to the database, given their name and PMKeyS
     * @param name The name of the person you wish to add
     * @param PMKeyS The PMKeyS of the person you wish to add
     */
    public final void addPersonToDatabase(String name, String ID, String password) {
        String[] passwd = getSecurePassword(password);
        personDatabase.setEntry(ID, name, false, false, passwd[0], passwd[1]);
    }

    /**
     * Add a product to the database given their name, barcode and price
     * @param name The name of the product you wish to add
     * @param barCode The barcode for the product you wish to add.
     * @param price The price of the product you wish to add.
     */
    public final void addItemToDatabase(String name, long barCode, long price) {
        itemDatabase.setEntry(name, price, barCode);
    }

    /**
     * Alter a product in the database
     * @param name The new name of the product
     * @param oldName The old name of the product
     * @param price The new price of the product
     * @param barcode The new barcode of the product
     * @param oldBarcode The old barcode of the product
     */
    public final void changeDatabaseProduct(String name, String oldName, long price, long barcode, long oldBarcode) {
        itemDatabase.changeItem(oldName, barcode, oldBarcode);
    }


    /**
     * Alter a product in the database
     * @param selectedIndex
     * @param name The new name of the person
     * @param pmkeys The new PMKeyS of the person
     * @param oldPmkeys The old PMKeyS of the person
     */
    public final void changeDatabasePerson(String selectedIndex, String name, long pmkeys, long oldPmkeys) {
        personDatabase.changeDatabasePerson(selectedIndex, name, pmkeys, oldPmkeys);
    }

    /**
     * Write out the CSV version of the database for the admin.
     * @param type "Person" for the person database or "Produt" for the product database
     * @throws IOException
     */
    public final void adminWriteOutDatabase(String type) {
        switch (type) {
            case ("Person"):
                personDatabase.writeDatabaseCSV("adminPersonDatabase.csv");
                break;
            case ("Item"): //TODO: change all instances of this in interface from product to item.
                itemDatabase.adminWriteOutDatabase("adminItemDatabase.csv");
                break;
			case ("general"):
				gd.adminWriteOutdatabase("adminGeneralDatabase.csv");
				break;
			case ("controlled"): 
				cd.adminWriteOutDatabase("adminControlledDatabase.csv");
				break;
            default:
                personDatabase.writeDatabaseCSV("adminItemDatabase.csv");
        }
    }
    public final void adminWriteOutDatabase(String type, String path)
    {
        switch (type) {
            case ("Person"):
                personDatabase.writeDatabaseCSV(path);
                break;
            case ("Item"): //TODO: change all instances of this in interface from product to item.
                itemDatabase.adminWriteOutDatabase(path);
                break;
            case ("general"):
                gd.adminWriteOutDatabase(path);
                break;
            case ("controlled"):
                cd.adminWriteOutDatabase(path);
            default:
                personDatabase.writeDatabaseCSV(path);
        }
    }

    /**
     * Delete the specified person
     * @param index The PMKeyS or name of the person as a string
     * @throws IOException
     * @throws InterruptedException
     */
    public final void removePerson(String ID, String rootID, String rootPasswd) throws IOException, InterruptedException {
        if(passwordsEqual(rootID, rootPasswd)) {
            personDatabase.deleteEntry(ID);
        }
    }

    /**
     * Remove the specified product
     * @param index The barcode or name of the product to be removed
     * @throws IOException
     * @throws InterruptedException
     */
    public final void removeItem(String ID) throws IOException, InterruptedException {
        itemDatabase.delItem(ID);
    }
    public final void removeItem(String ID, String rootID, String rootPassWd) {
        if(passwordsEqual(rootID, rootPassWd)) {
            cd.delItem(ID);
        }
    }

    /**
     * Delete a product from the checkout given it's index in the checkout array
     * @param index The index of the item to delete in the checkout array
     */
    public final void deleteProduct(int index)
    {
        checkOuts.delItem(index);
    }

    public final String getProductBarCode(int index) {
        return itemDatabase.getBarCode(index);
    }

    /**
     * Get the barcode of a product given it's name
     * @param name The name of the product to get the barcode of
     * @return The barcode of the product with the name specified.
     */
    public final String getProductBarCode(String name) {
        Product getting = itemDatabase.readDatabaseProduct(name);
        return getting.getBarCode();
    }

    /**
     * Get the name of a product given it's barcode
     * @param index The barcode of the product as a string
     * @return The name of the product with the given barcode
     */
    public final String getProductName(String barcode) {
        return itemDatabase.getName(barcode);
    }


    /**
     * Get the number of a product left in stock
     * @param name The name of the product you wish to check stock count.
     * @return The number of the specified product in stock
     */
    public final int getProductNumber(String ID) {
        return gd.getNumber(ID);
    }

    /**
     * set the number of a product in stock
     * @param name The name of the product you wish to set the stock count for
     * @param numberOfProducts The new stock count.
     */
    public final void setNumberOfProducts(String ID, int numberOfProducts) {
        gd.setNumber(ID, numberOfProducts);
    }

    /**
     * Determine whether there is a user logged in
     * @return The boolean value of whether the user is logged in.
     */
    public final boolean userLoggedIn() {
        return (userID != null && userName != null);
    }

    public static String getLogedInBarcode()
    {
        return userID;
    }

}
