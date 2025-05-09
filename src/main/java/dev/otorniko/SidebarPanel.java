package dev.otorniko;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SidebarPanel extends JPanel {

    private JTextField searchField;
    private JTree ingredientTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private List<IngredientData> allLoadedIngredients;

    private Runnable onSelectionChangeCallback = null;

    private static final String PLACEHOLDER_TEXT = "Hae...";
    private static final Color PLACEHOLDER_COLOR = Color.GRAY;
    private static final Color NORMAL_TEXT_COLOR = UIManager.getColor("TextField.foreground");

    private Set<String> centrallyCheckedIngredientNames = new HashSet<>();

    public SidebarPanel() {
        super(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        this.allLoadedIngredients = loadIngredientsFromJson("ingredients.json");
        initComponents();
    }

    public void setSelectionChangeCallback(Runnable callback) {
        this.onSelectionChangeCallback = callback;
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout(0, 5));
        JLabel titleLabel = new JLabel("Raaka-aineet");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        searchField = new JTextField();
        setPlaceholder();

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchField, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                if (!searchField.getText().equals(PLACEHOLDER_TEXT)
                        || searchField.getForeground().equals(NORMAL_TEXT_COLOR)) {
                    filterAndRebuildTree();
                }
            }

            public void removeUpdate(DocumentEvent e) {
                if (!searchField.getText().equals(PLACEHOLDER_TEXT) && !searchField.getText().isEmpty()) {
                    filterAndRebuildTree();
                } else if (searchField.getText().isEmpty() && searchField.getForeground().equals(NORMAL_TEXT_COLOR)) {
                    filterAndRebuildTree();
                }
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(PLACEHOLDER_TEXT)
                        && searchField.getForeground().equals(PLACEHOLDER_COLOR)) {
                    searchField.setText("");
                    searchField.setForeground(NORMAL_TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    setPlaceholder();
                    filterAndRebuildTree();
                }
            }
        });

        rootNode = new DefaultMutableTreeNode("Raaka-aineet");
        treeModel = new DefaultTreeModel(rootNode);
        ingredientTree = new JTree(treeModel);
        ingredientTree.setRootVisible(false);
        ingredientTree.setCellRenderer(new CheckboxTreeCellRenderer());

        ingredientTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = ingredientTree.getRowForLocation(e.getX(), e.getY());
                if (row == -1)
                    return;
                TreePath path = ingredientTree.getPathForRow(row);
                if (path == null)
                    return;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object userObject = node.getUserObject();
                if (node.isLeaf() && userObject instanceof CheckableNodeData) {
                    CheckableNodeData data = (CheckableNodeData) userObject;
                    boolean isNowChecked = !data.isChecked();
                    data.setChecked(isNowChecked);

                    if (isNowChecked) {
                        centrallyCheckedIngredientNames.add(data.getName());
                    } else {
                        centrallyCheckedIngredientNames.remove(data.getName());
                    }

                    Rectangle rowBounds = ingredientTree.getRowBounds(row);
                    if (rowBounds != null) {
                        ingredientTree.repaint(rowBounds);
                    }
                    if (onSelectionChangeCallback != null) {
                        onSelectionChangeCallback.run();
                    }
                }
            }
        });

        ingredientTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane treeScrollPane = new JScrollPane(ingredientTree);
        add(treeScrollPane, BorderLayout.CENTER);

        filterAndRebuildTree();
    }

    private void setPlaceholder() {
        searchField.setText(PLACEHOLDER_TEXT);
        searchField.setForeground(PLACEHOLDER_COLOR);
    }

    private void filterAndRebuildTree() {
        String searchTerm = getSearchTermInternal().toLowerCase().trim();
        List<IngredientData> ingredientsToDisplay;

        if (allLoadedIngredients == null) {
            rootNode.removeAllChildren();
            rootNode.add(new DefaultMutableTreeNode("Error: Ingredients not loaded!"));
            treeModel.reload(rootNode);
            return;
        }

        if (searchTerm.isEmpty()) {
            ingredientsToDisplay = new ArrayList<>(this.allLoadedIngredients);
        } else {
            ingredientsToDisplay = this.allLoadedIngredients.stream().filter(ingredient -> ingredient.getName()
                    .toLowerCase().contains(searchTerm)
                    || (ingredient.getCategory() != null && ingredient.getCategory().toLowerCase().contains(searchTerm))
                    || (ingredient.getTags() != null
                            && ingredient.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(searchTerm))))
                    .collect(Collectors.toList());
        }
        buildTreeFromIngredients(ingredientsToDisplay);
    }

    private void buildTreeFromIngredients(List<IngredientData> ingredients) {
        rootNode.removeAllChildren();

        if (ingredients == null || ingredients.isEmpty()) {
            if (!getSearchTermInternal().trim().isEmpty()) {
                rootNode.add(new DefaultMutableTreeNode("Ei hakutuloksia"));
            } else {
                rootNode.add(new DefaultMutableTreeNode("Raaka-aineita ei löytynyt"));
            }
            treeModel.reload(rootNode);
            return;
        }

        Map<String, List<IngredientData>> categorizedIngredients = new TreeMap<>();
        for (IngredientData ingredient : ingredients) {
            String category = ingredient.getCategory();
            if (category == null || category.trim().isEmpty()) {
                category = "Muut";
            }
            categorizedIngredients.computeIfAbsent(category, k -> new ArrayList<>()).add(ingredient);
        }

        for (Map.Entry<String, List<IngredientData>> entry : categorizedIngredients.entrySet()) {
            String categoryName = entry.getKey();
            List<IngredientData> itemsInCategory = entry.getValue();
            itemsInCategory.sort((i1, i2) -> i1.getName().compareToIgnoreCase(i2.getName()));

            DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(categoryName);
            boolean categoryHasVisibleChildren = false;
            for (IngredientData item : itemsInCategory) {
                boolean isChecked = centrallyCheckedIngredientNames.contains(item.getName());
                categoryNode.add(new DefaultMutableTreeNode(new CheckableNodeData(item.getName(), isChecked)));
                categoryHasVisibleChildren = true;
            }
            if (categoryHasVisibleChildren) {
                rootNode.add(categoryNode);
            }
        }

        treeModel.reload(rootNode);

        for (int i = 0; i < ingredientTree.getRowCount(); i++) {
            TreePath path = ingredientTree.getPathForRow(i);
            if (path != null && path.getPathCount() == 2) {
                ingredientTree.expandRow(i);
            }
        }
    }

    private List<IngredientData> loadIngredientsFromJson(String resourcePath) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            System.err.println("Cannot find resource: " + resourcePath);
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                    "Virhe: Raaka-ainetiedostoa '" + resourcePath + "' ei löytynyt!", "Latausvirhe",
                    JOptionPane.ERROR_MESSAGE));
            return new ArrayList<>();
        }
        try (Reader reader = new InputStreamReader(inputStream)) {
            Gson gson = new Gson();
            Type ingredientListType = new TypeToken<ArrayList<IngredientData>>() {
            }.getType();
            List<IngredientData> loaded = gson.fromJson(reader, ingredientListType);
            if (loaded == null) {
                System.err.println("Warning: Failed to parse ingredients or JSON file '" + resourcePath
                        + "' is empty/invalid. Result is null.");
                return new ArrayList<>();
            }
            return loaded;
            //ei toiminut ku importtas gson:sta :DDD
        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("FATAL ERROR: Invalid JSON syntax in " + resourcePath);
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                    "Virhe: Raaka-ainetiedoston '" + resourcePath + "' syntaksi virheellinen:\n" + e.getMessage(),
                    "Latausvirhe", JOptionPane.ERROR_MESSAGE));
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error reading or parsing JSON resource: " + resourcePath);
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                    "Virhe: Raaka-ainetiedoston '" + resourcePath + "' lukuvirhe:\n" + e.getMessage(), "Latausvirhe",
                    JOptionPane.ERROR_MESSAGE));
            return new ArrayList<>();
        }
    }

    private String getSearchTermInternal() {
        if (searchField != null) {
            String currentText = searchField.getText();
            if (currentText.equals(PLACEHOLDER_TEXT) && searchField.getForeground().equals(PLACEHOLDER_COLOR)) {
                return "";
            }
            return currentText;
        }
        return "";
    }

    public String getSearchTerm() { return getSearchTermInternal(); }

    public JTree getIngredientTree() { return ingredientTree; }

    public List<String> getSelectedIngredients() { return new ArrayList<>(centrallyCheckedIngredientNames); }
}