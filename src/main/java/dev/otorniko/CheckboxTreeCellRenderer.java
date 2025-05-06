package dev.otorniko;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class CheckboxTreeCellRenderer extends JCheckBox implements TreeCellRenderer {

    private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

    public CheckboxTreeCellRenderer() {
        super();
        setOpaque(false);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {

        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            if (leaf && userObject instanceof CheckableNodeData) {
                CheckableNodeData data = (CheckableNodeData) userObject;
                this.setText(data.getText());
                this.setSelected(data.isChecked());
                this.setEnabled(tree.isEnabled());
                return this;
            }
        }

        return defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }
}