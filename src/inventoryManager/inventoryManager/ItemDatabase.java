package inventoryManager;

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
 * @author Jarrah Gosbell
 */

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;

class ItemDatabase {
    /**
     * Stores the path of the database as a string, based on the OS being run.
     */
    private String databaseLocation;
    private SQLInterface db = new SQLInterface();

    /**
     * Constructor for ItemDatabase.
     * Will create a Person database with the ability to read and write people to the database location given in the preferences file of Settings
     */
    public ItemDatabase() {
        try {
            databaseLocation = Settings.productSettings();
        } catch (FileNotFoundException e) {
            Log.print(e);
        }
    }

    /**
     * Set a new product within the database.
     * Precondition: augments int productNo, String name, String artist, double size, double duration are input
     * Postcondition: Data for the currant working product in this database will be set.
     * @param name The name of the new product
     * @param barcode The barcode of the new product
     */
    public void addEntry(String barcode, String name) // take the products data and pass it to the products constructor
    {
        /**
         Class ItemDatabase: Method setDatabase
         Precondition: augments int productNo, String name, String artist, double size, double duration are input
         Postcondition: Data for the currant working product in this database will be set.
         */
        db.addEntry(barcode, name);

    }

    /**
     * Alter an existing product within the database
     * Precondition: augments int productNo, String name, String artist, double size, double duration are input
     * Postcondition: Data for the currant working product in this database will be set.
     * @param newID The new barcode of the product
     * @param ID The old barcode of the product
     * @param name The old name of the product
     */
    public final void changeItem(String name, String newID, String ID) // take the products data and pass it to the products constructor
    {
        db.updateEntry(ID, name, newID);
    }

    /**
     * Get the entire database as a string
     * Precondition: setDatabase has been run
     * Postcondition: the user will be see an output of the persons in the database.
     * @return A string containing the entire database
     */
    public final ArrayList<String> getDatabase() {
        return db.getDatabase(SQLInterface.TABITEM);
    }


    /**
     * Deletes the specified product from the database
     * Preconditions: setDatabase has been run
     * Postconditions: the chosen product will no longer exist.
     * @param barcode The barcode of the item you wish to delete
     */
    public void delItem(String barcode) {
        if (!this.isControlled(barcode)) {
            db.deleteEntry(SQLInterface.TABGENERAL, barcode);
        }
    }


    /**
     * Determine Whether a product Exists given only their barcode
     * @param barcode The barcode of the person you wish to check for
     * @return A boolean value of whether the product exists or not
     */
    final boolean itemExists(String barcode) {
        return db.entryExists(SQLInterface.TABITEM, barcode);
        // if you are running this, no product was found and therefore it is logical to conclude none exist.
        // similar to Kiri-Kin-Tha's first law of metaphysics.
    }

//    /**
//     * Write out the given product to the database
//     * @param productOut The person you wish to write out
//     * @return An integer, 0 meaning correct completion, 1 meaning an exception. Exception will be printed.
//     */
//	final int writeOutDatabaseProduct(Product productOut) {
//        if(productOut.productPrice() == 0) return 1;
//            try {
//                File check = new File(databaseLocation + productOut.getName());
//                if(check.exists()) check.delete();
//                check = new File(databaseLocation + productOut.getBarCode());
//                if(check.exists()) check.delete();
//                check = null;
//                FileOutputStream personOut = new FileOutputStream(databaseLocation + productOut.getName());
//                ObjectOutputStream out = new ObjectOutputStream(personOut);
//                out.writeObject(productOut);
//                out.close();
//                personOut.close();
//                FileOutputStream personOut1 = new FileOutputStream(databaseLocation + productOut.getBarCode());
//                ObjectOutputStream out1 = new ObjectOutputStream(personOut1);
//                out1.writeObject(productOut);
//                out1.close();
//                personOut.close();
//            }
//            catch (Exception e) {
//                Log.print(e);
//                return 1;
//            }
//            return 0;
//        }

//    /**
//     * Write out the given products array to the database
//     * @param productsOut The person you wish to write out
//     */
//	public final void writeOutDatabase(Product[] productsOut) {
//		for (Product productOut : productsOut) {
//            if(productOut.productPrice() == 0) continue;
//			try {
//                File check = new File(databaseLocation + productOut.getName());
//                if(check.exists()) check.delete();
//                check = new File(databaseLocation + productOut.getBarCode());
//                if(check.exists()) check.delete();
//                check = null;
//                FileOutputStream personOut = new FileOutputStream(databaseLocation + productOut.getName());
//                ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(personOut));
//                out.writeObject(productOut);
//                out.close();
//                personOut.close();
//                FileOutputStream personOut1 = new FileOutputStream(databaseLocation + productOut.getBarCode()); // do it all a second time for the barcode.
//                // it may be quicker to do this with the java.properties setup that I have made. The code for that will sit unused in settings.java.
//                ObjectOutputStream out1 = new ObjectOutputStream(personOut1);
//                out1.writeObject(productOut);
//                out1.close();
//                personOut.close();
//			} catch (Exception e) {
//				Log.print(e);
//			}
//		}
//	}

