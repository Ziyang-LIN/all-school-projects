package models;
import java.lang.Math;
import java.util.HashMap;

/**
 * A dish that is used to serve a table.
 */
public class Dish {

    /** The id of the server. */
    private String serverID;

    /** The name of this dish. */
    private final String nameOfDish;

    /** The selling price fo this Dish. */
    private double price;

    /** The HashMap of the needed ingredients and their corresponding amount. */
    private HashMap<Ingredient, Integer> ingredients;

    /**
     * Constructs a new Dish with its name *name*, selling price *price*, and each ingredient as well as its
     * corresponding required amount.
     * @param name        the name of this Dish.
     * @param price       the selling price of this Dish.
     * @param ingredients each ingredient as well as its corresponding default, minimum and maximum amount.
     */
    public Dish(String name, double price, HashMap<Ingredient, Integer> ingredients) {
        this.nameOfDish = name;
        this.ingredients = ingredients;
        this.price = price;
    }

    /**
     * Returns the id of the server that serves this Table.
     * @return the id of the server that serves this Table.
     */
    protected String getServerID() {
        return serverID;
    }

    /**
     * Returns the name of this Dish.
     * @return the name of this Dish.
     */
    public String getName() {
        return nameOfDish;
    }

    /**
     * Sets the selling price of this Dish.
     * @param price the new selling price of this Dish.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Returns the selling price of this Dish.
     * @return the selling price of this Dish.
     */
    public double getPrice() {
        return (double) Math.round(price * 100) * 0.01d;
    }

    /**
     * Sets the required ingredients of this Dish.
     * @param ingredients the required ingredients of this Dish.
     */
    public void setIngredients(HashMap<Ingredient, Integer> ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * Returns all ingredients as well as its corresponding amount.
     * @return all ingredients as well as its corresponding amount.
     */
    public HashMap<Ingredient, Integer> getIngredients() {
        return ingredients;
    }

    /**
     * Returns the string format of the dish ordered.
     * @return the string of the dish ordered.
     */
    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder();
        temp.append(this.nameOfDish);
        for (Ingredient i : this.ingredients.keySet()) {
            int dif = this.ingredients.get(i) -
                    Restaurant.resMenu.get(this.nameOfDish).getIngredients().get(i);
            if (dif > 0) {
                temp.append(" +").append(i.getName()).append(" x").append(Integer.toString(dif));
            } else if (dif < 0) {
                temp.append(" -").append(i.getName()).append(" x").append(Integer.toString(Math.abs(dif)));
            }
        }
        return temp.toString();
    }
}
