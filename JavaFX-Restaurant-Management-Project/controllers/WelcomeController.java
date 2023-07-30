package controllers;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The controller for the welcoming interface.
 */
public class WelcomeController {

    /**
     * Initializes the welcome interface.
     */
    public void initialize(){}

    /**
     * Enters into the customer interface.
     * @param event an event indicating a command of entering customer interface.
     * @throws Exception handles any possible exceptions in the console.
     */
    public void handleCustomer(MouseEvent event) throws Exception {
        Parent customerView = FXMLLoader.load(getClass().getResource("/views/CustomerView.fxml"));
        Scene customerScene = new Scene(customerView);
        Stage customerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        customerStage.setScene(customerScene);
        customerStage.show();
    }

    /**
     * Enters into the employee login interface.
     * @param events an event indicating a command of entering employee log-in interface.
     * @throws Exception handles any possible exceptions in the console.
     */
    public void handleEmployee(MouseEvent events) throws Exception {
        Parent loginView = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
        Scene loginScene = new Scene(loginView);
        Stage loginStage= (Stage) ((Node) events.getSource()).getScene().getWindow();
        loginStage.setScene(loginScene);
        loginStage.show();
    }
}
