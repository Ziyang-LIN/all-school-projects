package models;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A table for a customer to sit in the restaurant.
 */
public class Table {
    /** The number of this Table. */
    private final String tableNum;

    /** The ArrayList of the history of the number of customers the customer enters. */
    private final ArrayList<Integer> tempCustomerNum = new ArrayList<>();

    /** The total bill of this Table. */
    private double totalBill = 0.00;

    /** The id of the server that serves this Table. */
    private String serverID = null;

    /** The HashMap of orders that this Table. */
    private HashMap<String, Dish> orders = new HashMap<>();

    /** The ArrayList of waiting orders. */
    private ArrayList<String> waitingOrders = new ArrayList<>();

    /** The ArrayList of back orders. */
    private ArrayList<String> backOrders = new ArrayList<>();

    private final HashMap<String, Integer> allOrders = new HashMap<>();

    /** The ArrayList of the current dish. */
    protected ArrayList<String> curDish = new ArrayList<>();

    /** The ArrayList of the dishes that confirmed. */
    protected final ArrayList<String> toConfirm = new ArrayList<>();

    /**
     * Constructs a new Table with the table number *number*.
     * @param number the table number of this Table.
     */
    public Table(String number) {
        this.tableNum = number;
        if(!Restaurant.resTables.keySet().contains(tableNum)){
            Restaurant.resTables.put(tableNum, this);
        }else{
            orders = Restaurant.resTables.get(tableNum).orders;
            waitingOrders = Restaurant.resTables.get(tableNum).getWaitingOrders();
            backOrders = Restaurant.resTables.get(tableNum).getBackOrders();
            totalBill = Restaurant.resTables.get(tableNum).getTotalBill();
        }
    }

    /**
     * Returns the table number of this Table.
     * @return the table number of this Table.
     */
    public String getTableNum() {
        return tableNum;
    }

    /**
     * Returns the ArrayList of the history of the number of customers the customer enters.
     * @return the ArrayList of the history of the number of customers.
     */
    public ArrayList<Integer> getTempCustomerNum() {
        return Restaurant.resTables.get(tableNum).tempCustomerNum;
    }

    /**
     * Returns the ArrayList of the dishes that confirmed.
     * @return the ArrayList of the dishes that confirmed.
     */
    public ArrayList<String> getToConfirm() {
        return Restaurant.resTables.get(tableNum).toConfirm;
    }

    /**
     * Returns the ArrayList of the waiting orders.
     * @return the ArrayList of the waiting orders.
     */
    public ArrayList<String> getWaitingOrders() {
        return waitingOrders;
    }

    /**
     * Returns the ArrayList of the back orders.
     * @return the ArrayList of the back orders.
     */
    public ArrayList<String> getBackOrders() {
        return backOrders;
    }

    /**
     * Returns the HashMap of all the orders of the table.
     * @return the HashMap of all the orders of the table.
     */
    public HashMap<String, Integer> getAllOrders() {
        return allOrders;
    }

    /**
     * Returns the ArrayList of all the orders that are placed.
     * @return the ArrayList of all the orders that are placed.
     */
    public ArrayList<String> getPlacedOrders(){
        ArrayList<String> tem = new ArrayList<>();
        for(String dishName : Restaurant.resTables.get(tableNum).getAllOrders().keySet()){
            for (int i=0; i < Restaurant.resTables.get(tableNum).getAllOrders().get(dishName); i++){
                tem.add(dishName);
            }
        }
        return tem;
    }

    /**
     * Sets the list of orders that this Table.
     */
    public void setOrders(HashMap<String, Dish> orders) {
        this.orders = orders;
    }

    /**
     * Returns the list of orders that this Table.
     * @return the list of orders that this Table.
     */
    public HashMap<String, Dish> getOrders() {
        return orders;
    }

    /**
     * Returns the list of current dishes.
     * @return the list of current dishes.
     */
    public ArrayList<String> getCurDish() {
        return Restaurant.resTables.get(tableNum).curDish;
    }

    /**
     * Returns the total bill of this Table.
     * @return the total bill of this Table.
     */
    public double getTotalBill() {
        return totalBill;
    }

    /**
     * Returns whether two tables are the same.
     * @return true if the two tables are the same.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof String) && this.tableNum.equals(obj);
    }

    /**
     * The customer confirms to order this dish.
     * @param dish the dish the customer wants to order.
     */
    public void confirmDish(Dish dish) {
        Table curTable = Restaurant.resTables.get(tableNum);
        String curDishName = dish.toString();
        if(curTable.orders.containsKey(curDishName)){
            int newAmount = curTable.getAllOrders().get(curDishName) + 1;
            curTable.allOrders.put(curDishName, newAmount);
        }else{
            curTable.orders.put(dish.toString(), dish);
            curTable.allOrders.put(curDishName, 1);
        }
        curTable.curDish.add(curDishName);
        curTable.totalBill += dish.getPrice();
    }

