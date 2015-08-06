package inventoryManager;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by jarrah on 7/08/15.
 */
public class GeneralDatabase extends ItemDatabase {
    /**
     * Stores the path of the database as a string, based on the OS being run.
     */
    private String databaseLocation;
    private SQLInterface db = new SQLInterface();

    /**
     * Constructor for ItemDatabase.
     * Will create a Person database with the ability to read and write people to the database location given in the preferences file of Settings
     */
    public GeneralDatabase()
    {
        try {
            Settings config = new Settings();
            databaseLocation = config.productSettings();
        } catch (FileNotFoundException e) {
            Log.print(e);
        }
    }

    public void addEntry(String barcode, String name, String setName, String description, long quantity) {
        db.addEntry(barcode, name, setName, description, quantity);
    }
    @Override
    public final void adminWriteOutDatabase(String path) {
        db.export("general", path);
    }
    /**
     * Get the number of a given product left in stock
     * @param barcode the name of the product you wish to check
     * @return The number as an int of the product left in stock
     */
    public final int getNumber(String barcode)
    {
        return db.getQuantity(barcode);
    }

    /**
     * Set the number of a specified item you have in stock
     * @param barcode The name of the item you wish to set
     * @param number The number of that item you now have.
     */
    public final void setNumber(String barcode, int number)
    { //TODO: create a SQLInterface method to handle this.
        db.setQuantity(barcode, number);
    }
    /**
     * A list of the names of all products in the database
     * @return A String array of the names of all products in the database.
     */
    @Override
    public final ArrayList<String> getItemNames() {
        return db.getName("general");
    }
}