package org.protege.editor.owl.ui.ontology.annotation;

import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.editor.OWLAnnotationEditor;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

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
 * Date: Jun 1, 2009<br><br>
 */
public class OntologyAnnotationItem implements MListItem {

    private OWLOntology ont;

    private OWLAnnotation annot;

    private OWLEditorKit eKit;

    private OWLAnnotationEditor editor;


    public OntologyAnnotationItem(OWLOntology ont, OWLAnnotation annot, OWLEditorKit eKit) {
        this.ont = ont;
        this.annot = annot;
        this.eKit = eKit;
    }


    public boolean isEditable() {
        return true;
    }


    public void handleEdit() {
        // don't need to check the section as only the direct imports can be added
        if (editor == null){
            editor = new OWLAnnotationEditor(eKit);
        }
        editor.setEditedObject(annot);
        UIHelper uiHelper = new UIHelper(eKit);
        int ret = uiHelper.showValidatingDialog("Ontology Annotation", editor.getEditorComponent(), null);

        if (ret == JOptionPane.OK_OPTION) {
            OWLAnnotation newAnnotation = editor.getEditedObject();
            if (!newAnnotation.equals(annot)){
                List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
                changes.add(new RemoveOntologyAnnotation(ont, annot));
                changes.add(new AddOntologyAnnotation(ont, newAnnotation));
                eKit.getModelManager().applyChanges(changes);
            }
        }
    }


    public boolean isDeleteable() {
        return true;
    }


    public boolean handleDelete() {
        eKit.getModelManager().applyChange(new RemoveOntologyAnnotation(ont, annot));
        return true;
    }


    public String getTooltip() {
        return "";
    }


    public OWLAnnotation getAnnotation() {
        return annot;
    }
}
