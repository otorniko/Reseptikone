package dev.otorniko;

import java.util.List;

/**
 * Raaka-aineen tietoluokka, joka sisältää raaka-aineen nimen, kategoriat ja
 * tagit. Tämä luokka on tarkoitettu käytettäväksi raaka-aineiden käsittelyssä,
 * ei esittämisessä. Se sisältää getterit raaka-aineen nimen, kategorian ja
 * tagien hakemiseen.
 */
public class IngredientData {

    private String name;
    private List<String> tags;
    private String category;

    public IngredientData() {}

    public String getName() { return name; }

    /**
     * Palauttaa raaka-aineen tagit merkkijono listana.
     * 
     * @return <code>List&lt;String&gt; tags</code>
     */
    public List<String> getTags() { return tags; }

    public String getCategory() { return category; }

    @Override
    public String toString() {
        return "IngredientData{" + "name='" + name + '\'' + ", category='" + category + '\'' + ", tags=" + tags + '}';
    }
}