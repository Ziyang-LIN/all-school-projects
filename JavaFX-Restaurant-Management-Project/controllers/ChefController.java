package controllers;
import models.Chef;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The controller in the chef interface.
 */
public class ChefController {

    /** The listView of dishes ready to be cooked. */
    public ListView<String> dishToBeCooked;

    /** The button that the chef clicks to choose a dish to cook. */
    public Button takeOrder;

    /** The button that the chef clicks to request a delivery. */
    public Button askDeliver;

    /** The list of dishes ready to be cooked. */
    private final ObservableList<String> dishToBeCookedList = FXCollections.observableArrayList();

    /** The dish that the chef chooses to cook. */
    private String chosenDish;

    /** initialize a chef for the interface. */
    private Chef chef;


    /**
     * Initializes the chef interface.
     */
    public void initialize(){
        askDeliver.setDisable(true);
    }

    /**
     * The chef checks and loads a new shipment when he does not have work to do.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void checkShipment() throws IOException{
        Parent shipmentView = FXMLLoader.load(getClass().getResource("/views/ShipmentView.fxml"));
        Stage shipmentStage = new Stage();
        shipmentStage.setTitle("Shipment Information");
        shipmentStage.setScene(new Scene(shipmentView, 600, 329));
        shipmentStage.show();
    }

    /**
     * The chef checks to see the dishes that are ready to be cooked.
     */
    public void showDishesToBeCooked(){
        dishToBeCookedList.clear();
        chef = new Chef("00");
        ArrayList<String> dishes = chef.getDishesToCook();
        for (String name: dishes) {
            dishToBeCookedList.addAll(name);
        }
        dishToBeCooked.setItems(dishToBeCookedList);
    }

    /**
     * The chef chooses which dish he wants to cook.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void chooseDishToCook() throws IOException{
        chosenDish = dishToBeCooked.getSelectionModel().getSelectedItem();
        if (chosenDish != null) {
            chef.receive(chosenDish);
            askDeliver.setDisable(false);
        }
    }

    /**
     * The chef confirms a dish is ready to be delivered, and requests delivery.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void askForDeliver() throws IOException{
        if (chosenDish != null && dishToBeCooked.getSelectionModel().getSelectedItem().equals(chosenDish)) {
            takeOrder.setDisable(false);
            dishToBeCooked.getItems().remove(chosenDish);
            dishToBeCooked.refresh();
            chef.prepared();
            askDeliver.setDisable(true);
        }
    }

    /**
     * Log off from the chef interface.
     * @param event the chef clicks on the log off button.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void logOff(MouseEvent event) throws IOException {
        Parent loginView = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
        Scene loginScene = new Scene(loginView);
        Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        loginStage.setScene(loginScene);
        loginStage.show();
    }
}
