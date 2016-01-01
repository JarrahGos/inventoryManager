package inventoryManager;

/***
 * Copyright (C) 2015  Jarrah Gosbell
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General public static  License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General public static  License for more details.
 * <p>
 * You should have received a copy of the GNU General public static  License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Author: Jarrah Gosbell
 * Student Number: z5012558
 * Class: PersonDatabase
 * Description: This program will allow for the input and retrieval of the person database and will set the limits of the database.
 */


import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class SQLInterface {
    // Table names:
    public static final String TABCONTROLLED = "controlled";
    public static final String TABCONTROLLEDTYPE = "controlledType";
    public static final String TABGENERAL = "general";
    public static final String TABITEM = "item";
    public static final String TABITEMLOG = "itemLog";
    public static final String TABPERSON = "person";
    public static final String TABPERSONLOG = "personLog";
    public static final String TABSET = "itemSet";
    public static final String COLITEMLOGPERSID = "persID";
    public static final String COLITEMLOGITEMID = "itemID";
    // column names TABCONTROLLED
    private static final String COLCONTROLLEDID = "ID";
    private static final String COLCONTROLLEDTAGNO = "tagno";
    private static final String COLCONTROLLEDTYPE = "type";
    private static final String COLCONTROLLEDSTATE = "state";
    private static final int TABCONTROLLEDCOUNT = 3;
    // column names TABCONTROLLEDTYPE
    private static final String COLCONTROLLEDTYPEID = "ID";
    private static final String COLCONTROLLEDTYPENAME = "name";
    private static final int TABCONTROLLEDTYPECOUNT = 2;
    // Column names TABGENERAL
    private static final String COLGENERALID = "ID";
    private static final String COLGENERALDESCRIPTION = "description";
    private static final String COLGENERALQUANTITY = "quantity";
    private static final String COLGENERALLOCATION = "location";
    private static final int TABGENERALCOUNT = 3;
    // Column names TABITEM
    private static final String COLITEMID = "ID";
    private static final String COLITEMNAME = "name";
    private static final String COLITEMSETID = "setID";
    private static final int TABITEMCOUNT = 3;
    // Column names TABITEMLOG
    private static final String COLITEMLOGID = "ID"; // Is this log ID or item, create another for ITEM.
    private static final String COLITEMLOGOUTDATE = "outDate";
    private static final String COLITEMLOGOUT = "out";
    private static final String COLITEMLOGINDATE = "inDate";
    private static final String COLITEMLOGCONTROLLED = "controlled";
    private static final String COLITEMLOGADMINNAME = "adminName";
    private static final int TABITEMLOGCOUNT = 7;
    // Column names TABPERSON
    private static final String COLPERSONID = "ID";
    private static final String COLPERSONNAME = "name";
    private static final String COLPERSONADMIN = "admin";
    private static final String COLPERSONPASSOWRD = "password";
    private static final String COLPERSONSALT = "salt";
    private static final int TABPERSONCOUNT = 5;
    // Column names TABPERSONLOG
    private static final String COLPERSONLOGPERSID = "persID";
    private static final String COLPERSONLOGDATE = "changeDate";
    private static final String COLPERSONLOGAUTHNAME = "authName";
    private static final int TABPERSONLOGCOUNT = 3;
    // Column names TABSET
    private static final String COLSETID = "ID";
    private static final String COLSETNAME = "name";
    private static final int TABSETCOUNT = 2;
    private static String URL = "jdbc:sqlite:/Users/jarrah/ideaProjects/inventoryManager/inv.db"; // these will be initialised from the file.

    // used to generate a random wait time for database locks.
    private static Random rand = new Random();


    /**
     * Start the sqlite database connection.
     */
    private SQLInterface() {
        try {
            Class.forName("org.sqlite.JDBC").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Log.print("could not find driver class\n" + e.toString());
        }
//        String[] settings = new String[0];
//        try {
//            settings = Settings.SQLInterfaceSettings();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        URL = settings[0];


    }

    private static Optional<Connection> getDatabase() {

        try {
            Connection db = DriverManager.getConnection(URL);
            db.setAutoCommit(true);
            return Optional.of(db);
        } catch (java.sql.SQLException e) {
            Log.print(e);
            return Optional.empty();
        }

    }

    /**
     * Delete an entry in the person, item or controlled item tables.
     *
     * @param type    The table to delete from. Use the public variables available in this class.
     * @param barcode The barcode of the item or person to delete.
     */
    public static void deleteEntry(String type, String barcode) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in deleteEntry");
        String statement = "";
        switch (type) {
            case "person":
                statement = "DELETE FROM " + TABPERSON + " WHERE " + COLPERSONID + " = ?";
                break;
            case "GeneralItem":
                statement = "DELETE FROM " + TABGENERAL + " WHERE " + COLGENERALID + " = ?";
                break;
            case "controlledItem":
                statement = "DELETE FROM " + TABCONTROLLED + " WHERE " + COLCONTROLLEDID + " = ?"; // TODO: this will delete controlled but not item. Use the key and a cascade on delete.
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, barcode); //TODO: statement not executing, due to not having controlled and general set properly.
            executePS(db, ps);
            System.out.println("_X_X_X_X_X_X_X_ DB closed in deleteEntry");
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(32);
        }
    }

    private static void executePS(Connection db, PreparedStatement ps) throws SQLException {
        ps.execute();
        ps.closeOnCompletion();
        db.close();
    }

    /**
     * Add an entry to the person table.
     *
     * @param ID       The ID of the person.
     * @param name     The name of the person.
     * @param admin    Whether the person is an admin. Use the public variables available in personDatabase.
     * @param password The hashed password of the person.
     * @param salt     The salt used to hash the password.
     */
    public static void addEntry(String ID, String name, int admin, String password, String salt) { // add a new person
        String statement = "INSERT INTO " + TABPERSON + " (" + COLPERSONID + ", " + COLPERSONNAME + ", " + COLPERSONADMIN + ", " + COLPERSONPASSOWRD + ", " + COLPERSONSALT + ")" +
                "VALUES(?, ?, ?, ?, ?)";
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in addEntry1");
        System.out.println(statement + ID + name + admin + password + salt);
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            ps.setString(2, name);
            ps.setInt(3, admin);
            ps.setString(4, password);
            ps.setString(5, salt);
            executePS(db, ps);
            System.out.println("_X_X_X_X_X_X_X_ DB closed in addEntry1");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
    }

    /**
     * Add a new set to the database.
     *
     * @param name The name of the set to add.
     */
    public static void addEntry(String name) // add new set
    {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in addEntry2");
        String statement = "INSERT INTO " + TABSET + " (" + COLSETNAME + ") VALUES(?)";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, name);
            executePS(db, ps);
            System.out.println("_X_X_X_X_X_X_X_ DB closed in addEntry2");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
    }

    /**
     * Add a new general item to the database.
     *
     * @param ID          The ID of the item.
     * @param name        The name of the item.
     * @param setName     The setname of the item.
     * @param Description A description of the item.
     * @param Quantity    The number of the item in stock.
     */

    //TODO: Completly rewrite this from scratch. Section on sets is fucked.
    public static void addEntry(String ID, String name, String setName, String Description, Long Quantity, String location) { // Add generalItem
        addEntry(ID, name);

        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in addEntry3");
        String statement = "INSERT INTO " + TABGENERAL + " (" + COLGENERALID + ", " + COLGENERALDESCRIPTION + ", " + COLGENERALQUANTITY + ", " + COLGENERALLOCATION + ")" +
                "VALUES(?, ?, ?, ?)";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            ps.setString(2, Description);
            ps.setLong(3, Quantity);
            ps.setString(4, location);
            executePS(db, ps);
            System.out.println("_X_X_X_X_X_X_X_ DB closed in addEntry3");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        if (setName != null && !setName.isEmpty()) {
            db = getDatabase().get();
            System.out.println("_X_X_X_X_X_X_X_ New DB in addEntry3");
            statement = "SELECT " + COLSETID + " FROM " + TABSET + " WHERE " + COLSETNAME + " = ?";
            ResultSet rs = null;
            try {
                PreparedStatement ps = db.prepareStatement(statement);
                ps.setString(1, setName);
                rs = ps.executeQuery();

                if (rs.next()) {
                    statement = ""; // TODO: List of items is fucked.
                    ps = db.prepareStatement(statement);
                    ps.setString(1, name);
                }
                rs.close();
                ps.closeOnCompletion();
                db.close();
                System.out.println("_X_X_X_X_X_X_X_ DB closed in addEntry3");
            } catch (SQLException e) {
                Log.print(e);
                System.exit(32);
            }


        }
    }

    /**
     * Add a new controlled item to the database.
     *
     * @param ID      The ID of the item.
     * @param name    The name of the item.
     * @param setName The set name of the item.
     * @param state   The state of the item.
     * @param tagpos  The tag or position number of the item.
     * @param type    The type of the item.
     */
    public static void addEntry(String ID, String name, String setName, String state, String tagpos, String type) { // add Controlled " + TABITEM + "
        addEntry(ID, name);

        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in addEntry4");
        String statement;
        if (type != null && !type.isEmpty()) {
            statement = "SELECT " + COLCONTROLLEDTYPEID + " FROM " + TABCONTROLLEDTYPE + " WHERE " + COLCONTROLLEDTYPENAME + " = ?";
            ResultSet rs = null;
            try {
                PreparedStatement ps = db.prepareStatement(statement);
                ps.setString(1, type);
                rs = ps.executeQuery();
                ps.closeOnCompletion();
                db.close();
                System.out.println("_X_X_X_X_X_X_X_ DB closed in X");
                if (!rs.next()) {
                    db = getDatabase().get();
                    statement = "INSERT INTO " + TABCONTROLLEDTYPE + "(" + COLCONTROLLEDTYPENAME + ") VALUES (?)";
                    ps = db.prepareStatement(statement);
                    ps.setString(1, type);
                    ps.execute();
                    ps.closeOnCompletion();
                    statement = "SELECT " + COLCONTROLLEDTYPEID + " FROM " + TABCONTROLLEDTYPE + " WHERE " + COLCONTROLLEDTYPENAME + " = ?";
                    ps = db.prepareStatement(statement);
                    ps.setString(1, type);
                    rs = ps.executeQuery();
                    ps.closeOnCompletion();
                    db.close();
                    System.out.println("_X_X_X_X_X_X_X_ DB closed in addEntry4");
                }
                rs.close();

                statement = "INSERT INTO " + TABCONTROLLED + " (" + COLCONTROLLEDID + ", " + COLCONTROLLEDTAGNO + ", " + COLCONTROLLEDSTATE + ", " + COLCONTROLLEDTYPE + ")" + // TODO: DAFAQ is state (servicable or not)
                        "VALUES(?, ?, ?, ?)";
                try {
                    db = getDatabase().get();
                    System.out.println("_X_X_X_X_X_X_X_ New DB in addEntry4");
                    ps = db.prepareStatement(statement);
                    ps.setString(1, ID);
                    ps.setString(2, tagpos);
                    //ps.setInt(3, rs.getInt("ID"));
                    ps.setString(3, state);
                    ps.setString(4, getID(TABCONTROLLEDTYPE, type).orElse("null"));
                    executePS(db, ps);
                    System.out.println("_X_X_X_X_X_X_X_ DB closed in addEntry4");
                } catch (SQLException e) {
                    Log.print(e);
                    System.exit(32);
                }
            } catch (SQLException e) {
                Log.print(e);
                System.exit(32);
            }
        }
        if (setName != null && !setName.isEmpty()) {
            statement = "SELECT " + COLSETID + " FROM " + TABSET + "s WHERE " + COLSETNAME + " = ?";
            ResultSet rs = null;
            try {
                db = getDatabase().get();
                PreparedStatement ps = db.prepareStatement(statement);
                ps.setString(1, setName);
                rs = ps.executeQuery();
                ps.closeOnCompletion();
                db.close();
                if (rs.next()) {
                    db = getDatabase().get();
                    statement = ""; // TODO: List of items is fucked.
                    ps = db.prepareStatement(statement);
                    ps.setString(1, name);
                    executePS(db, ps);
                    System.out.println("_X_X_X_X_X_X_X_ DB closed in addEntry4");
                }
                rs.close();
            } catch (SQLException e) {
                Log.print(e);
                System.exit(32);
            }

        }
    }

    /**
     * Add a new item to the database.
     *
     * @param ID   The ID of the item.
     * @param name The name of the item.
     */
    public static void addEntry(String ID, String name) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in addEntry5");
        String statement = "INSERT INTO " + TABITEM + " (" + COLITEMID + ", " + COLITEMNAME + ") " +
                "VALUES(?, ?)";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            ps.setString(2, name);
            executePS(db, ps);
            System.out.println("_X_X_X_X_X_X_X_ DB closed in addEntry5");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
    }

    /**
     * Add a new set to the database.
     *
     * @param ID   The ID of the item.
     * @param name The name of the item.
     */
    public static void addSet(String ID, String name) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in addEntry5");
        String statement = "INSERT INTO " + TABSET + " (" + COLSETID + ", " + COLSETNAME + ") " +
                "VALUES(?, ?)";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            ps.setString(2, name);
            executePS(db, ps);
            System.out.println("_X_X_X_X_X_X_X_ DB closed in addEntry5");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
    }

    /**
     * Add a new person log to the database.
     *
     * @param persID  The ID of the person to log.
     * @param adminID The ID of the admin who allowed the change.
     */
    public static void addLog(String persID, String adminID) { // add to change password log.
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in addLog1");
        String statement = String.format("INSERT INTO %s (%s, %s, %s) VALUES(?, DATE('now', 'localtime'), (SELECT %s FROM %s WHERE %s = ?))",
                TABPERSONLOG, COLPERSONLOGPERSID, COLPERSONLOGDATE, COLPERSONLOGAUTHNAME, COLPERSONNAME, TABPERSON, COLPERSONID);
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, persID);
            ps.setString(2, adminID);
            executePS(db, ps);
            System.out.println("_X_X_X_X_X_X_X_ DB closed in addLog1");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }

    }

    /**
     * Add an entry to the item log
     *
     * @param itemID     The ID of the item which has been lent.
     * @param persID     The ID of the person who took the item.
     * @param controlled Whether the item is controlled or not.
     */
    public static void addLog(String itemID, String persID, boolean controlled) { // Sign an item out
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in addLog2");
        //TODO: Should this check the item as out in the controlled table?
        String statment = "INSERT INTO " + TABITEMLOG + " (" + COLITEMLOGITEMID + ", " + COLITEMLOGOUTDATE + ", " + COLITEMLOGINDATE + ", " + COLITEMLOGPERSID + ", " + COLITEMLOGCONTROLLED + ") " +
                "VALUES(?, DATE('now', 'localtime'), \"FALSE\", ?, ?)";
        try {
            PreparedStatement ps = db.prepareStatement(statment);
            ps.setString(1, itemID);
            ps.setString(2, persID);
            ps.setBoolean(3, controlled);
            executePS(db, ps);
            System.out.println("_X_X_X_X_X_X_X_ DB closed in addLog2");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
    }

    /**
     * Return an item to the store.
     *
     * @param itemID The ID of the item to return.
     * @param persID The ID of the admin returning the idem.
     */
    public static void returnItem(String itemID, String persID) { // Return a general item.
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in returnItem");
        String statement = "UPDATE " + TABITEMLOG + " SET " + COLITEMLOGINDATE + "=DATE('now', 'localtime')" +
                "WHERE " + COLITEMLOGITEMID + "=? AND " + COLITEMLOGPERSID + "=?";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, itemID);
            ps.setString(2, persID);
            executePS(db, ps);
            System.out.println("_X_X_X_X_X_X_X_ DB closed in returnItem");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
    }

    /**
     *<b>On Logs</b>
     * The logs in this system are created in the following suituations:
     * <ul>
     *     <li>Password reset</li>
     *     <li>Item signed out</li>
     * </ul>
     * Thus, logs will be available for people (passwords), and item.
     * Logs will be condensed by item type, date and ID.
     */
    /**
     * Get the entrire log of a given type.
     *
     * @param type The type of log you would like. Use the public variables
     * @return
     */
    public static ArrayList<String> getLog(String type, boolean outOnly) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getLog1");
        String statement;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int noOfResults = 0;
        String headings;
        switch (type) {
            case TABPERSONLOG:
                statement = "error";
                noOfResults = TABPERSONLOGCOUNT;
                headings = "ID\t\tDate\tAuth ID";
                break;
            case TABITEMLOG:
                if (outOnly) statement = "SELECT * FROM " + TABITEMLOG + " WHERE " + COLITEMLOGINDATE + " = \"FALSE\"";
                else statement = "SELECT * FROM " + TABITEMLOG + "";
                noOfResults = TABITEMLOGCOUNT;
                break;
            case "controlled":
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGCONTROLLED + "=TRUE";
                headings = "";
                break;
            case "general":
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGCONTROLLED + "=FALSE";
                headings = "";
                break;
            default:
                statement = "SELECT * FROM " + TABITEMLOG + "";
                headings = "ID\tOut Date\tIn Date\tPerson ID\tControlled\tReturned By\tItemID";
                break;
        }
        try {
            ps = db.prepareStatement(statement);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        ArrayList<String> ret = new ArrayList<>();
        try {
            ret = rsToString(rs, noOfResults);
            //ret.add(0, headings);
            rs.close();
            ps.closeOnCompletion();
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getLog1");
        } catch (SQLException e) {
            Log.print(e);
        }
        return ret;
    }

    public static ArrayList<PasswordLog> getPasswordLog() {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getLog4");
        ResultSet rs = null;
        ArrayList<PasswordLog> ret = new ArrayList();
        try {
            PreparedStatement ps = db.prepareStatement("SELECT * FROM " + TABPERSONLOG + " ");
            rs = ps.executeQuery();
            while (rs.next()) {
                ret.add(new PasswordLog(rs.getString(COLPERSONLOGPERSID), rs.getString(COLPERSONLOGDATE), rs.getString(COLPERSONLOGAUTHNAME)));
            }
            rs.close();
            ps.closeOnCompletion();
            for (PasswordLog entry : ret) {
                System.out.println(entry.getID());
            }
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getLog4");
            return ret;
        } catch (SQLException e) {
            Log.print(e);
        }
        return new ArrayList<>();
    }

    public static ArrayList<PasswordLog> getPasswordLog(LocalDate from, LocalDate to) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getLog4");
        ResultSet rs = null;
        ArrayList<PasswordLog> ret = new ArrayList();
        try {
            PreparedStatement ps = db.prepareStatement("SELECT * FROM " + TABPERSONLOG + " WHERE " + COLPERSONLOGDATE + " >= ? AND " +
                    COLPERSONLOGDATE + " <= ?;");
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                ret.add(new PasswordLog(rs.getString(COLPERSONLOGPERSID), rs.getString(COLPERSONLOGDATE), rs.getString(COLPERSONLOGAUTHNAME)));
            }
            rs.close();
            ps.closeOnCompletion();
            for (PasswordLog entry : ret) {
                System.out.println(entry.getID());
            }
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getLog4");
            return ret;
        } catch (SQLException e) {
            Log.print(e);
        }
        return new ArrayList<>();
    }

    public static ArrayList<ItemLog> getItemLog(boolean outOnly) {
        //                 headings = "ID\tOut Date\tIn Date\tPerson ID\tControlled\tReturned By\tItemID";
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getLog4");
        ResultSet rs = null;
        ArrayList<ItemLog> ret = new ArrayList();
        try {
            PreparedStatement ps;
            if (outOnly)
                ps = db.prepareStatement("SELECT * FROM " + TABITEMLOG + " WHERE " + COLITEMLOGINDATE + " = \"FALSE\"");
            else ps = db.prepareStatement("SELECT * FROM " + TABITEMLOG);
            rs = ps.executeQuery();
            while (rs.next()) {
                ret.add(new ItemLog(rs.getString(COLITEMLOGID), rs.getString(COLITEMLOGOUTDATE), rs.getString(COLITEMLOGINDATE),
                        rs.getString(COLITEMLOGPERSID), rs.getString(COLITEMLOGCONTROLLED), rs.getString(COLITEMLOGADMINNAME), rs.getString(COLITEMLOGITEMID)));
            }
            rs.close();
            ps.closeOnCompletion();
            for (ItemLog entry : ret) {
                System.out.println(entry.getID());
            }
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getLog4");
            return ret;
        } catch (SQLException e) {
            Log.print(e);
        }
        return new ArrayList<>();
    }

    public static ArrayList<ItemLog> getItemLog(boolean outOnly, LocalDate from, LocalDate to) {
        //                 headings = "ID\tOut Date\tIn Date\tPerson ID\tControlled\tReturned By\tItemID";
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getLog4");
        ResultSet rs = null;
        ArrayList<ItemLog> ret = new ArrayList();
        try {
            PreparedStatement ps;
            if (outOnly)
                ps = db.prepareStatement("SELECT * FROM " + TABITEMLOG + " WHERE " + COLITEMLOGINDATE + " = \"FALSE\" AND" +
                        COLITEMLOGOUTDATE + " <= ? AND " + COLITEMLOGINDATE + " >= ?;");
            else ps = db.prepareStatement("SELECT * FROM " + TABITEMLOG + " WHERE " +
                    COLITEMLOGOUTDATE + " <= ? AND " + COLITEMLOGINDATE + " >= ?;");
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                ret.add(new ItemLog(rs.getString(COLITEMLOGID), rs.getString(COLITEMLOGOUTDATE), rs.getString(COLITEMLOGINDATE),
                        rs.getString(COLITEMLOGPERSID), rs.getString(COLITEMLOGCONTROLLED), rs.getString(COLITEMLOGADMINNAME), rs.getString(COLITEMLOGITEMID)));
            }
            rs.close();
            ps.closeOnCompletion();
            for (ItemLog entry : ret) {
                System.out.println(entry.getID());
            }
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getLog4");
            return ret;
        } catch (SQLException e) {
            Log.print(e);
        }
        return new ArrayList<>();
    }

    /**
     * Get the log of a given type for a given ID. Person or all item logs are available.
     *
     * @param type The table type to get the log for. Use the public static  table strings available in this class.
     * @param ID   The ID to get the log for.
     * @return An arraylist of the records in the log file.
     */
    public static ArrayList<String> getLog(String type, String ID) { //TODO: Need a way to generate headings.
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getLog2");
        String statement;
        ResultSet rs = null;
        int noOfResults = 0;
        switch (type) {
            case TABPERSONLOG:
                statement = "SELECT * FROM " + TABPERSONLOG + " " +
                        "WHERE " + COLPERSONLOGPERSID + " = ?";
                noOfResults = TABPERSONLOGCOUNT;
                break;
            case TABITEMLOG:
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGID + " = ?";
                noOfResults = TABITEMLOGCOUNT;
                break;
            case "controlled":
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGCONTROLLED + "=TRUE AND " +
                        COLITEMLOGID + " = ?";
                break;
            case "general":
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGCONTROLLED + "=FALSE AND " +
                        COLITEMLOGID + " = ?";
                break;
            default:
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGID + " = ?";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
            ps.closeOnCompletion();
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getLog2");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        ArrayList<String> ret = new ArrayList<>();
        try {
            ret = rsToString(rs, noOfResults);
            rs.close();
        } catch (SQLException e) {
            Log.print(e);
        }
        return ret;
    }

    private static ArrayList<String> rsToString(ResultSet rs, int noOfResults) throws SQLException {
        ArrayList<String> ret = new ArrayList<>();
        while (rs.next()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= noOfResults; i++) {
                sb.append(rs.getString(i));
                sb.append("\t");
            }
            ret.add(sb.toString());
        }
        return ret;
    }

    /**
     * Get logs for person and both items for a given date.
     *
     * @param type The table to get the log for. Use the public static  Strings available within this class.
     * @param date The date that you would like to get the log for.
     * @return An arrayList of each record within the log.
     */
    public static ArrayList<String> getLog(String type, boolean outOnly, LocalDate from, LocalDate to) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getLog3");
        String statement;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int noOfResults = 0;
        switch (type) {
            case TABPERSONLOG:
                statement = "SELECT * FROM " + TABPERSONLOG + " " +
                        "WHERE " + COLPERSONLOGDATE + " > ?";
                noOfResults = TABPERSONLOGCOUNT;
                break;
            case TABITEMLOG:
                if (outOnly) statement = "SELECT * FROM " + TABITEMLOG +
                        " WHERE " + COLITEMLOGOUTDATE + " > ? AND " + COLITEMLOGINDATE + " = \"FALSE\"";
                else statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGOUTDATE + " > ?";
                noOfResults = TABITEMLOGCOUNT;
                break;
            case "controlled":
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGCONTROLLED + "=TRUE AND " +
                        COLITEMLOGOUTDATE + " > ?";
                break;
            case "general":
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGCONTROLLED + "=FALSE AND " +
                        COLITEMLOGOUTDATE + " >  ?";
                break;
            default:
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGOUTDATE + " > ?";
                break;
        }
        try {
            ps = db.prepareStatement(statement);
            ps.setDate(1, java.sql.Date.valueOf(from));
            rs = ps.executeQuery();
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        ArrayList<String> ret = null;
        try {
            ret = rsToString(rs, noOfResults);
            ps.closeOnCompletion();
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getLog3");
            rs.close();
        } catch (SQLException e) {
            Log.print(e);
        }
        return ret;
    }

    /**
     * Get the items that have been logged out
     *
     * @return An ArrayList of all items that have been logged out.
     */
    public static ArrayList<String> getOutItemsLog() {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getOutItemsLog");
        String statement = "SELECT " + COLITEMNAME + " FROM " + TABITEMLOG + " JOIN " + TABITEM + " ON " + TABITEMLOG + "." + COLITEMLOGITEMID + "=" + TABITEM + "." + COLITEMID + " WHERE " + COLITEMLOGINDATE + " = \"FALSE\"";
        ResultSet rs;
        ArrayList<String> ret = new ArrayList<>();
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            rs = ps.executeQuery();
            boolean next = rs.next();
            System.out.println(next);
            while (next) {
                System.out.println(rs.getString(1));
                ret.add(rs.getString(1));
                next = rs.next();
            }
            rs.close();
            ps.closeOnCompletion();
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getOutItemsLog");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        return ret;
    }

    public static ArrayList<String> getOutItemsLog(String type) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getOutItemsLog");
        String statement = "";
        switch (type) {
            case COLITEMLOGITEMID:
                statement = "SELECT " + COLITEMLOGITEMID + " FROM " + TABITEMLOG + " WHERE " + COLITEMLOGINDATE + " = \"FALSE\"";
                break;
            case COLITEMLOGPERSID:
                statement = "SELECT " + COLITEMLOGPERSID + " FROM " + TABITEMLOG + " WHERE " + COLITEMLOGINDATE + " = \"FALSE\"";
                break;
            default:
                statement = "SELECT " + COLITEMLOGITEMID + " FROM " + TABITEMLOG + " WHERE " + COLITEMLOGINDATE + " = \"FALSE\"";
        }
        ResultSet rs;
        ArrayList<String> ret = new ArrayList<>();
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            rs = ps.executeQuery();
            boolean next = rs.next();
            System.out.println(next);
            while (next) {
                System.out.println(rs.getString(1));
                ret.add(rs.getString(1));
                next = rs.next();
            }
            rs.close();
            ps.closeOnCompletion();
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getOutItemsLog");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        return ret;
    }

    /**
     * Get an ArrayList of the a given table within the database.
     *
     * @param type The table type. Use the public static  Strings available within this class.
     * @return An arraylist of every record within the given table.
     */
    public static ArrayList<String> getDatabase(String type) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getDatabase1");
        String statement;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int count = 0;
        switch (type) {
            case TABPERSON:
                statement = "SELECT * FROM " + TABPERSON + " ";
                count = TABPERSONCOUNT;
                break;
            case TABITEM:
                statement = "SELECT * FROM " + TABITEM + " i" +
                        "INNER JOIN " + TABGENERAL + " g" +
                        " ON i.ID = g.ID" +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID";
                count = TABITEMCOUNT;
                break;
            case TABCONTROLLED:
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID";
                count = TABCONTROLLEDCOUNT;
                break;
            case TABGENERAL:
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g " +
                        "ON i.ID = g.ID";
                count = TABGENERALCOUNT;
                break;
            default:
                statement = "SELECT * FROM " + TABITEM + " AS i" +
                        "INNER JOIN " + TABGENERAL + " AS g" +
                        " ON i.ID = g.ID" +
                        "INNER JOIN " + TABCONTROLLED + " AS c " +
                        "ON i.ID = c.ID";
                count = TABITEMCOUNT; //TODO: this needs to be added to general and item. Work out what the joins will return.
                break;
        }
        try {
            ps = db.prepareStatement(statement);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        ArrayList<String> ret = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        try {
            if (rs != null) {
                while (rs.next()) {
                    line = new StringBuilder();
                    for (int i = 1; i <= count; i++) {
                        line.append(rs.getString(i));
                    }
                    ret.add(line.toString());
                }
            }
            rs.close();
            ps.closeOnCompletion();
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getDatabase1");
        } catch (SQLException | NullPointerException e) {
            Log.print(e);
        }
        return ret;
    }

    /**
     * Get an ArrayList of the given table where the given ID is in place.
     *
     * @param type The table you would like to get. Use the public static  strings found in this class.
     * @param ID   The user or item ID to search for within the table.
     * @return An ArrayList of the records within the chosen table which match the given ID.
     */
    public static ArrayList<String> getDatabase(String type, String ID) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getDatabase2");
        String statement;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int count = 0;
        switch (type) {
            case TABPERSON:
                statement = "SELECT * FROM " + TABPERSON + " WHERE ID=?";
                count = TABPERSONCOUNT;
                break;
            case TABITEM:
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g" +
                        " ON i.ID = g.ID" +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID" +
                        " WHERE ID = ?";
                count = TABITEMCOUNT;
                break;
            case TABCONTROLLED:
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID" +
                        " WHERE ID = ?";
                count = TABCONTROLLEDCOUNT;
                break;
            case TABGENERAL:
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g " +
                        "ON i.ID = g.ID" +
                        " WHERE ID = ?";
                count = TABGENERALCOUNT;
                break;
            case "persGeneral":
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g " +
                        "ON i.ID = g.ID" +
                        " WHERE persID = ?";
                break;
            case "persControlled":
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID" +
                        " WHERE persID = ?";
                break;
            default:
                statement = "SELECT * FROM " + TABITEM + " i" +
                        "INNER JOIN " + TABGENERAL + " g" +
                        " ON i.ID = g.ID" +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID" +
                        " WHERE ID = ?";
                count = TABITEMCOUNT;
                break;
        }
        try {
            ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        ArrayList<String> ret = null;
        try {
            ret = rsToString(rs, count);
            rs.close();
            ps.closeOnCompletion();
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getDatabase2");
        } catch (SQLException e) {
            Log.print(e);
        }
        return ret;
    }

    /**
     * An ArrayList of every record within the given database that match the given date.
     *
     * @param type The table type that you would like to get. Use the public static  Strings found within this class.
     * @param date The date to search for within the records.
     * @return An ArrayList containing the records from the given table that match the given date.
     */
    public static ArrayList<String> getDatabase(String type, LocalDate date) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getDatabase3");
        String statement;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int count = 0;
        switch (type) {
            case TABPERSON:
                statement = "SELECT * FROM " + TABPERSON + " WHERE ID=?";
                count = TABPERSONCOUNT;
                break;
            case TABITEM:
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g" +
                        " ON i.ID = g.ID" +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID" +
                        " WHERE date > ?";
                count = TABITEMCOUNT;
                break;
            case TABCONTROLLED:
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID" +
                        " WHERE date > ?";
                count = TABCONTROLLEDCOUNT;
                break;
            case TABGENERAL:
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g " +
                        "ON i.ID = g.ID" +
                        " WHERE date > ?";
                count = TABGENERALCOUNT;
                break;
            default:
                statement = "SELECT * FROM " + TABITEM + " i" +
                        "INNER JOIN " + TABGENERAL + " g" +
                        " ON i.ID = g.ID" +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID" +
                        " WHERE date > ?";
                break;
        }
        ArrayList<String> ret = null;
        try {
            ps = db.prepareStatement(statement);
            ps.setDate(1, java.sql.Date.valueOf(date));
            rs = ps.executeQuery();
            ret = rsToString(rs, count);
            rs.close();
            ps.closeOnCompletion();
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getDatabase3");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        return ret;
    }

    /**
     * Reduce the quantity of a general item.
     *
     * @param ID  The ID of the item to reduce.
     * @param sub The number to subtract from the item.
     */
    public static void lowerQuantity(String ID, int sub) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in lowerQuantity");
        String statement = "UPDATE " + TABGENERAL + " SET " + COLGENERALQUANTITY + " = " +
                "((SELECT " + COLGENERALQUANTITY + " FROM " + TABGENERAL + " WHERE " + COLGENERALID + " = ?) - ?)" +
                " WHERE " + COLGENERALID + " = ?";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            ps.setInt(2, sub);
            ps.setString(3, ID);
            executePS(db, ps);
            System.out.println("_X_X_X_X_X_X_X_ DB closed in lowerQuantity");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
    }

    /**
     * Get the name of a given item or person.
     *
     * @param type The table name. Use the public static  strings found within this class.
     * @param ID   The ID of the item or person.
     * @return The name of the item or person.
     */
    public static Optional<String> getName(String type, String ID) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getName1");
        String statement;
        ResultSet rs;
        String out = null;
        switch (type) {
            case TABPERSON:
                statement = "SELECT " + COLPERSONNAME + " FROM " + TABPERSON + " WHERE " + COLPERSONID + "=?";
                break;
            case TABITEM:
                statement = "SELECT " + COLITEMNAME + " FROM " + TABITEM + "" +
                        " WHERE " + COLITEMID + " = ?";
                break;
            default:
                statement = "SELECT " + COLPERSONNAME + " FROM " + TABPERSON + " WHERE " + COLPERSONID + "=?";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
            if (rs.next()) {
                out = rs.getString(1);
            }
            rs.close();
            ps.closeOnCompletion();
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getName1");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        return Optional.ofNullable(out);
    }

    /**
     * Get the name every item within a given tabel
     *
     * @param type The name of the table to use. Use the public static  Strings found within this class.
     * @return An arraylist of every name in the table.
     */
    public static ArrayList<String> getName(String type) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getName2");
        String statement;
        ResultSet rs;
        ArrayList<String> out = new ArrayList<>();
        switch (type) {
            case TABPERSON:
                statement = "SELECT " + COLPERSONNAME + " FROM " + TABPERSON + "";
                break;
            case TABITEM:
                statement = "SELECT " + COLITEMNAME + " FROM " + TABITEM + "";
                break;
            case TABGENERAL:
                statement = "SELECT " + COLITEMNAME + " FROM " + TABITEM +
                        " JOIN " + TABGENERAL + " ON " + TABGENERAL + "." + COLGENERALID +
                        " = " + TABITEM + "." + COLITEMID + ";";
                break;
            case TABCONTROLLED:
                statement = "SELECT " + COLITEMNAME + " FROM " + TABITEM +
                        " JOIN " + TABCONTROLLED + " ON " + TABCONTROLLED + "." + COLCONTROLLEDID +
                        " = " + TABITEM + "." + COLITEMID + ";";
                break;
            case TABSET:
                statement = "SELECT " + COLSETNAME + " FROM " + TABSET + ";";
                break;
            case TABCONTROLLEDTYPE:
                statement = "SELECT " + COLCONTROLLEDTYPENAME + " FROM " + TABCONTROLLEDTYPE + ";";
                break;
            default:
                statement = "SELECT " + COLPERSONNAME + " FROM " + TABPERSON + "";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            rs = ps.executeQuery();
            ps.closeOnCompletion();
            while (rs.next()) {
                out.add(rs.getString(1));
            }
            rs.close();
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getName2");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        return out;
    }

    public static ArrayList<String> getDetails(String type) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getName2");
        String statement;
        ResultSet rs;
        ArrayList<String> out = new ArrayList<>();
        switch (type) {
            case TABPERSON:
                statement = "SELECT " + COLPERSONNAME + ", " + COLPERSONID + " FROM " + TABPERSON + "";
                break;
            case TABITEM:
                statement = "SELECT " + COLITEMNAME + ", " + COLITEMID + " FROM " + TABITEM + "";
                break;
            case TABGENERAL:
                statement = "SELECT " + COLITEMNAME + " FROM " + TABITEM +
                        " JOIN " + TABGENERAL + " ON " + TABGENERAL + "." + COLGENERALID +
                        " = " + TABITEM + "." + COLITEMID + ";";
                break;
            case TABCONTROLLED:
                statement = "SELECT " + COLITEMNAME + " FROM " + TABITEM +
                        " JOIN " + TABCONTROLLED + " ON " + TABCONTROLLED + "." + COLCONTROLLEDID +
                        " = " + TABITEM + "." + COLITEMID + ";";
                break;
            case TABSET:
                statement = "SELECT " + COLSETNAME + ", " + COLSETID + " FROM " + TABSET + ";";
                break;
            case TABCONTROLLEDTYPE:
                statement = "SELECT " + COLCONTROLLEDTYPENAME + ", " + COLCONTROLLEDTYPEID + " FROM " + TABCONTROLLEDTYPE + ";";
                break;
            default:
                statement = "SELECT " + COLPERSONNAME + " FROM " + TABPERSON + "";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            rs = ps.executeQuery();
            ps.closeOnCompletion();
            while (rs.next()) {
                out.add(rs.getString(2) + "\t\t" + rs.getString(1));
            }
            rs.close();
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getName2");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        return out;
    }

    /**
     * Get the ID of an item or person with the given name. Will return the first, not necessarily the only.
     *
     * @param type The table that you wish to get the name from. Use the public static  Strings found within this class.
     * @param name The name to search for.
     * @return The ID of the first record found which matches the name given.
     */
    @SuppressWarnings("Duplicates")
    public static Optional<String> getID(String type, String name) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getID");
        String statement;
        ResultSet rs;
        String out = null;
        switch (type) {
            case TABPERSON:
                statement = "SELECT " + COLPERSONID + " FROM " + TABPERSON + " WHERE " + COLPERSONNAME + "=?";
                break;
            case TABITEM:
                statement = "SELECT " + COLITEMID + " FROM " + TABITEM + "" +
                        " WHERE " + COLITEMNAME + " = ?";
                break;
            case TABCONTROLLEDTYPE:
                statement = "SELECT " + COLCONTROLLEDTYPEID + " FROM " + TABCONTROLLEDTYPE + " WHERE " + COLCONTROLLEDTYPENAME + " = ?;";
                break;
            default:
                statement = "SELECT " + COLPERSONID + " FROM " + TABPERSON + " WHERE " + COLPERSONNAME + " = ?";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                out = rs.getString(1);
            }
            rs.close();
            ps.closeOnCompletion();
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getID");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        return Optional.ofNullable(out);
    }

    /**
     * Get the password and salt of the member with the given barcode.
     *
     * @param barcode The barcode of the person to search for.
     * @return A String array with password at 0 and salt at 1
     */
    public static String[] getPassword(String barcode) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getPassword1");
        String statement = "SELECT " + COLPERSONPASSOWRD + ", " + COLPERSONSALT + " FROM " + TABPERSON + " WHERE " + COLPERSONID + " = ?";
        ResultSet rs;
        String[] out = new String[2];
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, barcode);
            rs = ps.executeQuery();
            if (rs.next()) {
                out[0] = rs.getString(COLPERSONPASSOWRD);
                System.out.println("Password: " + out[0]);
                out[1] = rs.getString(COLPERSONSALT);
                System.out.println("Salt: " + out[1]);
            } else System.out.print("userNotFound");
            rs.close();
            ps.closeOnCompletion();
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getPassword1");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        return out;
    }

    /**
     * Set the password of the person with the given ID to the password given. Store the salt alongside.
     *
     * @param ID       The ID of the person to change the password for.
     * @param password The password to enter into the database for this person
     * @param salt     The salt that the password was hashed with to enter into the database.
     */
    public static void setPassword(String ID, String password, String salt) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getPassword2");
        String statement = "UPDATE " + TABPERSON + " SET " + COLPERSONPASSOWRD + " = ?, " + COLPERSONSALT + " = ? WHERE " + COLPERSONID + " = ?";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, password);
            ps.setString(2, salt);
            ps.setString(3, ID);
            executePS(db, ps);
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getPassword2");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }

    }

    /**
     * Get the role of the given member. 0 for user, 1 for admin, 2 for root.
     *
     * @param barcode The barcode of the member to get the role of.
     * @return The role of the user: 0 for user, 1 for admin, 2 for root.
     */
    public static int getRole(String barcode) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getRole");
        String statement = "SELECT " + COLPERSONADMIN + " FROM " + TABPERSON + " WHERE " + COLPERSONID + " = ?";
        ResultSet rs;
        int admin = PersonDatabase.USER;
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, barcode);
            rs = ps.executeQuery();
            if (rs.next()) {
                admin = rs.getInt(1);
            }
            rs.close();
            ps.closeOnCompletion();
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getRole");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        return admin;
    }

    /**
     * Check that the entry for the given table and ID exists
     *
     * @param type The table to check for the given ID.
     * @param ID   The ID to search for.
     * @return True if the user exists. False otherwise. Multiple users with the same ID will return true.
     */
    public static boolean entryExists(String type, String ID) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in entryExists");
        String statement;
        ResultSet rs;
        boolean ret = false;
        switch (type) {
            case "person":
                statement = "SELECT " + COLPERSONID + " FROM " + TABPERSON + " WHERE " + COLPERSONID + " = ?";
                break;
            case "item":
                statement = "SELECT " + COLITEMID + " FROM " + TABITEM + " WHERE " + COLITEMID + " = ?";
                break;
            default:
                statement = "SELECT " + COLPERSONID + " FROM " + TABPERSON + " WHERE " + COLPERSONID + " = ?";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
            if (rs.next()) {
                ret = rs.getString(COLPERSONID).equals(ID);
            }
            rs.close();
            ps.closeOnCompletion();
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in entryExists");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        return ret;
    }

    /**
     * Export the given table to the give path. Item table will be joined with the relevant tables.
     *
     * @param type The table to export.
     * @param path The location within the filesystem to export the table(s) to.
     */
    @SuppressWarnings("Duplicates")
    public static void export(String type, String path) { //TODO: At least one of these would work in normal SQL.
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in export");
        String statement;
        switch (type) {
            case "person":
                statement = "SELECT * INTO OUTFILE \'?\' " +
                        "FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY \'\"\' " +
                        "LINES TERMINATED BY \'\n\' " +
                        "FROM " + TABPERSON + "";
                break;
            case "item":
                statement = "SELECT * INTO OUTFILE '?' " +
                        "FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY \'\"\' " +
                        "LINES TERMINATED BY \'\n\' FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g" +
                        " ON i.ID = g.ID" +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID";
                break;
            case "controlled":
                statement = "SELECT *  FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID INTO OUTFILE '?' " + "\n" +
                        "FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"' " +
                        "LINES TERMINATED BY '\n'";
                break;
            case "general":
                statement = "SELECT * INTO OUTFILE '?' " +
                        "FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY \'\"\' " +
                        "LINES TERMINATED BY \'\n\' FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g " +
                        "ON i.ID = g.ID";
                break;
            default:
                statement = "SELECT * INTO OUTFILE '?' " +
                        "FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY \'\"\' " +
                        "LINES TERMINATED BY \'\n\' FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g" +
                        " ON i.ID = g.ID" +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString('1', path);
            executePS(db, ps);
            System.out.println("_X_X_X_X_X_X_X_ DB closed in export");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
    }

    /**
     * Get the quantity of a given general item.
     *
     * @param ID The ID of the general item.
     * @return
     */
    public static int getQuantity(String ID) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in getQuantity");
        String statement = "SELECT " + COLGENERALQUANTITY + " FROM " + TABGENERAL + " WHERE " + COLGENERALID + " = ?";
        ResultSet rs = null;
        int ret = 0;
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
            if (rs.next()) {
                ret = rs.getInt(1);
            }
            rs.close();
            ps.closeOnCompletion();
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in getQuantity");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        return ret;
    }

    /**
     * Set the quantity of the general item given by the ID.
     *
     * @param ID       The ID of the item to set the ID of.
     * @param quantity The new quantity. Set to exactly the value given in the parameter.
     */
    public static void setQuantity(String ID, int quantity) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in setQuantity");
        String statement = "UPDATE " + TABGENERAL + " SET " + COLGENERALQUANTITY + "=?  WHERE " + COLGENERALID + " = ?";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setInt(1, quantity);
            ps.setString(2, ID);
            executePS(db, ps);
            System.out.println("_X_X_X_X_X_X_X_ DB closed in setQuantity");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
    }

    /**
     * Update an entry in the item database.
     *
     * @param ID    The ID of the item to change.
     * @param name  The new name of the item.
     * @param newID The new ID of the item. Re-enter the same ID as above for no change.
     */
    public static void updateEntry(String ID, String name, String newID, String type) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in updateEntry1");
        String statement;
        switch (type) {
            case TABITEM:
                statement = "UPDATE " + TABITEM + " SET " + COLITEMNAME + " = ?, " + COLITEMID + " = ?" +
                        " WHERE " + COLITEMID + " = ?";
                break;
            case TABPERSON:
                statement = "UPDATE " + TABPERSON + " SET " + COLPERSONNAME + " = ?, " + COLPERSONID + " =?" +
                        " WHERE " + COLPERSONID + " =?;";
                break;
            default:
                statement = "UPDATE " + TABPERSON + " SET " + COLPERSONNAME + " = ?, " + COLPERSONID + " =?" +
                        " WHERE " + COLPERSONID + " =?;";
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, name);
            ps.setString(2, newID);
            ps.setString(3, ID);
            executePS(db, ps);
            System.out.println("_X_X_X_X_X_X_X_ DB closed in updateEntry1");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
    }

    public static void updateEntry(String ID, int role) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in updateEntry2");
        String statement = "UPDATE " + TABPERSON + " SET " + COLPERSONADMIN + " = ? WHERE " + COLPERSONID + "= ?;";

        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setInt(1, role);
            ps.setString(2, ID);
            executePS(db, ps);
            System.out.println("_X_X_X_X_X_X_X_ DB closed in updateEntry2");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
    }

    /**
     * Will return the boolean check as to whether the item is controlled.
     *
     * @param ID The ID of the item to check.
     * @return True if item is controlled, false otherwise.
     */
    public static boolean isItemControlled(String ID) {
        Connection db = getDatabase().get();
        System.out.println("_X_X_X_X_X_X_X_ New DB in isItemControlled");
        String statement = "SELECT " + COLCONTROLLEDID + " FROM " + TABCONTROLLED + " WHERE " + COLCONTROLLEDID + " = ?";
        ResultSet rs;
        boolean ret = false;
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
            if (rs.next()) {
                ret = true;
            }
            rs.close();
            ps.closeOnCompletion();
            db.close();
            System.out.println("_X_X_X_X_X_X_X_ DB closed in isItemControlled");
        } catch (SQLException e) {
            Log.print(e);
            System.exit(32);
        }
        return ret;
    }

}
