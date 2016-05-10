package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.Disposable;
import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClassExpression;

import javax.swing.*;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Dec 23, 2008<br><br>
 */
public interface OWLClassExpressionEditor extends ProtegePluginInstance, VerifiedInputEditor, Disposable {

    String getEditorName();

    JComponent getComponent();

    boolean isValidInput();


    /**
     * @param description the class expression to be edited (may be null which is used to "reset" the editor)
     * @return false if the description cannot be represented by this editor (should always be able to handle null)
     */
    boolean setDescription(OWLClassExpression description);

    Set<OWLClassExpression> getClassExpressions();


    void setup(String uniqueIdentifier, String label, OWLEditorKit editorKit);


    /**
     * Called before initialisation - the user should not have to deal with this
     *  - this allows an editor to implement different behaviours based on where the description is going
     * @param type the type of axiom (if any) this description will be added to
     */
    @SuppressWarnings("unchecked")
    void setAxiomType(AxiomType type);
}
