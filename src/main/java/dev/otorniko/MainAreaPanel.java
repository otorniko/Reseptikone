package dev.otorniko;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;

/**
 * Luokka, joka esittää pääalueen paneelin, joka sisältää käyttöliittymän
 * ohjauspaneelin, tulospaneelin ja reseptin yksityiskohtaisen paneelin. Tämä
 * luokka käyttää JPanel-luokkaa käyttöliittymän rakentamiseen.
 */
public class MainAreaPanel extends JPanel {
    private ControlsPanel controlsPanel;
    private ResultsPanel resultsPanel;
    private JScrollPane resultsScrollPane;
    private RecipeDetailPanel detailPanel;

    public MainAreaPanel() {
        super(new BorderLayout(5, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        controlsPanel = new ControlsPanel();
        add(controlsPanel, BorderLayout.NORTH);

        resultsPanel = new ResultsPanel();
        resultsPanel.setRecipeActionCallback(this::showRecipeDetails);

        resultsScrollPane = new JScrollPane(resultsPanel);
        resultsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        resultsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(resultsScrollPane, BorderLayout.CENTER);
    }

    private void showRecipeDetails(RecipeData recipe) {
        if (detailPanel != null) {
            remove(detailPanel);
            detailPanel = null;
        }

        remove(resultsScrollPane);

        detailPanel = new RecipeDetailPanel(recipe);
        detailPanel.getBackButton().addActionListener(e -> showResults());
        add(detailPanel, BorderLayout.CENTER);

        revalidate();
        repaint();

        SwingUtilities.invokeLater(() -> {
            if (detailPanel != null) {
                detailPanel.requestFocusInWindow();
            }
        });
    }

    private void showResults() {
        if (detailPanel != null) {
            remove(detailPanel);
            detailPanel = null;
        }

        add(resultsScrollPane, BorderLayout.CENTER);

        revalidate();
        repaint();

        // Hakupalkin fokusoinnin estämiseks
        SwingUtilities.invokeLater(() -> {
            if (resultsPanel != null) {
                resultsPanel.requestFocusInWindow();
            } else if (resultsScrollPane != null) {
                resultsScrollPane.requestFocusInWindow();
            }
        });
    }

    public ControlsPanel getControlsPanel() { return controlsPanel; }

    public ResultsPanel getResultsPanel() { return resultsPanel; }

    public RecipeDetailPanel getDetailPanel() { return detailPanel; }
}