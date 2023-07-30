package models;
import java.util.HashMap;

/**
 * A dish builder that is used to implement the customization requirements by the customers.
 * Note that the dish builder takes in an original dish, then implements customization and outputs.
 */
public class DishBuilder {

    /** The customized dish to be built. */
    private Dish curDish;

    /** Constructs a new DishBuilder. */
    public DishBuilder(){}

    /**
     * Builds a dish.
     * @param orgDish represents the original recipe.
     */
    public void createDish(String orgDish){
        Dish dish = Restaurant.resMenu.get(orgDish);
        HashMap<Ingredient, Integer> temp = new HashMap<>();
        for(Ingredient ingredient: dish.getIngredients().keySet()){
            temp.put(ingredient, dish.getIngredients().get(ingredient));
        }
        this.curDish = new Dish(dish.getName(), dish.getPrice(), temp);
    }

    /**
     * Returns the customized dish.
     * @return the customized dish.
     */
    public Dish toDish(){
        for(Ingredient ingredient: curDish.getIngredients().keySet()){
            if(Restaurant.resInventory.getIngredients().get(ingredient.getName()).getAmount() <= curDish.getIngredients().get(ingredient)){
                return null;
            }
        }
        return curDish;
    }

    /**
     * Adds one unit of an ingredient to the original recipe.
     * @param ingredientName the ingredient to be added.
     */
    public boolean addIngredient(String ingredientName) {
        Ingredient ingredient = Restaurant.resInventory.getIngredients().get(ingredientName);
        if(ingredient.getAmount() >=  this.curDish.getIngredients().get(ingredient) + 1){
            this.curDish.getIngredients().put(ingredient, this.curDish.getIngredients().get(ingredient) + 1);
            this.curDish.setPrice(this.curDish.getPrice() + ingredient.getPrice());
            return true;
        }else{
            return false;
        }
    }

    /**
     * Subtracts one unit of an ingredient to the original recipe, if it will not be lower than the minimum amount.
     * @param ingredientName the ingredient to be subtracted.
     */
    public boolean subtractIngredient(String ingredientName) {
        Ingredient ingredient = Restaurant.resInventory.getIngredients().get(ingredientName);
        if(ingredient.getAmount() >=  this.curDish.getIngredients().get(ingredient) - 1 && curDish.getIngredients().get(ingredient)-1 >= 0){
            this.curDish.getIngredients().put(ingredient, this.curDish.getIngredients().get(ingredient) - 1);
            this.curDish.setPrice(this.curDish.getPrice() - ingredient.getPrice());
            return true;
        }else{
            return false;
        }
    }
}