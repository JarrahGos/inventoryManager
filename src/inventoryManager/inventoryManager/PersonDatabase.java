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

final class PersonDatabase implements Database {

    /**
     * The value of the the role a user will take in the system
     */
    public static final int USER = 0;
    /**
     * The value of the role an admin will take in the system
     */
    public static final int ADMIN = 2;
    /**
     * The value of the role a root user will take in the system.
     */
    public static final int ROOT = 3;
    /**
     * Stores the path of the database as a string, based on the OS being run.
     */
    private String databaseLocation;

    /** The value of the role the root user will take in the system.
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
     *
     * @param name    The name of the new person
     * @param admin The users role within the program
     * @param ID The ID of the user
     * @param passwd The hashed password of the user.
     * @param salt the salt used to hash the user's password.
     */
    public final void setEntry(String ID, String name, int admin, String passwd, String salt) // take the persons data and pass it to the persons constructor
    {
        System.out.printf("This entry exists %s", entryExists(ID));
        if (!entryExists(ID)) { // check whether the person already exists
            SQLInterface.addEntry(ID, name, admin, passwd, salt); // pass off the work to the constructor: "make it so."
        }
    }


    /**
     * Get the entire database as a string
     * Precondition: setDatabase has been run
     * Postcondition: the user will be see an output of the persons in the database.
     *
     * @return A string containing the entire database
     */
    public final ArrayList<String> getDatabase() {
        ArrayList<String> output = SQLInterface.getDatabase(SQLInterface.TABPERSON);

        return output; // send the calling program one large string containing the ingredients of all the persons in the database
    }

    /**
     * Deletes the specified person from the database
     * Preconditions: setDatabase has been run
     * Postconditions: the chosen person will no longer exist.
     *
     * @param barcode The barcode of the person you wish to delete
     */
    public final void deleteEntry(String barcode) {
        SQLInterface.deleteEntry(SQLInterface.TABPERSON, barcode);
    }

    /**
     * Get the name of the specified person
     * Preconditions: setDatabase has been run for the invoking person
     * Postconditions: the person name will be returned
     *
     * @param barcode The barcode of the person you wish to get
     * @return The name of the person with the specified barcode as a string or error if the person does not exist.
     */
    public final String getEntryName(String barcode) {
        return SQLInterface.getName(SQLInterface.TABPERSON, barcode);
    }

    /**
     * Convert a name into the ID of the given person. Lets hope that there isn't any name duplication as that will be completly undefined.
     *
     * @param name The name of the person to search for.
     * @return The ID of the first person found with that name.
     */
    public final String getEntryID(String name) {
        return SQLInterface.getID(SQLInterface.TABPERSON, name);
    }

    public final int getRole(String barcode) { // 0 = user, 1 = admin, 2 = root
        return SQLInterface.getRole(barcode);
    }

    /**
     * Get a list of the usernames of those in the database
     *
     * @return A String array of the names of those in the database
     */
    public final ArrayList<String> getNamesOfEntries() {
        return SQLInterface.getName(SQLInterface.TABPERSON);
    }


    /**
     * Determine Whether a person Exists given only their barcode
     *
     * @param barcode The barcode of the person you wish to check for
     * @return A boolean value of whether the person exists or not
     */
    public final boolean entryExists(String barcode) {
        return SQLInterface.entryExists(SQLInterface.TABPERSON, barcode); // if you are running this, no person was found and therefore it is logical to conclude none exist.
        // similar to Kiri-Kin-Tha's first law of metaphysics.
    }


    /**
     * Write out a CSV version of the database for future import.
     *
     * @param path The path to the directory you wish to output to
     * @return An integer of 1 if the file was not found and 0 if it worked.
     */
    public final void writeDatabaseCSV(String path) { //TODO: Ensure this works as a CSV
        SQLInterface.export(SQLInterface.TABPERSON, path); // ensure that this path is the full absolute path rather than a relative one.
    }


    /**
     * Changes the Admin password to the one specified
     *
     * @param barcode  The barcode of the user to store the password and salt for.
     * @param password The new password, prehashed.
     * @param salt     The salt that the password was hashed with for storage.
     */
    public final void setPassword(String barcode, String password, String salt) {
        SQLInterface.setPassword(barcode, password, salt);
    }

    public final String[] getPassword(String ID) {
        return SQLInterface.getPassword(ID);
    }

    /**
     * Determine whether the person should be given admin access to the program
     * @param barcode The barcode of the member to check permissions for.
     * @return An int to determine access. 0 for user, 2 for admin, 3 for root
     */

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
