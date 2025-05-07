package dev.otorniko;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

/**
 * Luokka, joka esittää löydetyt reseoptit graafisessa käyttöliittymässä. Tämä
 * luokka käyttää JPanel-luokkaa käyttöliittymän rakentamiseen ja esittää
 * reseptit kortteina, jotka sisältävät reseptin nimen, valmistusajan, annosten
 * määrän ja raaka-aineet.
 */
public class ResultsPanel extends JPanel {

    private JLabel statusLabel;
    private Consumer<RecipeData> recipeActionCallback = null;

    public ResultsPanel() {
        super(new WrapLayout(WrapLayout.CENTER, 10, 10));
        initComponents();
        showStatusMessage("Valitse raaka-aineita löytääksesi reseptejä.");
    }

    public void setRecipeActionCallback(Consumer<RecipeData> callback) { this.recipeActionCallback = callback; }

    private void initComponents() {
        statusLabel = new JLabel();
        statusLabel.setFont(statusLabel.getFont().deriveFont(16f));
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public void removeAll() { super.removeAll(); }

    /**
     * Näyttää tilaviestin tulospaneelissa.
     * 
     * @param message näytettävä viesti
     */
    public void showStatusMessage(String message) {
        removeAll();
        setLayout(new GridBagLayout());
        statusLabel.setText(message);
        add(statusLabel, new GridBagConstraints());
        revalidate();
        repaint();
    }

    /**
     * Näyttää reseptilistan tulospaneelissa.
     * 
     * @param recipes lista näytettäviä reseptejä
     */
    public void displayRecipes(List<RecipeData> recipes) {

        removeAll();
        setLayout(new WrapLayout(WrapLayout.CENTER, 10, 10));

        for (RecipeData recipe : recipes) {
            JPanel recipePanel = createRecipeCard(recipe);
            add(recipePanel);
        }
        revalidate();
        repaint();
    }

    /**
     * Luo korttikomponentin reseptille.
     * 
     * @param recipe reseptin tiedot, jotka näytetään kortilla
     * @return JPanel, joka edustaa reseptikorttia
     */
    private JPanel createRecipeCard(final RecipeData recipe) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createEtchedBorder());

        JLabel titleLabel = new JLabel(recipe.getName());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        card.add(titleLabel, BorderLayout.NORTH);

        JTextArea detailsArea = new JTextArea("Aika: " + recipe.getTimeMinutes() + " min\n" + "Annos: "
                + recipe.getPortions() + "\n" + "Ainekset: " + String.join(", ", recipe.getIngredients()));
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setOpaque(false);
        detailsArea.setFont(UIManager.getFont("Label.font"));

        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
        detailsScrollPane.setBorder(null);
        card.add(detailsScrollPane, BorderLayout.CENTER);

        card.setPreferredSize(new Dimension(220, 180));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (recipeActionCallback != null) {
                    recipeActionCallback.accept(recipe);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                card.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setCursor(Cursor.getDefaultCursor());
                card.setBackground(UIManager.getColor("Panel.background"));
            }
        });
        return card;
    }
}