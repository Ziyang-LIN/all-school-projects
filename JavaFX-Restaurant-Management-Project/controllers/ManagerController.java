package controllers;
import models.Manager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

/**
 * The controller for the manager interface.
 */
public class ManagerController {

    /** The listView of the inventory. */
    public ListView<String> inventory;

    /** The listView of undelivered dishes. */
    public ListView<String> undelivered;

    /** The listView of logging messages. */
    public ListView<String> logging;

    /** The list of updated ingredients. */
    private final ObservableList<String> inventoryList = FXCollections.observableArrayList();

    /** The list of undelivered dishes that are ready for delivery. */
    private final ObservableList<String> undeliveredList = FXCollections.observableArrayList();

    /** The list of logging messages to be recorded. */
    private final ObservableList<String> loggingList = FXCollections.observableArrayList();

    /**
     * Initializes the manager interface and the view of inventory.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void initialize() throws IOException{
        try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/src/txt files/Ingredients.txt"))) {
            String line = fileReader.readLine();
            while (line != null) {
                String[] splitLine = line.split(",");
                inventoryList.add(splitLine[0].trim()+ ": " +splitLine[1].trim());
                line = fileReader.readLine();
            }
        }
        inventory.setItems(inventoryList);
    }

    /**
     * See the updated restaurant inventory from the inventory file.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void updateInventory() throws IOException{
        inventoryList.clear();
        try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/src/txt files/Ingredients.txt"))) {
            String line = fileReader.readLine();
            while (line != null) {
                String[] splitLine = line.split(",");
                inventoryList.add(splitLine[0].trim()+ ": " +splitLine[1].trim());
                line = fileReader.readLine();
            }
        }
        inventory.setItems(inventoryList);
    }

    /**
     * The manager checks and loads a new shipment when he does not have work to do.
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
     * Check whether the request.txt is updated and send email to the supplier.
     */
    public void checkRequest() {
        Stage requestStage = new Stage();
        VBox windowScene = new VBox();
        Manager manager = new Manager("01");
        if (!manager.getShortage().isEmpty()) {
            Label title = new Label ("Send an email to the supplier");
            TextField email = new TextField("");
            Button send = new Button ("Send");
            windowScene.getChildren().addAll(title,email,send);
            email.setMaxWidth(300);
            email.setMaxHeight(20);
            send.setOnAction(e -> {
                BufferedWriter out = null;
                try {
                    FileWriter writer1 = new FileWriter("phase2/src/txt files/log.txt", true);
                    out = new BufferedWriter(writer1);
                    out.write(email.getText().substring(0,21) + " send a request of:' " + email.getText() + " ' \n");
                }catch (IOException ignored){ }
                finally {
                    if (out != null){
                        try{
                            out.close();
                        }catch (IOException ignored){}
                    }
                }
                requestStage.close();
            });
        } else {
            Label request = new Label ("No ingredient is under threshold");
            windowScene.getChildren().add(request);
        }
        windowScene.setAlignment(Pos.CENTER);
        Scene requestScene = new Scene(windowScene, 500, 100);
        requestStage.setScene(requestScene);
        requestStage.show();
    }

    /**
     * Checks all payments received by entering a date.
     */
    public void checkPayment() {
        Stage newStage = new Stage();
        VBox scene = new VBox();
        TextField date = new TextField("please enter the date (yyMMdd)");
        Button confirm = new Button("Confirm");
        scene.getChildren().addAll(date, confirm);
        Scene stageScene = new Scene(scene, 300, 100);
        newStage.setScene(stageScene);
        newStage.show();

        confirm.setOnAction(e -> {
            HashMap<String, String> paymentHistory = Manager.getPayments();
            if (paymentHistory.containsKey(date.getText())){
                Stage stage1 = new Stage();
                VBox scene1 = new VBox();
                TextField details = new TextField(paymentHistory.get(date.getText()));
                scene1.getChildren().addAll(details);
                Scene stageScene1 = new Scene(scene1, 300, 100);
                stage1.setScene(stageScene1);
                stage1.show();
            }
        });
    }

    /**
     * See all the information in the logging files.
     */
    public void checkLogging() throws IOException{
        loggingList.clear();
        try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/src/txt files/log.txt"))) {
            String line = fileReader.readLine();
            while (line != null) {
                loggingList.add(line);
                line = fileReader.readLine();
            }
        }
        logging.setItems(loggingList);
    }

    /**
     * Check all undelivered dishes in the restaurant system.
     */
    public void checkUndelivered() {
        undeliveredList.clear();
        HashMap<String, ArrayList<String>> undeliveredInfo = Manager.getWaitingOrders();
        for (String dish : undeliveredInfo.keySet()) {
            undeliveredList.addAll(undeliveredInfo.get(dish));
        }
        undelivered.setItems(undeliveredList);
    }

    /**
     * Log off from this manager interface.
     * @param events an event indicating a command of logging off.
     * @throws IOException handles any possible exceptions in the console.
     */
    public void logOff(MouseEvent events) throws IOException {
        Parent loginView = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
        Scene loginScene = new Scene(loginView);
        Stage loginStage = (Stage) ((Node) events.getSource()).getScene().getWindow();
        loginStage.setScene(loginScene);
        loginStage.show();
    }
}
