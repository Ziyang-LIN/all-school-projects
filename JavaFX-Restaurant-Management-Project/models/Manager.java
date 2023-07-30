package models;
import java.util.*;


/**
 * A restaurant manager that is responsible for checking inventory shortages and requesting for ingredients supply,
 * and also supervise all employees in the restaurant to manage the operations.
 */
public class Manager extends Employee {
    /**
     * Constructs a new Manager with employee id *id* and inventory *inventory*.
     * @param id the id of this Manager.
     */
    public Manager(String id) {
        super(id);
        Restaurant.resEmployees.put(id, this);
    }

    public ArrayList<String> getShortage(){
        return Restaurant.resInventory.getOutOfStock();
    }

    /**
     * Obtains all received payments.
     * @return the list of all received payments.
     */
    public static HashMap<String, String> getPayments() {
        return Restaurant.getAllPayments;
    }

    /**
     * Obtains all undelivered orders.
     * @return the list of all undelivered orders.
     */
    public static HashMap<String, ArrayList<String>> getWaitingOrders(){
        return Restaurant.undeliveredOrders;
    }


}






