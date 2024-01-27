package org.protege.editor.owl.model.search;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 09/02/16
 */
public class SearchManagePluginListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        SearchManagerPlugin plugin = (SearchManagerPlugin) value;
        label.setText(plugin.getName());
        return label;
    }

}
