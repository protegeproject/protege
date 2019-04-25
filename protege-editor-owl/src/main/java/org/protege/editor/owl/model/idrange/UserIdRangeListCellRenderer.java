package org.protege.editor.owl.model.idrange;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-25
 */
public class UserIdRangeListCellRenderer implements ListCellRenderer<UserIdRange> {

    private static final Border BORDER = BorderFactory.createEmptyBorder(1, 0, 1, 0);

    private final DefaultListCellRenderer delegate = new DefaultListCellRenderer();

    @Override
    public Component getListCellRendererComponent(JList<? extends UserIdRange> list,
                                                  UserIdRange value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        int from = value.getIdRange().getLowerBound();
        int to = value.getIdRange().getUpperBound();
        String stringValue = String.format("<html><body><b>%s</b>    [from %d to %d]</body></html>", value.getUserId(), from, to);
        JComponent component = (JComponent) delegate.getListCellRendererComponent(list, stringValue, index, isSelected, cellHasFocus);
        component.setBorder(BORDER);
        return component;
    }
}
