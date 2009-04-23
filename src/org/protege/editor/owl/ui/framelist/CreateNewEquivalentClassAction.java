package org.protege.editor.owl.ui.framelist;

import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.ui.CreateDefinedClassPanel;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassExpression;
import org.semanticweb.owl.model.OWLObject;

import java.awt.event.ActionEvent;
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
 * Date: Nov 24, 2008<br><br>
 */
public class CreateNewEquivalentClassAction<C extends OWLObject> extends OWLFrameListPopupMenuAction<C> {

    protected String getName() {
        return "Create new defined class";
    }


    protected void initialise() throws Exception {
    }


    protected void dispose() throws Exception {
    }


    private OWLClassExpression getSelectedRowDescription() {
        Object selVal = getFrameList().getSelectedValue();
        if (selVal instanceof OWLFrameSectionRow) {
            List objects = ((OWLFrameSectionRow) selVal).getManipulatableObjects();
            if (objects.size() == 1){
                Object o = objects.iterator().next();
                if (o instanceof OWLClassExpression && ((OWLClassExpression)o).isAnonymous()){
                    return (OWLClassExpression)o;
                }
            }
        }
        return null;
    }


    protected void updateState() {
        setEnabled(getSelectedRowDescription() != null);
    }


    public void actionPerformed(ActionEvent e) {
        OWLClassExpression descr = getSelectedRowDescription();
        if (descr != null) {
            OWLEntityCreationSet<OWLClass> creationSet = CreateDefinedClassPanel.showDialog(descr, getOWLEditorKit());
            if (creationSet != null){
                getOWLModelManager().applyChanges(creationSet.getOntologyChanges());
                getOWLEditorKit().getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(creationSet.getOWLEntity());
            }
        }
    }
}