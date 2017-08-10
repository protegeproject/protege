package org.protege.editor.owl.ui.library;

import org.protege.xmlcatalog.entry.Entry;

import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Created by vblagodarov on 10-08-17.
 */
public interface ILibraryEntryEditor<T extends Entry> {
    T getEntry(T oldEntry);

    boolean isInputValid();

    void addDocumentListener(DocumentListener documentListener);

    Component getComponent();
}
