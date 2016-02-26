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

public class ItemLog {
    private final SimpleStringProperty ID;
    private final SimpleStringProperty outDate;
    private final SimpleStringProperty inDate;
    private final SimpleStringProperty persID;
    private final SimpleStringProperty controlled;
    private final SimpleStringProperty returnedBy;
    private final SimpleStringProperty itemID;

    public ItemLog(String extID, String extOutDate, String extInDate, String extPersID,
                   String extControlled, String extReturnedBy, String extItemID) {
        this.ID = new SimpleStringProperty(extID);
        this.outDate = new SimpleStringProperty(extOutDate);
        this.inDate = new SimpleStringProperty(extInDate);
        this.persID = new SimpleStringProperty(extPersID);
        this.controlled = new SimpleStringProperty(extControlled);
        this.returnedBy = new SimpleStringProperty(extReturnedBy);
        this.itemID = new SimpleStringProperty(extItemID);
    }

    public String getID() {
        return ID.get();
    }

    public void setID(String extID) {
        ID.set(extID);
    }

    public String getOutDate() {
        return outDate.get();
    }

    public void setOutDate(String outDate) {
        this.outDate.set(outDate);
    }

    public SimpleStringProperty outDateProperty() {
        return outDate;
    }

    public String getInDate() {
        return inDate.get();
    }

    public void setInDate(String inDate) {
        this.inDate.set(inDate);
    }

    public SimpleStringProperty inDateProperty() {
        return inDate;
    }

    public String getPersID() {
        return persID.get();
    }

    public void setPersID(String persID) {
        this.persID.set(persID);
    }

    public SimpleStringProperty persIDProperty() {
        return persID;
    }

    public String getControlled() {
        return controlled.get();
    }

    public void setControlled(String controlled) {
        this.controlled.set(controlled);
    }

    public SimpleStringProperty controlledProperty() {
        return controlled;
    }

    public String getReturnedBy() {
        return returnedBy.get();
    }

    public void setReturnedBy(String returnedBy) {
        this.returnedBy.set(returnedBy);
    }

    public SimpleStringProperty returnedByProperty() {
        return returnedBy;
    }

    public String getItemID() {
        return itemID.get();
    }

    public void setItemID(String itemID) {
        this.itemID.set(itemID);
    }

    public SimpleStringProperty itemIDProperty() {
        return itemID;
    }


}

