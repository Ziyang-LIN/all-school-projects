package models;
import java.io.IOException;
/**
 * An employee that works in the restaurant.
 */
public class Employee {

    /** The employee id of this Employee. */
    public String id;

    /**
     * Constructs a new Employee with the employee id *id*, and with the currently workStatus False.
     * @param id the id of this Employee.
     */
    public Employee(String id) {
        this.id = id;
    }

    /**
     * Returns the id of this Employee.
     * @return the id of this Employee.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns whether is the correct employee.
     * @return true if the employeeIDs match.
     */
    @Override
    public boolean equals(Object employeeID){
        return employeeID instanceof String && this.id.equals(employeeID);
    }

    /**
     * Load the newly arriving shipment of an ingredient into the inventory.
     * @param newShipment the newly arriving ingredient.
     * @param quantity    the amount of this new shipment of ingredient.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void loadShipment(String newShipment, int quantity) throws IOException {
        Restaurant.resInventory.loadIngredient(newShipment, quantity);
    }
}


