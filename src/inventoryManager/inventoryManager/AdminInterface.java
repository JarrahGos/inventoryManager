package inventoryManager;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class AdminInterface extends Interface {
    /**
     * Create an interface instance with it's parameters set by the config file
     *
     * @throws IOException
     */
    private AdminInterface() throws IOException {
        super();
    }

    public static void enterAdminMode(Stage lastStage, int privelage) {
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
        final String[] itemSettingsList = {"Return Items", "Add Items", "Remove General Items", "Change a General Item", "Enter General Item Counts", "Save Item Database", "Create Set"};
        final String[] AdminSettingsList = {"Change Password", "Save Databases To USB", "Close The Program"};
        final String[] LogSettingsList = {"Item Logs", "Password Logs"};
        final String[] RootSettingsList = {"Create Admins", "Delete Controlled Items"}; //TODO: finish this by looking at the spec.
        items.setAll(PersonSettingsList);
        optionList.setItems(items);
        optionList.maxWidthProperty().bind(split.widthProperty().multiply(0.2));

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
                    if (selectedOption == null) return;
                    switch (selectedOption) {
                        case "Change a Person":
                            changePerson(grid);
                            break;
                        case "Save Person Database":
                            savePersonDatabase(adminStage, grid);
                            break;
                        case "Return Items":
                            returnItems(grid);
                            break;
                        case "Add Items":
                            addItem(grid);
                            break;
                        case "Remove General Items":
                            removeItem(grid, SQLInterface.TABGENERAL);
                            break;
                        case "Change a General Item":
                            changeItem(grid);
                            break;
                        case "Enter General Item Counts":
                            enterStockCounts(grid);
                            break;
                        case "Save Item Database":
                            saveItemDatabase(adminStage, grid);
                            break;
                        case "Change Password":
                            changeAdminPassword(grid);
                            break;
                        case "Save Databases To USB": //TODO SQL here is dead.
                            SaveDatabases(adminStage, grid);
                            break;
                        case "Close The Program":
                            CloseProgram(grid);
                            break;
                        case "Create Admins":
                            createAdmins(grid);
                            break;
                        case "Delete Controlled Items":
                            removeItem(grid, SQLInterface.TABCONTROLLED);
                            break;
                        case "Item Logs":
                            showItemLog(grid);
                            break;
                        case "Password Logs":
                            showPasswordLog(grid);
                            break;
                        case "Create Set":
                            createSet(grid);
                            break;
                        default:
                            changePerson(grid);

                    }
                });
        Scene adminScene = new Scene(split, horizontalSize, verticalSize);
        adminStage.setScene(adminScene);
        adminStage.setOnCloseRequest((WindowEvent event) -> {
            lastStage.show();
        });
        adminStage.show();
        adminStage.toFront();

    }


    private static void changePerson(GridPane grid) {
        grid.getChildren().clear();

        ListView<String> personList = new ListView<>();
        ObservableList<String> person = FXCollections.observableArrayList();
        person.setAll(WorkingUser.getUserNames());
        personList.setItems(person);
        grid.add(personList, 0, 0, 1, 4);

        //Text nameLabel = new Text("Name:");
        //grid.add(nameLabel, 1, 0);
        TextField nameEntry = new TextField();
        nameEntry.setPromptText("Name");
        nameEntry.requestFocus();
        grid.add(nameEntry, 1, 0);
        //Text IDLabel = new Text("ID:");
        //grid.add(IDLabel, 1, 1);
        TextField ID = new TextField();
        ID.setPromptText("ID");
        grid.add(ID, 1, 1);
        StringBuilder oldID = new StringBuilder();
        personList.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> vo, String oldVal, String selectedPerson) -> {
                    if (selectedPerson != null) {
                        oldID.substring(0, 0); //TODO: This is hacky as fuck.
                        nameEntry.setText(selectedPerson);
                        String IDVal = WorkingUser.getPersonID(selectedPerson).orElse("ERROR getting ID");
                        ID.setText(IDVal);
                        oldID.append(IDVal);
                    }
                });
        nameEntry.setOnAction((ActionEvent e) -> ID.requestFocus());

        ID.setOnAction((ActionEvent e) -> {
            String IDNew = null;
            try {
                IDNew = ID.getText();
            } catch (NumberFormatException e1) {
                flashColour(1500, Color.RED, ID);
            }
            if (IDNew != null && !Objects.equals(IDNew, "")) {
                String name = personList.getSelectionModel().getSelectedItem();
                WorkingUser.changeDatabasePerson(nameEntry.getText(), IDNew, oldID.toString());
                nameEntry.clear();
                ID.clear();
                nameEntry.requestFocus();
                flashColour(1500, Color.AQUAMARINE, nameEntry, ID);
                //Now need to update the form
                String selectedIndex = personList.getSelectionModel().getSelectedItem();
                person.setAll(WorkingUser.getUserNames());
                personList.setItems(person);
                personList.getSelectionModel().select(selectedIndex);
            }
        });
    }

    private static void savePersonDatabase(Stage adminStage, GridPane grid) {
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
                flashColour(1500, Color.AQUAMARINE, saveDirBtn);
            } else {
                flashColour(1500, Color.RED, saveDirBtn);
            }
        });

        saveBtn.setOnAction((ActionEvent e) -> {
            try {
                WorkingUser.adminWriteOutDatabase("Person"); //adminPersonDatabase.csv

                File adminPersonFile = new File(Compatibility.getFilePath("adminPersonDatabase.csv"));
                if (filePath.getText() != null || !filePath.getText().isEmpty()) {
                    File destPers = new File(filePath.getText() + "/adminPersonDatabase.csv");
                    Files.copy(adminPersonFile.toPath(), destPers.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    flashColour(3000, Color.AQUAMARINE, saveBtn);
                } else {
                    flashColour(3000, Color.RED, saveBtn, filePath);
                }

            } catch (IOException e1) {
                Log.print(e1);
                flashColour(3000, Color.RED, saveBtn);
            }
        });
    }

    private static void returnItems(GridPane grid) { //TODO: Maybe use a checkout here.
        grid.getChildren().clear();

        //Text barcodeLabel = new Text("Enter Barcode");
        TextField barcodeEntry = new TextField();
        barcodeEntry.setPromptText("Barcode");

        SplitPane inOut = new SplitPane();
        ObservableList<String> outItems = FXCollections.observableArrayList();
        LinkedList<String> out = new LinkedList<>();
        ArrayList<String> names = WorkingUser.getOutItems();
        ArrayList<String> itemID = WorkingUser.getOutItemIDs();
        ArrayList<String> persID = WorkingUser.getOutItemPersIDs();
        for (int i = 0; i < names.size(); i++) {
            out.add(itemID.get(i) + "\t" + names.get(i) + "\t" + persID.get(i)); //TODO: Formatting, create columns within listview.
        }
        outItems.setAll(out);
        ListView<String> outList = new ListView<>();
        outList.setItems(outItems);

        ObservableList<String> inItems = FXCollections.observableArrayList();
        ListView<String> inList = new ListView<>();
        inList.setItems(inItems);
        LinkedList<String> barcodesIn = new LinkedList<>();

        barcodeEntry.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                String toAdd = WorkingUser.getItemName(barcodeEntry.getText());
                if (toAdd == null) {
                    flashColour(1500, Color.RED, barcodeEntry);
                } else {
                    inItems.setAll(toAdd);
                    inList.setItems(inItems);
                    outItems.remove(toAdd);
                    outList.setItems(outItems);
                }
            }
        });
        //grid.add(barcodeLabel, 0, 0);
        grid.add(barcodeEntry, 0, 0);

        inOut.getItems().addAll(outList, inList); //TODO: Headings for this list.
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
            ArrayList<String> in = new ArrayList<>();
            for (String item : inItems) {
                in.add(item);
            }
            WorkingUser.signItemsIn(in);
        });
        grid.add(signIn, 1, 2);
    }

    private static void addItem(GridPane grid) {
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
        ChoiceBox<String> set = new ChoiceBox<>();
        ObservableList<String> sets = FXCollections.observableArrayList();
        sets.setAll(WorkingUser.getSets());
        set.setItems(sets);
        grid.add(set, 1, 2);
        CheckBox cb = new CheckBox("Controlled Item?");
        grid.add(cb, 1, 3);

        // General Item information
        Text descriptionLabel = new Text("Item Description:");
        TextField description = new TextField();
        Text quantityLabel = new Text("Quantity:");
        TextField quantity = new TextField();
        Text locationLabel = new Text("Item Location:");
        TextField location = new TextField();

        // Controlled item information.
        Text type = new Text("Type:");
        ChoiceBox<String> typeBox = new ChoiceBox<>();
        Text tagnoLabel = new Text("Tag/Position number:");
        TextField tagno = new TextField();
        Text stateLabel = new Text("Item State:");
        TextField state = new TextField();


        cb.setOnAction((ActionEvent e) -> {
            if (!cb.isSelected()) {
                grid.getChildren().removeAll(type, typeBox, tagno, tagnoLabel, stateLabel, state);
                grid.add(descriptionLabel, 0, 4);
                grid.add(description, 1, 4);

                grid.add(quantityLabel, 0, 5);
                grid.add(quantity, 1, 5);

                grid.add(locationLabel, 0, 6);
                grid.add(location, 1, 6);
            } else {
                grid.getChildren().removeAll(description, descriptionLabel, quantity, quantityLabel, location, locationLabel);
                ObservableList<String> types = FXCollections.observableArrayList();
                types.setAll(WorkingUser.getTypes());
                typeBox.setItems(types);
                grid.add(type, 0, 4);
                grid.add(typeBox, 1, 4);

                grid.add(tagnoLabel, 0, 5);
                grid.add(tagno, 1, 5);

                grid.add(stateLabel, 0, 6);
                grid.add(state, 1, 6);
            }
        });
        cb.setSelected(false);


        nameEntry.setOnAction((ActionEvent e) -> BarCodeEntry.requestFocus());

        location.setOnAction((ActionEvent e) -> {
            boolean valid = true;
            try {
                Long.parseLong(quantity.getText());
            } catch (NumberFormatException e1) {
                flashColour(1500, Color.RED, quantity);
                valid = false;
            }
            if (elementsAreNotEmpty(nameEntry, BarCodeEntry, quantity, location) && valid) { //Description may be empty
                WorkingUser.addItemToDatabase(nameEntry.getText(), BarCodeEntry.getText(), description.getText(), Long.parseLong(quantity.getText()), location.getText(), set.getValue());
                nameEntry.clear();
                BarCodeEntry.clear();
                description.clear();
                quantity.clear();
                location.clear();
                nameEntry.requestFocus();
                flashColour(1500, Color.AQUAMARINE, nameEntry, BarCodeEntry, description, quantity, location);
            }
        });
        state.setOnAction((ActionEvent e) -> {
            if (nameEntry.getText() != null && !nameEntry.getText().isEmpty() && BarCodeEntry.getText() != null && !BarCodeEntry.getText().isEmpty()) {
                WorkingUser.addItemToDatabase(nameEntry.getText(), BarCodeEntry.getText(), typeBox.getValue(), tagno.getText(), set.getValue(), state.getText());
                nameEntry.clear();
                BarCodeEntry.clear();
                typeBox.setValue("");
                tagno.clear();
                set.setValue("");
                state.clear();
                nameEntry.requestFocus();
                flashColour(1500, Color.AQUAMARINE, nameEntry, BarCodeEntry, typeBox, set, tagno, state);
            }
        });
    }

    private static void createSet(GridPane grid) {
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
            if (elementsAreNotEmpty(nameEntry, BarCodeEntry)) {
                WorkingUser.addSet(nameEntry.getText(), BarCodeEntry.getText());
                flashColour(1500, Color.AQUAMARINE, nameEntry, BarCodeEntry);
                nameEntry.clear();
                BarCodeEntry.clear();
                nameEntry.requestFocus();
            } else flashColour(1500, Color.RED, nameEntry, BarCodeEntry);
        });
    }

    private static void removeItem(GridPane grid, String type) {
        grid.getChildren().clear();

        Button remove = new Button("Remove");
        ListView<String> productList = new ListView<>();
        ObservableList<String> product = FXCollections.observableArrayList();
        product.setAll(WorkingUser.getProductNames(type));
        productList.setItems(product);
        grid.add(productList, 0, 1);
        product.setAll(WorkingUser.getProductNames(type));
        productList.setItems(product);

        remove.setOnAction((ActionEvent e) -> {
            String index = productList.getSelectionModel().getSelectedItem();
            try {
                WorkingUser.removeItem(index);
                flashColour(1500, Color.AQUAMARINE, remove);
            } catch (IOException | InterruptedException e1) {
                e1.printStackTrace();
                flashColour(1500, Color.RED, remove);
            }
            product.setAll(WorkingUser.getProductNames(type));
        });
        grid.add(remove, 1, 0);
        product.setAll(WorkingUser.getProductNames(type));
    }

    private static void changeItem(GridPane grid) { //TODO: Changing an item does nothing.
        grid.getChildren().clear();
        ListView<String> productList = new ListView<>();
        ObservableList<String> product = FXCollections.observableArrayList();
        product.setAll(WorkingUser.getProductNames(SQLInterface.TABGENERAL));
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
                    String BC = String.valueOf(WorkingUser.getProductBarCode(productList.getSelectionModel().getSelectedItem()));
                    barCodeEntry.setText(BC);

                });
        nameEntry.setOnAction((ActionEvent e) -> {
            barCodeEntry.requestFocus();
        });
        barCodeEntry.setOnAction((ActionEvent e) -> {
            if (WorkingUser.itemExists(barCodeEntry.getText())) {
                flashColour(1500, Color.AQUAMARINE, barCodeEntry);
            } else flashColour(1500, Color.RED, barCodeEntry);

//                            WorkingUser.changeDatabaseProduct(nameEntry.getText(), WorkingUser.getProductName(productList.getSelectionModel().getSelectedItem()), price,
//                                    barCode, WorkingUser.getProductBarCode(productList.getSelectionModel().getSelectedItem()));
            nameEntry.clear();
            barCodeEntry.clear();
            nameEntry.requestFocus();
            flashColour(1500, Color.AQUAMARINE, nameEntry, barCodeEntry);

            //Now need to update the form
            String selectedProduct = productList.getSelectionModel().getSelectedItem();

            nameEntry.setText(selectedProduct);
            String BC = String.valueOf(WorkingUser.getProductBarCode(productList.getSelectionModel().getSelectedItem()));
            barCodeEntry.setText(BC);
            product.setAll(WorkingUser.getProductNames(SQLInterface.TABGENERAL));
            productList.setItems(product);
        });
    }

    private static void enterStockCounts(GridPane grid) {
        grid.getChildren().clear();
        ListView<String> productList = new ListView<>();
        ObservableList<String> product = FXCollections.observableArrayList();
        product.setAll(WorkingUser.getProductNames(SQLInterface.TABGENERAL));
        productList.setItems(product);
        grid.add(productList, 0, 0, 1, 4);
        Text numberLabel = new Text("Number:");
        grid.add(numberLabel, 1, 0);
        TextField numberEntry = new TextField();
        grid.add(numberEntry, 2, 0);

        productList.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> vo, String oldVal, String selectedProduct) -> {
                    String numberOfProduct = Integer.toString(WorkingUser.getProductNumber(productList.getSelectionModel().getSelectedItem()));
                    numberEntry.setText(numberOfProduct);
                    numberEntry.requestFocus();

                });
        numberEntry.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode() == KeyCode.ENTER) { //TODO: Next line needs to convert from name to ID before processing.
                WorkingUser.setNumberOfProducts(productList.getSelectionModel().getSelectedItem(), Integer.parseInt(numberEntry.getText()));
                productList.getSelectionModel().select(productList.getSelectionModel().getSelectedIndex() + 1);
                numberEntry.requestFocus();
                flashColour(1500, Color.AQUAMARINE, numberEntry);
            }
        });
    }

    private static void saveItemDatabase(Stage adminStage, GridPane grid) {
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
                flashColour(1500, Color.AQUAMARINE, saveDirBtn);
            } else {
                flashColour(1500, Color.RED, saveDirBtn);
            }
        });

        saveBtn.setOnAction((ActionEvent e) -> {
            WorkingUser.adminWriteOutDatabase(SQLInterface.TABITEM); //adminProductDatabase.csv
            File adminProductFile = new File(Compatibility.getFilePath("adminProductDatabase.csv"));
            if (filePath.getText() != null || !filePath.getText().isEmpty()) {
                File destProd = new File(filePath.getText() + "/adminProductDatabase.csv");
                try {
                    Files.copy(adminProductFile.toPath(), destProd.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e1) {
                    Log.print(e1);
                }
                flashColour(3000, Color.AQUAMARINE, saveBtn);
            } else {
                flashColour(3000, Color.RED, saveBtn, filePath);
            }
        });
    }

    private static void changeAdminPassword(GridPane grid) {
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
                        success = WorkingUser.setPassword(WorkingUser.getUserID(), firstInput.getText(), WorkingUser.getUserID(), currentInput.getText());
                    }

                    // Success is an int, 0 = success, 1 = user not found, 2 = admin not found/password issue
                    switch (success) { // In this instance we have checked that the user exists on login. If they have gotten to this point we have a security
                        // risk which flashing a textfield at them will not resolve.
                        case 0:
                            flashColour(1500, Color.AQUAMARINE, firstInput, secondInput, currentInput);
                            break;
                        case 2:
                            flashColour(1500, Color.RED, currentInput);
                            break;
                        default:
                            flashColour(1500, Color.RED, firstInput, secondInput, currentInput);
                    }

                }
            }
        });
    }

    private static void SaveDatabases(Stage adminStage, GridPane grid) {
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
                flashColour(1500, Color.AQUAMARINE, saveDirBtn);
            } else {
                flashColour(1500, Color.RED, saveDirBtn);
            }
        });

        saveBtn.setOnAction((ActionEvent e) -> {
            try {
                WorkingUser.adminWriteOutDatabase("Person"); //adminPersonDatabase.csv
                WorkingUser.adminWriteOutDatabase("Product"); //adminProductDatabase.csv

                File adminPersonFile = new File(Compatibility.getFilePath("adminPersonDatabase.csv"));
                File adminProductFile = new File(Compatibility.getFilePath("adminProductDatabase.csv"));
                if (filePath.getText() != null || filePath.getText().isEmpty()) {
                    File destPers = new File(filePath.getText() + "/adminPersonDatabase.csv");
                    File destProd = new File(filePath.getText() + "/adminProductDatabase.csv");
                    Files.copy(adminPersonFile.toPath(), destPers.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Files.copy(adminProductFile.toPath(), destProd.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    flashColour(3000, Color.AQUAMARINE, saveBtn);
                } else {
                    flashColour(3000, Color.RED, saveBtn, filePath);
                }

            } catch (IOException e1) {
                Log.print(e1);
                flashColour(3000, Color.RED, saveBtn);
            }
        });
    }

    private static void CloseProgram(GridPane grid) {
        grid.getChildren().clear();
        Button exit = new Button("Close The Program");
        grid.add(exit, 1, 1);
        exit.setOnAction((ActionEvent e) -> {
            flashColour(1500, Color.AQUAMARINE, exit);
            System.exit(0);
        });
    }

    private static void createAdmins(GridPane grid) {
        grid.getChildren().clear();
        Text IDLabel = new Text("New admin's ID:");
        TextField ID = new TextField();
        ChoiceBox<String> level = new ChoiceBox<String>();
        level.getItems().setAll("USER", "ADMIN", "STAFF");
        Button save = new Button("Save");
        save.setOnAction((ActionEvent e) -> {
            if (!WorkingUser.personExists(ID.getText())) {
                flashColour(1500, Color.RED, ID);
            } else {
                int levelInt = PersonDatabase.USER;
                switch (level.getSelectionModel().getSelectedItem()) {
                    case "USER":
                        break;
                    case "ADMIN":
                        levelInt = PersonDatabase.ADMIN;
                        break;
                    case "STAFF":
                        levelInt = PersonDatabase.ROOT;
                        break;
                }
                WorkingUser.updateRole(ID.getText(), levelInt);
                flashColour(1500, Color.AQUAMARINE, save);
            }
        });

        grid.add(IDLabel, 0, 0);
        grid.add(ID, 1, 0);
        grid.add(level, 2, 0);
        grid.add(save, 3, 0);
    }

    public static void showPasswordLog(GridPane grid) {
        grid.getChildren().clear();
        DatePicker dpTo = new DatePicker(LocalDate.now());
        DatePicker dpFrom = new DatePicker(LocalDate.now());
        ListView<String> productList = new ListView<>();
        ObservableList<String> product = FXCollections.observableArrayList();
        if (dpTo.getValue().equals(dpFrom.getValue())) {
            product.setAll(WorkingUser.getPasswordLog());
            productList.setItems(product);
        } else {
            product.setAll(WorkingUser.getPasswordLog(dpFrom.getValue(), dpTo.getValue()));
            productList.setItems(product);
        }
        dpFrom.setOnAction((ActionEvent e) -> {
            product.setAll(WorkingUser.getPasswordLog(dpFrom.getValue(), dpTo.getValue()));
            productList.setItems(product);
        });
        dpTo.setOnAction((ActionEvent e) -> {
            product.setAll(WorkingUser.getPasswordLog(dpFrom.getValue(), dpTo.getValue()));
            productList.setItems(product);
        });
        productList.setMinWidth(grid.getMaxWidth());
        grid.add(dpFrom, 0, 0);
        grid.add(dpTo, 1, 0);
        grid.add(productList, 0, 1, 5, 10);
    }

    public static void showItemLog(GridPane grid) { //TODO: headings and formatting.
        grid.getChildren().clear();
        DatePicker dpTo = new DatePicker(LocalDate.now());
        DatePicker dpFrom = new DatePicker(LocalDate.now());
        CheckBox cb = new CheckBox("Only out items");
        ListView<String> productList = new ListView<>();
        ObservableList<String> product = FXCollections.observableArrayList();
        if (dpTo.getValue().equals(dpFrom.getValue())) {
            product.setAll(WorkingUser.getItemLog(cb.isSelected()));
            productList.setItems(product);
        } else {
            product.setAll(WorkingUser.getItemLog(cb.isSelected(), dpFrom.getValue(), dpTo.getValue()));
            productList.setItems(product);
        }
        dpFrom.setOnAction((ActionEvent e) -> {
            product.setAll(WorkingUser.getItemLog(cb.isSelected(), dpFrom.getValue(), dpTo.getValue()));
            productList.setItems(product);
        });
        dpTo.setOnAction((ActionEvent e) -> {
            product.setAll(WorkingUser.getItemLog(cb.isSelected(), dpFrom.getValue(), dpTo.getValue()));
            productList.setItems(product);
        });
        cb.setOnAction((ActionEvent e) -> {
            if (dpTo.getValue().equals(dpFrom.getValue())) {
                product.setAll(WorkingUser.getItemLog(cb.isSelected()));
            } else {
                product.setAll(WorkingUser.getItemLog(cb.isSelected(), dpFrom.getValue(), dpTo.getValue()));
                productList.setItems(product);
            }
        });
        productList.setMinWidth(grid.getMaxWidth());
        grid.add(dpFrom, 0, 0);
        grid.add(dpTo, 1, 0);
        grid.add(cb, 2, 0);
        grid.add(productList, 0, 1, 5, 10);
    }
    public static void addUser(String userID) {

        Stage AddStage = new Stage();
        AddStage.setTitle("Add User");
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
                if (!WorkingUser.personExists(IDInput.getText())) {
                    if (secondInput.getText().equals(firstInput.getText())) {
                        WorkingUser.addPersonToDatabase(nameInput.getText(), IDInput.getText(), firstInput.getText());
                        flashColour(1500, Color.AQUAMARINE, IDInput, nameInput, firstInput, secondInput);
                    } else {
                        flashColour(1500, Color.RED, firstInput, secondInput);
                        grid.getChildren().remove(error);
                        error.setText("Passwords do not match");
                        grid.add(error, 0, 5, 2, 1);
                    }
                } else {
                    flashColour(1500, Color.RED, IDInput);
                    grid.getChildren().remove(error);
                    error.setText("ID already exists, contact the LOGO to reset your password");
                    grid.add(error, 0, 5, 2, 1);
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

    public static void changePassword(String userID) {
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
                    flashColour(1500, Color.RED, firstInput, secondInput);
                }
            }
        });
        adminID.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                adminPass.requestFocus();
            }
        });
        adminPass.setOnKeyPressed((KeyEvent ke) -> {
            int success = 0; //TODO: This needs to be updated with the result of setPassword
            if (ke.getCode().equals(KeyCode.ENTER)) {
                if (firstInput.getText().equals(secondInput.getText()) && IDInput != adminID) {
                    new Thread(() -> {
                        WorkingUser.setPassword(IDInput.getText(), firstInput.getText(), adminID.getText(), adminPass.getText());
                    }).start();

                }

                if (success == 0) {
                    flashColour(1500, Color.AQUAMARINE, IDInput, firstInput, secondInput, adminID, adminPass);
                } else {
                    // Success is an int, 0 = success, 1 = user not found, 2 = admin not found/password issue
                    switch (success) {
                        case 1:
                            flashColour(1500, Color.RED, IDInput);
                            break;
                        case 2:
                            flashColour(1500, Color.RED, adminID, adminPass);
                            break;
                        default:
                            flashColour(1500, Color.RED, IDInput, firstInput, secondInput, adminID, adminPass);
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

    private static boolean elementsAreNotEmpty(Node... nodes) {
        for (Node node : nodes) {
            if (node == null) return false;
            if (node instanceof TextField && ((TextField) node).getText() == "") return false;
        }
        return true;
    }
}
