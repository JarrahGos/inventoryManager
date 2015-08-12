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

class ItemDatabase
{
    /**
     * Stores the path of the database as a string, based on the OS being run.
     */
	private String databaseLocation;
	private SQLInterface db = new SQLInterface();
	private ControlledDatabase cd = new ControlledDatabase();
	private GeneralDatabase gd = new GeneralDatabase();

    /**
     * Constructor for ItemDatabase.
     * Will create a Person database with the ability to read and write people to the database location given in the preferences file of Settings
     */
	public ItemDatabase()
	{
		try {
            Settings config = new Settings();
			databaseLocation = config.productSettings();
		} catch (FileNotFoundException e) {
			Log.print(e);
		}
	}

    /**
     * Set a new product within the database.
     * Precondition: augments int productNo, String name, String artist, double size, double duration are input
     * Postcondition: Data for the currant working product in this database will be set.
     * @param name The name of the new product
     * @param price The price of the new product
     * @param barCode The barcode of the new product
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
	 * @param barCode The new barcode of the product
     * @param oldBarCode The old barcode of the product
	 * @param oldName The old name of the product
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
		return db.getDatabase("item");
	}
	

    /**
     * Deletes the specified product from the database
     * Preconditions: setDatabase has been run
     * Postconditions: the chosen product will no longer exist.
	 * @param type The type of item you wish to delete
     * @param barcode The barcode of the item you wish to delete
	 */
	public void delItem(String barcode) {
		if(!cd.isControlled(barcode)) {
			db.deleteEntry("general", barcode);
		}
	}


    /**
     * Determine Whether a product Exists given only their barcode
     * @param barcode The barcode of the person you wish to check for
     * @return A boolean value of whether the product exists or not
     */
	final boolean itemExists(String barcode)
	{
		return db.entryExists("item", barcode);
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
	public void adminWriteOutDatabase(String path)  {
		db.export("item", path);
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
	public ArrayList<String> getItemNames() {
		return db.getName("item");
	}
	public String getItemName(String ID) {
		return db.getName("item", ID);
	}
	public void logItemOut(String ID, String persID) {
		db.addLog(ID, persID, cd.isControlled(ID));
	}
	public void logItemsOut(LinkedList<String> IDs, String persID) {
		for(String ID : IDs) {
			logItemOut(ID, persID);
	}
}
