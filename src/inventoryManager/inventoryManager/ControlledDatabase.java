package inventoryManager;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by jarrah on 7/08/15.
 */
public class ControlledDatabase extends ItemDatabase {
    /**
     * Stores the path of the database as a string, based on the OS being run.
     */
    private String databaseLocation;
    private SQLInterface db = new SQLInterface();

    /**
     * Constructor for ItemDatabase.
     * Will create a Person database with the ability to read and write people to the database location given in the preferences file of Settings
     */
    public ControlledDatabase()
    {
        try {
            Settings config = new Settings();
            databaseLocation = config.productSettings();
        } catch (FileNotFoundException e) {
            Log.print(e);
        }
    }

    public void addEntry(String barcode, String name, String setName, String state, String tagPos, String type) {
        db.addEntry(barcode, name, setName, state, tagPos, type);
    }
    @Override
    public final void adminWriteOutDatabase(String path) {
        db.export("controlled", path);
    }
    /**
     * A list of the names of all products in the database
     * @return A String array of the names of all products in the database.
     */
    @Override
    public final ArrayList<String> getItemNames() {
        return db.getName("controlled");
    }
}
