package inventoryManager;

import inventoryManager.formatters.ReturnItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

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

/*
* Author: Jarrah Gosbell
* Student Number: z5012558
* Class: CheckOut
* Description: This program will allow for the creation, retrieval, modification and deletion of checkOuts created from items in the product database.
*/

final class ReturnCheckOut {
    // create the necessary variables in the order of use
    /**
     * A list of all items which are currently in the checkout
     */
    private LinkedList<ReturnItem> items;
    /**
     * The size of the above two lists
     */
    private int logicalSize;

    private ItemDatabase itemDB = new ItemDatabase();

    /**
     * Construct a new checkout with no items
     */
    public ReturnCheckOut() {
        items = new LinkedList<>();
        logicalSize = 0;
    }

    /**
     * Add a new product to the checkout
     */
    public final void addProduct(inventoryManager.formatters.ReturnItem ret) {
        items.add(ret);
        logicalSize++;
    }

    /**
     * Get the names and quantities of everything in the checkout
     *
     * @return A String array of all names and quantities
     */
    public final ArrayList<ReturnItem> getCheckOutNames() {
        return new ArrayList<>(items);
    }

    public final ArrayList<ReturnItem> getCheckOutNames(String search) {
        ArrayList<ReturnItem> ret = new ArrayList<>();
        ret.addAll(items.stream().filter(item -> item.getID().equals(search)).collect(Collectors.toList()));
        return ret;
    }


    /**
     * Delete a product within the checkout.
     *
     * @param productNo The index of the item within the checkout.
     */
    public final void delItem(int productNo) // array store exception
    {
        /**
         Class CheckOut: Method delProduct
         Preconditions: productNo has been entered as an integer parameter
         PostConditions: the product corresponding to productNo will have been deleted
         */

        if (productNo < logicalSize) { // check that the product exists
            items.remove(productNo);
        }
    }

    public final void delItem(ReturnItem del) {
        items.removeFirstOccurrence(del);
    }

    /**
     * Reduce the stock counts for the purchased items and return the product array to be stored
     *
     * @return The product array, having been reduced in stock.
     */
    public final LinkedList<ReturnItem> productBought() {
        return items;
    }
}
