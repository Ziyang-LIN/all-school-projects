package controllers;
import models.Table;
import javafx.scene.input.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;

/**
 * The controller that manages the customer interface.
 */
public class CustomerController {

    /** Name of the displayed dish. */
    public Label dishName;

    /** The are that let customers to enter table number. */
    public TextField tableNum;

    /** The area that let customers to enter number of customers. */
    public TextField customerNum;

    /** The images of appetizers. */
    public ImageView appetizers;

    /** The images of soup and salad. */
    public ImageView soupAndSalad;

    /** The images of entrees. */
    public ImageView entrees;

    /** The images of desserts. */
    public ImageView desserts;

    /** The images of beverages. */
    public ImageView beverages;

    /** Represents the appetizers category in the menu. */
    public Tab typeA;

    /** Represents the soup and salad category in the menu. */
    public Tab typeS;

    /** Represents the entrees category in the menu. */
    public Tab typeE;

    /** Represents the desserts category in the menu. */
    public Tab typeD;

    /** Represents the beverages category in the menu. */
    public Tab typeB;

    /** Represents the page of the menu that enters table number and number of customers. */
    public Tab Set;

    /** The listView of the dishes the customers ordered. */
    public ListView<String> myOrders;

    /** The listView of the dishes that have not been delivered to this customer. */
    public ListView<String> undeliveredOrders;

    /** The button that the customer clicks to make payment */
    public Button payBills;

    /** The button that the customer clicks to cancel a dish before sending the dishes to the server. */
    public Button cancelDish;

    /** The button that the customer clicks to see the entire orders. */
    public Button viewOrders;

    /** The button that the customer clicks to send the orders to the server. */
    public Button finishOrdering;

    /** The button that the customer clicks to confirm the dish is received. */
    public Button confirmDeliver;

    /** The button that the customer clicks to send the dish back. */
    public Button sendDishBack;

    /** The image file of the current displayed dish. */
    private File DishImageFile;

    /** The image of currently displayed dish. */
    private ImageView displayedDish;

    /** The HashMap of the displayed dishes. */
    private final HashMap<String, String> dishViewed = new HashMap<>();

    /** The HashMap of all the dishes in the menu */
    private final HashMap<String, String> dishList = new HashMap<>();

    /** The list of dishes that the customer orders. */
    private final ObservableList<String> myOrdersList = FXCollections.observableArrayList();

    /** The list of the orders that have not been delivered to this customer. */
    private final ObservableList<String> undeliveredList = FXCollections.observableArrayList();

    /** Whether the customer decides to separate the bill. */
    private Boolean separateBill = true;

    /** The table number that this customer enters. */
    private String tableNumber;

    /** Initialize a table for the interface. */
    private Table table;


    /**
     * Initializes the menu and the customer interface.
     */
    public void initialize() {
        File appetizersFile = new File("phase2/src/menu pictures/a/a1.jpg");
        Image imageA = new Image(new File("phase2/src/menu pictures/a/a1.jpg").toURI().toString());
        appetizers.setImage(imageA);
        DishImageFile = appetizersFile;
        displayedDish = appetizers;
        Image imageS = new Image(new File("phase2/src/menu pictures/s/s1.jpg").toURI().toString());
        Image imageE = new Image(new File("phase2/src/menu pictures/e/e1.jpg").toURI().toString());
        Image imageD = new Image(new File("phase2/src/menu pictures/d/d1.jpg").toURI().toString());
        Image imageB = new Image(new File("phase2/src/menu pictures/b/b1.jpg").toURI().toString());
        soupAndSalad.setImage(imageS);
        entrees.setImage(imageE);
        desserts.setImage(imageD);
        beverages.setImage(imageB);
        dishViewed.put("a", "a1.jpg");
        dishViewed.put("s", "s1.jpg");
        dishViewed.put("e", "e1.jpg");
        dishViewed.put("d", "d1.jpg");
        dishViewed.put("b", "b1.jpg");
        dishList.put("a1", "Mozzarella Sticks $4.99");
        dishList.put("s1", "Taco Salad $6.99");
        dishList.put("e1", "BBQ Ribs $16.99");
        dishList.put("d1", "Key Lime Pie $13.99");
        dishList.put("b1", "Apple Juice $3.99");
        dishName.setText("Set View");
        payBills.setDisable(true);
        viewOrders.setDisable(true);
        cancelDish.setDisable(true);
        finishOrdering.setDisable(true);
        confirmDeliver.setDisable(true);
        sendDishBack.setDisable(true);
    }

    /**
     * Get the relevant information of this customer: table number and number of customers.
     */
    public void getTableInfo(){
        if (tableNum.getText() != null && tableNum.getText().trim().matches("[0-1]" + "[0-8]") && Integer.parseInt(customerNum.getText()) > 0) {
            tableNumber = tableNum.getText().trim();
            table = new Table( tableNumber);
            table.getTempCustomerNum().add(Integer.parseInt(customerNum.getText().trim()));
            cancelDish.setDisable(false);
            viewOrders.setDisable(false);
            finishOrdering.setDisable(false);
            tableNum.setText(null);
            customerNum.setText(null);
        } else {
            tableNum.setText(null);
            customerNum.setText(null);
        }
    }

