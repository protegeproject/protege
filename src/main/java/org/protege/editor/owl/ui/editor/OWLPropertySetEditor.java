package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLRuntimeException;

import javax.swing.*;
import java.awt.*;
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
 * Date: Jun 4, 2009<br><br>
 */
public class OWLPropertySetEditor extends AbstractOWLObjectEditor<Set<OWLPropertyExpression>> implements VerifiedInputEditor {

    private ExpressionEditor<Set<OWLPropertyExpression>> editor;


    public OWLPropertySetEditor(OWLEditorKit eKit) {
        OWLExpressionChecker<Set<OWLPropertyExpression>> checker = eKit.getModelManager().getOWLExpressionCheckerFactory().getPropertySetChecker();
        editor = new ExpressionEditor<Set<OWLPropertyExpression>>(eKit, checker);
        editor.setPreferredSize(new Dimension(300, 200));
    }


    public String getEditorTypeName() {
        return "Set of properties";
    }


    public boolean canEdit(Object object) {
        return checkSet(object, OWLPropertyExpression.class);
    }


    public JComponent getEditorComponent() {
        return editor;
    }


    public Set<OWLPropertyExpression> getEditedObject() {
        try {
            return editor.createObject();
        }
        catch (OWLException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public boolean setEditedObject(Set<OWLPropertyExpression> editedObject) {
        editor.setExpressionObject(editedObject);
        return true;
    }


    public void dispose() {
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        editor.addStatusChangedListener(listener);
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        editor.removeStatusChangedListener(listener);
    }
}
