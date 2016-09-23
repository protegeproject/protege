package org.protege.editor.owl.ui.editor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 15-Feb-2007<br><br>
 */
public interface OWLObjectEditor<O> {

    @Nonnull
    String getEditorTypeName();

    boolean canEdit(Object object);

    @Nonnull
    JComponent getEditorComponent();

    @Nullable
    O getEditedObject();

    Set<O> getEditedObjects();

    boolean setEditedObject(@Nullable O editedObject);

    boolean isMultiEditSupported();

    void clear();

    void dispose();

    void setHandler(OWLObjectEditorHandler<O> handler);

    OWLObjectEditorHandler<O> getHandler();
}