    /**
     * Reads from the menu file to get the name of all dishes.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    private void readMenu() throws IOException{
        try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/src/txt files/Menu.txt"))) {
            String line = fileReader.readLine();
            while (line != null) {
                String[] type = line.split(":");
                String[] dish = type[1].split(",");
                for (int i = 0; i < dish.length; i++) {
                    dishList.put(type[0].substring(0, 1).toLowerCase() + Integer.toString(i + 1), dish[i].trim());
                }
                line = fileReader.readLine();
            }
        }
    }

    /**
     * Manage the views of the menu when the customer switch to other dish categories.
     */
    public void changeDishCategory() {
        for (String dishDisplayed: dishViewed.keySet()) {
            if (dishDisplayed.substring(0, 1).equals(DishImageFile.getName().substring(0, 1))) {
                if (!dishDisplayed.equals(DishImageFile.getName())) {
                    dishViewed.put(dishDisplayed.substring(0, 1), DishImageFile.getName());
                }
            }
        }
        dishName.setText("Table Information");
        if (typeA.isSelected()) {
            DishImageFile = new File("phase2/src/menu pictures/a/" + dishViewed.get("a"));
            displayedDish = appetizers;
        } else if (typeS.isSelected()) {
            DishImageFile = new File("phase2/src/menu pictures/s/" + dishViewed.get("s"));
            displayedDish = soupAndSalad;
        } else if (typeE.isSelected()) {
            DishImageFile = new File("phase2/src/menu pictures/e/" + dishViewed.get("e"));
            displayedDish = entrees;
        } else if (typeD.isSelected()) {
            DishImageFile = new File("phase2/src/menu pictures/d/" + dishViewed.get("d"));
            displayedDish = desserts;
        } else if (typeB.isSelected()) {
            DishImageFile = new File("phase2/src/menu pictures/b/" + dishViewed.get("b"));
            displayedDish = beverages;
        }
        dishName.setText(dishList.get(DishImageFile.getName().substring(0, 2)));
    }

    /**
     * Manage the views of the menu when the customer goes back the previous menu item.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void handlePrevious() throws IOException {
        if (!Set.isSelected()) {
            readMenu();
            dishName.setText(dishList.get(DishImageFile.getName().substring(0, 2)));
            int fileOrder = Integer.parseInt(DishImageFile.getName().substring(1, 2));
            if (fileOrder - 1 > 0) {
                DishImageFile = new File("phase2/src/menu pictures/" + DishImageFile.getName().substring(0, 1) + "/" + DishImageFile.getName().substring(0, 1) + Integer.toString(fileOrder - 1) + ".jpg");
                displayedDish.setImage(new Image(DishImageFile.toURI().toString()));
            } else {
                displayedDish.setImage(new Image(DishImageFile.toURI().toString()));
            }
            dishName.setText(dishList.get(DishImageFile.getName().substring(0, 2)));
        }
    }

    /**
     * Manage the views of the menu when the customer goes back the next menu item.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void handleNext() throws IOException{
        if (!Set.isSelected()) {
            readMenu();
            int count = 0;
            dishName.setText(dishList.get(DishImageFile.getName().substring(0, 2)));
            File f = new File(DishImageFile.getPath().substring(0, 27));
            if (f.listFiles() != null) {
                for (File file : Objects.requireNonNull(f.listFiles())) {
                    if (file.isFile()) {
                        count++;
                    }
                }
            }
            int dishOrder = Integer.parseInt(DishImageFile.getName().substring(1, 2));
            if (dishOrder + 1 < count) {
                DishImageFile = new File("phase2/src/menu pictures/" + DishImageFile.getName().substring(0, 1) + "/" + DishImageFile.getName().substring(0, 1) + Integer.toString(dishOrder + 1) + ".jpg");
                displayedDish.setImage(new Image(DishImageFile.toURI().toString()));
            } else {
                displayedDish.setImage(new Image(DishImageFile.toURI().toString()));
            }
            dishName.setText(dishList.get(DishImageFile.getName().substring(0, 2)));
        }
    }

    /**
     * Read the dishIngredients.txt to get the ingredients that can be added or subtracted and triggers the ingredient interface.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void viewIngredients() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/IngredientView.fxml"));
        loader.load();
        IngredientController ingredients = loader.getController();
        try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/src/txt files/dishIngredients.txt"))) {
            String line = fileReader.readLine();
            ArrayList<String> dishInfo = new ArrayList<>();
            while (line != null) {
                String[] type = line.split(":");
                if (type[0].equals(dishName.getText())) {
                    String[] name = dishName.getText().split(Pattern.quote("$"));
                    dishInfo.add(name[0]);
                    String[] dishIngredients = type[1].split(",");
                    Collections.addAll(dishInfo, dishIngredients);
                    String[] result = new String[dishInfo.size()];
                    result = dishInfo.toArray(result);
                    ingredients.setIngredientText(result);
                    ingredients.setTableNum(tableNumber);
                    break;
                }
                line = fileReader.readLine();
            }
        }
        Parent ingredientView = loader.getRoot();
        Stage ingredientStage = new Stage();
        ingredientStage.setTitle("Adjust the amount of the ingredients ");
        ingredientStage.setScene(new Scene(ingredientView, 600, 329));
        ingredientStage.show();
    }

    /**
     * Set the views of the customer's orders.
     */
    public void showOrders() {
        myOrdersList.clear();
        for(String name: table.getPlacedOrders()) {
            myOrdersList.addAll(name);
            myOrders.setItems(myOrdersList);
        }
    }

