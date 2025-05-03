package dev.otorniko;

import javax.swing.*;
import java.awt.*;

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
    }

    private void showResults() {
        if (detailPanel != null) {
            remove(detailPanel);
            detailPanel = null;
        }

        add(resultsScrollPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public ControlsPanel getControlsPanel() {
        return controlsPanel;
    }
    public ResultsPanel getResultsPanel() {
        return resultsPanel;
    }
    public RecipeDetailPanel getDetailPanel() {
        return detailPanel;
    }
}