    /**
     * Write out a CSV version of the database for future import.
     * @param path The path to the directory you wish to output to
     * @return An integer of 1 if the file was not found and 0 if it worked.
     */
    public void adminWriteOutDatabase(String type, String path) {
        db.export(type, path);
    }


//    /**
//     * Reads one product from the database.
//     * @param barcode The barcode of the product you wish to read
//     * @return The person in the database which correlates with the barcode, or null if the person is not found
//     */
//	final Product readDatabaseProduct(long barcode){
//            Product importing = null;
//            try {
//                FileInputStream productIn = new FileInputStream(databaseLocation + String.valueOf(barcode));
//                ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(productIn));
//                importing = (Product)in.readObject();
//                in.close();
//                productIn.close();
//            }
//            catch (IOException e) {
//                Log.print(e);
//                return null;
//            } catch (ClassNotFoundException e) {
//				Log.print(e);
//			}
//			 return importing;
//        }

//    /**
//     * Reads one product from the database.
//     * @param name The name of the product you wish to read
//     * @return The person in the database which correlates with the barcode, or null if the person is not found
//     */
//    public final Product readDatabaseProduct(String name){
//            Product importing = null;
//            try {
//                FileInputStream productIn = new FileInputStream(databaseLocation + name);
//                ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(productIn));
//                importing = (Product)in.readObject();
//                in.close();
//                productIn.close();
//            }
//            catch (IOException e) {
//                Log.print(e);
//                return null;
//            } catch (ClassNotFoundException e) {
//				Log.print(e);
//			}
//			 return importing;
//        }
//    /**
//     * Create an array of products from the provided string of paths
//     * @param databaseList A File array of files which are to be put into the array
//     * @return An array of all products found from the given file array
//     */
//	final Product[] readDatabase(File[] databaseList){
//		Product[] importing = new Product[databaseList.length];
//        int i = 0;
//		for(File product : databaseList) {
//            Product inProd = null;
//            try {
//                FileInputStream productIn = new FileInputStream(product);
//                ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(productIn));
//                inProd = (Product) in.readObject();
//                in.close();
//                productIn.close();
//            } catch (IOException e) {
//                Log.print(e);
//                return null;
//            } catch (ClassNotFoundException e) {
//                Log.print(e);
//            }
//            boolean alreadyExists = false;
//            if(inProd != null) {
//                for (Product prod : importing) {
//                    if (prod != null && inProd.getBarCode() == prod.getBarCode()) {
//                        alreadyExists = true;
//                        break;
//                    }
//                }
//            }
//            else alreadyExists = true;
//            if (!alreadyExists) {
//                importing[i] = inProd;
//                i++;
//            }
//		}
//		return importing;
//	}
//    /**
//     * Create an array of products from the provided string of paths
//     * @param databaseList A string array of paths to files which are to be put into the array
//     * @return An array of all Products found from the given string
//     */
//    final Product[] readDatabase(String[] databaseList){
//        Product[] importing = new Product[databaseList.length];
//        int i = 0;
//
//		for(String product : databaseList) {
//            Product inProd = null;
//
//			try {
//                FileInputStream productIn = new FileInputStream(product);
//                ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(productIn));
//                inProd = (Product) in.readObject();
//                in.close();
//                productIn.close();
//
//			} catch (IOException e) {
//                Log.print(e);
//                return null;
//
//			} catch (ClassNotFoundException e) {
//                Log.print(e);
//
//			}
//            boolean alreadyExists = false;
//            if(inProd != null) {
//                for (Product prod : importing) {
//                    if (prod != null && inProd.getBarCode() == prod.getBarCode()) {
//                        alreadyExists = true;
//                        break;
//                    }
//                }
//            }
//            else alreadyExists = true;
//            if (!alreadyExists) {
//                importing[i] = inProd;
//                i++;
//            }
//        }
//        return importing;
//    }


