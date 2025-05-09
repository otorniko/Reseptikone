package dev.otorniko;

import com.google.gson.Gson;

import java.awt.BorderLayout;
import java.awt.Taskbar;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.Image;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

/**
 * Reseptikoneen pääluokka, joka hallitsee käyttöliittymää ja reseptien
 * suodattamista.
 */
public class RecipeApp extends JFrame {
    private SidebarPanel sidebarPanel; // Sivupaneeli, joka sisältää "otsikon", raaka-aineet ja hakupalkin
    private MainAreaPanel mainAreaPanel; // Pääalueen paneeli, joka sisältää tulokset ja reseptin yksityiskohdat
    private List<RecipeData> allLoadedRecipes; // Kaikki ladatut reseptit
    private JSplitPane splitPane; // Että sivupaneelin koko pysyy oikeassa suhteessa pääpaneeliin ikkunaa suurennettaessa

    public RecipeApp() {
        super("Reseptikone");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(980, 600));
        setLayout(new BorderLayout());

        sidebarPanel = new SidebarPanel();
        mainAreaPanel = new MainAreaPanel();
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarPanel, mainAreaPanel);

        int initialSidebarWidth = 220;
        int initialFrameWidth = getPreferredSize().width;

        splitPane.setDividerLocation(initialSidebarWidth);

        if (initialFrameWidth > 0) {
            double sidebarProportion = (double) initialSidebarWidth / initialFrameWidth;
            splitPane.setResizeWeight(sidebarProportion);
        } else {
            splitPane.setResizeWeight(0.22);
        }

        splitPane.setEnabled(false);
        splitPane.setOneTouchExpandable(false);
        splitPane.setContinuousLayout(true);

        add(splitPane, BorderLayout.CENTER);

        loadRecipeData();
        setupActionListeners();

        pack();
        setLocationRelativeTo(null);

        // En tykänny kuinka hakukenttä sai heti fokuksen
        SwingUtilities.invokeLater(() -> {
            this.getRootPane().requestFocusInWindow();
        });

        List<Image> icons = new ArrayList<>();
        try {
            URL icon16URL = getClass().getResource("/icons/icon16px.png");
            URL icon24URL = getClass().getResource("/icons/icon24px.png");
            URL icon32URL = getClass().getResource("/icons/icon32px.png");
            URL icon48URL = getClass().getResource("/icons/icon48px.png");
            URL icon64URL = getClass().getResource("/icons/icon64px.png");
            URL icon128URL = getClass().getResource("/icons/icon128px.png");
            URL icon256URL = getClass().getResource("/icons/icon256px.png");
            URL icon512URL = getClass().getResource("/icons/icon512px.png");

            if (icon16URL != null)
                icons.add(new ImageIcon(icon16URL).getImage());
            if (icon24URL != null)
                icons.add(new ImageIcon(icon24URL).getImage());
            if (icon32URL != null)
                icons.add(new ImageIcon(icon32URL).getImage());
            if (icon48URL != null)
                icons.add(new ImageIcon(icon48URL).getImage());
            if (icon64URL != null)
                icons.add(new ImageIcon(icon64URL).getImage());
            if (icon128URL != null)
                icons.add(new ImageIcon(icon128URL).getImage());
            if (icon256URL != null)
                icons.add(new ImageIcon(icon256URL).getImage());
            if (icon512URL != null)
                icons.add(new ImageIcon(icon512URL).getImage());

            if (!icons.isEmpty()) {
                // Ikonit ei toimi MacOs ilman Taskbaria
                if (Taskbar.isTaskbarSupported()) {
                    Taskbar taskbar = Taskbar.getTaskbar();
                    if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                        taskbar.setIconImage(icons.get(icons.size() - 1));
                    }
                }
                // Aseta silti ikkunakuvakkeet normaalisti kaikkia alustoja varten
                setIconImages(icons);
            } else {
                System.err.println("Icon images not found or list is empty.");
            }

        } catch (Exception e) {
            System.err.println("Error loading icons: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Lataa reseptit JSON-tiedostosta ja alustaa sovelluksen tiedot.
     */
    private void loadRecipeData() {
        String resourcePath = "recipes.json";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        
        if (inputStream == null) {
            JOptionPane.showMessageDialog(this, "Virhe: Reseptitiedostoa '" + resourcePath + "' ei löytynyt!",
                    "Lukuvirhe", JOptionPane.ERROR_MESSAGE);
            this.allLoadedRecipes = Collections.emptyList();
            return;
        }

        try (Reader reader = new InputStreamReader(inputStream)) {
            Gson gson = new Gson();
            java.lang.reflect.Type recipeListType = new com.google.gson.reflect.TypeToken<ArrayList<RecipeData>>() {
            }
                    .getType();
            this.allLoadedRecipes = gson.fromJson(reader, recipeListType);
            if (this.allLoadedRecipes == null) {
                System.err.println("Warning: Failed to parse recipes or JSON file is empty/invalid. Result is null.");
                this.allLoadedRecipes = Collections.emptyList();
            }
        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("FATAL ERROR: Invalid JSON syntax in " + resourcePath);
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Virhe: Reseptitiedoston syntaksi virheellinen:\n" + e.getMessage(),
                    "Lukuvirhe", JOptionPane.ERROR_MESSAGE);
            this.allLoadedRecipes = Collections.emptyList();
        } catch (Exception e) {
            System.err.println("FATAL ERROR: Error reading recipe resource: " + resourcePath);
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Virhe: Reseptitiedoston lukuvirhe:\n" + e.getMessage(), "Lukuvirhe",
                    JOptionPane.ERROR_MESSAGE);
            this.allLoadedRecipes = Collections.emptyList();
        }
    }

    /**
     * Asettaa toimintakuuntelijat käyttöliittymän komponenteille käyttäjän toimien
     * käsittelemiseksi.
     */
    private void setupActionListeners() {
        ActionListener generalUpdateListener = e -> updateFilteredRecipes();

        if (mainAreaPanel != null && mainAreaPanel.getControlsPanel() != null) {
            mainAreaPanel.getControlsPanel().addControlChangeListener(generalUpdateListener);

        } else {
            System.err.println("Initialization Error: ControlsPanel not ready when setting up listeners.");
        }

        if (sidebarPanel != null) {
            sidebarPanel.setSelectionChangeCallback(this::updateFilteredRecipes);

        } else {
            System.err.println("Initialization Error: SidebarPanel not ready when setting up listeners.");
        }
    }

    /**
     * Päivittää suodatettujen reseptien listan käyttäjän valitsemien kriteerien
     * perusteella.
     */
    private void updateFilteredRecipes() {
        if (sidebarPanel == null || mainAreaPanel == null || mainAreaPanel.getControlsPanel() == null
                || mainAreaPanel.getResultsPanel() == null) {
            System.err.println("Update called before UI panels are ready.");
            return;
        }
        if (allLoadedRecipes == null) {
            System.err.println("Update called before recipe data is loaded.");
            mainAreaPanel.getResultsPanel().showStatusMessage("Ladataan reseptejä...");
            return;
        }

        List<String> selectedIngredients = sidebarPanel.getSelectedIngredients();
        ControlsPanel controls = mainAreaPanel.getControlsPanel();
        String selectedDiet = controls.getSelectedDietOption();
        boolean isNopeat = controls.isNopeatFilterActive();
        boolean isHelpot = controls.isHelpotFilterActive();
        boolean isHitaat = controls.isHitaatFilterActive();
        boolean isVaikeat = controls.isVaikeatFilterActive();
        boolean isKokoPerheelle = controls.isKokoPerheelleFilterActive();
        String sortOption = controls.getSelectedSortOption();
        List<RecipeData> filteredRecipes = new ArrayList<>(allLoadedRecipes);

        if (selectedIngredients != null && !selectedIngredients.isEmpty()) {
            final Set<String> selectedIngredientSet = new HashSet<>(selectedIngredients);
            filteredRecipes = filteredRecipes.stream().filter(recipe -> {
                List<String> recipeIngredients = recipe.getIngredients();
                if (recipeIngredients == null || recipeIngredients.isEmpty()) {
                    return false;
                }

                return selectedIngredientSet.containsAll(recipeIngredients);
            }).collect(Collectors.toList());
        } else {
            filteredRecipes.clear();
            mainAreaPanel.getResultsPanel().showStatusMessage("Valitse raaka-aineita löytääksesi reseptejä.");
            return;
        }

        if (!"Ei ole".equalsIgnoreCase(selectedDiet)) {
            final String selectedDietLower = selectedDiet.toLowerCase();
            filteredRecipes = filteredRecipes.stream().filter(recipe -> {
                List<String> recipeDiets = recipe.getErityisruokavalio();
                if (recipeDiets == null || recipeDiets.isEmpty()) {
                    return false;
                }

                Set<String> recipeDietsLower = recipeDiets.stream().map(String::toLowerCase)
                        .collect(Collectors.toSet());
                if ("kasvis".equals(selectedDietLower)) {
                    return recipeDietsLower.contains("kasvis") || recipeDietsLower.contains("vegaani");
                } else {
                    return recipeDietsLower.contains(selectedDietLower);
                }
            }).collect(Collectors.toList());
        }
        if (isNopeat) {
            final int maxTimeMinutesForNopeat = 20;
            filteredRecipes = filteredRecipes.stream()
                    .filter(recipe -> recipe.getTimeMinutes() <= maxTimeMinutesForNopeat).collect(Collectors.toList());
        }

        if (isHelpot) {
            final int maxStepsForHelpot = 3;
            filteredRecipes = filteredRecipes.stream()
                    .filter(recipe -> recipe.getSteps() != null && recipe.getSteps().size() <= maxStepsForHelpot)
                    .collect(Collectors.toList());
        }

        if (isHitaat) {
            final int maxTimeMinutesForHitaat = 60;
            filteredRecipes = filteredRecipes.stream()
                    .filter(recipe -> recipe.getTimeMinutes() > maxTimeMinutesForHitaat).collect(Collectors.toList());
        }

        if (isVaikeat) {
            final int minStepsForVaikeat = 5;
            filteredRecipes = filteredRecipes.stream()
                    .filter(recipe -> recipe.getSteps() != null && recipe.getSteps().size() >= minStepsForVaikeat)
                    .collect(Collectors.toList());
        }

        if (isKokoPerheelle) {
            filteredRecipes = filteredRecipes.stream().filter(recipe -> recipe.getPortions() > 2)
                    .collect(Collectors.toList());
        }

        if (sortOption != null) {
            switch (sortOption) {
                case "Aika Lyhin":
                    filteredRecipes.sort(Comparator.comparingInt(RecipeData::getTimeMinutes));
                    break;
                case "Nimi A-Ö":
                    filteredRecipes.sort(Comparator.comparing(RecipeData::getName, String.CASE_INSENSITIVE_ORDER));
                    break;
                default:
                    break;
            }
        }

        ResultsPanel results = mainAreaPanel.getResultsPanel();

        if (filteredRecipes.isEmpty()) {
            if (selectedIngredients != null && !selectedIngredients.isEmpty()) {
                results.showStatusMessage("Ei reseptejä valituilla hakuehdoilla.");
            } else {
                results.showStatusMessage("Valitse raaka-aineita löytääksesi reseptejä.");
            }
        } else {
            results.displayRecipes(filteredRecipes);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RecipeApp app = new RecipeApp();
            app.setVisible(true);
        });
    }
}