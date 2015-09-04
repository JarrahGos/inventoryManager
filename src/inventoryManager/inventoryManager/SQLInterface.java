package inventoryManager;

/***
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

/*
 * Author: Jarrah Gosbell
 * Student Number: z5012558
 * Class: PersonDatabase
 * Description: This program will allow for the input and retreval of the person database and will set the limits of the database.
 */


import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;


public class SQLInterface {
    private String URL = "jdbc:sqlite:/home/jarrah/ideaProjects/inv.db"; // these will be initialised from the file.
    private String user = "jarrah"; // when sure it works, remove these.
    private String password = "password";
    private Connection db;

    public SQLInterface()
    {
        try {
            Class.forName("org.sqlite.JDBC").newInstance();
        }
        catch (ClassNotFoundException e) {
//            Log.print("could not find driver class\n" + e.toString());
        }
        catch (InstantiationException | IllegalAccessException e)
        {
//            Log.print("could not create instance\n" + e.toString());
        }
        String[] settings = new String[0];
        try {
            settings = Settings.SQLInterfaceSettings();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        URL = settings[0];
//        user = settings[1];
//        password = settings[2];
        try {
            db = DriverManager.getConnection(URL);
//            System.out.println("\n\n\n\n\n\n\n DB Connected \n\n\n\n\n\n\n\n\n\n");
        }
        catch (java.sql.SQLException e){
//            Log.print("error connecting to DB, check the settings\n" + e.toString());
//            Log.print(URL + "\n" + user + "\n" + password);
        }
//        System.out.println(db == null ? "null" : db.toString());
    }

    public void deleteEntry(String type, String barcode) {
        String statement = "";
        switch (type) {
            case "person":
                statement = "DELETE * FROM people WHERE barcode = ?";
                break;
            case "GeneralItem":
                statement = "DELETE * FROM generalItems WHERE barcode = ?";
                break;
            case "controlledItem": statement = "DELETE * FROM controlledItems WHERE barcode = \"?\""; // TODO: this will delete controlled but not item. Use the key and a delete on cascade.
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, barcode);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addEntry(String ID, String name, boolean admin, boolean root, String password, String salt) { // add a new person
        String statement = "INSERT INTO people (ID, name, admin, root, password, salt)" +
                "VALUES(?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            ps.setString(2, name);
            ps.setBoolean(3, admin);
            ps.setBoolean(3, root);
            ps.setString(4, password);
            ps.setString(5, salt);
            ps.execute();
        } catch (SQLException e) {
            Log.print(e);
        }
    }
    public void addEntry(String name) // add new set
    {
        String statement = "INSERT INTO sets (name) VALUES(?)";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, name);
            ps.execute();
        }
        catch (SQLException e) {
            Log.print(e);
        }
    }
    public void addEntry(String ID, String name, String setName, String Description, Long Quantity) { // Add general Item
        String statement = "INSERT INTO items (ID, name)" +
                "VALUES(?, ?)";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            ps.setString(2, name);
            ps.execute();
        } catch (SQLException e) {
            Log.print(e);
        }
        statement = "INSERT INTO general (ID, description, quantity)" +
                "VALUES(?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            ps.setString(2, Description);
            ps.setLong(3, Quantity);
            ps.execute();
        } catch (SQLException e) {
            Log.print(e);
        }
        if(setName != null && !setName.isEmpty()) {
            statement = "SELECT ID FROM sets where name = \"?\"";
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
            } catch (SQLException e) {
                Log.print(e);
            }

        }
    }
    public void addEntry(String ID, String name, String setName, String state, String tagpos, String type) { // add Controlled Item
        String statement = "INSERT INTO items (ID, name)" +
                "VALUES(?, ?)"; // Sort SetID at the end. There may not be a set ID for every item.
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            ps.setString(2, name);
            ps.execute();
        } catch (SQLException e) {
            Log.print(e);
        }
        if(type != null && !type.isEmpty()) {
            statement = "SELECT ID FROM type where name = \"?\"";
            ResultSet rs = null;
            try {
                PreparedStatement ps = db.prepareStatement(statement);
                ps.setString(1, type);
                rs = ps.executeQuery();

                if (!rs.next()) {
                    statement = "INSERT INTO controlledType (\"?\")";
                    ps = db.prepareStatement(statement);
                    ps.setString(1, type);
                    ps.execute();
                    statement = "SELECT ID FROM type where name = \"?\"";
                    ps = db.prepareStatement(statement);
                    ps.setString(1, type);
                    rs = ps.executeQuery();
                }

                statement = "INSERT INTO controlled (ID, tagpos, State)" +
                        "VALUES(?, ?, ?, ?, ?)";
                try {
                    ps = db.prepareStatement(statement);
                    ps.setString(1, ID);
                    ps.setString(2, tagpos);
                    ps.setInt(3, rs.getInt("ID"));
                    ps.setString(4, state);
                    ps.execute();
                } catch (SQLException e) {
                    Log.print(e);
                }
            } catch (SQLException e) {
                Log.print(e);
            }
        }
        if(setName != null && !setName.isEmpty()) {
            statement = "SELECT ID FROM sets where name = \"?\"";
            ResultSet rs = null;
            try {
                PreparedStatement ps = db.prepareStatement(statement);
                ps.setString(1, setName);
                rs = ps.executeQuery();
                if (rs.next()) {
                    statement = ""; // TODO: List of items is fucked.
                    ps = db.prepareStatement(statement);
                    ps.setString(1, name);
                    ps.execute();
                }
            } catch (SQLException e) {
                Log.print(e);
            }

        }
    }
    public void addEntry(String ID, String name) {
        String statement = "INSERT INTO items (ID, name) " +
                "VALUES(\"?\", \"?\")";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            ps.setString(2, name);
            ps.execute();
        }
        catch (SQLException e) {
            Log.print(e);
        }
    }
    public void addLog(String persID, String adminID) { // add to change password log.
        String statement = "INSERT INTO personLog (persID, date, authName) " +
                "VALUES(\"?\", NOW(), (Select name FROM people where ID = \"?\")";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, persID);
            ps.setString(2, adminID);
            ps.execute();
        }
        catch (SQLException e) {
            Log.print(e);
        }

    }
    public void addLog(String itemID, String persID, boolean controlled) { // Sign an item out
        //TODO: Should this check the item as out in the controlled table?
        String statment = "INSERT INTO itemLog (ID, date, out, in, persID, controlled) " +
                "VALUES(\"?\", NOW(), TRUE, \"FALSE\", \"?\", \"?\")";
        try {
            PreparedStatement ps = db.prepareStatement(statment);
            ps.setString(1, itemID);
            ps.setString(2, persID);
            ps.setBoolean(3, controlled);
            ps.execute();
        }
        catch (SQLException e) {
            Log.print(e);
        }
    }
    public void returnItem(String itemID) { // Return an item
        String statement = "update itemLog SET in=TRUE, inDate=NOW()" +
                "WHERE ID=\"?\"";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, itemID);
            ps.execute();
        }
        catch (SQLException e) {
            Log.print(e);
        }
    }
    public void returnItem(String itemID, String persID) { // Return a general item.
        String statement = "update itemLog SET in=TRUE, inDate=NOW()" +
                "WHERE ID=\"?\" AND persID=\"?\"";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, itemID);
            ps.setString(2, persID);
            ps.execute();
        }
        catch (SQLException e) {
            Log.print(e);
        }
    }

    /**
    *<b>On Logs</b>
    * The logs in this system are created in the following suituations:
    * <ul>
    *     <li>Password reset</li>
    *     <li>Item signed out</li>
    * </ul>
    * Thus, logs will be available for people (passwords), and items.
    * Logs will be condensed by item type, date and ID.
     */
    public ArrayList<String> getLog(String type) {
        String statement;
        ResultSet rs = null;
        switch (type) {
            case "person":
                statement = "SELECT * FROM personLog ";
                break;
            case "item":
                statement = "Select * FROM itemLog";
                break;
            case "controlled":
                statement = "SELECT * FROM itemLog " +
                        "WHERE controlled=TRUE";
                break;
            case "general":
                statement = "SELECT * FROM itemLog " +
                        "WHERE controlled=FALSE";
                break;
            default:
                statement = "Select * FROM itemLog";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            rs = ps.executeQuery();
        }
        catch (SQLException e) {
            Log.print(e);
        }
        ArrayList<String> ret = null;
        try {
            for (int i = 0; rs.next(); i++) {
                ret.add(rs.toString()); // TODO: test this toString
            }
        }
        catch (SQLException e) {
            Log.print(e);
        }
        return ret;
    }
    public ArrayList<String> getLog(String type, String ID) {
        String statement;
        ResultSet rs = null;
        switch (type) {
            case "person":
                statement = "SELECT * FROM personLog " +
                        "WHERE ID = \"?\"";
                break;
            case "item":
                statement = "Select * FROM itemLog " +
                        "WHERE ID = \"?\"";
                break;
            case "controlled":
                statement = "SELECT * FROM itemLog " +
                        "WHERE controlled=TRUE AND " +
                        "ID = \"?\"";
                break;
            case "general":
                statement = "SELECT * FROM itemLog " +
                        "WHERE controlled=FALSE AND " +
                        "ID = \"?\"";
                break;
            default:
                statement = "Select * FROM itemLog " +
                        "WHERE ID = \"?\"";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
        }
        catch (SQLException e) {
            Log.print(e);
        }
        ArrayList<String> ret = null;
        try {
            for (int i = 0; rs.next(); i++) {
                ret.add(rs.toString()); // TODO: test this toString.
            }
        }
        catch (SQLException e) {
            Log.print(e);
        }
        return ret;
    }
    public ArrayList<String> getLog(String type, LocalDate date) {
        String statement;
        ResultSet rs = null;
        switch (type) {
            case "person":
                statement = "SELECT * FROM personLog " +
                        "WHERE date > \"?\"";
                break;
            case "item":
                statement = "Select * FROM itemLog " +
                        "WHERE date > \"?\"";
                break;
            case "controlled":
                statement = "SELECT * FROM itemLog " +
                        "WHERE controlled=TRUE AND " +
                        "date > \"?\"";
                break;
            case "general":
                statement = "SELECT * FROM itemLog " +
                        "WHERE controlled=FALSE AND " +
                        "date >  \"?\"";
                break;
            default:
                statement = "Select * FROM itemLog " +
                        "WHERE date > \"?\"";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setDate(1, java.sql.Date.valueOf(date));
            rs = ps.executeQuery();
        }
        catch (SQLException e) {
            Log.print(e);
        }
        ArrayList<String> ret = null;
        try {
            for (int i = 0; rs.next(); i++) {
                ret.add(rs.toString()); // TODO: test this toString.
            }
        }
        catch (SQLException e) {
            Log.print(e);
        }
        return ret;
    }
    public ArrayList<String> getDatabase(String type) {
        String statement;
        ResultSet rs = null;
        switch (type) {
            case "person":
                statement = "SELECT * FROM people ";
                break;
            case "item":
                statement = "Select * FROM items i" +
                        "INNER JOIN general g" +
                        " on i.ID = g.ID" +
                        "INNER JOIN controlled c " +
                        "on i.ID = c.ID";
                break;
            case "controlled":
                statement = "SELECT * FROM items i " +
                        "INNER JOIN controlled c " +
                        "on i.ID = c.ID";
                break;
            case "general":
                statement = "SELECT * FROM items i " +
                        "INNER JOIN general g " +
                        "ON i.ID = g.ID";
                break;
            default:
                statement = "Select * FROM items i" +
                        "INNER JOIN general g" +
                        " on i.ID = g.ID" +
                        "INNER JOIN controlled c " +
                        "on i.ID = c.ID";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            rs = ps.executeQuery();
        }
        catch (SQLException e) {
            Log.print(e);
        }
        ArrayList<String> ret = null;
        try {
            for (int i = 0; rs.next(); i++) {
                ret.add(rs.toString()); // TODO: Test this toString.
            }
        }
        catch (SQLException e) {
            Log.print(e);
        }
        return ret;
    }
    public ArrayList<String> getDatabase(String type, String ID)
    {
        String statement;
        ResultSet rs = null;
        switch (type) {
            case "person":
                statement = "SELECT * FROM people WHERE ID=\"?\"";
                break;
            case "item":
                statement = "Select * FROM items i " +
                        "INNER JOIN general g" +
                        " on i.ID = g.ID" +
                        "INNER JOIN controlled c " +
                        "on i.ID = c.ID" +
                        " WHERE ID = \"?\"";
                break;
            case "controlled":
                statement = "SELECT * FROM items i " +
                        "INNER JOIN controlled c " +
                        "on i.ID = c.ID" +
                        " WHERE ID = \"?\"";
                break;
            case "general":
                statement = "SELECT * FROM items i " +
                        "INNER JOIN general g " +
                        "ON i.ID = g.ID" +
                        " WHERE ID = \"?\"";
                break;
            case "persGeneral":
                statement = "SELECT * FROM items i " +
                        "INNER JOIN general g " +
                        "ON i.ID = g.ID" +
                        " WHERE persID = \"?\"";
                break;
            case "persControlled":
                statement = "SELECT * FROM items i " +
                        "INNER JOIN controlled c " +
                        "ON i.ID = c.ID" +
                        " WHERE persID = \"?\"";
                break;
            default:
                statement = "Select * FROM items i" +
                        "INNER JOIN general g" +
                        " on i.ID = g.ID" +
                        "INNER JOIN controlled c " +
                        "on i.ID = c.ID" +
                        " WHERE ID = \"?\"";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
        }
        catch (SQLException e) {
            Log.print(e);
        }
        ArrayList<String> ret = null;
        try {
            for (int i = 0; rs.next(); i++) {
                ret.add(rs.toString()); // TODO: test this toString
            }
        }
        catch (SQLException e) {
            Log.print(e);
        }
        return ret;
    }
    public ArrayList<String> getDatabase(String type, LocalDate date) {
        String statement;
        ResultSet rs = null;
        switch (type) {
            case "person":
                statement = "SELECT * FROM people WHERE ID=\"?\"";
                break;
            case "item":
                statement = "Select * FROM items i " +
                        "INNER JOIN general g" +
                        " on i.ID = g.ID" +
                        "INNER JOIN controlled c " +
                        "on i.ID = c.ID" +
                        " WHERE date > \"?\"";
                break;
            case "controlled":
                statement = "SELECT * FROM items i " +
                        "INNER JOIN controlled c " +
                        "on i.ID = c.ID" +
                        " WHERE date > \"?\"";
                break;
            case "general":
                statement = "SELECT * FROM items i " +
                        "INNER JOIN general g " +
                        "ON i.ID = g.ID" +
                        " WHERE date > \"?\"";
                break;
            default:
                statement = "Select * FROM items i" +
                        "INNER JOIN general g" +
                        " on i.ID = g.ID" +
                        "INNER JOIN controlled c " +
                        "on i.ID = c.ID" +
                        " WHERE date > \"?\"";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setDate(1, java.sql.Date.valueOf(date));
            rs = ps.executeQuery();
        } catch (SQLException e) {
            Log.print(e);
        }
        ArrayList<String> ret = null;
        try {
            for (int i = 0; rs.next(); i++) {
                ret.add(rs.toString()); // TODO: test this toString
            }
        } catch (SQLException e) {
            Log.print(e);
        }
        return ret;
    }
    public void lowerQuantity(String ID, int sub) {
        String statement = "UPDATE general SET quantity = " +
                "((Select quantity From general WHERE ID = \"?\") - \"?\")" +
                " WHERE ID = \"?\"";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            ps.setInt(2, sub);
            ps.setString(3, ID);
            ps.execute();
        } catch (SQLException e) {
            Log.print(e);
        }
    }
    public String getName(String type, String ID) {
        String statement;
        ResultSet rs;
        String out = "";
        switch (type) {
            case "person":
                statement = "SELECT name FROM people WHERE ID=\"?\"";
                break;
            case "item":
                statement = "Select name FROM items" +
                        " WHERE name = \"?\"";
                break;
            default:
                statement = "SELECT name FROM people WHERE ID=\"?\"";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
            out = rs.getString(0);
        } catch (SQLException e) {
            Log.print(e);
        }
        return out;
    }
    public ArrayList<String> getName(String type) {
        String statement;
        ResultSet rs;
        ArrayList<String> out = new ArrayList<>();
        switch (type) {
            case "person":
                statement = "SELECT name FROM people";
                break;
            case "item":
                statement = "Select name FROM items";
                break;
            default:
                statement = "SELECT name FROM people";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            rs = ps.executeQuery();
            while(rs.next()) {
                out.add(rs.getString(0));
            }
        } catch (SQLException e) {
            Log.print(e);
        }
        return out;
    }
    public String getID(String type, String name) {
        String statement;
        ResultSet rs;
        String out = "";
        switch (type) {
            case "person":
                statement = "SELECT ID FROM people WHERE name=\"?\"";
                break;
            case "item":
                statement = "Select ID FROM items" +
                        " WHERE name = \"?\"";
                break;
            default:
                statement = "SELECT name FROM people WHERE name=\"?\"";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, name);
            rs = ps.executeQuery();
            out = rs.getString(0);
        } catch (SQLException e) {
            Log.print(e);
        }
        return out;
    }
    public String[] getPassword(String barcode) {
        String statement = "SELECT password, salt FROM people WHERE ID = \"?\"";
        ResultSet rs;
        String[] out = new String[2];
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, barcode);
            rs = ps.executeQuery();
            out[0] = rs.getString("password");
            out[1] = rs.getString("salt");
        } catch (SQLException e) {
            Log.print(e);
        }
        return out;
    }
    public void setPassword(String ID, String password, String salt) {
        String statement = "UPDATE people SET password = \"?\", salt = \"?\" WHERE ID = \"?\"";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, password);
            ps.setString(2, salt);
            ps.setString(3, ID);
            ps.execute();
        }
        catch (SQLException e) {
            Log.print(e);
        }

    }
    public int getRole(String barcode) {
        String statement = "SELECT admin FROM people WHERE ID = \"?\"";
        ResultSet rs;
        boolean admin = false;
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, barcode);
            rs = ps.executeQuery();
            admin = rs.getBoolean(1);
            if(admin) {
                statement = "SELECT root FROM people WHERE ID = \"?\"";
                ps = db.prepareStatement(statement);
                ps.setString(1, barcode);
                rs = ps.executeQuery();
                admin = rs.getBoolean(1);
                if (admin) return PersonDatabase.ROOT;
                return PersonDatabase.ADMIN;
            }
        } catch (SQLException e) {
            Log.print(e);
        }
        return PersonDatabase.USER;
    }
    public boolean entryExists(String type, String ID) {
        String statement;
        ResultSet rs;
        switch (type) {
            case "person":
                statement = "SELECT name FROM people WHERE ID = \"?\"";
                break;
            case "item":
                statement = "Select name FROM items WHERE ID = \"?\"";
                break;
            default:
                statement = "SELECT name FROM people WHERE ID = \"?\"";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
            if(rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            Log.print(e);
        }
        return false;
    }
    public void export(String type, String path) {
        String statement;
        switch (type) {
            case "person":
                statement = "SELECT * INTO OUTFILE \'?\' " +
                        "FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY \'\"\' " +
                        "LINES TERMINATED BY \'\n\' " +
                        "FROM people";
                break;
            case "item":
                statement = "Select * INTO OUTFILE '?' \" +\n" +
                        "FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY \'\"\' " +
                        "LINES TERMINATED BY \'\n\' FROM items i " +
                        "INNER JOIN general g" +
                        " on i.ID = g.ID" +
                        "INNER JOIN controlled c " +
                        "on i.ID = c.ID";
                break;
            case "controlled":
                statement = "Select * INTO OUTFILE '?' \" +\n" +
                        "FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY \'\"\' " +
                        "LINES TERMINATED BY \'\n\' FROM items i " +
                        "INNER JOIN controlled c " +
                        "on i.ID = c.ID";
                break;
            case "general":
                statement = "Select * INTO OUTFILE '?' \" +\n" +
                        "FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY \'\"\' " +
                        "LINES TERMINATED BY \'\n\' FROM items i " +
                        "INNER JOIN general g " +
                        "on i.ID = g.ID";
                break;
            default:
                statement = "Select * INTO OUTFILE '?' \" +\n" +
                        "FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY \'\"\' " +
                        "LINES TERMINATED BY \'\n\' FROM items i " +
                        "INNER JOIN general g" +
                        " on i.ID = g.ID" +
                        "INNER JOIN controlled c " +
                        "on i.ID = c.ID";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, path);
            ps.execute();
        } catch (SQLException e) {
            Log.print(e);
        }
    }
    public int getQuantity(String ID) {
        String statement = "SELECT quantity FROM general WHERE ID = \"?\"";
        ResultSet rs = null;
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
            if (rs != null) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            Log.print(e);
        }
        return 0;
    }
    public void setQuantity(String ID, int quantity) {
        String statement = "UPDATE general SET quantity=\"?\"  WHERE ID = \"?\"";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setInt(1, quantity);
            ps.setString(2, ID);
            ps.execute();
        } catch (SQLException e) {
            Log.print(e);
        }
    }
    public void updateEntry(String ID, String name, String newID) {
        String statement = "UPDATE item SET name = \"?\", set ID = \"?\" " +
                "WHERE ID = \"?\"";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, name);
            ps.setString(2, newID);
            ps.setString(3, ID);
            ps.execute();
        } catch (SQLException e) {
            Log.print(e);
        }
    }
    public boolean isItemControlled(String ID) {
        String statement = "SELECT ID FROM controlled WHERE ID = \"?\"";
        ResultSet rs;
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            Log.print(e);
        }
        return false;
    }
    public boolean isAdmin(String ID) {
        String statement = "SELECT admin FROM people WHERE ID = \"?\"";
        ResultSet rs = null;
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
            if (rs != null) {
                rs.next();
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            Log.print(e);
        }
        return false;
    }
    public boolean isRoot(String ID) {
        String statement = "SELECT root FROM people WHERE ID = \"?\"";
        ResultSet rs = null;
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
            if (rs != null) {
                rs.next();
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            Log.print(e);
        }
        return false;
    }
}
