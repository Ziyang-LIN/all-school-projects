package models;
/**
 * A food ingredient that is used in the recipe.
 */
public class Ingredient {

    /** The name of this Ingredient. */
    private String name;

    /** The remaining amount of this Ingredient in the restaurant. */
    private int amount;

    /**
     * The selling price of this Ingredient (This is used when customers want addition or subtraction
     * of ingredient in their customized dish.
     */
    private double price;

    /** The threshold to report a shortage of this Ingredient. */
    private int threshold;

    /**
     * Constructs a new Ingredient with the name *name*, remaining amount *amount*, selling price *price*,
     * and shortage threshold *threshold*.
     * @param name This Ingredient's name.
     * @param amount This Ingredient's remaining amount.
     * @param price This Ingredient's selling price.
     * @param threshold This Ingredient's shortage threshold.
     */
    public Ingredient(String name, int amount, double price, int threshold){
        this.name = name;
        this.amount = amount;
        this.price = price;
        this.threshold = threshold;
    }

    /**
     * Sets the remaining amount of this Ingredient to be *amount*.
     * @param amount the new remaining amount of this Ingredient to be set.
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Returns the remaining amount of this Ingredient.
     * @return the amount of this Ingredient.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Returns the selling price of this Ingredient.
     * @return the selling price of this Ingredient.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Returns the name of this Ingredient.
     * @return the name of this Ingredient.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the threshold of this Ingredient.
     * @return the threshold of this Ingredient.
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     * Returns the boolean of whether two ingredients are the same.
     * @return true if two ingredients are the same.
     */
    @Override
    public boolean equals(Object ingredientName){
        return (ingredientName instanceof String && this.name.equals(ingredientName)) ||
                (ingredientName instanceof Ingredient && ((Ingredient) ingredientName).getName().equals(this.name));
    }

    /**
     * Returns the string format of this Ingredient.
     * @return the string format of this Ingredient.
     */
    @Override
    public String toString(){
        return name + ", " + "remaining quantity: " + amount + ", with a threshold of: " + threshold;
    }

    /**
     * Checks if this Ingredient's remaining amount is less than its threshold.
     * @return true if this Ingredient's remaining amount is less than its threshold.
     */
    protected boolean checkShortage(){
        return this.amount < this.threshold;
    }
}