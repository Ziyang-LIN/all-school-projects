package controllers;
import models.Server;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.input.MouseEvent;


/**
 * The controller for the server interface.
 */
public class ServerController {

    /** The listView of orders of a table. */
    public ListView<String> tableOrders;

    /** The listView of back orders of a table. */
    public ListView<String> backOrdersView;

    /** The button to reject back orders of a table. */
    public Button rejectBackOrders;

    /** The button to accept back orders of a table. */
    public Button acceptBackOrders;

    /** The tab of the view of table orders. */
    public Tab orders;

    /** The tab of the view of table back order. */
    public Tab backOrders;

    /** The list of undelivered orders of a table. */
    private final ObservableList<String> tableOrdersList = FXCollections.observableArrayList();

    /** The list of back orders of a table. */
    private final ObservableList<String> backOrdersList = FXCollections.observableArrayList();

    /** The ID of the server. */
    private String serverID;

    /** initialize a server for the interface. */
    private Server server;

    /** the table number of the currently selected table. */
    private String currentTable;

    /**
     * Initializes the server interface.
     */
    public void initialize(){}

    /**
     * Sets server's serverID.
     */
    protected void setServerID(String ID) {
        serverID = ID;
        server = new Server(serverID);
    }

    /**
     * The server decides to place orders for a table.
     */
    public void placeOrders() {
        server = new Server(serverID);
        HashMap<String, ArrayList<String>> orderInfo = Server.getAvailable();
        if (!orderInfo.isEmpty()) {
            Stage placeOrderStage = new Stage();
            VBox windowScene = new VBox();
            ListView<String> tableInfoList = new ListView<>();
            ObservableList<String> tableInfoObList = FXCollections.observableArrayList();
            tableInfoObList.addAll(orderInfo.keySet());
            tableInfoList.setItems(tableInfoObList);

            HBox buttonScene = new HBox();
            buttonScene.setSpacing(10);
            Button viewOrders = new Button("View Orders");
            Button placeOrder = new Button("Place Order");
            ListView<String> orderList = new ListView<>();
            ObservableList<String> orderObList = FXCollections.observableArrayList();
            buttonScene.getChildren().addAll(viewOrders, placeOrder);
            windowScene.getChildren().addAll(tableInfoList, buttonScene, orderList);
            viewOrders.setOnAction(e -> {
                orderObList.clear();
                String name = tableInfoList.getSelectionModel().getSelectedItem();
                if (name != null) {
                    orderObList.addAll(orderInfo.get(name));
                    orderList.setItems(orderObList);
                }
            });
            placeOrder.setOnAction(e2 -> {
                try {
                    orderObList.clear();
                    String name2 = tableInfoList.getSelectionModel().getSelectedItem(); //table number
                    if (name2 != null) {
                        server.confirmPlaceOrder(name2);
                        tableInfoList.getItems().remove(name2);
                        tableInfoList.refresh();
                    }
                } catch (IOException ignored) {
                }
            });
            Scene placeOrderScene = new Scene(windowScene, 400, 400);
            placeOrderStage.setScene(placeOrderScene);
            placeOrderStage.show();
        }
    }

    /**
     * Checks the list of orders and back orders for a table.
     * @param events an event indicating the corresponding command.
     */
    public void checkTableOrders(MouseEvent events) {
        String buttonClicked = events.getSource().toString();
        String buttonName = buttonClicked.substring(buttonClicked.length() - 3);
        tableOrdersList.clear();
        backOrdersList.clear();

        HashMap<String, ArrayList<String>> ordersInfo = Server.getWaitingOrders();
        HashMap<String, ArrayList<String>> backOrdersInfo = Server.getBackOrders();
        ArrayList<String> waitingOrders = ordersInfo.get(buttonName.substring(0, 2));
        ArrayList<String> backOrder = backOrdersInfo.get(buttonName.substring(0, 2));
        currentTable = buttonName.substring(0, 2);

        if (waitingOrders != null) {
            for (String wName : waitingOrders) {
                tableOrdersList.addAll(wName);
            }
        }
        if (backOrder != null) {
            for (String bName : backOrder) {
                backOrdersList.addAll(bName);
            }
        }
        if (orders.isSelected()) {
            tableOrders.setItems(tableOrdersList);
        } else if (backOrders.isSelected()) {
            backOrdersView.setItems( backOrdersList);
        }
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
     * Checks whether the chef is asking for delivery.
     */
    public void checkStatus() {
        StringBuilder deliverMessage = new StringBuilder();
        server = new Server(serverID);
        if (server.callForDeliver() != null) {
            Stage deliverStage = new Stage();
            VBox windowScene = new VBox();
            for (String deliverInfo : server.callForDeliver()) {
                deliverMessage.append(deliverInfo).append("is ready to be delivered\n");
            }
            Label paymentInfo = new Label(deliverMessage.toString());
            Button confirm = new Button("Confirm");
            confirm.setOnAction(e -> {
                try {
                    server.confirmPick();
                    deliverStage.close();
                } catch (IOException ignored){
                }
            });
            windowScene.getChildren().addAll(paymentInfo, confirm);
            Scene deliverScene = new Scene(windowScene, 400, 300);
            deliverStage.setScene(deliverScene);
            deliverStage.show();
        }
    }

    /**
     * Process the returned orders by rejecting the request or accepting request.
     */
    public void handleBackOrders(ActionEvent actionEvent) {
        server = new Server(serverID);
        String backOrderName = backOrdersView.getSelectionModel().getSelectedItem();
        String buttonClicked = actionEvent.getSource().toString();
        String buttonName = buttonClicked.substring(10, 11);
        if (backOrderName != null) {
            if (buttonName.equals("a")) {
                server.agreeReturn(currentTable, backOrderName);
            } else if (buttonName .equals("r")) {
                server.rejectReturn(currentTable, backOrderName);
            }
            backOrdersView.getItems().remove(backOrderName);
            backOrdersView.refresh();
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