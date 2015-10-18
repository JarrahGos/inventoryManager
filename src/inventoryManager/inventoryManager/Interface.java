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
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public final class Interface extends Application {
    /**
     * the number of horizontal pixels, defaulted to 1024 but set by the settings class
     */
    private static int horizontalSize = 1024;
    /**
     * the number of vertical pixels, defaulted to 576 but set by the settings class
     */
    private static int verticalSize = 576;
    /**
     * The class which the user logs into and which handles all interaction with the program.
     */
    private final WorkingUser workingUser; // Place for all data to go through
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
        workingUser = new WorkingUser();
        System.out.println(workingUser.toString());
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
    private static void flashColour(Node node, int duration, Color colour) {

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

    private static void flashColour(Node[] node, int duration, Color colour) {

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
     * Bind the scrollbars of two listviews
     *
     * @param lv1 The first listview to bind
     * @param lv2 The second Listview to bind
     */
    public static void bind(ListView lv1, ListView lv2) { //TODO: this does not work.
        ScrollBar bar1 = null;
        ScrollBar bar2 = null;

        for (Node node : lv1.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar && ((ScrollBar) node).getOrientation().equals(Orientation.VERTICAL)) {
                bar1 = (ScrollBar) node;
            }
        }
        for (Node node : lv2.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar && ((ScrollBar) node).getOrientation().equals(Orientation.VERTICAL)) {
                bar2 = (ScrollBar) node;
            }
        }
        if (bar1 == null || bar2 == null) return;

        final ScrollBar fbar1 = bar1;
        final ScrollBar fbar2 = bar2;
        if (fbar1 != null) {
            fbar1.valueProperty().addListener((observable, oldValue, newValue) -> {
                fbar2.setValue(newValue.doubleValue());
            });
        }
        if (fbar2 != null) {
            fbar2.valueProperty().addListener((observable, oldValue, newValue) -> {
                fbar1.setValue(newValue.doubleValue());
            });
        }
    }

    /**
     * The main method of the program
     *
     * @param args No arguments needed, -w int for width, -h int for height, both in pixels.
     */
    public static void main(String[] args) {
//		try {
//			Interface i = new Interface();
//		}
//		catch (IOException e) {
//			Log.print(e);
//		}
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
        if (workingUser.userLoggedIn()) {
            items.setAll(workingUser.getCheckOutNames());
        }
        itemList.setItems(items);

        grid.add(itemList, 0, 1, 8, 7);
        Button adminMode = new Button("Enter Admin Mode");

        enterBarCode.setOnAction((ActionEvent e) -> {
            if ((pass.getText() == null || pass.getText().isEmpty()) && !workingUser.userLoggedIn()) {
                pass.requestFocus();
                flashColour(pass, 1500, Color.RED);
            } else if (!workingUser.userLoggedIn()) { // treat the input as a barcode
                int userError;
                userError = barcodeEntered(input.getText(), pass.getText()); // take the text, do user logon stuff with it.


                if (workingUser.userLoggedIn()) {
                    privelage = workingUser.getRole();
                    Thread thread = new Thread(new Runnable() { //TODO: make this work.

                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1); // after this time, log the user out.
                                workingUser.logOut(); // set user number to -1 and delete any checkout made.

                                grid.getChildren().remove(userLabel); // make it look like no user is logged in
                                inputLabel.setText("Enter your barcode"); // set the input label to something appropriate.
//                                total.setText(String.valueOf("$" + workingUser.getPrice())); // set the total price to 0.00.
                            } catch (InterruptedException e) {
                                // do nothing here.
                            }
                        }
                    });
                    thread.setDaemon(true);
                    thread.setPriority(Thread.MIN_PRIORITY);
                    thread.start();
                    thread.interrupt();
                    flashColour(input, 1500, Color.AQUAMARINE);
                    flashColour(pass, 1500, Color.AQUAMARINE);
                    input.requestFocus();
                    userLabel.setText(workingUser.userName(userError)); // find the name of those who dare log on.
                    inputLabel.setText("Enter Barcode"); // change the label to suit the next action.
                    grid.getChildren().remove(userLabel); // remove any error labels which may have appeared.
                    grid.add(userLabel, 3, 0); // add the new user label
                    grid.getChildren().remove(addUser);
                    // the above two are done as we do not know whether a user label exists there. Adding two things to the same place causes an exception.
                    if (privelage > PersonDatabase.USER) {
                        grid.add(adminMode, 0, 8); // add the button to the bottum left of the screen.
                    }
                    input.clear(); // clear the barcode from the input ready for product bar codes.
                    pass.clear();
                    grid.getChildren().remove(pass);

                } else {
                    input.clear(); // there was an error with the barcode, get ready for another.
                    pass.clear();
                    input.requestFocus();
                    userLabel.setText(workingUser.userName(userError)); // tell the user there was a problem. Maybe this could be done better.
                    grid.getChildren().remove(userLabel); // Remove a userlabel, as above.
                    grid.add(userLabel, 3, 0); // add it again, as above.
                    flashColour(input, 1500, Color.RED);
                    flashColour(pass, 1500, Color.RED);
                }
            } else {
                System.out.println(input.getText());
                boolean correct = productEntered(input.getText());
                if (correct) {
                    productError.setText("");
                    items.setAll(workingUser.getCheckOutNames());
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
        input.setOnKeyPressed((KeyEvent ke) -> { // the following allows the user to hit enter rather than OK. Works exactly the same as hitting OK.
            if (ke.getCode().equals(KeyCode.ENTER) && !workingUser.userLoggedIn()) {
                pass.requestFocus();
            } else if (ke.getCode().equals(KeyCode.ENTER)) {
                System.out.println(input.getText());
                boolean correct = productEntered(input.getText());
                System.out.println(correct);
                if (correct) {
                    productError.setText("");
                    items.setAll(workingUser.getCheckOutNames());
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
                if (pass.getText() == null || pass.getText().isEmpty()) {
                    pass.requestFocus();
                    flashColour(pass, 1500, Color.RED);
                } else if (!workingUser.userLoggedIn()) {
                    int userError = barcodeEntered(input.getText(), pass.getText());

                    if (workingUser.userLoggedIn()) {
                        userLabel.setText(workingUser.userName(userError));
                        inputLabel.setText("Enter Barcode");
                        grid.getChildren().remove(userLabel);
                        grid.add(userLabel, 3, 0);
                        input.clear();
                        flashColour(input, 1500, Color.AQUAMARINE);
                        flashColour(pass, 1500, Color.AQUAMARINE);
                        input.requestFocus();
                        grid.getChildren().remove(addUser);
                        privelage = workingUser.getRole();
                        if (privelage >= PersonDatabase.ADMIN) {
                            grid.add(adminMode, 0, 8); // add the button to the bottum left of the screen.
                        }
                        pass.clear();
                        grid.getChildren().remove(pass);
                    } else {
                        input.clear();
                        userLabel.setText(workingUser.userName(userError));
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
                        items.setAll(workingUser.getCheckOutNames());
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
        });


        // create and listen on admin button
        adminMode.setOnAction((ActionEvent e) -> {
            //TODO: Should this log out the user?
            workingUser.logOut(); // set user number to -1 and delete any checkout made.
            grid.getChildren().remove(userLabel); // make it look like no user is logged in
            inputLabel.setText("Enter your barcode"); // set the input label to something appropriate.
            items.setAll(workingUser.getCheckOutNames());
            itemList.setItems(items);
            input.requestFocus();
            enterAdminMode(primaryStage); // method which will work the admin mode features.
        });

        addUser.setOnAction((ActionEvent e) -> {
            addUser(input.getText());
        });
        grid.add(addUser, 0, 8);

        Button removeProduct = new Button("Remove"); // button which will bring up the admin mode.
        removeProduct.setOnAction((ActionEvent e) -> {
            int index = itemList.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                workingUser.deleteProduct(index);
                items.setAll(workingUser.getCheckOutNames());
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
            if (workingUser.userLoggedIn()) {
                privelage = PersonDatabase.USER;
                workingUser.checkOutItems(); // add the cost to the bill.
                grid.getChildren().remove(userLabel); // make it look like the user has been logged out.
                inputLabel.setText("Enter your ID"); // Set the input label to something better for user login.
                input.clear(); // clear the input ready for a barcode
                items.setAll(workingUser.getCheckOutNames());
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
            if (workingUser.userLoggedIn()) {
                privelage = PersonDatabase.USER;
                workingUser.logOut(); // set user number to -1 and delete any checkout made.
                grid.getChildren().remove(userLabel); // make it look like no user is logged in
                inputLabel.setText("Enter your barcode"); // set the input label to something appropriate.
                items.setAll(workingUser.getCheckOutNames());
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
            changePassword(workingUser.getUserID());
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

    /**
     * Log the user into working user given their barcode
     *
     * @param input The users barcode as a string
     * @return The error from logging the user in.
     */
    private int barcodeEntered(String input, String pass) {
        return workingUser.getbarcode(input, pass);
    }

    /**
     * Add a product to the checkout
     *
     * @param input The barcode of the product as a string
     * @return A Boolean value of whether the action worked
     */
    private boolean productEntered(String input) {
        return workingUser.addToCart(input);
    }

    /**
     * Will open the admin panel of the program.
     *
     * @param lastStage The stage which opened this stage
     */
    private void enterAdminMode(Stage lastStage) {
        lastStage.hide();
        Stage adminStage = new Stage();
        adminStage.setTitle("Inventory Admin");
        SplitPane split = new SplitPane();
        VBox rightPane = new VBox();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15, 15, 15, 15));
        ListView<String> optionList = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList();
        final String[] PersonSettingsList = {"Change a Person", "Save Person Database"};
        final String[] itemSettingsList = {"Return Items", "Add Items", "Remove General Items", "Change a General Item", "Enter General Item Counts", "List Items", "Save Item Database"};
        final String[] AdminSettingsList = {"Change Password", "Save Databases To USB", "Close The Program"};
        final String[] LogSettingsList = {"Item Logs", "Password Logs"};
        final String[] RootSettingsList = {"Create Admins", "Delete Controlled Items"}; //TODO: finish this by looking at the spec.
        items.setAll(PersonSettingsList);
        optionList.setItems(items);

        grid.add(optionList, 0, 0, 1, 7);
        Button logs = new Button("Logs");
        logs.setOnAction((ActionEvent e) -> {
            items.setAll(LogSettingsList);
            optionList.setItems(items);
            optionList.getSelectionModel().select(0);
        });
        Button people = new Button("People");
        people.setOnAction((ActionEvent e) -> {
            items.setAll(PersonSettingsList);
            optionList.setItems(items);
            optionList.getSelectionModel().select(0);
        });
        Button products = new Button("Items");
        products.setOnAction((ActionEvent e) -> {
            items.setAll(itemSettingsList);
            optionList.setItems(items);
            optionList.getSelectionModel().select(0);
        });
        Button admin = new Button("Admin");
        admin.setOnAction((ActionEvent e) -> {
            items.setAll(AdminSettingsList);
            optionList.setItems(items);
            optionList.getSelectionModel().select(0);
        });
        Button root = new Button("Staff");
        root.setOnAction((ActionEvent e) -> {
            items.setAll(RootSettingsList);
            optionList.setItems(items);
            optionList.getSelectionModel().select(0);
        });

        Button logout = new Button("Logout");
        logout.setOnAction((ActionEvent e) -> {
            adminStage.close();
            lastStage.show();
        });

        ToolBar buttonBar;
        if (privelage == PersonDatabase.ROOT) {
            buttonBar = new ToolBar(people, products, logs, admin, root, logout);
        } else {
            buttonBar = new ToolBar(people, products, logs, admin, logout);
        }
        rightPane.getChildren().addAll(buttonBar, grid);
        split.getItems().addAll(optionList, rightPane);
        split.setDividerPositions(0.2f);
        optionList.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> ov, String old_val, String selectedOption) -> {
                    if (selectedOption == null) {
                    } else if (selectedOption.equals("Save Person Database")) {
                        DirectoryChooser fc = new DirectoryChooser();


                        grid.getChildren().clear();
                        Text fileLabel = new Text("Save Directory");
                        TextField filePath = new TextField("");
                        filePath.setEditable(true);
                        Button saveDirBtn = new Button("Choose Save Directory");
                        Button saveBtn = new Button("Save Database to Selected Directory");

                        grid.add(saveBtn, 1, 5);
                        grid.add(fileLabel, 0, 0);
                        grid.add(filePath, 0, 1);
                        grid.add(saveDirBtn, 1, 1);

                        saveDirBtn.setOnAction((ActionEvent e) -> {
                            File returnVal = fc.showDialog(adminStage);

                            if (returnVal != null) {
                                filePath.setText(returnVal.getPath());
                                flashColour(saveDirBtn, 1500, Color.AQUAMARINE);
                            } else {
                                flashColour(saveDirBtn, 1500, Color.RED);
                            }
                        });

                        saveBtn.setOnAction((ActionEvent e) -> {
                            try {
                                workingUser.adminWriteOutDatabase("Person"); //adminPersonDatabase.csv

                                File adminPersonFile = new File(Compatibility.getFilePath("adminPersonDatabase.csv"));
                                if (filePath.getText() != null || !filePath.getText().isEmpty()) {
                                    File destPers = new File(filePath.getText() + "/adminPersonDatabase.csv");
                                    Files.copy(adminPersonFile.toPath(), destPers.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    flashColour(saveBtn, 3000, Color.AQUAMARINE);
                                } else {
                                    flashColour(saveBtn, 3000, Color.RED);
                                    flashColour(filePath, 3000, Color.RED);
                                }

                            } catch (IOException e1) {
                                Log.print(e1);
                                flashColour(saveBtn, 3000, Color.RED);
                            }
                        });

                    } else if (selectedOption.equals("Return Items")) {
                        grid.getChildren().clear();

                        Text barcodeLabel = new Text("Enter Barcode");
                        TextField barcodeEntry = new TextField();

                        SplitPane inOut = new SplitPane();
                        ObservableList<String> outItems = FXCollections.observableArrayList();
                        outItems.setAll(workingUser.getOutItems());
                        ListView<String> outList = new ListView<>();
                        outList.setItems(outItems);

                        ObservableList<String> inItems = FXCollections.observableArrayList();
                        ListView<String> inList = new ListView<>();
                        inList.setItems(inItems);

                        barcodeEntry.setOnKeyPressed((KeyEvent ke) -> {
                            if (ke.getCode().equals(KeyCode.ENTER)) {
                                String toAdd = workingUser.getItemName(barcodeEntry.getText());
                                if (toAdd == null) {
                                    flashColour(barcodeEntry, 1500, Color.RED);
                                } else {
                                    inItems.setAll(toAdd);
                                    inList.setItems(inItems);
                                    outItems.remove(toAdd);
                                    outList.setItems(outItems);
                                }
                            }
                        });
                        grid.add(barcodeLabel, 0, 0);
                        grid.add(barcodeEntry, 1, 0);

                        inOut.getItems().addAll(outList, inList);
                        inOut.setDividerPositions(0.5f);
                        grid.add(inOut, 0, 1, 2, 1);

                        Button checkIn = new Button("Check In");
                        checkIn.setOnAction((ActionEvent e) -> {
                            inItems.addAll(outList.getSelectionModel().getSelectedItems());
                            inList.setItems(inItems);
                            outItems.removeAll(outList.getSelectionModel().getSelectedItems());
                            outList.setItems(outItems);

                        });

                        Button signIn = new Button("Sign In Items");
                        signIn.setOnAction((ActionEvent e) -> {
                            workingUser.signItemsIn((ArrayList<String>) inItems);
                            //TODO: This requires the barcodes as the names will not be unique.
                        });
                        grid.add(signIn, 1, 2);
                    } else if (selectedOption.equals("Add Items")) {
                        grid.getChildren().clear();
                        Text nameLabel = new Text("Name:");
                        grid.add(nameLabel, 0, 0);
                        TextField nameEntry = new TextField();
                        nameEntry.requestFocus();
                        grid.add(nameEntry, 1, 0);
                        Text BarCodeLabel = new Text("Barcode:");
                        grid.add(BarCodeLabel, 0, 1);
                        TextField BarCodeEntry = new TextField();
                        grid.add(BarCodeEntry, 1, 1);
                        nameEntry.setOnAction((ActionEvent e) -> BarCodeEntry.requestFocus());
                        BarCodeEntry.setOnAction((ActionEvent e) -> {
                            long barCode = -1;
                            try {
                                barCode = Long.parseLong(BarCodeEntry.getText());
                            } catch (NumberFormatException e1) {
                                flashColour(BarCodeEntry, 1500, Color.RED);
                            }
                            if (nameEntry.getText() != null && !nameEntry.getText().isEmpty() && BarCodeEntry.getText() != null && !BarCodeEntry.getText().isEmpty()) {
                                workingUser.addItemToDatabase(nameEntry.getText(), BarCodeEntry.getText());
                                nameEntry.clear();
                                BarCodeEntry.clear();
                                nameEntry.requestFocus();
                                flashColour(nameEntry, 1500, Color.AQUAMARINE);
                                flashColour(BarCodeEntry, 1500, Color.AQUAMARINE);
                            }
                        });

                    } else if (selectedOption.equals("Remove General Items")) {
                        grid.getChildren().clear();

                        Button remove = new Button("Remove");
                        ListView<String> productList = new ListView<>();
                        ObservableList<String> product = FXCollections.observableArrayList();
                        product.setAll(workingUser.getProductNames(SQLInterface.TABGENERAL));
                        productList.setItems(product);
                        grid.add(productList, 0, 1);
                        product.setAll(workingUser.getProductNames(SQLInterface.TABGENERAL));
                        productList.setItems(product);

                        remove.setOnAction((ActionEvent e) -> {
                            String index = productList.getSelectionModel().getSelectedItem();
                            try {
                                workingUser.removeItem(index);
                                flashColour(remove, 1500, Color.AQUAMARINE);
                            } catch (IOException | InterruptedException e1) {
                                e1.printStackTrace();
                                flashColour(remove, 1500, Color.RED);
                            }
                            product.setAll(workingUser.getProductNames("Item"));
                        });
                        grid.add(remove, 1, 0);
                        product.setAll(workingUser.getProductNames("Item"));
                    } else if (selectedOption.equals("Change a Product")) { //TODO
                        grid.getChildren().clear();
                        ListView<String> productList = new ListView<>();
                        ObservableList<String> product = FXCollections.observableArrayList();
                        product.setAll(workingUser.getProductNames(SQLInterface.TABGENERAL));
                        productList.setItems(product);
                        grid.add(productList, 0, 0, 1, 4);
                        Text nameLabel = new Text("Name:");
                        grid.add(nameLabel, 1, 0);
                        TextField nameEntry = new TextField();
                        nameEntry.requestFocus();
                        grid.add(nameEntry, 2, 0);
                        Text BarCodeLabel = new Text("Barcode:");
                        grid.add(BarCodeLabel, 1, 1);
                        TextField barCodeEntry = new TextField();
                        grid.add(barCodeEntry, 2, 1);
                        productList.getSelectionModel().selectedItemProperty().addListener(
                                (ObservableValue<? extends String> vo, String oldVal, String selectedProduct) -> {
                                    nameEntry.setText(selectedProduct);
                                    String BC = String.valueOf(workingUser.getProductBarCode(productList.getSelectionModel().getSelectedItem()));
                                    barCodeEntry.setText(BC);

                                });
                        nameEntry.setOnAction((ActionEvent e) -> {
                            barCodeEntry.requestFocus();
                        });
                        barCodeEntry.setOnAction((ActionEvent e) -> {
                            if (workingUser.itemExists(barCodeEntry.getText())) {
                                flashColour(barCodeEntry, 1500, Color.AQUAMARINE);
                            } else flashColour(barCodeEntry, 1500, Color.RED);

//                            workingUser.changeDatabaseProduct(nameEntry.getText(), workingUser.getProductName(productList.getSelectionModel().getSelectedItem()), price,
//                                    barCode, workingUser.getProductBarCode(productList.getSelectionModel().getSelectedItem()));
                            nameEntry.clear();
                            barCodeEntry.clear();
                            nameEntry.requestFocus();
                            flashColour(nameEntry, 1500, Color.AQUAMARINE);
                            flashColour(barCodeEntry, 1500, Color.AQUAMARINE);


                            //Now need to update the form
                            String selectedProduct = productList.getSelectionModel().getSelectedItem();

                            nameEntry.setText(selectedProduct);
                            String BC = String.valueOf(workingUser.getProductBarCode(productList.getSelectionModel().getSelectedItem()));
                            barCodeEntry.setText(BC);
                            product.setAll(workingUser.getProductNames(SQLInterface.TABGENERAL));
                            productList.setItems(product);
                        });


                    } else if (selectedOption.equals("Enter Stock Counts")) {
                        grid.getChildren().clear();
                        ListView<String> productList = new ListView<>();
                        ObservableList<String> product = FXCollections.observableArrayList();
                        product.setAll(workingUser.getProductNames("General"));
                        productList.setItems(product);
                        grid.add(productList, 0, 0, 1, 4);
                        Text numberLabel = new Text("Number:");
                        grid.add(numberLabel, 1, 0);
                        TextField numberEntry = new TextField();
                        grid.add(numberEntry, 2, 0);

                        productList.getSelectionModel().selectedItemProperty().addListener(
                                (ObservableValue<? extends String> vo, String oldVal, String selectedProduct) -> {
                                    String numberOfProduct = Integer.toString(workingUser.getProductNumber(productList.getSelectionModel().getSelectedItem()));
                                    numberEntry.setText(numberOfProduct);
                                    numberEntry.requestFocus();

                                });
                        numberEntry.setOnAction((ActionEvent e) -> {
                            workingUser.setNumberOfProducts(productList.getSelectionModel().getSelectedItem(), Integer.parseInt(numberEntry.getText()));
                            productList.getSelectionModel().select(productList.getSelectionModel().getSelectedIndex() + 1);
                            numberEntry.requestFocus();
                            flashColour(numberEntry, 1500, Color.AQUAMARINE);
                        });


                    } else if (selectedOption.equals("List Products")) {
                        grid.getChildren().clear();
                        ScrollPane productList = null;
                        try {
                            productList = workingUser.printDatabase("Product");
                        } catch (IOException e) {
                            Log.print(e);
                        }
                        grid.add(productList, 0, 0);
                    } else if (selectedOption.equals("Save Product Database")) {
                        DirectoryChooser fc = new DirectoryChooser();


                        grid.getChildren().clear();
                        Text fileLabel = new Text("Save Directory");
                        TextField filePath = new TextField("");
                        filePath.setEditable(true);
                        Button saveDirBtn = new Button("Choose Save Directory");
                        Button saveBtn = new Button("Save Database to Selected Directory");

                        grid.add(saveBtn, 1, 5);
                        grid.add(fileLabel, 0, 0);
                        grid.add(filePath, 0, 1);
                        grid.add(saveDirBtn, 1, 1);

                        saveDirBtn.setOnAction((ActionEvent e) -> {
                            File returnVal = fc.showDialog(adminStage);

                            if (returnVal != null) {
                                filePath.setText(returnVal.getPath());
                                flashColour(saveDirBtn, 1500, Color.AQUAMARINE);
                            } else {
                                flashColour(saveDirBtn, 1500, Color.RED);
                            }
                        });

                        saveBtn.setOnAction((ActionEvent e) -> {
                            workingUser.adminWriteOutDatabase("Product"); //adminProductDatabase.csv
                            File adminProductFile = new File(Compatibility.getFilePath("adminProductDatabase.csv"));
                            if (filePath.getText() != null || !filePath.getText().isEmpty()) {
                                File destProd = new File(filePath.getText() + "/adminProductDatabase.csv");
                                try {
                                    Files.copy(adminProductFile.toPath(), destProd.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                } catch (IOException e1) {
                                    Log.print(e1);
                                }
                                flashColour(saveBtn, 3000, Color.AQUAMARINE);
                            } else {
                                flashColour(saveBtn, 3000, Color.RED);
                                flashColour(filePath, 3000, Color.RED);
                            }
                        });

                    } else if (selectedOption.equals("Change Password")) {
                        grid.getChildren().clear();

                        Text current = new Text("Enter your current password: ");
                        Text first = new Text("New Password:");
                        Text second = new Text("Retype New Password:");
                        PasswordField currentInput = new PasswordField();
                        PasswordField firstInput = new PasswordField();
                        PasswordField secondInput = new PasswordField();
                        grid.add(current, 0, 1);
                        grid.add(currentInput, 1, 1);
                        grid.add(first, 0, 2);
                        grid.add(firstInput, 1, 2);
                        grid.add(second, 0, 3);
                        grid.add(secondInput, 1, 3);


                        firstInput.setOnKeyPressed((KeyEvent ke) -> {
                            if (ke.getCode().equals(KeyCode.ENTER)) {
                                secondInput.requestFocus();
                            }
                        });
                        secondInput.setOnKeyPressed((KeyEvent ke) -> {
                            if (ke.getCode().equals(KeyCode.ENTER)) {
                                int success = -1;
                                if (ke.getCode().equals(KeyCode.ENTER)) {
                                    if (firstInput.getText().equals(secondInput.getText())) {
                                        success = workingUser.setPassword(workingUser.getUserID(), firstInput.getText(), workingUser.getUserID(), currentInput.getText());
                                    }

                                    // Success is an int, 0 = success, 1 = user not found, 2 = admin not found/password issue
                                    switch (success) { // In this instance we have checked that the user exists on login. If they have gotten to this point we have a security
                                        // risk which flashing a textfield at them will not resolve.
                                        case 0:
                                            flashColour(new Node[]{firstInput, secondInput, currentInput}, 1500, Color.AQUAMARINE);
                                            break;
                                        case 2:
                                            flashColour(currentInput, 1500, Color.RED);
                                            break;
                                        default:
                                            flashColour(new Node[]{firstInput, secondInput, currentInput}, 1500, Color.RED);
                                    }

                                }
                            }
                        });


                    } else if (selectedOption.equals("Save Databases To USB")) { //TODO: Bring admin stage to front after

                        DirectoryChooser fc = new DirectoryChooser();


                        grid.getChildren().clear();
                        Text fileLabel = new Text("Save Directory");
                        TextField filePath = new TextField("");
                        filePath.setEditable(true);
                        Button saveDirBtn = new Button("Choose Save Directory");
                        Button saveBtn = new Button("Save Databases to Selected Directories");

                        grid.add(saveBtn, 1, 5);
                        grid.add(fileLabel, 0, 0);
                        grid.add(filePath, 0, 1);
                        grid.add(saveDirBtn, 1, 1);

                        saveDirBtn.setOnAction((ActionEvent e) -> {
                            File returnVal = fc.showDialog(adminStage);

                            if (returnVal != null) {
                                filePath.setText(returnVal.getPath());
                                flashColour(saveDirBtn, 1500, Color.AQUAMARINE);
                            } else {
                                flashColour(saveDirBtn, 1500, Color.RED);
                            }
                        });

                        saveBtn.setOnAction((ActionEvent e) -> {
                            try {
                                workingUser.adminWriteOutDatabase("Person"); //adminPersonDatabase.csv
                                workingUser.adminWriteOutDatabase("Product"); //adminProductDatabase.csv

                                File adminPersonFile = new File(Compatibility.getFilePath("adminPersonDatabase.csv"));
                                File adminProductFile = new File(Compatibility.getFilePath("adminProductDatabase.csv"));
                                if (filePath.getText() != null || filePath.getText().isEmpty()) {
                                    File destPers = new File(filePath.getText() + "/adminPersonDatabase.csv");
                                    File destProd = new File(filePath.getText() + "/adminProductDatabase.csv");
                                    Files.copy(adminPersonFile.toPath(), destPers.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    Files.copy(adminProductFile.toPath(), destProd.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    flashColour(saveBtn, 3000, Color.AQUAMARINE);
                                } else {
                                    flashColour(saveBtn, 3000, Color.RED);
                                    flashColour(filePath, 3000, Color.RED);
                                }

                            } catch (IOException e1) {
                                Log.print(e1);
                                flashColour(saveBtn, 3000, Color.RED);
                            }
                        });

                    } else if (selectedOption.equals("Close The Program")) {
                        grid.getChildren().clear();
                        Button save = new Button("Close The Program");
                        grid.add(save, 1, 1);
                        save.setOnAction((ActionEvent e) -> {
                            flashColour(save, 1500, Color.AQUAMARINE);
                            System.exit(0);
                        });
                    }
                });
        Scene adminScene = new Scene(split, horizontalSize, verticalSize);
        adminStage.setScene(adminScene);
        adminStage.show();
        adminStage.toFront();

    }

    public void addUser(String userID) {
        Stage AddStage = new Stage();
        AddStage.setTitle("Change Your Password");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15, 15, 15, 15));

        Text ID = new Text("Enter your ID:");
        grid.add(ID, 0, 0);
        Text name = new Text("Enter your name:");
        grid.add(name, 0, 1);
        Text first = new Text("Enter your password:");
        grid.add(first, 0, 2);
        Text second = new Text("Reenter your password:");
        grid.add(second, 0, 3);
        Text error = new Text("Passwords do not match");

        TextField IDInput = new TextField(userID);
        grid.add(IDInput, 1, 0);
        TextField nameInput = new TextField();
        grid.add(nameInput, 1, 1);
        PasswordField firstInput = new PasswordField();
        grid.add(firstInput, 1, 2);
        PasswordField secondInput = new PasswordField();
        grid.add(secondInput, 1, 3);

        IDInput.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                nameInput.requestFocus();
            }
        });
        nameInput.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                firstInput.requestFocus();
            }
        });
        firstInput.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                secondInput.requestFocus();
            }
        });
        secondInput.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                if (!workingUser.PersonExists(IDInput.getText())) {
                    if (secondInput.getText().equals(firstInput.getText())) {
                        workingUser.addPersonToDatabase(nameInput.getText(), IDInput.getText(), firstInput.getText());
                        flashColour(new Node[]{IDInput, nameInput, firstInput, secondInput}, 1500, Color.AQUAMARINE);
                    } else {
                        flashColour(new Node[]{firstInput, secondInput}, 1500, Color.RED);
                        grid.getChildren().remove(error);
                        error.setText("Passwords do not match");
                        grid.add(error, 2, 3);
                    }
                } else {
                    flashColour(IDInput, 1500, Color.RED);
                    grid.getChildren().remove(error);
                    error.setText("ID already exists, contact the LOGO to change your password.");
                    grid.add(error, 2, 0);
                }
            }
        });
        Button close = new Button("Close");
        close.setOnAction((ActionEvent e) -> AddStage.close());
        grid.add(close, 1, 4);


        Scene PassScene = new Scene(grid, 500, 500);
        AddStage.setScene(PassScene);
        AddStage.show();
        AddStage.toFront();
    }

    public void changePassword(String userID) {
        Stage PassStage = new Stage();
        PassStage.setTitle("Change Your Password");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15, 15, 15, 15));

        Text ID = new Text("Enter your ID:");
        Text first = new Text("New Password:");
        Text second = new Text("Retype New Password:");
        TextField IDInput = new TextField();
        IDInput.setText(userID);
        PasswordField firstInput = new PasswordField();
        PasswordField secondInput = new PasswordField();
        grid.add(ID, 0, 0);
        grid.add(IDInput, 1, 0);
        grid.add(first, 0, 1);
        grid.add(firstInput, 1, 1);
        grid.add(second, 0, 2);
        grid.add(secondInput, 1, 2);
        Text admin = new Text("Enter admin ID");
        Text admin2 = new Text("Enter admin password");
        TextField adminID = new TextField();
        PasswordField adminPass = new PasswordField();
        grid.add(admin, 0, 4);
        grid.add(adminID, 1, 4);
        grid.add(admin2, 0, 5);
        grid.add(adminPass, 1, 5);

        IDInput.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                firstInput.requestFocus();
            }
        });
        firstInput.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                secondInput.requestFocus();
            }
        });
        secondInput.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                if (firstInput.getText().equals(secondInput.getText())) {
                    adminID.requestFocus();
                } else {
                    flashColour(firstInput, 1500, Color.RED);
                    flashColour(secondInput, 1500, Color.RED);
                }
            }
        });
        adminID.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                adminPass.requestFocus();
            }
        });
        adminPass.setOnKeyPressed((KeyEvent ke) -> {
            int success = -1;
            if (ke.getCode().equals(KeyCode.ENTER)) {
                if (firstInput.getText().equals(secondInput.getText()) && IDInput != adminID) {
                    success = workingUser.setPassword(ID.getText(), firstInput.getText(), adminID.getText(), adminPass.getText());
                }

                if (success == 0) {
                    flashColour(new Node[]{IDInput, firstInput, secondInput, adminID, adminPass}, 1500, Color.AQUAMARINE);
                } else {
                    // Success is an int, 0 = success, 1 = user not found, 2 = admin not found/password issue
                    switch (success) {
                        case 1:
                            flashColour(IDInput, 1500, Color.RED);
                            break;
                        case 2:
                            flashColour(new Node[]{adminID, adminPass}, 1500, Color.RED);
                            break;
                        default:
                            flashColour(new Node[]{IDInput, firstInput, secondInput, adminID, adminPass}, 1500, Color.RED);
                    }
                }
            }
        });
        Button close = new Button("Close");
        close.setOnAction((ActionEvent e) -> PassStage.close());
        grid.add(close, 1, 6);
        Scene PassScene = new Scene(grid, 500, 500);
        PassStage.setScene(PassScene);
        PassStage.show();
        PassStage.toFront();
    }
}
