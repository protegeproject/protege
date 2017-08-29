package org.protege.editor.owl.ui.deprecation;

import org.protege.editor.owl.model.deprecation.DeprecationProfile;

import javax.swing.*;
import java.awt.*;

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
