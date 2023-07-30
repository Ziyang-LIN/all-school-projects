package controllers;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * The controller for the login interface.
 */
public class LoginController{

    /** Input text field for username. */
    public TextField username;

    /** Input text field for password. */
    public TextField password;

    /** The sign in label. */
    public Label SignIn;

    /** The list of employees of this restaurant. */
    private static final HashMap<String, String> employeeList = new HashMap<>();

    /**
     * Initializes the login interface.
     */
    public void initialize(){}

    /**
     * Log in command to different interfaces of the application.
     * @param event the user clicks on the enter button.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void LogIn(MouseEvent event) throws IOException {
        String employeeID = username.getText();
        String employeePsd = password.getText();
        getEmployeeList();
        if (employeeList.containsKey(employeeID) && employeePsd.equals(employeeList.get(employeeID))) {
            Parent nextView;
            switch (employeeID.substring(0, 2)) {
                case "01":
                    nextView = FXMLLoader.load(getClass().getResource("/views/ManagerView.fxml"));
                    break;
                case "02":
                    nextView = FXMLLoader.load(getClass().getResource("/views/ChefView.fxml"));
                    break;
                default:
                    nextView = FXMLLoader.load(getClass().getResource("/views/ServerView.fxml"));
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/views/ServerView.fxml"));
                    loader.load();
                    ServerController server = loader.getController();
                    server.setServerID(employeeID);
                    break;
            }
            Scene nextViewScene = new Scene(nextView);
            Stage nextViewStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            nextViewStage.setScene(nextViewScene);
            nextViewStage.show();
        } else {
            SignIn.setText("Failed");
            SignIn.setStyle("-fx-text-fill: red");
        }
    }

    /**
     * Configure the employee list for the application.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    private void getEmployeeList() throws IOException{
        try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/src/txt files/Employees.txt"))) {
            String line = fileReader.readLine();
            while (line != null) {
                String[] lineSplit = line.split(", ");
                String user = lineSplit[0];
                String code = lineSplit[1];
                employeeList.put(user, code);
                line = fileReader.readLine();
            }
        }
    }

    /**
     * Command of going back to the welcome page.
     * @param events the user clicks on the back button.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void backToWelcome(MouseEvent events) throws IOException {
        Parent welcomeView = FXMLLoader.load(getClass().getResource("/views/Welcome.fxml"));
        Scene welcomeScene = new Scene(welcomeView);
        Stage welcomeStage = (Stage) ((Node) events.getSource()).getScene().getWindow();
        welcomeStage.setScene(welcomeScene);
        welcomeStage.show();
    }

}
