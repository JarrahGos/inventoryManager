package inventoryManager;

/*
*    Inventory Manager is a simple program to run item hire and return within a small group.
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


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Interface extends Application {
    /**
     * the number of horizontal pixels, defaulted to 1024 but set by the settings class
     */
    public static int horizontalSize = 1024;
    /**
     * the number of vertical pixels, defaulted to 576 but set by the settings class
     */
    public static int verticalSize = 576;
    /**
     * The text size of the program, set by the settings class
     */
    private final int textSize;
    private int privelage = 0; //ensure that this is set back to 0 on logout.


    /**
     * Create an interface instance with it's parameters set by the config file
     *
     * @throws IOException
     */
    public Interface() throws IOException {
        String[] settings = Settings.interfaceSettings();
//        horizontalSize = Integer.parseInt(settings[0]);
//        verticalSize = Integer.parseInt(settings[1]);
        textSize = Integer.parseInt(settings[2]);
        //initalize the variables created above

    }

    /**
     * Flash the given node a given colour for a given time
     *
     * @param node     The node to be flashed
     * @param duration the duration in ms for the node to be flashed
     * @param colour   The colour (from Color) that you wish to flash.
     */
    public static void flashColour(Node node, int duration, Color colour) {

        InnerShadow shadow = new InnerShadow();
        shadow.setRadius(25d);
        shadow.setColor(colour);
        node.setEffect(shadow);

        Timeline time = new Timeline();

        time.setCycleCount(1);

        List<KeyFrame> frames = new ArrayList<>();
        frames.add(new KeyFrame(Duration.ZERO, new KeyValue(shadow.radiusProperty(), 25)));
        frames.add(new KeyFrame(new Duration(duration), new KeyValue(shadow.radiusProperty(), 0)));
        time.getKeyFrames().addAll(frames);

        time.playFromStart();
    }

    public static void flashColour(Node[] node, int duration, Color colour) {

        InnerShadow shadow = new InnerShadow();
        shadow.setRadius(25d);
        shadow.setColor(colour);
        for (Node n : node) {
            n.setEffect(shadow);
        }

        Timeline time = new Timeline();

        time.setCycleCount(1);

        List<KeyFrame> frames = new ArrayList<>();
        frames.add(new KeyFrame(Duration.ZERO, new KeyValue(shadow.radiusProperty(), 25)));
        frames.add(new KeyFrame(new Duration(duration), new KeyValue(shadow.radiusProperty(), 0)));
        time.getKeyFrames().addAll(frames);

        time.playFromStart();
    }


    /**
     * The main method of the program
     *
     * @param args No arguments needed, -w int for width, -h int for height, both in pixels.
     */
    public static void main(String[] args) {
        for (int i = args.length - 1; i > 0; i--) {
            if (args[i].equals("-w") && i != args.length - 1) {
                horizontalSize = Integer.parseInt(args[i + 1]);
            } else if (args[i].equals("-h") && i != args.length - 1) {
                verticalSize = Integer.parseInt(args[i + 1]);
            }
        }
        Application.launch(args);
    }

    /**
     * The user part of the GUI
     *
     * @param primaryStage The base stage of the program
     */
    @Override
    public void start(Stage primaryStage) {
        // create the layout
        primaryStage.setTitle("Inventory Management System"); // set the window title.
        GridPane grid = new GridPane(); // create the layout manager
//	    grid.setGridLinesVisible(true); // used for debugging object placement
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15, 15, 15, 15)); // window borders

        // create the thread which will be used for logging the user out after a given time.
        // create label for input
        Text inputLabel = new Text("Enter your ID");
        inputLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, textSize));
        grid.add(inputLabel, 0, 0); // place in top left hand corner

        // create input textfield
        TextField input = new TextField();
        grid.add(input, 1, 0); // place to the right of the input label
        input.requestFocus(); // make this the focus for the keyboard when the program starts
        Text userLabel = new Text("Error");

        // create password textField
        PasswordField pass = new PasswordField();
        grid.add(pass, 2, 0);


        // create button to enter data from input
        Button enterBarCode = new Button("OK"); // button linked to action on input text field.
        grid.add(enterBarCode, 4, 0, 1, 1); // add to the direct right of the input text field

        //create product error text
        Text productError = new Text();
        grid.add(productError, 1, 8);

        // Button to add a new user
        Button addUser = new Button("Add User");


        // create the lists for the checkout.

        ListView<String> itemList = new ListView<>();
        itemList.setPrefHeight(500);
        ObservableList<String> items = FXCollections.observableArrayList();
        if (WorkingUser.userLoggedIn()) {
            items.setAll(WorkingUser.getCheckOutNames());
        }
        itemList.setItems(items);

        grid.add(itemList, 0, 1, 8, 7);
        Button adminMode = new Button("Enter Admin Mode");

        enterBarCode.setOnAction((ActionEvent e) -> {
            OnOKPressed(grid, inputLabel, input, userLabel, pass, addUser, itemList, items, adminMode);
        });
        input.setOnKeyPressed((KeyEvent ke) -> { // the following allows the user to hit enter rather than OK. Works exactly the same as hitting OK.
            if (ke.getCode().equals(KeyCode.ENTER) && !WorkingUser.userLoggedIn()) {
                pass.requestFocus();
            } else if (ke.getCode().equals(KeyCode.ENTER)) {
                System.out.println(input.getText());
                boolean correct = productEntered(input.getText());
                System.out.println(correct);
                if (correct) {
                    productError.setText("");
                    items.setAll(WorkingUser.getCheckOutNames());
                    itemList.setItems(items);
                    input.clear();
                    input.requestFocus();
                    flashColour(input, 500, Color.AQUAMARINE);
                } else {
                    productError.setText("Could not read that product");
                    input.clear();
                    input.requestFocus();
                    flashColour(input, 500, Color.RED);
                }
            }
        });
        pass.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) { //TODO: this is duplicate code, make a method call.
                OnOKPressed(grid, inputLabel, input, userLabel, pass, addUser, itemList, items, adminMode);
            }
        });


        // create and listen on admin button
        adminMode.setOnAction((ActionEvent e) -> {
            WorkingUser.logOut(); // set user number to -1 and delete any checkout made.
            grid.getChildren().remove(userLabel); // make it look like no user is logged in
            inputLabel.setText("Enter your barcode"); // set the input label to something appropriate.
            items.setAll(WorkingUser.getCheckOutNames());
            itemList.setItems(items);
            input.requestFocus();
            AdminInterface.enterAdminMode(primaryStage, privelage); // method which will work the admin mode features.
        });

        addUser.setOnAction((ActionEvent e) -> {
            AdminInterface.addUser(input.getText());
        });
        grid.add(addUser, 0, 8);

        Button removeProduct = new Button("Remove"); // button which will bring up the admin mode.
        removeProduct.setOnAction((ActionEvent e) -> {
            int index = itemList.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                WorkingUser.deleteProduct(index);
                items.setAll(WorkingUser.getCheckOutNames());
                itemList.setItems(items); //TODO: add select top.
                itemList.scrollTo(index);
                input.requestFocus();
                flashColour(removeProduct, 1500, Color.AQUAMARINE);
            } else flashColour(removeProduct, 1500, Color.RED);
        });
        grid.add(removeProduct, 2, 8); // add the button to the bottum left of the screen.

        // create and listen on purchase button
        Button purchase = new Button("Sign items out"); // button which will add the cost of the items to the users bill
        purchase.setOnAction((ActionEvent e) -> {
            if (WorkingUser.userLoggedIn()) {
                privelage = PersonDatabase.USER;
                WorkingUser.checkOutItems(); // add the cost to the bill.
                grid.getChildren().remove(userLabel); // make it look like the user has been logged out.
                inputLabel.setText("Enter your ID"); // Set the input label to something better for user login.
                input.clear(); // clear the input ready for a barcode
                items.setAll(WorkingUser.getCheckOutNames());
                itemList.setItems(items);
                input.requestFocus();
                grid.add(pass, 2, 0);
                grid.getChildren().remove(adminMode);
                grid.add(addUser, 0, 8);
                flashColour(purchase, 1500, Color.AQUAMARINE);
                privelage = 0;
            } else {
                flashColour(purchase, 1500, Color.RED);
                flashColour(input, 1500, Color.RED);
            }
        });
        grid.add(purchase, 4, 8, 2, 1); // add the button to the bottum right corner, next to the total price.

        Button cancel = new Button("Cancel");
        cancel.setOnAction((ActionEvent e) -> {
            if (WorkingUser.userLoggedIn()) {
                privelage = PersonDatabase.USER;
                WorkingUser.logOut(); // set user number to -1 and delete any checkout made.
                grid.getChildren().remove(userLabel); // make it look like no user is logged in
                inputLabel.setText("Enter your barcode"); // set the input label to something appropriate.
                items.setAll(WorkingUser.getCheckOutNames());
                itemList.setItems(items);
                input.requestFocus();
                grid.add(pass, 2, 0);
                grid.getChildren().remove(adminMode);
                grid.add(addUser, 0, 8);
            }
        });
        grid.add(cancel, 5, 0, 3, 1); // add the button to the right of the user name.

        // Reset password button
        Button resetButton = new Button("Reset Password");
        resetButton.setOnAction((ActionEvent e) -> {
            AdminInterface.changePassword(WorkingUser.getUserID());
        });
        grid.add(resetButton, 3, 8);

        Platform.setImplicitExit(false);
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            event.consume();
            input.requestFocus();
        });
        Scene primaryScene = new Scene(grid, horizontalSize, verticalSize); // create the scene at the given size
        primaryStage.setScene(primaryScene);

        primaryStage.show();
    }

    private void OnOKPressed(GridPane grid, Text inputLabel, TextField input, Text userLabel, PasswordField pass, Button addUser, ListView<String> itemList, ObservableList<String> items, Button adminMode) {
        if (pass.getText() == null || pass.getText().isEmpty()) {
            pass.requestFocus();
            flashColour(pass, 1500, Color.RED);
        } else if (!WorkingUser.userLoggedIn()) {
            int userError = barcodeEntered(input.getText(), pass.getText());

            if (WorkingUser.userLoggedIn()) {
                userLabel.setText(WorkingUser.userName(userError));
                inputLabel.setText("Enter Barcode");
                grid.getChildren().remove(userLabel);
                grid.add(userLabel, 3, 0);
                input.clear();
                flashColour(input, 1500, Color.AQUAMARINE);
                flashColour(pass, 1500, Color.AQUAMARINE);
                input.requestFocus();
                grid.getChildren().remove(addUser);
                privelage = WorkingUser.getRole();
                if (privelage >= PersonDatabase.ADMIN) {
                    grid.add(adminMode, 0, 8); // add the button to the bottum left of the screen.
                }
                pass.clear();
                grid.getChildren().remove(pass);
            } else {
                input.clear();
                userLabel.setText(WorkingUser.userName(userError));
                grid.getChildren().remove(userLabel);
                grid.add(userLabel, 3, 0);
                input.clear();
                pass.clear();
                input.requestFocus();
                flashColour(input, 1500, Color.RED);
                flashColour(pass, 1500, Color.RED);
            }
        } else {
            boolean correct = productEntered(input.getText());
            if (correct) {
                items.setAll(WorkingUser.getCheckOutNames());
                itemList.setItems(items);
                input.clear();
                input.requestFocus();
                flashColour(input, 500, Color.AQUAMARINE);
            } else {
                input.clear();
                input.requestFocus();
                flashColour(input, 500, Color.RED);
            }
        }
    }

    /**
     * Log the user into working user given their barcode
     *
     * @param input The users barcode as a string
     * @return The error from logging the user in.
     */
    private int barcodeEntered(String input, String pass) {
        return WorkingUser.getbarcode(input, pass);
    }

    /**
     * Add a product to the checkout
     *
     * @param input The barcode of the product as a string
     * @return A Boolean value of whether the action worked
     */
    private boolean productEntered(String input) {
        return WorkingUser.addToCart(input);
    }

    /**
     * Will open the admin panel of the program.
     *
     * @param lastStage The stage which opened this stage
     */

}
