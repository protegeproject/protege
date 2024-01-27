package org.protege.editor.owl.ui.deprecation;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.protege.editor.owl.model.deprecation.DeprecationProfile;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29 Aug 2017
 */
public class DeprecationProfileRenderer implements ListCellRenderer<DeprecationProfile> {

    private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();

    @Override
    public Component getListCellRendererComponent(JList<? extends DeprecationProfile> list,
                                                  DeprecationProfile value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        return renderer.getListCellRendererComponent(list,
                                                     value.getName(),
                                                     index,
                                                     isSelected,
                                                     cellHasFocus);
    }
}
