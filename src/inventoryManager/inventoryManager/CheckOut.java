package inventoryManager;

import java.util.LinkedList;
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

/*
* Author: Jarrah Gosbell
* Student Number: z5012558
* Class: CheckOut
* Description: This program will allow for the creation, retrieval, modification and deletion of checkOuts created from items in the product database.
*/

final class CheckOut {
    // create the necessary variables in the order of use
    /**
     * A list of all items which are currently in the checkout
     */
    private LinkedList<String> items;
    /**
     * A list of the names of the items in the checkout ready for printing in the interface.
     */
    private LinkedList<String> names;
    /**
     * A list corresponding to items which contains integers, each denoting the number of it's respective product being bought
     */
    private LinkedList<Integer> quantities;
    /**
     * The size of the above two lists
     */
    private int logicalSize;

    private ItemDatabase itemDB = new ItemDatabase();

    /**
     * Construct a new checkout with no items
     */
    public CheckOut() {
        items = new LinkedList<>();
        names = new LinkedList<>();
        quantities = new LinkedList<>();
        logicalSize = 0;
    }

    /**
     * Add a new product to the checkout
     */
    public final void addProduct(String ID, String name) {
        int quantity = 1; // this can be changed when the user can input a number.
        boolean alreadyExists = false;
        int i = 0;
        for (String item : items) {// replace this with the library method
            if (ID.equals(item)) {
                alreadyExists = true;
                break;
            } else i++;
        }
        if (!alreadyExists) {
            items.add(ID);
            names.add(name);
            //names.add(itemDB.getEntryName(ID).get());
            quantities.add(quantity);
            ++logicalSize;
        } else {
            int quantityStored = quantities.get(i) + quantity;
            quantities.add(i, quantityStored);
            quantities.remove(i + 1);
        }
    }

    /**
     * Get the names and quantities of everything in the checkout
     *
     * @return A String array of all names and quantities
     */
    public final LinkedList<String> getCheckOutNames() {
        return names;
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
            if (quantities.get(productNo) != 1) {
                quantities.add(productNo, quantities.get(productNo) - 1);
                quantities.remove(productNo + 1);
            } else {
                items.remove(productNo);
                quantities.remove(productNo);
                names.remove(productNo);
                logicalSize--;
            }
        }
    }

    public final void delItem(String name) {
        if (names.contains(name)) {
            if (quantities.get(names.indexOf(name)) != 1) {
                quantities.add(names.indexOf(name), quantities.get(names.indexOf(name) - 1));
                quantities.remove(names.indexOf(name) + 1);
            } else {
                items.remove(names.indexOf(name));
                quantities.remove(names.indexOf(name));
                names.remove(name);
                logicalSize--;
            }
        }
    }

    /**
     * Reduce the stock counts for the purchased items and return the product array to be stored
     *
     * @return The product array, having been reduced in stock.
     */
    public final LinkedList<String> productBought() {
        return items;
    }
}