    /**
     * A list of the names of all products in the database
     * @return A String array of the names of all products in the database.
     */
    public ArrayList<String> getItemNames(String type) {
        return db.getName(type);
    }

    /**
     * Get the name of an item given it's ID
     * @param ID The ID of the item to get the name of.
     * @return The name of the item.
     */
    public String getItemName(String ID) {
        return db.getName(SQLInterface.TABITEM, ID);
    }

    /**
     * Log a single item out of the database.
     * @param ID The ID of the item to log out.
     * @param persID The ID of the person to log the item out to.
     */
    public void logItemOut(String ID, String persID) {
        db.addLog(ID, persID, this.isControlled(ID));
    }

    /**
     * Log a linked list of items out of the database to a given user.
     * @param IDs A linked list of IDs to log out in the database.
     * @param persID The ID of the person to log the items to.
     */
    public void logItemsOut(LinkedList<String> IDs, String persID) {
        for (String ID : IDs) {
            logItemOut(ID, persID);
        }
    }

    /**
     * Get the barcode of an item given it's name.
     * @param name The name of the item to search for.
     * @return The ID of the first item in the database with the given name.
     */
    public final String getBarcode(String name) {
        return db.getID(SQLInterface.TABITEM, name);
    }

    /**
     * Add an entry to the controlled item database
     * @param barcode The barcode of the item to add.
     * @param name The name of the item to add.
     * @param setName The set name of the item to add. May be null.
     * @param state The state of the item to add.
     * @param tagPos The tag number or position number of the item.
     * @param type The type of the item.
     */
    public void addEntry(String barcode, String name, String setName, String state, String tagPos, String type) {
        db.addEntry(barcode, name, setName, state, tagPos, type);
    }

    /**
     * Determine whether an item is a controlled item.
     * @param ID The ID of the item to check
     * @return True if the item is controlled.
     */
    public final boolean isControlled(String ID) {
        return db.isItemControlled(ID);
    }

    /**
     * Remove an item from the database.
     * @param ID The ID of the item to delete.
     * @param controlled Whether the item is stored in the controlled database or in the general dataase.
     */
    public void delItem(String ID, boolean controlled) {
        if (controlled) {
            db.deleteEntry(SQLInterface.TABCONTROLLED, ID);
            db.deleteEntry(SQLInterface.TABITEM, ID);
        } else {
            db.deleteEntry(SQLInterface.TABGENERAL, ID);
            db.deleteEntry(SQLInterface.TABITEM, ID);
        }
    }


    /**
     * add an entry to the item database.
     * @param barcode The barcode of the item to add. Will be used as the database key.
     * @param name The name of the item.
     * @param setName the name of the set the item is is. May be null.
     * @param description A description of the item.
     * @param quantity The quantity of the item that exists.
     */
    public void addEntry(String barcode, String name, String setName, String description, long quantity) {
        db.addEntry(barcode, name, setName, description, quantity);
    }

    /**
     * Get the number of a given product left in stock
     * @param barcode the name of the product you wish to check
     * @return The number as an int of the product left in stock
     */
    public final int getNumber(String barcode) {
        return db.getQuantity(barcode);
    }

    /**
     * Set the number of a specified item you have in stock
     * @param barcode The name of the item you wish to set
     * @param number The number of that item you now have.
     */
    public final void setNumber(String barcode, int number) {
        db.setQuantity(barcode, number);
    }

    /**
     * A list of the names of all products in the database
     * @return A String array of the names of all products in the database.
     */
    public final ArrayList<String> getItemNames() {
        return db.getName(SQLInterface.TABGENERAL);
    }
}
