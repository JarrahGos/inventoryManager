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


public class SQLInterface {
    private String URL = "jdbc:mariadb//localhost:3306/toc"; // these will be initialised from the file.
    private String user = "jarrah"; // when sure it works, remove these.
    private String password = "password";
    private Connection db;
    private Settings config = new Settings();
    public SQLInterface() throws FileNotFoundException
    {
        try {
            Class.forName("org.mariadb.jdbc.Driver").newInstance();
        }
        catch (ClassNotFoundException e) {
            System.out.println("could not find driver class\n" + e.toString());
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            System.out.println("could not create instance\n" + e.toString());
        }
        String[] settings = config.SQLInterfaceSettings();
        URL = settings[0];
        user = settings[1];
        password = settings[2];
        try {
            db = DriverManager.getConnection(URL, user, password);
            System.out.println("\n\n\n\n\n\n\n DB Connected \n\n\n\n\n\n\n\n\n\n");
        }
        catch (java.sql.SQLException e){
            System.out.println("error connecting to DB, check the settings\n" + e.toString());
            System.out.println(URL + "\n" + user + "\n" + password);
        }
        System.out.println(db == null ? "null" : db.toString());
    }
    public final String getDatabase(String type) {
        String statement = "";
        ResultSet rs = null;
        switch (type) {
            case "person":
                statement = "Select * from people";
                break;
            case "item":
                statement = "select * from controlledItems AND GeneralItems";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            rs = ps.executeQuery(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rs != null) {
            return rs.toString();
        }
        return "";
    }

    public void deleteEntry(String type, String barcode) {
        String statement = "";
        switch (type) {
            case "person":
                statement = "DELETE * FROM people WHERE barcode = ?";
                break;
            case "GeneralItem":
                statement = "DELETE * FROM generalITems WHERE barcode = ?";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, barcode);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public final String SQLRead(String table,String columnName, String where)
    {
        String result = "";
        ResultSet rs = null;
        try {
            Statement request = db.createStatement();
            rs = request.executeQuery("SELECT " + columnName + " FROM " + table + " WHERE " + where);
        }
        catch (java.sql.SQLException e) {
            System.out.println("Unable to read database.\n" + e.toString());
        }
        try {
            if(rs != null)
                result = rs.getString(1);
        }
        catch (java.sql.SQLException e) {
            System.out.println("could not read string from result" + e.toString());
        }
        return result;
    }
    public final String[] SQLReadSet(String table, String columnName, String where)
    {
        String[] result = null;
        ResultSet rs = null;
        try {
            Statement request = db.createStatement();
            rs = request.executeQuery("SELECT " + columnName + " FROM " + table + " WHERE " + where);
        }
        catch (java.sql.SQLException e) {
            System.out.println("Unable to read database.\n" + e.toString());
        }
        try {
            if(rs != null){
                for(int i = 0; rs.next(); i++) {
                    result[i] = rs.getString(1);
                }
            }
        }
        catch (java.sql.SQLException e) {
            System.out.println("could not read string from result" + e.toString());
        }
        return result;
    }
    public final void SQLSet(String table, String set, String where)
    {
        try {
            Statement request = db.createStatement();
            request.executeQuery("UPDATE " + table + " SET " + set + " WHERE " + where);
        }
        catch (java.sql.SQLException e) {
            System.out.println("Unable to write to database.\n" + e.toString());
        }
    }
    public final void SQLDelete(String table, String where)
    {
        try {
            Statement request = db.createStatement();
            request.executeQuery("UPDATE " + table + " DELETE " + " WHERE " + where);
        }
        catch (java.sql.SQLException e) {
            System.out.println("Unable to write to database.\n" + e.toString());
        }
    }
    public final boolean SQLOutputCSV(String table, String file)
    {
        try {
            Statement request = db.createStatement();
            request.executeQuery("SELECT a,b,a+b INTO OUTFILE '" + file + "'" +
                    "FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"'" +
                    "LINES TERMINATED BY '\n'" +
                    "FROM " + table);
            return true;
        }
        catch (java.sql.SQLException e) {
            System.out.println("Unable to write out table  " + table + " to csv.\n" + e.toString());
            return false;
        }
    }
    public final void SQLInsert(String table, String values) // make a field for the column required
    {
        try {
            Statement request = db.createStatement();
            request.executeUpdate("INSERT INTO " + table + " VALUES " + values + ";");
        }
        catch (java.sql.SQLException e) {
            System.out.println("Unable to write to database.\n" + e.toString());
        }
    }

}