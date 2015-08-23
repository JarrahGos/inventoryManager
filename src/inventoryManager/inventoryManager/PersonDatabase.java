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

final class PersonDatabase implements  Database{

	/** Stores the path of the database as a string, based on the OS being run. */
	private String databaseLocation;
	private SQLInterface db = new SQLInterface();
	public static final int USER = 0;
	public static final int ADMIN = 2;
	public static final int ROOT = 3;

    /**
     * Constructor for PersonDatabase.
     * Will create a Person database with the ability to read and write people to the database location given in the preferences file of Settings
     */
	public PersonDatabase() {
		try {
			databaseLocation = Settings.personSettings();
		} catch (FileNotFoundException e) {
			Log.print(e);
		}
	}

    /**
     * Set a new person within the database
     * Precondition: augments int personNo, String name, String artist, double size, double duration are input
     * Postcondition: Data for the currant working person in this database will be set.
     * @param name The name of the new person
     * @param running The running bill of the person
     * @param week The current bill of the person
     * @param barCode The barcode of the person
     * @param canBuy Whether the person can buy or not
     */
	public final void setEntry(String ID, String name, boolean admin, boolean root, String passwd, String salt) // take the persons data and pass it to the persons constructor
	{
		if (!entryExists(ID)) { // check whether the person already exists
			db.addEntry(ID, name, admin, root, passwd, salt); // pass off the work to the constructor: "make it so."
		}
	}



	/**
     * Get the entire database as a string
     * Precondition: setDatabase has been run
     * Postcondition: the user will be see an output of the persons in the database.
     * @return A string containing the entire database
     */
	public final ArrayList<String> getDatabase() {
		ArrayList<String> output = db.getDatabase("person");

		return output; // send the calling program one large string containing the ingredients of all the persons in the database
	}

    /**
     * Deletes the specified person from the database
     * Preconditions: setDatabase has been run
     * Postconditions: the chosen person will no longer exist.
     * @param barcode The barcode of the person you wish to delete
     */
    public final void deleteEntry(String barcode) {
        db.deleteEntry("person", barcode);
    }

    /**
     * Get the name of the specified person
     * Preconditions: setDatabase has been run for the invoking person
     * Postconditions: the person name will be returned
     * @param barcode The barcode of the person you wish to get
     * @return The name of the person with the specified barcode as a string or error if the person does not exist.
     */
	public final String getEntryName(String barcode) {
		return db.getName("person", barcode);
	}
	public final String getEntryID(String name) {
		return db.getID("person", name);
	}
	public final int getRole(String barcode) { // 0 = user, 1 = admin, 2 = root
		return db.getRole(barcode);
	}

    /**
     * Get a list of the usernames of those in the database
     * @return A String array of the names of those in the database
     */
	public final ArrayList<String> getNamesOfEntries() {
        return db.getName("person");
	}


    /**
     * Determine Whether a person Exists given only their barcode
     * @param barcode The barcode of the person you wish to check for
     * @return A boolean value of whether the person exists or not
     */
	public final boolean entryExists(String barcode) {
		return db.entryExists("person", barcode); // if you are running this, no person was found and therefore it is logical to conclude none exist.
		// similar to Kiri-Kin-Tha's first law of metaphysics.
	}
	public final boolean entryExists(String type, String barcode) {
		return db.entryExists(type, barcode);
	}

    /**
     * Write out the given person to the database
     * @param persOut The person you wish to write out
     * @return An integer, 0 meaning correct completion, 1 meaning an exception. Stack trace will be printed on error.
     */ //TODO is this necessary?
//	public final int writeDatabaseEntry(Person persOut) {
//            try {
//                File check = new File(databaseLocation + persOut.getEName());
//                if(check.exists()) check.delete();
//                check = new File(databaseLocation + persOut.getBarCode());
//                if(check.exists()) check.delete();
//                check = null;
//                if(persOut.getBarCode() != 7000000) {
//                    FileOutputStream personOut = new FileOutputStream(databaseLocation + persOut.getName());
//                    ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(personOut));
//                    out.writeObject(persOut);
//		            out.close();
//                    personOut.close();
//                }
//                // it may be quicker to do this with the java.properties setup that I have made. The code for that will sit unused in settings.java.
//				FileOutputStream personOut1 = new FileOutputStream(databaseLocation + persOut.getBarCode());
//				ObjectOutputStream out1 = new ObjectOutputStream(personOut1);
//				out1.writeObject(persOut);
//				out1.close();
//				personOut1.close();
//			}
//            catch (Exception e) {
//                Log.print(e);
//                return 1;
//            }
//            return 0;
//    }

    /**
     * Write out a CSV version of the database for future import.
     * @param path The path to the directory you wish to output to
     * @return An integer of 1 if the file was not found and 0 if it worked.
     */
	public final void writeDatabaseCSV(String path)  { //TODO: Ensure this works as a CSV
		db.export("person", path); // ensure that this path is the full absolute path rather than a relative one.
	}

//    /**
//     * Reads one person from the database
//     * @param barcode The name of the person you wish to read
//     * @return The person in the database which correlates with the name, or null if the person is not found
//     */
//         public final Person readEntry(String barcode) {
//			 Person importing = null;
//			 try {
//				 FileInputStream personIn = new FileInputStream(databaseLocation + barcode );
//				 ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(personIn));
//				 importing = (Person) in.readObject();
//				 in.close();
//				 personIn.close();
//			 } catch (IOException e) {
//				 Log.print(e);
//				 return null;
//			 } catch (ClassNotFoundException e) {
//				 Log.print(e);
//			 }
//			 return importing;
//		 }

    /**
     * Create an array of people from the provided string of paths
     * @param databaseList A string array of paths to files which are to be put into the array
     * @return An array of all people found from the given string
     */
//	public final Person[] readEntries(String[] databaseList){
//		Person[] importing = new Person[databaseList.length];
//        int i = 0;
//		for(String person : databaseList) {
//			Person inPers = null;
//			try {
//				FileInputStream personIn = new FileInputStream(person);
//				ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(personIn));
//				inPers = (Person) in.readObject();
//				in.close();
//				personIn.close();
//			}
//			catch (IOException | ClassNotFoundException e) {
//				Log.print(e);
//			}
//            boolean alreadyExists = false;
//			if(inPers != null) {
//				for (Person pers : importing) {
//					if ( pers != null && inPers.getBarCode() != 7000000 && inPers.getBarCode() == pers.getBarCode()) {
//						alreadyExists = true;
//						break;
//					}
//				}
//			}
//			else alreadyExists = true;
//			if(!alreadyExists) {
//                importing[i] = inPers;
//                i++;
//            }
//		}
//		return importing;
//	}

    /**
     * Changes the Admin password to the one specified
     * @param extPassword The new password, prehashed.
     */
	public final void setPassword(String barcode, String password, String salt) {
		db.setPassword(barcode, password, salt);
	}
	public final String[] getPassword(String ID) {
		return db.getPassword(ID);
	}
	//TODO: this needs to be written for SQL
//    public void changeDatabasePerson(String selectedIndex, String name, long pmkeys, long oldPmkeys)
//	{
//		Person oldPerson = readEntry(oldPmkeys);
//		Person newPerson = new Person(name, pmkeys, (long)oldPerson.totalCostRunning()*100, (long)oldPerson.totalCostWeek() *100,oldPerson.canBuy());
//
//		deleteEntry(selectedIndex);
//		writeDatabaseEntry(newPerson);
//	}
}
