package org.protege.editor.owl.ui.lang;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.protege.editor.owl.model.lang.LangCode;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-03-15
 */
public class LangCodeRenderer implements ListCellRenderer<LangCode> {

    private final ListCellRenderer delegate = new DefaultListCellRenderer();

    @Override
    public Component getListCellRendererComponent(JList<? extends LangCode> list,
                                                  LangCode value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        String renderedValue;
        if(value != null) {
            renderedValue = "<html><body><b>" + value.getLangCode() + "</b>  ("
                    + value.getDescription() + ")</body></html>";
        }
        else {
            renderedValue = "";
        }
        return delegate.getListCellRendererComponent(list,
                                                     renderedValue,
                                                     index,
                                                     isSelected,
                                                     cellHasFocus);
    }
}
