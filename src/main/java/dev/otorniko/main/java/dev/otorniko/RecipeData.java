package dev.otorniko;

import java.util.List;

public class RecipeData {
    private String name;
    private List<String> ingredients;
    private int timeMinutes;
    private int portions;
    private List<String> erityisruokavalio; 
    private List<String> steps;

    public RecipeData(String name, List<String> ingredients, int timeMinutes, int portions, List<String> erityisruokavalio, List<String> steps) {
        this.name = name;
        this.ingredients = ingredients;
        this.timeMinutes = timeMinutes;
        this.portions = portions;
        this.erityisruokavalio = erityisruokavalio;
        this.steps = steps;
    }
    
    public String getName() { return name; }
    public List<String> getIngredients() { return ingredients; }
    public int getTimeMinutes() { return timeMinutes; }
    public int getPortions() { return portions; }
    public List<String> getErityisruokavalio() { return erityisruokavalio; }
    public List<String> getSteps() { return steps; }

    public RecipeData() {
    }
    
    public void setName(String name) { this.name = name; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }
    public void setTimeMinutes(int timeMinutes) { this.timeMinutes = timeMinutes; }
    public void setPortions(int portions) { this.portions = portions; }
    public void setErityisruokavalio(List<String> erityisruokavalio) { this.erityisruokavalio = erityisruokavalio; }
    public void setSteps(List<String> steps) { this.steps = steps; }

    public boolean isVegan() {
        return erityisruokavalio != null && erityisruokavalio.contains("vegan");
    }

    public boolean isVegetarian() {
        return erityisruokavalio != null && erityisruokavalio.contains("vegetarian");
    }

    public boolean isGlutenFree() {
        return erityisruokavalio != null && erityisruokavalio.contains("gluten-free");
    }

    public boolean isDairyFree() {
        return erityisruokavalio != null && erityisruokavalio.contains("dairy-free");
    }

    public boolean hasRestriction(String restriction) {
        return erityisruokavalio != null && erityisruokavalio.contains(restriction);
    }

    @Override
    public String toString() { 
        return "RecipeData{" + "name='" + name + '\'' + '}';
    }
}
