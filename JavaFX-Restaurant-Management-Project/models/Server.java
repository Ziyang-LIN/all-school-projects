package models;
import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * A server in this restaurant.
 */
public class Server extends Employee {

    /**
     * Constructs a new server with employee id *id*, and inventory of ingredients *inventory*.
     * @param id the id of this server.
     */
    public Server(String id) {
        super(id);
        if(! Restaurant.resEmployees.keySet().contains(id))
            Restaurant.resEmployees.put(id, this);
    }

    /**
     * Obtains all tables that the server serves.
     * @return the HashMap of the all tables that the server serves.
     */
    public static HashMap<String, ArrayList<String>> getAvailable(){
        return Restaurant.getTablesToServe;
    }

    /**
     * Obtains all undelivered orders.
     * @return the HashMap of the all undelivered orders.
     */
    public static HashMap<String, ArrayList<String>> getWaitingOrders(){
        return Restaurant.undeliveredOrders;
    }

    /**
     * Obtains all back orders.
     * @return the HashMap of the all back orders.
     */
    public static HashMap<String, ArrayList<String>> getBackOrders(){
        return Restaurant.backOrders;
    }

    /**
     * Confirms that the order from table *t* of the dish *dish* is placed.
     * @param tableNum the table that corresponds to this order.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void confirmPlaceOrder(String tableNum) throws IOException{
        for(String dish : Restaurant.resTables.get(tableNum).getCurDish()){
            Restaurant.dishToBeCooked.add(tableNum + " " +  dish);
            writeLogCof(tableNum, dish);
        }
        Restaurant.resTables.get(tableNum).curDish = new ArrayList<>();
        Restaurant.getTablesToServe.remove(tableNum);
    }

    /**
     * Write the information about placing orders to the log file.
     * @param tableNum the table number of the table that is placed orders.
     * @param dishName the name of the dish that is placed.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    private void writeLogCof(String tableNum, String dishName) throws IOException {
        BufferedWriter out = null;
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String curTime = currentTime.format(formatter);
        try {
            FileWriter writer1 = new FileWriter("phase2/src/txt files/log.txt", true);
            out = new BufferedWriter(writer1);
            out.write("\n[" + curTime + "] the order: " + dishName +" from Table" +tableNum  + " has been placed");
        }finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Get the ArrayList of the prepared dishes of the tables that the server serves.
     * @return the ArrayList of the prepared dishes of the tables that the server serves.
     */
    public ArrayList<String> callForDeliver(){
        return (Restaurant.allPrepared.get(id));
    }

    /**
     * Confirms that the dishes that are ready to be delivered are picked up.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void confirmPick() throws IOException{
        for(String td: Restaurant.allPrepared.get(id)){
            Table curTable = Restaurant.resTables.get(td.substring(0,2));
            curTable.toConfirm.add(td.substring(3));
            writeLogPck(td);
        }
        Restaurant.allPrepared.remove(id);
    }

    /**
     * Write the information about picking up dishes to the log file.
     * @param dishName the name of the dish that is picked up bu the server.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    private void writeLogPck(String dishName) throws IOException{
        BufferedWriter out = null;
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatted = currentTime.format(formatter);
        try {
            FileWriter writer1 = new FileWriter("phase2/src/txt files/log.txt", true);
            out = new BufferedWriter(writer1);
            out.write("\n[" + formatted + "] " + dishName + " has been picked up for delivery.");
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * reject the customers' requests for back orders.
     * @param tableNum the table number of the customer that requests back orders.
     * @param dishName the name of the dish that is rejected by the customer.
     */
    public void rejectReturn(String tableNum, String dishName){
        Table table = Restaurant.resTables.get(tableNum);
        table.getBackOrders().remove(dishName);
        table.receiveDish(dishName);
    }

    /**
     * accept the customers' requests for back orders.
     * @param tableNum the table number of the customer that requests back orders.
     * @param dishName the name of the dish that is rejected by the customer.
     */
    public void agreeReturn(String tableNum, String dishName){
        Table table = Restaurant.resTables.get(tableNum);
        table.getWaitingOrders().remove(dishName);
        table.getBackOrders().remove(dishName);
        table.getOrders().remove(dishName);
        Restaurant.dishToBeCooked.add(tableNum + " " + dishName);
        System.out.println(Restaurant.dishToBeCooked);
    }
}
