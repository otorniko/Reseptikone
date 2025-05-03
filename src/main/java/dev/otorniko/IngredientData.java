package dev.otorniko;
import java.util.List;

public class IngredientData {
    private String name;
    private List<String> tags;
    private String category; 

    public IngredientData() {}

    public String getName() {
        return name;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "IngredientData{" +
               "name='" + name + '\'' +
               ", category='" + category + '\'' + 
               ", tags=" + tags +
               '}';
    }
}