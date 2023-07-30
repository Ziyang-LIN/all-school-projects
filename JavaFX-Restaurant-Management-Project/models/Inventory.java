package models;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The inventory of ingredients in the kitchen.
 */
public class Inventory {

    /** The list of ingredients in this Inventory. */
    private final HashMap<String, Ingredient> ingredients = new HashMap<>();

    /** The list of ingredients that are under threshold in this Inventory. */
    private final HashMap<String, Ingredient> outOfStock = new HashMap<>();

    /** Constructs a new Inventory of ingredients. */
    public Inventory() { }

    /**
     * Returns the list of ingredients in this Inventory.
     * @return the list of ingredients in this Inventory.
     */
    public HashMap<String, Ingredient> getIngredients() {
        return Restaurant.resInventory.ingredients;
    }


    protected ArrayList<String> getOutOfStock() {
        ArrayList<String> result = new ArrayList<>();
        result.addAll(outOfStock.keySet());
        return result;
    }

    /**
     * Check whether an ingredient is under threshold.
     * @param ingredient the ingredient that needs to be checked.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    private void checkIngredient(Ingredient ingredient) throws IOException{
        if(ingredient.checkShortage()){
            if(!outOfStock.containsKey(ingredient.getName())){
                outOfStock.put(ingredient.getName(), ingredient);
                writeRequest(ingredient.getName());
            }
        }else{
            if(outOfStock.containsKey(ingredient.getName())){
                outOfStock.remove(ingredient.getName());
            }
        }
    }

    /**
     * Adds a new ingredient into this Inventory
     * @param newIngredient the new ingredient to be added into this Inventory.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    public void addIngredient(Ingredient newIngredient) throws IOException{
        checkIngredient(newIngredient);
        ingredients.put(newIngredient.getName(), newIngredient);
    }

    /**
     * Uses a certain amount of an ingredient from this Inventory.
     * @param ingredientName the ingredient in this Inventory to be used.
     * @param amount the usage of the ingredient.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    protected void useIngredient(String ingredientName, int amount) throws IOException {
        Ingredient targetedIngredient = ingredients.get(ingredientName);
        String threshold = Integer.toString(targetedIngredient.getThreshold());
        String price = Double.toString(targetedIngredient.getPrice());
        targetedIngredient.setAmount(targetedIngredient.getAmount() - amount);
        checkIngredient(Restaurant.resInventory.getIngredients().get(ingredientName));

        String modifiedAmount = Integer.toString(targetedIngredient.getAmount());
        String newLine = ingredientName + ", " + modifiedAmount + ", " + threshold + ", " + price;
        StringBuilder newContent = new StringBuilder("");
        try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/src/txt files/Ingredients.txt"))) {
            String line = fileReader.readLine();
            while (line != null) {
                if (line.substring(0, line.indexOf(",")).equals(ingredientName)) {
                    newContent.append(newLine);
                    newContent.append(System.lineSeparator());
                } else {
                    newContent.append(line);
                    newContent.append(System.lineSeparator());
                }
                line = fileReader.readLine();
            }
            FileWriter writer = new FileWriter("phase2/src/txt files/Ingredients.txt");
            writer.write(newContent.toString());
            fileReader.close();
            writer.close();
        }
    }

    /**
     * Write the information about requesting new ingredients to the request file.
     * @param ingredientName the name of the ingredient that is under threshold.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    private void writeRequest(String ingredientName) throws IOException {
        String fileName = "phase2/src/txt files/Requests.txt";
        StringBuilder content = new StringBuilder("");
        try (BufferedReader fileReader = new BufferedReader(new FileReader(fileName))) {
            String line = fileReader.readLine();
            while (line != null) {
                content.append(line);
                content.append(System.lineSeparator());
                line = fileReader.readLine();
            }
            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String curTime  = currentTime.format(formatter);
            String newRequest = "[" +curTime+ "] Could you please send 20 supplies of " + ingredientName + "? Thank you!";
            content.append(newRequest);
            FileWriter writer = new FileWriter(fileName);
            writer.write(content.toString());
            writer.close();
            fileReader.close();
        }
    }

    /**
     * Loads a new shipment of an ingredient to this Inventory.
     * @param newIngredient the newly arriving ingredient.
     * @param amount the amount of the new shipment.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    protected void loadIngredient(String newIngredient, int amount) throws IOException {
        Ingredient targetedIngredient = ingredients.get(newIngredient);
        targetedIngredient.setAmount(targetedIngredient.getAmount() + amount);
        checkIngredient(Restaurant.resInventory.getIngredients().get(newIngredient));

        String newLine = targetedIngredient.getName() + ", " + Integer.toString(targetedIngredient.getAmount()) +
                ", " + Integer.toString(targetedIngredient.getThreshold()) + ", " +
                Double.toString(targetedIngredient.getPrice());
        StringBuilder newContent = new StringBuilder("");
        try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/src/txt files/Ingredients.txt"))) {
            String line = fileReader.readLine();
            while (line != null) {
                if (line.substring(0, line.indexOf(",")).equals(targetedIngredient.getName())) {
                    newContent.append(newLine);
                    newContent.append(System.lineSeparator());
                } else {
                    newContent.append(line);
                    newContent.append(System.lineSeparator());
                }
                line = fileReader.readLine();
            }
            FileWriter writer = new FileWriter("phase2/src/txt files/Ingredients.txt");
            writer.write(newContent.toString());
            fileReader.close();
            writer.close();
            writeLog(newIngredient);
        }
    }

    /**
     * Write the information about loading ingredients to the log file.
     * @param ingredient the name of the ingredient that is loaded.
     * @throws IOException handles any errors when an input or output operation is failed or interpreted.
     */
    private void writeLog(String ingredient) throws IOException {
        BufferedWriter out = null;
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatted = currentTime.format(formatter);
        try {
            FileWriter writer = new FileWriter("phase2/src/txt files/log.txt", true);
            out = new BufferedWriter(writer);
            out.write("\n[" + formatted + "]  Ingredient: " + ingredient + " is loaded");
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Returns the string format of the ingredient.
     * @return the string ofthe ingredient.
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("");
        for (Ingredient ingredient: ingredients.values()) {
            output.append(ingredient.toString());
            output.append(System.lineSeparator());
        }
        return output.toString();
    }



}
