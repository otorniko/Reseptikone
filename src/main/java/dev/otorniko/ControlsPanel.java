package dev.otorniko;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ControlsPanel extends JPanel {

    // Diet
    private ButtonGroup dietGroup;
    private JRadioButton rbNone, rbVeg, rbVegan;
    private JButton infoButton;

    // Sorting & Filtering
    private JComboBox<String> sortComboBox;
    private JToggleButton nopeatButton; 
    private JToggleButton helpotButton;
    private JButton showAllFiltersButton; 

    public ControlsPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        initComponents();
    }

    private void initComponents() {

        JPanel dietPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2)); 
        dietPanel.add(new JLabel("Valitse erityisruokavalio:"));

        rbNone = new JRadioButton("Ei ole", true); 
        rbVeg = new JRadioButton("Kasvis");
        rbVegan = new JRadioButton("Vegaani");

        dietGroup = new ButtonGroup();
        dietGroup.add(rbNone);
        dietGroup.add(rbVeg);
        dietGroup.add(rbVegan);

        dietPanel.add(rbNone);
        dietPanel.add(rbVeg);
        dietPanel.add(rbVegan);

        infoButton = new JButton("i");
        infoButton.setMargin(new Insets(1, 4, 1, 4));
        infoButton.addActionListener(e -> showHelpDialog()); 

        JPanel dietAndInfoPanel = new JPanel(new BorderLayout());
        dietAndInfoPanel.add(dietPanel, BorderLayout.CENTER);
        dietAndInfoPanel.add(infoButton, BorderLayout.EAST);
        dietAndInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);


        JPanel sortFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        sortFilterPanel.add(new JLabel("Järjestä:"));
        sortComboBox = new JComboBox<>(new String[]{"Oletus", "Nimi A-Ö", "Aika Lyhin"});

        sortFilterPanel.add(sortComboBox);
        sortFilterPanel.add(Box.createHorizontalStrut(15));
        sortFilterPanel.add(new JLabel("Suodattimet:"));

        nopeatButton = new JToggleButton("Nopeat");
        helpotButton = new JToggleButton("Helpot");
        showAllFiltersButton = new JButton("Poista suodattimet"); 

        sortFilterPanel.add(nopeatButton);
        sortFilterPanel.add(helpotButton);
        sortFilterPanel.add(showAllFiltersButton);
        sortFilterPanel.setAlignmentX(Component.LEFT_ALIGNMENT); 

        add(dietAndInfoPanel);
        add(Box.createVerticalStrut(5)); 
        add(sortFilterPanel);

        showAllFiltersButton.addActionListener(e -> {
            if (nopeatButton != null) nopeatButton.setSelected(false);
            if (helpotButton != null) helpotButton.setSelected(false);
        });
    }

    public String getSelectedDietOption() {
        if (rbVeg != null && rbVeg.isSelected()) return "Kasvis";
        if (rbVegan != null && rbVegan.isSelected()) return "Vegaani";
        return "Ei ole"; 
    }

    public String getSelectedSortOption() {
        if (sortComboBox != null) {
            return (String) sortComboBox.getSelectedItem();
        }
        return "Oletus";
    }

    public boolean isNopeatFilterActive() {
        return nopeatButton != null && nopeatButton.isSelected();
    }

    public boolean isHelpotFilterActive() {
        return helpotButton != null && helpotButton.isSelected();
    }

    public void addControlChangeListener(ActionListener listener) {
        // Diet Radio Buttons
        if (rbNone != null) rbNone.addActionListener(listener);
        if (rbVeg != null) rbVeg.addActionListener(listener);
        if (rbVegan != null) rbVegan.addActionListener(listener);

        // Sort Combo Box
        if (sortComboBox != null) sortComboBox.addActionListener(listener);

        // Filter Toggle Buttons 
        if (nopeatButton != null) nopeatButton.addActionListener(listener);
        if (helpotButton != null) helpotButton.addActionListener(listener);
        if (showAllFiltersButton != null) showAllFiltersButton.addActionListener(listener);
    }

    private void showHelpDialog() {
        String helpMessage = "<html>" +
                "<b>Perusohjeet:</b><br>" +
                "1. Valitse raaka-aineita listasta<br>" +
                "2. Selaa löytyneitä reseptejä<br>" +
                "3. Klikkaa reseptiä nähdäksesi ohjeet<br><br>" +
                "<b>Vinkki:</b> Voit järjestää ja suodattaa listaa yläpalkin valinnoilla.<br>" +
                "</html>";

        JOptionPane.showMessageDialog(this,
                                      helpMessage,
                                      "Käyttöohje", 
                                      JOptionPane.INFORMATION_MESSAGE); 
    }
}