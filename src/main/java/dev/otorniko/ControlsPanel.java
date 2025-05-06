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
    private JComboBox<String> filterComboBox;

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

        JPanel sortFilterPanel = new JPanel();
        sortFilterPanel.setLayout(new BoxLayout(sortFilterPanel, BoxLayout.X_AXIS)); 
        sortFilterPanel.add(new JLabel("Järjestä:"));
        sortFilterPanel.add(Box.createHorizontalStrut(5));
        sortComboBox = new JComboBox<>(new String[]{"Oletus", "Nimi A-Ö", "Aika Lyhin"});
        sortComboBox.setMaximumSize(sortComboBox.getPreferredSize());
        sortFilterPanel.add(sortComboBox);
        sortFilterPanel.add(Box.createHorizontalGlue());
        sortFilterPanel.add(new JLabel("Suodattimet:"));
        sortFilterPanel.add(Box.createHorizontalStrut(5));
        nopeatButton = new JToggleButton("Nopeat");
        helpotButton = new JToggleButton("Helpot");
        filterComboBox = new JComboBox<>(new String[]{"Näytä Kaikki", "Hitaat", "Vaikeat", "Koko Perheelle"});
        filterComboBox.setMaximumSize(filterComboBox.getPreferredSize()); 
        sortFilterPanel.add(nopeatButton);
        sortFilterPanel.add(Box.createHorizontalStrut(5));
        sortFilterPanel.add(helpotButton);
        sortFilterPanel.add(Box.createHorizontalStrut(5));
        sortFilterPanel.add(filterComboBox); 
        sortFilterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(dietAndInfoPanel);
        add(Box.createVerticalStrut(5));
        add(sortFilterPanel);
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

    public String getSelectedFilterOption() {
        if (filterComboBox != null) {
            return (String) filterComboBox.getSelectedItem();
        }
        return "Näytä Kaikki";
    }

    public boolean isNopeatFilterActive() {
        return nopeatButton != null && nopeatButton.isSelected();
    }

    public boolean isHelpotFilterActive() {
        return helpotButton != null && helpotButton.isSelected();
    }

    public boolean isHitaatFilterActive() {
        return filterComboBox != null && filterComboBox.getSelectedItem().equals("Hitaat");
    }

    public boolean isVaikeatFilterActive() {
        return filterComboBox != null && filterComboBox.getSelectedItem().equals("Vaikeat");
    }

    public boolean isKokoPerheelleFilterActive() {
        return filterComboBox != null && filterComboBox.getSelectedItem().equals("Koko Perheelle");
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
        
        // Filter Combo Box
        if (filterComboBox != null) filterComboBox.addActionListener(listener);

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