package models;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

/**
 * Represents the restaurant Cafe Bellissimo.
 */
public class Restaurant {
    /** The (static) restaurant inventory. */
    protected static final Inventory resInventory;
    static {
        Inventory tem = new Inventory();
        try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/src/txt files/Ingredients.txt"))) {
            String line = fileReader.readLine();
            while (line != null) {
                String[] lineSplit = line.split(", ");
                String name = lineSplit[0];
                int amount = Integer.parseInt(lineSplit[1]);
                int threshold = Integer.parseInt(lineSplit[2]);
                double price = Double.parseDouble(lineSplit[3]);
                Ingredient thisIngredient = new Ingredient(name, amount, price, threshold);
                tem.addIngredient(thisIngredient);
                line = fileReader.readLine();
            }
            fileReader.close();
        } catch (IOException ignored) {

        }
        resInventory = tem;
    }

    /** The static HashMap of restaurant menu. */
    protected static final HashMap<String, Dish> resMenu;
    static{
        HashMap<String, Dish> temp = new HashMap<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/src/txt files/Recipes.txt"))) {
            String line = fileReader.readLine();
            while (line != null) {
                HashMap<Ingredient, Integer> ingredients = new HashMap<>();
                String[] lineSplit = line.split("/");
                String name = lineSplit[0];
                Double price = Double.parseDouble(lineSplit[2]);
                String[] ingToAmount = lineSplit[1].split(", ");
                for (String ingredient : ingToAmount) {
                    String[] temp1 = ingredient.split(" ");
                    String ingName = temp1[1];
                    int amount = Integer.parseInt(temp1[0]);
                    Ingredient thisIngredient = resInventory.getIngredients().get(ingName);
                    ingredients.put(thisIngredient, amount);
                }
                temp.put(name, new Dish(name, price, ingredients));
                line = fileReader.readLine();
            }
        } catch (IOException ignored) {
        }
        resMenu = temp;
    }

    /** The static HashMap of restaurant employees. */
    protected static final HashMap<String, Employee> resEmployees;
    static {
        resEmployees = new HashMap<>();
    }

    /** The static HashMap of tables of the restaurant. */
    protected static final HashMap<String, Table> resTables;
    static {
        resTables = new HashMap<>();
    }

    /** The static HashMap of tables served by the server. */
    protected static final HashMap<String, ArrayList<String>> getTablesToServe;
    static {
        getTablesToServe = new HashMap<>();
    }

    /** The static ArrayList of dishes that the chef is cooking. */
    protected static final ArrayList<String> dishCooking;
    static {
        dishCooking = new ArrayList<>();
    }

    /** The static HashMap of dishes that are prepared by the chef. */
    protected static final HashMap<String, ArrayList<String>> allPrepared;
    static{
        HashMap<String, ArrayList<String>> t = new HashMap<>();
        for (String employee : resEmployees.keySet()){
            if(resEmployees.get(employee) instanceof Server){
                t.put(employee, new ArrayList<>());
            }
        }
        allPrepared = t;
    }

    /** The static HashMap of tables that have checked out. */
    protected static final HashMap<String, ArrayList<String>> tablesToCheckOut;
    static {
        HashMap<String, ArrayList<String>> t = new HashMap<>();
        for (String employee : resEmployees.keySet()){
            if(resEmployees.get(employee) instanceof Server){
                t.put(employee, new ArrayList<>());
            }
        }
        tablesToCheckOut = t;
    }

    /** The static ArrayList of dishes that are waited to be cooked by the chef. */
    protected static final ArrayList<String> dishToBeCooked = new ArrayList<>();

    /** The static HashMap orders that have not been delivered. */
    protected static final HashMap<String, ArrayList<String>> undeliveredOrders = new HashMap<>();

    /** The static HashMap of orders rejected by the customers. */
    protected static final HashMap<String, ArrayList<String>> backOrders = new HashMap<>();

    /** The static HashMap of payments received. */
    protected static final HashMap<String, String> getAllPayments = new HashMap<>();
}