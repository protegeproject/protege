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
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
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
