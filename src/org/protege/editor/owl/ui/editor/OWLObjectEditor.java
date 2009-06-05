package org.protege.editor.owl.ui.editor;

import javax.swing.*;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 15-Feb-2007<br><br>
 */
public interface OWLObjectEditor<O> {

    String getEditorTypeName();


    boolean canEdit(Object object);


//    boolean isPreferred(Object object);


    JComponent getEditorComponent();


    O getEditedObject();


    Set<O> getEditedObjects();


    boolean setEditedObject(O editedObject);


    boolean isMultiEditSupported();

    
    void clear();


    void dispose();


    void setHandler(OWLObjectEditorHandler<O> handler);


    OWLObjectEditorHandler<O> getHandler();
}
