package org.protege.editor.owl.ui.prefix;

import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-06-10
 */
public class PrefixMappingRenderer implements ListCellRenderer<PrefixListElement> {

    private final DefaultListCellRenderer delegate = new DefaultListCellRenderer();

    @Override
    public Component getListCellRendererComponent(JList<? extends PrefixListElement> list,
                                                  PrefixListElement value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        String rendering;
        if(value instanceof PrefixesListSection) {
            rendering = ((PrefixesListSection) value).getName();
        }
        else if(value instanceof PrefixesListSectionRow) {
            PrefixMapping pm = ((PrefixesListSectionRow) value).getValue();
            rendering = String.format("<html><body><div style=\"font-weight: bold;\">%s</div><div>%s</div></body></html>",
                          pm.getPrefixName(),
                          pm.getPrefix());
        }
        else {
            rendering = value.toString();
        }
        return delegate.getListCellRendererComponent(list, rendering, index, isSelected, cellHasFocus);
    }
}
