package dev.otorniko;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Luokka, joka esittää sivupaneelin, jossa käyttäjä voi valita raaka-aineita.
 * Tämä luokka käyttää JTree-luokkaa raaka-aineiden esittämiseen puumaisessa
 * rakenteessa.
 */
public class SidebarPanel extends JPanel {

    private JTextField searchField;
    private JTree ingredientTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;

    private Runnable onSelectionChangeCallback = null;

    public SidebarPanel() {
        super(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadAndBuildTree();
    }

    public void setSelectionChangeCallback(Runnable callback) { this.onSelectionChangeCallback = callback; }

    /**
     * Alustaa SidebarPanelin komponentit, mukaan lukien raaka-ainepuun.
     */
    private void initComponents() {
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
                    data.setChecked(!data.isChecked());
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
    }

    /**
     * Lataa raaka-aineet JSON-tiedostosta ja rakentaa puumaisen rakenteen
     * raaka-ainepuulle.
     */
    private void loadAndBuildTree() {
        List<IngredientData> ingredients = loadIngredientsFromJson("ingredients.json");
        if (ingredients == null) {
            rootNode.add(new DefaultMutableTreeNode("Error loading ingredients!"));
            treeModel.reload();
            System.err.println("Failed to load ingredients.");
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

        rootNode.removeAllChildren();
        for (Map.Entry<String, List<IngredientData>> entry : categorizedIngredients.entrySet()) {
            String categoryName = entry.getKey();
            List<IngredientData> itemsInCategory = entry.getValue();
            itemsInCategory.sort((i1, i2) -> i1.getName().compareToIgnoreCase(i2.getName()));
            DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(categoryName);
            for (IngredientData item : itemsInCategory) {
                categoryNode.add(new DefaultMutableTreeNode(new CheckableNodeData(item.getName(), false)));
            }
            rootNode.add(categoryNode);
        }
        treeModel.reload(rootNode);
        for (int i = 0; i < ingredientTree.getRowCount(); i++) {
            TreePath path = ingredientTree.getPathForRow(i);
            if (path != null && path.getPathCount() == 2) {
                ingredientTree.expandRow(i);
            }
        }
    }

    /**
     * Lataa raaka-aineet JSON-resurssitiedostosta.
     *
     * @param resourcePath JSON-resurssitiedoston polku
     * @return lista IngredientData-olioita tai null, jos lataus epäonnistuu
     */
    private List<IngredientData> loadIngredientsFromJson(String resourcePath) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            System.err.println("Cannot find resource: " + resourcePath);
            return null;
        }
        try (Reader reader = new InputStreamReader(inputStream)) {
            Gson gson = new Gson();
            Type ingredientListType = new TypeToken<ArrayList<IngredientData>>() {}.getType();
            return gson.fromJson(reader, ingredientListType);
        } catch (Exception e) {
            System.err.println("Error reading or parsing JSON resource: " + resourcePath);
            e.printStackTrace();
            return null;
        }
    }

    public String getSearchTerm() { return searchField.getText(); }

    public JTree getIngredientTree() { return ingredientTree; }

    public List<String> getSelectedIngredients() {
        List<String> selected = new ArrayList<>();
        findCheckedLeafNodes(rootNode, selected);
        return selected;
    }

    private void findCheckedLeafNodes(DefaultMutableTreeNode node, List<String> checkedItems) {
        Object userObject = node.getUserObject();
        if (node.isLeaf() && userObject instanceof CheckableNodeData) {
            CheckableNodeData data = (CheckableNodeData) userObject;
            if (data.isChecked()) {
                checkedItems.add(data.getName());
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChildAt(i) instanceof DefaultMutableTreeNode) {
                    findCheckedLeafNodes((DefaultMutableTreeNode) node.getChildAt(i), checkedItems);
                }
            }
        }
    }
}