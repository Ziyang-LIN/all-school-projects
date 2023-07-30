package controllers;
import models.DishBuilder;
import models.Table;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Manage the ingredient customization interface.
 */
public class IngredientController {

    /** The listView of ingredients that can be customized. */
    public ListView<String> ingredients;

    /** The button for the customer to add a certain ingredient. */
    public Button add;

    /** The button for the customer to subtract a certain ingredient. */
    public Button subtract;

    /** The button to confirm the customization. */
    public Button confirm;

    /** Shows the amount of each customization. */
    public Label amount;

    /** The list of ingredients that can be customized. */
    private final ObservableList<String> ingredientList = FXCollections.observableArrayList();

    /** The HashMap of the ingredients and its quantity */
    private final HashMap<String, String> ingredientInfo = new HashMap<>();

    /** The dish builder to generate a dish. */
    private final DishBuilder dishbuilder = new DishBuilder();

    /** The table number that the customers enters in the customer interface. */
    private String tableNumber;


    /**
     * Initializes the ingredient customization interface.
     */
    public void initialize() {
        amount.setText("0");
    }

    /**
     * Sets the table number for this customization.
     * @param tableNum the table number for this customization.
     */
    public void setTableNum(String tableNum){
        tableNumber = tableNum;
    }

    /**
     * Initializes the text contents in the interface.
     * @param ingredientAvailable the ingredients that can be customized.
     */
    public void setIngredientText(String[] ingredientAvailable) {
        for (String anIngredientAvailable : ingredientAvailable) {
            ingredientList.add(anIngredientAvailable.trim());
        }
        ingredients.setItems(ingredientList);
        for (String anIngredientList : ingredientList) {
            ingredientInfo.put(anIngredientList, "0");
        }
        dishbuilder.createDish(ingredientList.get(0));

        ingredientInfo.remove(ingredientList.get(0));
        ingredientList.remove(0);
    }

    /**
     * Customize the amount of the corresponding ingredients.
     */
    public void changeAmount(){
        String ingredientChose = ingredients.getSelectionModel().getSelectedItem();
        amount.setText(ingredientInfo.get(ingredientChose));
    }

    /**
     * Adds one unit of the selected ingredient.
     */
    public void addIngredient(){
        String name = ingredients.getSelectionModel().getSelectedItem();
        if (name !=null){
            amount.setText(ingredientInfo.get(name));
            int quantity = Integer.parseInt(amount.getText());
            if (dishbuilder.addIngredient(name)){
                amount.setText(Integer.toString(quantity+1));
                ingredientInfo.put(name,Integer.toString(quantity+1));
            } else {
                amount.setText(Integer.toString(quantity));
            }
        }
    }

    /**
     * Subtracts one unit of the selected ingredient.
     */
    public void subtractIngredient() {
        String name = ingredients.getSelectionModel().getSelectedItem();
        if (name != null) {
            amount.setText(ingredientInfo.get(name));
            int quantity = Integer.parseInt(amount.getText());
            if (dishbuilder.subtractIngredient(name)){
                amount.setText(Integer.toString(quantity - 1));
                ingredientInfo.put(name, Integer.toString(quantity - 1));
            } else {
                amount.setText(Integer.toString(quantity));
            }
        }
    }

    /**
     * Confirms the ingredient customizations for the dish.
     */
    public void confirmIngredient(){
        /* Initialize a table for the interface */
        Table table = new Table(tableNumber);
        if (dishbuilder.toDish()!= null) {
            table.confirmDish(dishbuilder.toDish());
            Stage stage = (Stage) confirm.getScene().getWindow();
            stage.close();
        }
    }
}