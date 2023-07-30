package controllers;
import models.Employee;
import java.io.*;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

/**
 * The controller for the shipment interface.
 */
public class ShipmentController {

    /** The listView of shipments arrived. */
    public ListView<String> shipment;

    /** The working status label for loading the shipment. */
    public Label workStatus;

    /** The list of shipments ready to be processed. */
    private final ObservableList<String> shipmentList = FXCollections.observableArrayList();

    /** The shipments that are being processed. */
    private final ArrayList<String> processingShipment = new ArrayList<>();


    /** Initializes the shipment interface.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void initialize()throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/src/txt files/Events.txt"))) {
            String line = fileReader.readLine();
            while (line != null) {
                shipmentList.add(line);
                line = fileReader.readLine();
            }
        }
        shipment.setItems(shipmentList);
        workStatus.setText("");
    }

    /**
     * Confirms that a shipment has been received.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void confirmShipment() throws IOException{
        workStatus.setText("loading");
        File inputFile = new File("phase2/src/txt files/Events.txt");
        File tempFile = new File("myTempFile");
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        String lineToMove = shipment.getSelectionModel().getSelectedItem();
        String currentLine;

        while((currentLine = reader.readLine()) != null) {
            if(currentLine.equals(lineToMove)) continue;
            writer.write(currentLine + System.getProperty("line.separator"));
        }
        writer.close();
        reader.close();
        Boolean result = tempFile.renameTo(inputFile);
        if (result) {
            String shipmentInfo = shipment.getSelectionModel().getSelectedItem();
            processingShipment.add(shipmentInfo);
        }
    }

    /**
     * Loads the shipment into the inventory.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void finishLoading() throws IOException{
        Employee employee = new Employee("00");
        if (!shipment.getSelectionModel().isEmpty()) {
            String shipmentFinished = shipment.getSelectionModel().getSelectedItem();
            String[] number = shipmentFinished.split(";");
            if (processingShipment.contains(shipmentFinished)) {
                workStatus.setText("Done!!");
                employee.loadShipment(number[0], Integer.parseInt(number[1].trim()));
                shipmentList.remove(shipmentFinished);
                shipment.refresh();
            }
        }
    }
}