    /**
     * Display all delivered orders for this customer.
     */
    public void viewUndelivered(){
        undeliveredList.clear();
        for(String name: table.getWaitingOrders()) {
            undeliveredList.addAll(name);
            undeliveredOrders.setItems(undeliveredList);
        }
        if (!undeliveredList.isEmpty()) {
            confirmDeliver.setDisable(false);
            sendDishBack.setDisable(false);
            payBills.setDisable(false);
        }
    }

    /**
     * This customer cancels a dish before finishing ordering.
     */
    public void cancelDish(){
        String name = myOrders.getSelectionModel().getSelectedItem();
        table.cancelDish(name);
        if (name!= null) {
            myOrders.getItems().remove(name);
            myOrders.refresh();
        }
    }

    /**
     * This customer submits the order to the server.
     */
    public void submitOrders(){
        if (!table.getPlacedOrders().isEmpty()) {
            table.placeOrder();
            cancelDish.setDisable(true);
            confirmDeliver.setDisable(false);
            sendDishBack.setDisable(false);
            payBills.setDisable(false);
        }
    }

    /**
     * This customer confirms to receive a dish.
     */
    public void receiveOrder(){
        String dish = undeliveredOrders.getSelectionModel().getSelectedItem();
        if (dish!= null && table.getToConfirm().contains(dish)) {
            table.receiveDish(dish);
            undeliveredOrders.getItems().remove(dish);
            undeliveredOrders.refresh();
        }
    }

    /**
     * This customer request to return the dish for some reasons.
     */
    public void requestBackOrders(){
        String dishName = undeliveredOrders.getSelectionModel().getSelectedItem();
        if (dishName  != null && table.getToConfirm().contains(dishName ) && !table.getBackOrders().contains(dishName )) {
            table.rejectDish(dishName );
        }
    }


    /**
     * Create the interface for this customer to make payment, and records it into the system.
     */
    public void payBill() {
        Stage paymentStage = new Stage();
        VBox windowViews = new VBox();
        HBox tipsViews = new HBox();
        tipsViews.setSpacing(10);
        Label instruction = new Label("please select the tip(%):");
        ChoiceBox<String> tipsChoice = new ChoiceBox<>();
        if (table.getTempCustomerNum().get(0) < 8) {
            tipsChoice.getItems().add("10%") ;
        } else {
            tipsChoice.getItems().add("15%");
        }
        tipsChoice.getItems().add("18%");
        tipsChoice.getItems().add("20%");
        Button confirm = new Button("Confirm");
        confirm.setOnAction(event -> {
            confirm.setDisable(true);
            Label question = new Label("Do you want to separate the bill?");
            HBox buttonViews = new HBox();
            buttonViews.setSpacing(10);
            Button yes = new Button("Yes");
            Button no = new Button("No");
            Label billType = new Label("");
            String tips = tipsChoice.getValue();
            yes.setOnAction(event1 -> {
                no.setDisable(true);
                billType.setText(table.checkOut(Integer.parseInt(tips.substring(0,2)), separateBill, table.getTempCustomerNum().get(0)));
            });
            no.setOnAction(event1 -> {
                separateBill = true;
                yes.setDisable(true);
                billType.setText(table.checkOut(Integer.parseInt(tips.substring(0,2)), separateBill, table.getTempCustomerNum().get(0)));
            });
            buttonViews.getChildren().addAll(yes, no);
            windowViews.getChildren().addAll(question,buttonViews,billType);
        });
        tipsViews.getChildren().addAll(instruction,tipsChoice,confirm);
        windowViews.getChildren().addAll(tipsViews);
        Scene stageScene = new Scene(windowViews, 500, 400);
        paymentStage.setScene(stageScene);
        paymentStage.show();
    }

    /**
     * Log off from this customer interface.
     * @param events the customer clicks on the log off button.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void logOff(MouseEvent events) throws IOException {
        Parent welcomeView = FXMLLoader.load(getClass().getResource("/views/Welcome.fxml"));
        Scene welcomeScene = new Scene(welcomeView);
        Stage welcomeStage = (Stage) ((Node) events.getSource()).getScene().getWindow();
        welcomeStage.setScene(welcomeScene);
        welcomeStage.show();
    }
}