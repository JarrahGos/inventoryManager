/*
 * Copyright (C) 2014  Jarrah Gosbell
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General public static  License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General public static  License for more details.
 * <p>
 * You should have received a copy of the GNU General public static  License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package inventoryManager.formatters;

import javafx.beans.property.SimpleStringProperty;

/***
 * Copyright (C) 2015  Jarrah Gosbell
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public class ReturnItem {
    private final SimpleStringProperty ID;
    private final SimpleStringProperty name;
    private final SimpleStringProperty userID;

    public ReturnItem(String extID, String extName, String extUID) {
        this.ID = new SimpleStringProperty(extID);
        this.name = new SimpleStringProperty(extName);
        this.userID = new SimpleStringProperty(extUID);
    }

    public String getID() {
        return ID.get();
    }

    public void setID(String extID) {
        ID.set(extID);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String extDate) {
        name.set(extDate);
    }

    public String getUserID() {
        return userID.get();
    }

    public void setUserID(String userID) {
        this.userID.set(userID);
    }

    public SimpleStringProperty userIDProperty() {
        return userID;
    }
}