    /**
     * Cancels the dish for this Table before finish ordering.
     * @param dishString the dish that this Table wants to cancel.
     */
    public void cancelDish(String dishString) {
        Table curTable = Restaurant.resTables.get(tableNum);
        curTable.curDish.remove(dishString);
        curTable.totalBill -= orders.get(dishString).getPrice();
        if(curTable.allOrders.get(dishString) > 1){
            int newAmount = curTable.allOrders.get(dishString) - 1;
            curTable.allOrders. replace(dishString, newAmount);
        }else{
            curTable.orders.remove(dishString);
            curTable.allOrders.remove(dishString);
        }
    }

    /**
     * The customer finishes ordering.
     */
    public void placeOrder(){
        Table curTable = Restaurant.resTables.get(tableNum);
        if(!curTable.curDish.isEmpty() &&
                !((Restaurant.getTablesToServe.containsKey(tableNum))
                        && Restaurant.getTablesToServe.get(tableNum).equals(curTable.curDish))){
            if(Restaurant.getTablesToServe.containsKey(tableNum)){
                if(curTable.curDish.size() - Restaurant.getTablesToServe.get(tableNum).size() > 0){
                    for(int i = 0; i < curTable.curDish.size() - Restaurant.getTablesToServe.get(tableNum).size(); i++){
                        curTable.waitingOrders.add(curTable.curDish.get(Restaurant.getTablesToServe.get(tableNum).size()+i));
                    }
                }
            }else{
                curTable.waitingOrders.addAll(curTable.curDish);
            }
            Restaurant.undeliveredOrders.put(tableNum, curTable.waitingOrders);
            ArrayList<String> temp = new ArrayList<>(curTable.curDish);
            Restaurant.getTablesToServe.put(tableNum, temp);
        }
    }

    /**
     * Confirms that this Table has receive the dish *d*.
     * @param dish the dish that is delivered to this Table.
     */
    public void receiveDish(String dish){
        Restaurant.resTables.get(tableNum).toConfirm.remove(dish);
        waitingOrders.remove(dish);
    }

    /**
     * The customer rejects the dish and wants to return it.
     * @param dish the dish that is delivered to this Table.
     */
    public void rejectDish(String dish) {
        if(Restaurant.backOrders.containsKey(tableNum)){
            Restaurant.backOrders.get(tableNum).add(dish);
        }else{
            ArrayList<String> temp = new ArrayList<>();
            temp.add(dish);
            Restaurant.backOrders.put(tableNum, temp);
        }
        backOrders.add(dish);
    }

    /**
     * Prints out the bill of the table *t*.
     * @param tipPercentage the tip percentage the customer chooses.
     * @param separate the boolean whether the customer chooses to separate the bill.
     * @param numOfPeople the number of customers the table has.
     * @return the bill of the table.
     */
    public String checkOut(int tipPercentage, boolean separate, int numOfPeople){
        StringBuilder temp = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = now.format(formatter);
        double subTotal = separate ? Math.round(totalBill/numOfPeople) : totalBill;
        double tax = (double) Math.round(subTotal * 0.13);
        double tips = (double) Math.round((tipPercentage * subTotal)/100);
        double total = subTotal + tax + tips;
        double total2 = subTotal*numOfPeople;

        temp.append("              CAFE BELLISSIMO              " + "\n-------------------------------------------" + "\nTABLE: ").append(tableNum).append("\nDATE: ").append(formatDateTime).append("\n------------------Orders-------------------\n");

        for (String dish : orders.keySet()) {
            temp.append(dish).append("                   ").append(orders.get(dish).getPrice()).append("\n");
            temp.append(System.lineSeparator());
        }

        temp.append("\n-----------------Payments------------------" + "\n                   Sub Total:").append(subTotal).append("\n                   Sales Tax:").append(tax).append("\n                   Tips:").append(tips).append("\n                   Total:").append(total);
        Restaurant.resTables.remove(tableNum);
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyMMdd");
        String curTime = now.format(format);
        StringBuilder tem = new StringBuilder();
        Double t = separate ? total * numOfPeople : total;
        if (Restaurant.getAllPayments.containsKey(curTime)){
            tem.append(Restaurant.getAllPayments.get(curTime)).append("Table").append(tableNum).append(" paid ").append(Double.toString(t));
            Restaurant.getAllPayments.put(curTime, tem.toString());
        }else{
            Restaurant.getAllPayments.put(curTime, "Table" + tableNum + " paid " + Double.toString(t));
        }
        return temp.toString();
    }
}