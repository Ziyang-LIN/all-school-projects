package models;
import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A chef that cooks dish in this restaurant.
 */
public class Chef extends Employee {

    /** The list of dishes that are waiting to be cooked. */
    private Dish dishToCook;

    /** The table number of the dish */
    private String dishTableNum;

    /**
     * Construct a new Chef with the employee id *id*, and inventory of ingredients *inventory*.
     * @param id the id of this Chef.
     */
    public Chef(String id){
        super(id);
        if(!Restaurant.resEmployees.keySet().contains(id)){
            Restaurant.resEmployees.put(id, this);
        }
    }

    /**
     * Returns the ArrayList of the dishes waited to be cooked.
     * @return the ArrayList of the dishes waited to be cooked.
     */
    public ArrayList<String> getDishesToCook() { return Restaurant.dishToBeCooked; }

    /**
     * Confirms that the chef has received the order for a dish *dish* from a table *table*.
     * @param tableNumAndDishName the dish that this order corresponds to.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void receive(String tableNumAndDishName) throws IOException {
        Restaurant.dishToBeCooked.remove(tableNumAndDishName);
        dishTableNum = tableNumAndDishName.substring(0, 2);
        String dishName = tableNumAndDishName.substring(3);
        dishToCook = Restaurant.resTables.get(dishTableNum).getOrders().get(dishName);
        writeLogRe(dishName);
    }

    /**
     * Write the information about receiving dishes to the log file.
     * @param dishName the name of the dish that is received by the chef.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    private void writeLogRe(String dishName) throws IOException{
        BufferedWriter out = null;
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatted = currentTime.format(formatter);
        try {
            FileWriter writer1 = new FileWriter("phase2/src/txt files/log.txt", true);
            out = new BufferedWriter(writer1);
            out.write("\n[" + formatted + "] " + dishName + "is received by the cook.");
        }finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Prepares the dish that this chef has received.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void prepared() throws IOException {
        String serverId = dishToCook.getServerID();
        String dishName = dishToCook.toString();

        if (Restaurant.allPrepared.keySet().contains(serverId)) {
            Restaurant.allPrepared.get(serverId).add(dishTableNum + " " + dishName);
            Restaurant.allPrepared.put(serverId, Restaurant.allPrepared.get(serverId));
        } else {
            ArrayList<String> tem = new ArrayList<>();
            tem.add(dishTableNum + " " + dishToCook.toString());
            Restaurant.allPrepared.put(serverId, tem);
        }
        for (Ingredient ingredient : dishToCook.getIngredients().keySet()) {
            Restaurant.resInventory.useIngredient(ingredient.getName(), dishToCook.getIngredients().get(ingredient));
        }
        writeLogPre(dishName);
    }

    /**
     * Write the information about finishing cooking the dish to the log file.
     * @param dishName the name of the dish that is ready to be delivered.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    private void writeLogPre(String dishName) throws IOException {
        BufferedWriter out = null;
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatted = currentTime.format(formatter);
        try {
            FileWriter writer1 = new FileWriter("phase2/src/txt files/log.txt", true);
            out = new BufferedWriter(writer1);
            out.write("\n[" + formatted + "] " + dishName + "is ready for pick up.");
        } finally {
            if (out != null) {
                out.close();
            }
        } dishToCook = null;
    }
}