package dev.otorniko;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;

/**
 * Luokka, joka esittää reseptin yksityiskohtia. Tämä luokka sisältää reseptin
 * nimen, valmistusajan, annosten määrän, raaka-aineet ja valmistusohjeet.
 * Luokka käyttää JPanel-luokkaa käyttöliittymän rakentamiseen ja esittää
 * reseptin tiedot käyttöliittymässä.
 */
public class RecipeDetailPanel extends JPanel {

    private JButton backButton;

    /**
     * Luo RecipeDetailPanel-olion, joka näyttää yksityiskohtaiset tiedot
     * reseptistä.
     * 
     * @param recipe näytettävän reseptin tiedot
     */
    public RecipeDetailPanel(RecipeData recipe) {
        super(new BorderLayout(10, 10));

        Border visibleBorder = BorderFactory.createEtchedBorder();
        Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);

        this.setBorder(BorderFactory.createCompoundBorder(visibleBorder, paddingBorder));

        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        backButton = new JButton("<< Takaisin");
        backButton.setMargin(new Insets(2, 5, 2, 5));
        headerPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel(recipe.getName());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainContentPanel = new JPanel(new BorderLayout(15, 0));

        JPanel infoIngredientsPanel = new JPanel();
        infoIngredientsPanel.setLayout(new BoxLayout(infoIngredientsPanel, BoxLayout.Y_AXIS));
        infoIngredientsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel infoLabel = new JLabel(
                String.format("Aika: %d min | Annos: %d", recipe.getTimeMinutes(), recipe.getPortions()));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoIngredientsPanel.add(infoLabel);
        infoIngredientsPanel.add(Box.createVerticalStrut(10));

        JLabel ingredientsTitle = new JLabel("Ainekset:");
        ingredientsTitle.setFont(ingredientsTitle.getFont().deriveFont(Font.BOLD));
        ingredientsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoIngredientsPanel.add(ingredientsTitle);

        JTextArea ingredientsArea = new JTextArea(String.join("\n", recipe.getIngredients()));
        ingredientsArea.setEditable(false);
        ingredientsArea.setOpaque(false);
        ingredientsArea.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
        ingredientsArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoIngredientsPanel.add(ingredientsArea);
        infoIngredientsPanel.add(Box.createVerticalGlue());

        mainContentPanel.add(infoIngredientsPanel, BorderLayout.EAST);

        JPanel stepsPanel = new JPanel(new BorderLayout(0, 5));
        stepsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel stepsTitle = new JLabel("Ohjeet:");
        stepsTitle.setFont(stepsTitle.getFont().deriveFont(Font.BOLD));
        stepsPanel.add(stepsTitle, BorderLayout.NORTH);

        JTextArea stepsArea = new JTextArea();
        List<String> steps = recipe.getSteps();

        if (steps != null) {
            for (int i = 0; i < steps.size(); i++) {
                stepsArea.append((i + 1) + ". " + steps.get(i) + "\n");
            }
        } else {
            stepsArea.setText("Ohjeita ei löytynyt.");
        }

        stepsArea.setEditable(false);
        stepsArea.setLineWrap(true);
        stepsArea.setWrapStyleWord(true);
        stepsArea.setOpaque(false);

        JScrollPane stepsScrollPane = new JScrollPane(stepsArea);
        stepsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        stepsScrollPane.setPreferredSize(new Dimension(300, 200));

        stepsPanel.add(stepsScrollPane, BorderLayout.CENTER);
        mainContentPanel.add(stepsPanel, BorderLayout.CENTER);
        add(mainContentPanel, BorderLayout.CENTER);
    }

    public JButton getBackButton() { return backButton; }
}