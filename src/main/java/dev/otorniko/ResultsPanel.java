package dev.otorniko;
import javax.swing.*;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;


public class ResultsPanel extends JPanel {

    private JLabel statusLabel;
    private Consumer<RecipeData> recipeActionCallback = null;

    public ResultsPanel() {
        super(new GridBagLayout());
        setLayout(new GridBagLayout());
        initComponents();
        showStatusMessage("<html><div style='text-align: center;'>Ei tuloksia...<br>Lisää raaka-aineita löytääksesi resepti</div></html>");
    }

    public void setRecipeActionCallback(Consumer<RecipeData> callback) {
        this.recipeActionCallback = callback;
    }

    private void initComponents() {
        statusLabel = new JLabel();
        statusLabel.setFont(statusLabel.getFont().deriveFont(16f));
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        add(statusLabel, new GridBagConstraints());
    }

    public void showStatusMessage(String message) {
        removeAll();
        setLayout(new GridBagLayout());
        statusLabel.setText(message);
        add(statusLabel, new GridBagConstraints());
        revalidate();
        repaint();
    }

    public void displayRecipes(List<RecipeData> recipes) {
         removeAll();
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        for (RecipeData recipe : recipes) {
            JPanel recipePanel = createRecipeCard(recipe);
            add(recipePanel);
        }
         revalidate();
         repaint();
    }

    private JPanel createRecipeCard(RecipeData recipe) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createEtchedBorder());

        JLabel titleLabel = new JLabel(recipe.getName());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        card.add(titleLabel, BorderLayout.NORTH);

        JTextArea detailsArea = new JTextArea(
            "Aika: " + recipe.getTimeMinutes() + " min\n" +
            "Annos: " + recipe.getPortions() + "\n" +
            "Ainekset: " + String.join(", ", recipe.getIngredients())
        );
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setOpaque(false);
        detailsArea.setFont(UIManager.getFont("Label.font"));

        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
        detailsScrollPane.setBorder(null);
        card.add(detailsScrollPane, BorderLayout.CENTER);

        card.setPreferredSize(new Dimension(200, 150));

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (recipeActionCallback != null) {
                    recipeActionCallback.accept(recipe);
                } else {
                    System.err.println("WARN: recipeActionCallback is null in ResultsPanel.");
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